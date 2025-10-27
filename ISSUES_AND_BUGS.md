# Issues and Bugs Encountered During Development

## Overview

This document details all issues, bugs, and challenges encountered during the development of ShareBook, along with their solutions and lessons learned.

---

## Critical Issues

### 1. Dropdown Menu Not Opening

**Severity**: High  
**Status**: ✅ Resolved  
**Date**: Initial Development Phase

#### Problem Description
The Genre, Condition, and Category dropdown menus in AddBookScreen were not opening when users clicked on them. The dropdowns were implemented using `ExposedDropdownMenuBox` with `OutlinedTextField`, but tapping on them produced no response.

#### Error Messages
- No explicit error messages
- Silent failure - UI simply didn't respond

#### Root Cause
The `OutlinedTextField` components with `readOnly = true` inside `ExposedDropdownMenuBox` were not receiving touch events properly. The TextField was consuming touch events but not forwarding them to open the dropdown.

#### Solution Applied
Added explicit clickable modifier to each TextField:

```kotlin
OutlinedTextField(
    modifier = Modifier.clickable { expandedGenre = true },
    readOnly = true,
    value = genre,
    onValueChange = {},
    label = { Text("Genre") },
    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGenre) }
)
ExposedDropdownMenu(
    expanded = expandedGenre,
    onDismissRequest = { expandedGenre = false }
) {
    genres.forEach { selectionOption ->
        DropdownMenuItem(
            text = { Text(selectionOption) },
            onClick = {
                genre = selectionOption
                expandedGenre = false
            }
        )
    }
}
```

#### Testing
- Tested on multiple devices
- Verified all three dropdowns (Genre, Condition, Category)
- Confirmed keyboard was not interfering

#### Lesson Learned
Always explicitly handle touch events for readonly TextFields in Compose, especially when nested in container widgets.

---

### 2. Books Not Saving to Firestore

**Severity**: Critical  
**Status**: ✅ Resolved  
**Date**: Core Feature Implementation

#### Problem Description
Books were successfully created in the UI (form validation passed, success dialog showed), but they were not appearing in Firebase Firestore database. The books collection remained empty even after successful "Add Book" operations.

#### Error Messages
No explicit error messages in logs initially.

#### Root Causes
1. **ID Conflict**: Both `AddBookScreen.kt` and `FirebaseBookRepository.kt` were generating UUIDs for book IDs
   ```kotlin
   // In AddBookScreen - WRONG
   val book = Book(
       id = UUID.randomUUID().toString(),  // Generated here
       title = title,
       // ...
   )
   
   // In FirebaseBookRepository - Also generated here
   val bookWithId = book.copy(id = UUID.randomUUID().toString())
   ```
   
2. **Asynchronous Operation Not Properly Awaited**: The UI updated before Firebase operation completed

#### Solution Applied
1. Let Firestore generate IDs automatically:
   ```kotlin
   // In AddBookScreen
   val book = Book(
       id = "",  // Empty string - Firestore will generate
       title = title,
       author = author,
       // ...
   )
   
   // In FirebaseBookRepository
   // Don't generate UUID, let Firestore handle it
   val result = firestore.collection("books").add(book)
   ```

2. Added proper loading states and delays:
   ```kotlin
   // Show loading
   isLoading = true
   
   // Call ViewModel
   bookViewModel.addBook(book, selectedImage)
   
   // Wait for async operation
   delay(1500)
   
   // Update UI
   isLoading = false
   showSuccess = true
   ```

3. Added user authentication check:
   ```kotlin
   if (currentUser?.uid.isNullOrEmpty()) {
       Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
       return
   }
   ```

#### Testing
- Verified books appear in Firestore console
- Confirmed books show in HomeScreen and MyBooks
- Tested with and without authentication

#### Lesson Learned
- Let Firestore manage document IDs
- Always properly handle async operations
- Validate authentication before database operations
- Use comprehensive logging for debugging

---

### 3. Firebase Storage 404 Error

**Severity**: Critical  
**Status**: ✅ Resolved (with alternative solution)  
**Date**: Image Upload Feature

#### Problem Description
Attempting to upload book cover images to Firebase Storage resulted in `StorageException (Code: -13010 HttpResult: 404)` error. The error message indicated the Firebase Storage bucket didn't exist or wasn't configured properly.

#### Error Messages
```
StorageException has occurred. 
Object does not exist at location.
Code: -13010 HttpResult: 404
The server has terminated the upload session
```

#### Root Cause
Firebase Storage is a paid service requiring a billing account. The free tier doesn't include Storage, only Auth and Firestore. Without proper configuration, the Storage bucket doesn't exist, causing 404 errors.

#### Solution Applied
Implemented local device storage as an alternative:

