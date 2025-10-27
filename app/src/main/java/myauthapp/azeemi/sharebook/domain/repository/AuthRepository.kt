package myauthapp.azeemi.sharebook.domain.repository

import myauthapp.azeemi.sharebook.domain.model.AuthUser
import myauthapp.azeemi.sharebook.domain.model.User

/**
 * Domain repository interface for authentication operations
 * This defines the contract for authentication in the domain layer
 */
interface AuthRepository {
    val currentUser: AuthUser?
    val isLoggedIn: Boolean
    
    suspend fun signIn(email: String, password: String): Result<AuthUser>
    suspend fun signUp(email: String, password: String, userData: User): Result<AuthUser>
    suspend fun signInWithGoogle(idToken: String): Result<AuthUser>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUserData(): Result<User>
    suspend fun updateUserData(user: User): Result<Unit>
    suspend fun updateUserProfile(user: User, profileImageUri: android.net.Uri? = null): Result<Unit>
}
