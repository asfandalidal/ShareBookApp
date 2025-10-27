package myauthapp.azeemi.sharebook.domain.model

/**
 * Domain model for Book - matches Supabase database schema
 * This is independent of any external frameworks or data sources
 */
data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val description: String = "",
    val isbn: String = "",
    val genre: String = "",
    val coverImageUrl: String = "",
    val localCoverImagePath: String = "",
    val ownerUid: String = "",
    val isAvailable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {

    val displayImages: List<String>
        get() = if (localCoverImagePath.isNotEmpty()) {
            listOf(localCoverImagePath)
        } else if (coverImageUrl.isNotEmpty()) {
            listOf(coverImageUrl)
        } else {
            emptyList()
        }

    val firstImage: String
        get() = displayImages.firstOrNull() ?: "https://cdn-icons-png.flaticon.com/512/29/29302.png"

    val shortDescription: String
        get() = if (description.length > 100) {
            description.take(97) + "..."
        } else {
            description
        }

    // Backward-compatible properties for UI components
    val imageUrls: List<String>
        get() = if (coverImageUrl.isNotEmpty()) listOf(coverImageUrl) else emptyList()
    
    val category: String
        get() = genre
    
    val condition: String
        get() = "Good" // Default condition
    
    val price: Double
        get() = 0.0 // Default price
    
    val isFree: Boolean
        get() = true // Default to free
    
    val ownerId: String
        get() = ownerUid
    
    // Placeholder owner details (will be fetched separately in real implementation)
    val ownerName: String
        get() = "Owner" // Placeholder
    
    val ownerEmail: String
        get() = "owner@example.com" // Placeholder
    
    val ownerPhone: String
        get() = "" // Placeholder
    
    val ownerLocation: String
        get() = "" // Placeholder
    
    val contactInfo: String
        get() = "" // Placeholder

    // Validation methods
    val isValid: Boolean
        get() = title.isNotBlank() && author.isNotBlank() && ownerUid.isNotBlank()
}