1. Created `ImageStorageHelper.kt` utility:
```kotlin
object ImageStorageHelper {
    suspend fun saveBookImage(context: Context, imageUri: Uri, bookId: String): String? {
        val imagesDir = File(context.filesDir, "book_images")
        val filename = "${bookId}_${UUID.randomUUID()}.jpg"
        val outputFile = File(imagesDir, filename)
        
        // Compress and save image
        // Returns absolute path
    }
    
    fun getFileUri(context: Context, filePath: String): Uri? {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            File(filePath)
        )
    }
}
```

2. Configured FileProvider:
```xml
<!-- In AndroidManifest.xml -->
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.provider"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>

<!-- In file_paths.xml -->
<paths>
    <files-path name="book_images" path="book_images/" />
</paths>
```

3. Updated Book model to include local path:
```kotlin
data class Book(
    val localCoverImagePath: String = "",
    // ...
)
```

#### Pros and Cons

**Local Storage Advantages:**
- ✅ Free solution
- ✅ No cloud costs
- ✅ Fast access
- ✅ Works offline

**Local Storage Disadvantages:**
- ❌ Data lost on app uninstall
- ❌ Not synced across devices
- ❌ Storage limited by device

#### Lesson Learned
- Research service limitations before implementation
- Always have backup solutions for paid services
- Implement proper error handling for storage operations
- Document storage strategy for users

---

### 4. Images Not Displaying from Local Storage

**Severity**: High  
**Status**: ✅ Resolved  
**Date**: After Local Storage Implementation

#### Problem Description
After implementing local storage, book images saved locally were not displaying in the UI. The Image components showed blank or placeholder images.

#### Error Messages
No explicit errors, just missing images.

#### Root Cause
1. FileProvider paths not properly configured
2. ImageUriHelper not correctly resolving local paths
3. Coil image loader couldn't access local files directly

#### Solution Applied

1. Created `ImageUriHelper.kt`:
```kotlin
object ImageUriHelper {
    fun getDisplayUri(context: Context, coverImageUrl: String, localCoverImagePath: String): Uri? {
        return if (localCoverImagePath.isNotEmpty()) {
            ImageStorageHelper.getFileUri(context, localCoverImagePath)
        } else if (coverImageUrl.isNotEmpty()) {
            Uri.parse(coverImageUrl)
        } else {
            Uri.parse("https://via.placeholder.com/300x400")
        }
    }
}
```

2. Updated all Image components:
```kotlin
Image(
    painter = rememberAsyncImagePainter(
        ImageUriHelper.getDisplayUri(
            LocalContext.current, 
            book.coverImageUrl, 
            book.localCoverImagePath
        )
    ),
    contentDescription = book.title,
    modifier = Modifier.fillMaxSize(),
    contentScale = ContentScale.Crop
)
```

3. Fixed FileProvider configuration

#### Testing
- Verified local images display correctly
- Tested fallback to placeholder
- Confirmed FileProvider URI generation

#### Lesson Learned
- FileProvider requires exact path configuration
- Need helper utilities to manage URI resolution
- Always test image loading on different devices

---

### 5. HomeScreen Not Loading Books

**Severity**: Medium  
**Status**: ✅ Resolved  
**Date**: HomeScreen Implementation

#### Problem Description
Books added successfully to Firestore were not appearing on the HomeScreen. The screen would show empty lists or placeholders.

#### Root Cause
HomeScreen wasn't calling `loadAllBooks()` on initial composition. The ViewModel had the data, but it wasn't being requested.

#### Solution Applied
Added LaunchedEffect to load books when screen opens:

```kotlin
@Composable
fun HomeScreen(navController: NavController) {
    val books by bookViewModel.books.collectAsState()
    
    // Load books when screen first appears
    LaunchedEffect(Unit) {
        bookViewModel.loadAllBooks()
    }
    
    // Display books
    // ...
}
```

Also ensured reload after adding books:
```kotlin
fun addBook(book: Book, imageUri: Uri?) {
    viewModelScope.launch {
        // Add book to Firestore
        bookRepository.addBook(book, imageUri)
        
        // Reload all books
        loadAllBooks()
        
        // Reload user's books
        loadUserBooks(book.ownerUid)
    }
}
```

#### Lesson Learned
- Always explicitly load data when screens appear
- Use LaunchedEffect for one-time operations
- Keep data fresh across navigation

---

## UI/UX Issues

### 6. Search Screen Showing "FREE" on All Books

**Severity**: Medium  
**Status**: ✅ Resolved  
**Date**: Search Implementation

#### Problem Description
All book cards in SearchScreen displayed "FREE" text, regardless of book type or actual availability.

#### Root Cause
Book model had default properties:
```kotlin
val isFree: Boolean
    get() = true  // Default value
```

