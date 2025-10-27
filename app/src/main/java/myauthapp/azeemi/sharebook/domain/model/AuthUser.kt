package myauthapp.azeemi.sharebook.domain.model

/**
 * Domain model for authenticated user
 * This represents the authenticated user in the domain layer
 */
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean = false
)
