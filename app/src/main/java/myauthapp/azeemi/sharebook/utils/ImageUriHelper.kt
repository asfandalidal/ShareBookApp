package myauthapp.azeemi.sharebook.utils

import android.content.Context
import android.net.Uri
import java.io.File

object ImageUriHelper {
    /**
     * Get the proper URI for displaying an image
     * Handles both content:// URIs and placeholder URLs
     */
    fun getDisplayUri(context: Context, coverImageUrl: String, localCoverImagePath: String): String {
        return when {
            // If we have a local path, convert to FileProvider URI
            localCoverImagePath.isNotEmpty() && localCoverImagePath.startsWith("/") -> {
                try {
                    val file = File(localCoverImagePath)
                    if (file.exists()) {
                        // Return the content URI that was already generated
                        coverImageUrl.ifEmpty { localCoverImagePath }
                    } else {
                        // File doesn't exist, use placeholder
                        "https://via.placeholder.com/300x400/9E9E9E/FFFFFF?text=Book"
                    }
                } catch (e: Exception) {
                    coverImageUrl.ifEmpty { "https://via.placeholder.com/300x400/9E9E9E/FFFFFF?text=Book" }
                }
            }
            // If coverImageUrl starts with content://, use it directly
            coverImageUrl.startsWith("content://") -> coverImageUrl
            // If coverImageUrl is a valid URL, use it
            coverImageUrl.startsWith("http://") || coverImageUrl.startsWith("https://") -> coverImageUrl
            // Default placeholder
            else -> "https://via.placeholder.com/300x400/9E9E9E/FFFFFF?text=Book"
        }
    }
}

