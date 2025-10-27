# ShareBook - Book Sharing Mobile Application

<div align="center">
  
![Android](https://img.shields.io/badge/Platform-Android-green)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange)
![Firebase](https://img.shields.io/badge/Firebase-Enabled-yellow)

**A modern Android application for sharing books with your community**

[Features](#-features) • [Problem Statement](#-real-world-problem-identification) • [Solution](#-proposed-solution) • [Installation](#-installation) • [Documentation](#-documentation)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Real World Problem Identification](#-real-world-problem-identification)
- [Proposed Solution](#-proposed-solution)
- [Features](#-features)
- [Screenshots](#-screenshots)
- [Technical Stack](#-technical-stack)
- [Architecture](#-architecture)
- [Data Storage](#-data-storage)
- [State Management](#-state-management)
- [Responsive Design](#-responsive-design)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [APIs and Dependencies](#-apis-and-dependencies)
- [Project Structure](#-project-structure)
- [Issues and Bugs Encountered](#-issues-and-bugs-encountered)
- [Testing](#-testing)
- [Future Enhancements](#-future-enhancements)
- [Contributors](#-contributors)
- [License](#-license)

---

## 🌟 Overview

**ShareBook** is a mobile application developed for Android that facilitates book sharing within communities. Users can add books from their collection, search for books, and connect with other book enthusiasts to share, donate, or exchange books locally.

### Key Highlights

- 📱 Native Android application built with Kotlin and Jetpack Compose
- ☁️ Cloud-based data storage using Firebase Firestore
- 🔐 Secure authentication with Firebase Auth
- 📸 Local image storage for book covers
- 🎨 Modern Material Design 3 UI
- 📱 Responsive design supporting multiple screen sizes
- 🔍 Advanced search and filtering capabilities

---

## 🎯 Real World Problem Identification

### Problem Statement

In today's world, many people have books sitting on their shelves that they've already read or no longer need. Simultaneously, there are countless individuals looking for books to read but facing several challenges:

#### Current Challenges:

1. **High Cost**: Purchasing new books can be expensive, especially for students and avid readers
2. **Limited Access**: Public libraries may not have all desired books
3. **Waste of Resources**: Unused books accumulate space and waste valuable resources
4. **No Local Network**: No convenient way to find and connect with local book owners
5. **Environmental Impact**: Manufacturing new books has significant environmental costs

### Target Users

- **Students** looking for affordable academic and leisure books
- **Book Enthusiasts** wanting to discover new books and share their collection
- **Community Members** interested in sustainable practices
- **Local Communities** seeking to build a sharing economy

---

## 💡 Proposed Solution

**ShareBook** addresses these challenges by creating a local community-based book sharing platform that enables:

### Core Solution Features:

1. **Digital Catalog**: Users can upload their books with details (title, author, description, genre)
2. **Smart Search**: Find books by title, author, or genre
3. **Contact Integration**: Direct communication via WhatsApp or Email
4. **Local Storage**: Store book images locally to avoid cloud storage costs
5. **User Authentication**: Secure user accounts with Firebase Auth
6. **Profile Management**: Users can manage their profile and book collection

### Benefits:

- ✅ **Cost-Effective**: Free book sharing and exchange
- ✅ **Eco-Friendly**: Promotes reuse and reduces waste
- ✅ **Community Building**: Connects like-minded readers
- ✅ **Accessible**: Easy-to-use interface for all age groups
- ✅ **Secure**: Protected with Firebase authentication
- ✅ **Scalable**: Can serve communities of any size

---

## ✨ Features

### 🔐 Authentication
- User registration and login
- Secure password management
- Session persistence
- Profile management

### 📚 Book Management
- Add new books with cover images
- Edit book details
- Delete books from collection
- View personal book library

### 🔍 Search & Discovery
- Search books by title or author
- Filter by genre (Fiction, Non-Fiction, Mystery, etc.)
- Browse all available books
- View featured books on homepage

### 🖼️ Image Handling
- Upload book cover images from gallery
- Local storage for images (free solution)
- Automatic image compression
- FileProvider for secure access

### 💬 Contact Features
- WhatsApp integration for messaging
- Email integration with pre-filled details
- Direct communication with book owners

### 📱 User Interface
- Modern Material Design 3
- Dark theme support
- Smooth animations and transitions
- Intuitive navigation

## 🛠 Technical Stack

### Language & Framework
- **Kotlin** - Modern, concise, and expressive programming language
- **Jetpack Compose** - Declarative UI toolkit for Android
- **Material Design 3** - Latest design system for modern interfaces

### Architecture
- **MVVM (Model-View-ViewModel)** - Separation of concerns
- **Clean Architecture** - Scalable and maintainable code structure
- **Repository Pattern** - Data abstraction layer

### Dependency Injection
- **Hilt** - Dependency injection framework for Android
- **Dagger 2** - Underlying DI framework

### Asynchronous Operations
- **Kotlin Coroutines** - Asynchronous programming
- **Flow** - Reactive streams
- **StateFlow** - State management

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                            │
│  ┌─────────┐  ┌──────────┐  ┌──────────┐  ┌─────────┐ │
│  │ Compose │  │ ViewModel│  │  Screen  │  │  State  │ │
│  │  Views  │  │          │  │          │  │         │ │
│  └─────────┘  └──────────┘  └──────────┘  └─────────┘ │
└─────────────────────────────────────────────────────────┘
                         ↓ ↑
┌─────────────────────────────────────────────────────────┐
│                   Domain Layer                           │
│  ┌────────────┐  ┌──────────┐  ┌───────────────────┐  │
│  │ Use Cases  │  │ Entities │  │ Business Logic    │  │
│  └────────────┘  └──────────┘  └───────────────────┘  │
└─────────────────────────────────────────────────────────┘
                         ↓ ↑
┌─────────────────────────────────────────────────────────┐
│                    Data Layer                            │
│  ┌────────────┐  ┌──────────┐  ┌───────────────────┐  │
│  │ Repository │  │ Firebase │  │ Local Storage     │  │
│  │            │  │ Firestore│  │                   │  │
│  └────────────┘  └──────────┘  └───────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### Component Responsibilities

- **UI Layer**: Displays data, handles user interactions
- **ViewModel**: Manages UI-related data, survives configuration changes
- **Repository**: Single source of truth for data
- **Use Cases**: Business logic execution
- **Data Sources**: Firebase and local storage

---

## 💾 Data Storage

### Database Choice: Firebase Firestore

#### Justification for Firebase Firestore:

1. **NoSQL Document Database**:
   - Flexible schema suitable for book sharing data
   - Easy to add new fields without migration
   - Handles nested data structures (book details, user info)

2. **Cloud-Based**:
   - No need for local database setup
   - Automatic backups and synchronization
   - Access from multiple devices

3. **Real-time Updates**:
   - Books added by users appear instantly
   - No manual refresh needed
   - Live data synchronization

4. **Free Tier**:
   - Generous free quota (50K reads/day, 20K writes/day)
   - Perfect for MVP and small communities
   - No upfront costs

5. **Authentication Integration**:
   - Built-in Firebase Auth
   - Secure user management
   - OAuth support

6. **Scalability**:
   - Handles growing user base automatically
   - Global CDN for fast access
   - Automatic scaling

### Data Structure

```json
// Users Collection
users/
  {userId}/
    name: "Asfand Ali"
    email: "asfand.azeemi@gmail.com"
    phone: "+92 300 1234567"
    location: "Karachi, Pakistan"

// Books Collection
books/
  {bookId}/
    title: "Clean Code"
    author: "Robert Martin"
    description: "A handbook of agile software craftsmanship"
    genre: "Technology"
    coverImageUrl: ""
    localCoverImagePath: "/path/to/local/image.jpg"
    ownerUid: "user123"
    isAvailable: true
    createdAt: 1234567890
```

### Why Not SQLite?

- Requires complex schema design
- Manual synchronization across devices
- More code for CRUD operations
- No built-in authentication
- Harder to scale

---

## 📊 State Management

### Technique: MVVM with StateFlow

#### Implementation:
- **ViewModels** manage UI state using `StateFlow`
- **Compose State** observes ViewModel state
- **Unidirectional data flow**

#### Justification:

1. **Official Android Pattern**:
   - Recommended by Google
   - Well-documented and supported
   - Integrates with Compose seamlessly

2. **Lifecycle Awareness**:
   - Automatically handles configuration changes
   - Survives screen rotations
   - Prevents memory leaks

3. **Testability**:
   - Easy to unit test ViewModels
   - Isolated business logic
   - Mock repositories

4. **State Flow Benefits**:
   - Reactive updates
   - Efficient state emission
   - Compose integration

### State Management Example:

```kotlin
class BookViewModel @Inject constructor(
    private val bookUseCase: BookUseCase
) : ViewModel() {
    
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun loadAllBooks() {
        finally() viewModelScope.launch {
            _isLoading.value = true
            _books.value = bookUseCase.getAllBooks()
            _isLoading.value = false
        }
    }
}
```

---

## 📱 Responsive Design

### Multi-Screen Support

The application is designed to work seamlessly across different screen sizes:

#### Phones (Portrait)
- Single column layout
- Optimized card sizes
- Touch-friendly buttons (48dp minimum)

#### Phones (Landscape)
- Adjusted spacing
- Horizontal scrolling for categories
- Efficient use of screen space

#### Tablets
- Grid layouts for book listings
- Larger content cards
- Side navigation support

### Implementation Techniques:

1. **Compose Constraints**:
   - `fillMaxSize()`, `weight()`
   - Adaptive layout based on screen width
   - `Modifier.windowSizeClass()` for responsive breakpoints

2. **Flexible Components**:
   - Cards adapt to content
   - Text wrapping and ellipsis
   - Scrollable lists

3. **Density Independence**:
   - Using `dp` (density-independent pixels)
   - Scalable vector graphics
   - Proper text sizing (`sp`)

---

## 🚀 Installation

### Prerequisites

- **Android Studio** Hedgehog | 2023.1.1 or later
- **JDK** 17 or later
- **Android SDK** API 24 (Android 7.0) or higher
- **Firebase Account** (for backend services)

### Step 1: Clone Repository

```bash
git clone https://github.com/yourusername/ShareBook.git
cd ShareBook
```

### Step 2: Setup Firebase

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use existing
3. Add Android app with package name: `myauthapp.azeemi.sharebook`
4. Download `google-services.json`
5. Place it in `app/` directory

### Step 3: Configure Firestore

1. Go to Firestore Database in Firebase Console
2. Click "Create database"
3. Start in **test mode** (for development)
4. Set location (choose closest to users)

#### Update Firestore Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /books/{bookId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && request.auth.uid == resource.data.ownerUid;
    }
    
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### Step 4: Build and Run

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Or use Android Studio
# 1. Click "Run" button
# 2. Select device/emulator
```

---

## ⚙️ Configuration

### Firebase Configuration

Ensure `google-services.json` is properly placed in `app/` directory:

```
app/
  ├── google-services.json  ← Must be here
  └── build.gradle.kts
```

### File Provider Configuration

For local image storage, verify `app/src/main/res/xml/file_paths.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <files-path name="book_images" path="book_images/" />
</paths>
```

### Permissions

All required permissions are declared in `AndroidManifest.xml`:
- Internet access
- Storage read/write (for images)
- Media images (Android 13+)

---

## 📦 APIs and Dependencies

### Core Dependencies

| Dependency | Version | Justification |
|------------|---------|---------------|
| **Compose** | Latest | Modern, declarative UI framework |
| **Hilt** | 2.48 | Dependency injection for clean architecture |
| **Firebase** | Latest | Authentication and cloud database |
| **Coil** | Latest | Image loading library (lightweight) |
| **Coroutines** | Latest | Asynchronous operations |
| **Navigation** | Latest | Type-safe navigation between screens |

### Detailed Justifications

#### 1. Jetpack Compose
- **Why**: Declarative UI, less boilerplate code, better performance
- **Alternative**: Traditional XML views
- **Decision**: Compose is the future of Android UI development

#### 2. Hilt (Dependency Injection)
- **Why**: Reduces manual dependency management, improves testability
- **Alternative**: Manual dependency injection or Koin
- **Decision**: Official Google solution, better integration

#### 3. Firebase Firestore
- **Why**: Real-time database, free tier, cloud storage
- **Alternative**: SQLite + sync solution
- **Decision**: Easier to implement and maintain

#### 4. Coil (Image Loading)
- **Why":" Lightweight, Kotlin-first, Coroutine-based
- **Alternative**: Glide or Picasso
- **Decision**: Designed for Compose, better performance

#### 5. Kotlin Coroutines
- **Why**: Asynchronous programming, suspend functions
- **Alternative**: RxJava or callbacks
- **Decision**: Official Kotlin solution, more readable

### Complete Dependency List

```gradle
dependencies {
    // Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose")
    
    // Hilt
    implementation("com.google.dagger:hilt-android")
    kapt("com.google.dagger:hilt-compiler")
    
    // Firebase
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")
    
    // Image Loading
    implementation("io.coil-kt:coil-compose")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-and削除id")
}
```

---

## 📁 Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/myauthapp/azeemi/sharebook/
│   │   │   ├── data/              # Data layer
│   │   │   │   ├── repository/
│   │   │   │   │   └── FirebaseBookRepository.kt
│   │   │   │   └── model/         # Data models
│   │   │   │       └── Book.kt
│   │   │   ├── domain/            # Business logic
│   │   │   │   ├── model/         # Domain models
│   │   │   │   ├── usecase/       # Use cases
│   │   │   │   │   ├── BookUseCase.kt
│   │   │   │   │   └── AuthUseCase.kt
│   │   │   │   └── repository/    # Repository interfaces
│   │   │   ├── ui/                # UI layer
│   │   │   │   ├── screen/        # Screens
│   │   │   │   │   ├── book/
│   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   ├── AddBookScreen.kt
│   │   │   │   │   │   ├── MyBooksScreen.kt
│   │   │   │   │   │   ├── BookDetailScreen.kt
│   │   │   │   │   │   └── SearchScreen.kt
│   │   │   │   │   ├── auth/
│   │   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   │   └── SignUpScreen.kt
│   │   │   │   │   └── profile/
│   │   │   │   │       └── ProfileScreen.kt
│   │   │   │   ├── viewmodel/     # ViewModels
│   │   │   │   │   ├── BookViewModel.kt
│   │   │   │   │   ├── AuthViewModel.kt
│   │   │   │   │   └── ProfileViewModel.kt
│   │   │   │   ├── components/    # Reusable components
│   │   │   │   │   └── BookCard.kt
│   │   │   │   └── navigation/    # Navigation
│   │   │   │       └── ShareBookNavigation.kt
│   │   │   ├── di/                # Dependency Injection
│   │   │   │   └── AppModule.kt
│   │   │   ├── utils/             # Utilities
│   │   │   │   ├── ImageUriHelper.kt
│   │   │   │   └── ImageStorageHelper.kt
│   │   │   └── ShareBookApplication.kt
│   │   ├── res/                   # Resources
│   │   │   ├── drawable/          # Icons, graphics
│   │   │   ├── mipmap/            # App icons
│   │   │   ├── xml/               # XML configurations
│   │   │   └── values/            # Colors, strings, themes
│   │   └── AndroidManifest.xml
│   └── test/                      # Unit tests
└── build.gradle.kts               # Module build config
```

### Key Files

- **Screen Files**: UI compositions for each screen
- **ViewModel**: Business logic and state management
- **Repository**: Data source abstraction
- **Use Cases**: Business operations
- **Models**: Data structures

---

## 🐛 Issues and Bugs Encountered

### Issue 1: Dropdown Menu Not Opening

**Problem**: Dropdown lists in AddBookScreen were not responding to clicks

**Root Cause**: `OutlinedTextField` with `readOnly = true` inside `ExposedDropdownMenuBox` was not receiving touch events

**Solution**:
```kotlin
OutlinedTextField(
    modifier = Modifier.clickable { expandedGenre = true },
    readOnly = true,
    // ... other properties
)
```

**Lesson**: Explicitly handle touch events for readonly TextFields

---

### Issue 2: Books Not Saving to Firestore

**Problem**: Books were created in UI but not appearing in Firestore database

**Root Causes**:
1. ID conflict: UUID generated in both UI and Repository
2. Async operation not properly awaited

**Solution**:
```kotlin
// In AddBookScreen
val book = Book(
    id = "",  // Let Firestore generate ID
    title = title,
    // ...
)

// Add delay for UI feedback
delay(1500)
```

**Lesson**: Let Firestore manage document IDs, ensure async operations complete

---

### Issue 3: Firebase Storage 404 Error

**Problem**: `StorageException (Code: -13010 HttpResult: 404)` when uploading images

**Root Cause**: Firebase Storage bucket not configured (paid service)

**Solution**: Implemented local device storage instead
```kotlin
// Save image locally
val localPath = ImageStorageHelper.saveBookImage(context, imageUri, bookId)
// Convert to FileProvider URI
val fileUri = ImageStorageHelper.getFileUri(context, localPath)
```

**Lesson**: Consider free tier limitations, provide alternative solutions

---

### Issue 4: Images Not Displaying from Local Storage

**Problem**: Locally saved images not showing in UI

**Root Cause**: FileProvider not properly configured

**Solution**:
1. Added to `file_paths.xml`:
```xml
<files-path name="book_images" path="book_images/" />
```
2. Updated AndroidManifest:
```xml
<provider
    android:authorities="${applicationId}.provider"
    android:name="androidx.core.content.FileProvider">
```

**Lesson**: FileProvider paths must match actual storage locations

---

### Issue 5: Books Not Showing in HomeScreen

**Problem**: Added books not appearing in home screen list

**Root Cause**: HomeScreen not loading books on initial composition

**Solution**:
```kotlin
LaunchedEffect(Unit) {
    bookViewModel.loadAllBooks()
}
```

**Lesson**: Explicitly load data when screen becomes visible

---

### Issue 6: Search Showing "FREE" on All Books

**Problem**: Book cards displaying "FREE" text incorrectly

**Root Cause**: Using `isFree` property which defaults to true

**Solution**: Removed price display, added availability badge instead
```kotlin
Row {
    Icon(Icons.Default.CheckCircle)
    Text("Available")
}
```

**Lesson**: Use proper data models, avoid hardcoded defaults

---

### Issue 7: MyBooks Screen Syntax Errors

**Problem**: Compilation errors due to mismatched parentheses

**Root Cause**: Copy-paste errors introducing incorrect syntax

**Solution**: Carefully reviewed and corrected all parentheses and brackets

**Lesson**: Always use code linting and IDE syntax checking

---

## ✅ Testing

### Manual Testing Checklist

- [x] User registration and login
- [x] Add new book with image
- [x] Edit book details
- [x] Delete book
- [x] Search by title and author
- [x] Filter by genre
- [x] Contact via WhatsApp
- [x] Contact via Email
- [x] Profile management
- [x] Sign out functionality
- [x] Image display from local storage
- [x] Screen rotation handling

### Test Scenarios

1. **Authentication Flow**
   - Register new user
   - Login with credentials
   - Session persistence on app restart

2. **Book Management**
   - Add book without image
   - Add book with image
   - View added books
   - Delete book
   - Edit book details

3. **Search and Discovery**
   - Search for existing books
   - Search for non-existent books
   - Filter by genre
   - Browse all books

4. **Contact Features**
   - WhatsApp button opens WhatsApp
   - Email button opens email client
   - Proper error handling for missing apps

---

## 🔮 Future Enhancements

### Planned Features

1. **Book Requests**
   - Users can request books from owners
   - Notification system
   - Request status tracking

2. **Reviews and Ratings**
   - Rate books
   - Write reviews
   - View community ratings

3. **Chat System**
   - In-app messaging
   - Real-time chat
   - Message history

4. **Geolocation**
   - Find books near me
   - Map view of book locations
   - Distance calculation

5. **Book History**
   - Track shared books
   - Borrowing history
   - Return reminders

6. **Social Features**
   - Follow other users
   - Book recommendations
   - Reading clubs

### Technical Improvements

- Unit tests for ViewModels
- UI tests for critical flows
- Performance optimization
- Offline mode support
- Image caching improvements

---

## 👥 Contributors

- **Asfand Ali Shoaib Mangi** - Developer
  - Email: asfand.azeemi@gmail.com
  - LinkedIn: [Your LinkedIn](https://linkedin.com/in/yourprofile)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📞 Support

For support, email asfand.azeemi@gmail.com or open an issue in the repository.

---

## 🙏 Acknowledgments

- Firebase for backend services
- Jetpack Compose team
- Material Design guidelines
- All contributors and testers

---

<div align="center">

**Made with ❤️ for the book sharing community**

⭐ Star this repo if you found it helpful!

</div>

