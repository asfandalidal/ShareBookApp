# Firebase Storage Setup - Fix 404 Error

## Problem:
Getting a 404 error when uploading book cover images to Firebase Storage:
```
StorageException: Object does not exist at location
Code: -13010 HttpResult: 404
```

## Root Cause:
Firebase Storage bucket hasn't been set up yet or doesn't have write permissions.

## Solution Options:

### Option 1: Set Up Firebase Storage (Recommended)

1. Go to Firebase Console: https://console.firebase.google.com/project/bookshare-72ba6/storage

2. Click "Get Started" to create the storage bucket

3. Set Storage Rules (go to Rules tab):
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Allow anyone to read images
    match /book_covers/{fileName} {
      allow read: if true;
      // Allow authenticated users to upload
      allow write: if request.auth != null;
    }
    
    // Allow authenticated users to manage their own files
    match /{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

4. Click "Publish" to save the rules

### Option 2: Use Without Images (Temporary Fix)

I've updated the code to save books **without images** if Storage upload fails. Books will save to Firestore successfully even if the image upload fails.

**Current behavior:**
- If image upload fails → Book saves to Firestore with empty `coverImageUrl`
- Book still appears in your app
- No error will crash the app

## Quick Test:

### Test Without Image:
1. Open AddBookScreen
2. Fill in book details
3. **Don't select an image**
4. Click "Add Book"
5. Book should save successfully to Firestore

### Test With Image:
1. Set up Firebase Storage (Option 1)
2. Add a book with an image
3. Image should upload to `book_covers/` folder
4. Book saves with image URL

## Current Code Behavior:

```kotlin
// If image upload fails, it returns empty string
// Book still saves to Firestore successfully
private suspend fun uploadBookCover(...): String {
    return try {
        // Try to upload image
        storageRef.putFile(imageUri).await()
        storageRef.downloadUrl.await().toString()
    } catch (e: Exception) {
        Log.e("FirebaseBook", "Upload failed: ${e.message}")
        "" // Return empty string - don't crash
    }
}
```

## Verification Steps:

1. **Check Firestore**: https://console.firebase.google.com/project/bookshare-72ba6/firestore
   - Look for `books` collection
   - Check if new books are being created

2. **Check Storage**: https://console.firebase.google.com/project/bookshare-72ba6/storage
   - Look for `book_covers` folder
   - Only if Storage is set up

3. **Check App**:
   - HomeScreen should show books (even without images)
   - MyBooks should show your added books

## Fix Applied:

✅ Code updated to handle Storage 404 errors gracefully
✅ Books will save to Firestore even if image upload fails
✅ No more crashes when trying to upload images

## Next Steps:

1. Add books **without images** for now (works immediately)
2. Set up Firebase Storage when ready (follow Option 1)
3. Then add books **with images** - they'll upload successfully

## Status:

- ✅ Firestore working
- ✅ Books saving without images
- ⚠️ Image upload: Need to set up Firebase Storage