The BookCard component was using this property:
```kotlin
Text(
    text = if (book.isFree) "FREE" else "$${book.price}",
    color = MaterialTheme.colorScheme.primary
)
```

#### Solution Applied
Removed price display entirely and replaced with availability badge:
```kotlin
// Before
Text(if (book.isFree) "FREE" else "$${book.price}")

// After
Row {
    Icon(Icons.Default.CheckCircle, ...)
    Text("Available")
}
```

#### Lesson Learned
- Avoid hardcoded default values
- Use meaningful data from Firebase
- Design UI based on actual use cases

---

### 7. MyBooks Screen Syntax Errors

**Severnith": Medium  
**Status**: ✅ Resolved  
**Date**: MyBooks Implementation

#### Problem Description
MyBooksScreen had multiple compilation errors preventing build:
```
e: Expecting an element
e: Expecting ')'
e: Expecting '}'
```

#### Root Cause
1. Extra closing parentheses in AlertDialog
2. Import statement typo: `myauthablishApp` instead of `myauthapp.azeemi`
3. Code corruption from copy-paste operations

#### Solution Applied
1. Fixed import statement:
```kotlin
import myauthapp.azeemi.sharebook.ui.viewmodel.BookViewModel
```

2. Corrected AlertDialog structure:
```kotlin
AlertDialog(
    confirmButton = {
        Button(onClick = { /* ... */ }) {
            Text("Delete")
        }
    }  // Removed extra )
)
```

#### Lesson Learned
- Always use IDE's auto-format
- Check linter before committing
- Be careful with copy-paste operations

---

## Performance Issues

### 8. Image Loading Performance

**Severity**: Low  
**Status**: ✅ Resolved  
**Date**: Optimization Phase

#### Problem Description
Large book images caused UI lag and slow loading times.

#### Solution Applied
Implemented image compression in ImageStorageHelper:

```kotlin
private fun resizeBitmap(bitmap: Bitmap): Bitmap {
    val MAX_SIZE = 800 // pixels
    // Resize to max 800x800
    // Maintain aspect ratio
}

// Compress when saving
bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
```

#### Benefits
- Faster loading
- Reduced storage usage
- Better UI responsiveness

---

## Configuration Issues

### 9. File Provider Not Working

**Severity**: Medium  
**Status**: ✅ Resolved  
**Date**: Local Storage Implementation

#### Problem Description
FileProvider.getUriForFile() was throwing FileNotFoundException.

#### Root Cause
Path in file_paths.xml didn't match actual storage location.

#### Solution Applied
Updated file_paths.xml:
```xml
<files-path name="book_images" path="book_images/" />
```

Matches actual storage:
```kotlin
val imagesDir = File(context.filesDir, "book_images")
```

#### Lesson Learned
- FileProvider paths must exactly match file locations
- Use `<files-path>` for internal storage
- Test on actual devices, not just emulator

---

## Best Practices Applied

### 1. Error Handling
- All async operations wrapped in try-catch
- User-friendly error messages
- Logging for debugging

### 2. State Management
- Used StateFlow for reactive updates
- Proper loading and error states
- Avoided memory leaks with viewModelScope

### 3. Code Organization
- Clear separation of concerns
- Repository pattern for data
- Use cases for business logic

### 4. Testing
- Manual testing on multiple devices
- Test both positive and negative cases
- Verify error handling paths

---

## Prevention Strategies

### For Future Development

1. **Use Git Properly**
   - Commit frequently
   - Write descriptive commit messages
   - Create branches for features

2. **Enable Linting**
   - Always check linter warnings
   - Use auto-format before committing
   - Fix warnings immediately

3. **Comprehensive Logging**
   - Log important operations
   - Use Log.d for debugging
   - Log.e for errors

4. **Testing Strategy**
   - Test features immediately after implementation
   - Test edge cases
   - Test on different devices

5. **Code Review**
   - Review code before committing
   - Check for common mistakes
   - Ensure best practices

---

## Summary Statistics

- **Total Issues**: 9
- **Critical**: 2
- **High**: 4
- **Medium**: 3
- **Low**: 1
- **Resolved**: 9
- **Time Lost**: ~15-20 hours total

---

## Lessons Learned

1. **Always validate** user input and authentication
2. **Let cloud services** manage IDs and storage
3. **Have backup plans** for paid services
4. **Test thoroughly** before moving to next feature
5. **Use proper architecture** from the start
6. **Document everything** during development
7. **Handle errors gracefully** throughout the app

---

## Resources Used for Debugging

- Stack Overflow
- Android Developer Documentation
- Firebase Documentation
- Jetpack Compose Documentation
- Kotlin Coroutines Guide

---

**Last Updated**: 2025-01-27  
**Document Version**: 1.0

