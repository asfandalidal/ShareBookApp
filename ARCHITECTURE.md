# ShareBook - Clean Architecture Project Structure

## Overview
This project has been refactored to follow Clean Architecture principles, removing all Firebase dependencies and preparing for Supabase integration.

## Architecture Layers

### 1. Domain Layer (`domain/`)
- **Models**: Core business entities (`User`, `Book`, `AuthUser`)
- **Repositories**: Interfaces defining data contracts (`AuthRepository`, `BookRepository`)
- **Use Cases**: Business logic and validation (`AuthUseCase`, `BookUseCase`)

### 2. Data Layer (`data/`)
- **Repository Implementations**: Concrete implementations of domain interfaces
  - `AuthRepositoryImpl`: Handles authentication operations
  - `BookRepositoryImpl`: Handles book management operations
- **Local Storage**: `LocalStorageRepository` for local file management

### 3. Presentation Layer (`ui/`)
- **ViewModels**: UI state management using domain use cases
  - `AuthViewModel`: Authentication UI logic
  - `ProfileViewModel`: User profile management
- **Compose UI**: Jetpack Compose screens and components

### 4. Dependency Injection (`di/`)
- **AppModule**: Hilt module providing all dependencies
- **Clean separation**: Domain interfaces injected into use cases, implementations injected into repositories

## Key Benefits

### 1. **Separation of Concerns**
- Domain layer is independent of external frameworks
- Business logic is centralized in use cases
- Data layer can be easily swapped (Firebase → Supabase)

### 2. **Testability**
- Domain layer can be unit tested without Android dependencies
- Use cases contain business logic validation
- Repository interfaces enable easy mocking

### 3. **Maintainability**
- Clear boundaries between layers
- Single responsibility principle
- Easy to extend and modify

### 4. **Production Ready**
- Proper error handling with Result types
- Input validation in use cases
- Logging for debugging
- Clean dependency injection

## Current Implementation Status

### ✅ Completed
- Firebase dependencies removed
- Clean architecture implemented
- Domain models created
- Repository interfaces defined
- Use cases with business logic
- Mock implementations for testing
- Dependency injection configured
- ViewModels updated to use domain layer

### 🔄 Ready for Supabase Integration
- Repository implementations can be easily replaced
- Domain layer remains unchanged
- Use cases handle all business logic
- Local storage for images already implemented

## Next Steps for Supabase Integration

1. **Add Supabase Dependencies**
   ```kotlin
   implementation("io.github.jan-tennert.supabase:postgrest-kt:2.1.3")
   implementation("io.github.jan-tennert.supabase:realtime-kt:2.1.3")
   implementation("io.github.jan-tennert.supabase:gotrue-kt:2.1.3")
   implementation("io.github.jan-tennert.supabase:storage-kt:2.1.3")
   ```

2. **Create Supabase Repository Implementations**
   - `SupabaseAuthRepositoryImpl` implementing `AuthRepository`
   - `SupabaseBookRepositoryImpl` implementing `BookRepository`

3. **Update DI Module**
   - Replace mock implementations with Supabase implementations
   - Add Supabase client configuration

4. **Database Schema**
   - Use provided `supabase_schema.sql` for database setup
   - Follow the schema for User and Book tables

## File Structure
```
app/src/main/java/myauthapp/azeemi/sharebook/
├── domain/
│   ├── model/
│   │   ├── User.kt
│   │   ├── Book.kt
│   │   └── AuthUser.kt
│   ├── repository/
│   │   ├── AuthRepository.kt
│   │   └── BookRepository.kt
│   └── usecase/
│       ├── AuthUseCase.kt
│       └── BookUseCase.kt
├── data/
│   └── repository/
│       ├── AuthRepositoryImpl.kt
│       ├── BookRepositoryImpl.kt
│       └── LocalStorageRepository.kt
├── ui/
│   └── viewmodel/
│       ├── AuthViewModel.kt
│       └── ProfileViewModel.kt
└── di/
    └── AppModule.kt
```

This architecture ensures the app is production-ready, maintainable, and easily extensible for future requirements.
