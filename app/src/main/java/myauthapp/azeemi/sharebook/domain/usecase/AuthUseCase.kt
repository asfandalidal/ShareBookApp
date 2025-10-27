package myauthapp.azeemi.sharebook.domain.usecase

import myauthapp.azeemi.sharebook.domain.model.AuthUser
import myauthapp.azeemi.sharebook.domain.model.User
import myauthapp.azeemi.sharebook.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for user authentication operations
 * This encapsulates the business logic for authentication
 */
class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    suspend fun signIn(email: String, password: String): Result<AuthUser> {
        // Add any business logic validation here
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email and password cannot be empty"))
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid email format"))
        }
        
        return authRepository.signIn(email, password)
    }
    
    suspend fun signUp(email: String, password: String, userData: User): Result<AuthUser> {
        // Add any business logic validation here
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email and password cannot be empty"))
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid email format"))
        }
        
        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }
        
        if (userData.name.isBlank()) {
            return Result.failure(Exception("Name cannot be empty"))
        }
        
        return authRepository.signUp(email, password, userData)
    }
    
    suspend fun signInWithGoogle(idToken: String): Result<AuthUser> {
        if (idToken.isBlank()) {
            return Result.failure(Exception("ID token cannot be empty"))
        }
        
        return authRepository.signInWithGoogle(idToken)
    }

    suspend fun signOut(): Result<Unit> {
        return authRepository.signOut()
    }
    
    suspend fun getCurrentUserData(): Result<User> {
        return authRepository.getCurrentUserData()
    }
    
    suspend fun updateUserData(user: User): Result<Unit> {
        // Add any business logic validation here
        if (user.name.isBlank()) {
            return Result.failure(Exception("Name cannot be empty"))
        }
        
        return authRepository.updateUserData(user)
    }
    
    suspend fun updateUserProfile(user: User, profileImageUri: android.net.Uri? = null): Result<Unit> {
        // Add any business logic validation here
        if (user.name.isBlank()) {
            return Result.failure(Exception("Name cannot be empty"))
        }
        
        return authRepository.updateUserProfile(user, profileImageUri)
    }
    
    val currentUser: AuthUser? get() = authRepository.currentUser
    val isLoggedIn: Boolean get() = authRepository.isLoggedIn
}
