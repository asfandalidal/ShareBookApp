package myauthapp.azeemi.sharebook.domain.model

/**
 * Domain model for User - represents the core business entity
 * This is independent of any external frameworks or data sources
 */
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val bio: String? = null,
    val profileImageUrl: String = "",
    val localProfileImagePath: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val displayName: String
        get() = name.ifBlank { email.substringBefore("@") }

    val initials: String
        get() = displayName.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()
}
