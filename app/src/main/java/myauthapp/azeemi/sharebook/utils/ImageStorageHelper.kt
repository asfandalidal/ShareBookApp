package myauthapp.azeemi.sharebook.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageStorageHelper {
    
    private const val BOOK_IMAGES_DIR = "book_images"
    private const val MAX_IMAGE_WIDTH = 800
    private const val MAX_IMAGE_HEIGHT = 800
    
    /**
     * Save image to local storage and return the file path
     */
    suspend fun saveBookImage(context: Context, imageUri: Uri, bookId: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Create directory if it doesn't exist
                val imagesDir = File(context.filesDir, BOOK_IMAGES_DIR)
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs()
                }
                
                // Create unique filename
                val filename = "book_cover_$bookId.jpg"
                val outputFile = File(imagesDir, filename)
                
                // Read and compress image
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                
                if (bitmap == null) {
                    Log.e("ImageStorageHelper", "Failed to decode bitmap")
                    return@withContext null
                }
                
                // Resize if needed
                val resizedBitmap = resizeBitmap(bitmap)
                
                // Save to file
                val outputStream = FileOutputStream(outputFile)
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                outputStream.flush()
                outputStream.close()
                
                // Return the file path
                outputFile.absolutePath
                
            } catch (e: Exception) {
                Log.e("ImageStorageHelper", "Error saving image: ${e.message}", e)
                null
            }
        }
    }
    
    /**
     * Load image from local path
     */
    suspend fun loadImage(context: Context, imagePath: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(imagePath)
                if (file.exists()) {
                    BitmapFactory.decodeFile(file.absolutePath)
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("ImageStorageHelper", "Error loading image: ${e.message}", e)
                null
            }
        }
    }
    
    /**
     * Check if image file exists
     */
    fun imageExists(context: Context, imagePath: String): Boolean {
        return try {
            File(imagePath).exists()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Delete image from local storage
     */
    suspend fun deleteImage(context: Context, imagePath: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(imagePath)
                if (file.exists()) {
                    file.delete()
                } else {
                    true // File doesn't exist, consider it deleted
                }
            } catch (e: Exception) {
                Log.e("ImageStorageHelper", "Error deleting image: ${e.message}", e)
                false
            }
        }
    }
    
    /**
     * Get file URI from local path
     */
    fun getFileUri(context: Context, imagePath: String): Uri? {
        return try {
            val file = File(imagePath)
            if (file.exists()) {
                // Use FileProvider for secure file access
                val provider = "myauthapp.azeemi.sharebook.provider"
                androidx.core.content.FileProvider.getUriForFile(context, provider, file)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ImageStorageHelper", "Error getting file URI: ${e.message}", e)
            null
        }
    }
    
    /**
     * Resize bitmap if it's too large
     */
    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        
        // Check if resizing is needed
        if (width <= MAX_IMAGE_WIDTH && height <= MAX_IMAGE_HEIGHT) {
            return bitmap
        }
        
        // Calculate new dimensions maintaining aspect ratio
        val ratio = width.toFloat() / height.toFloat()
        
        if (width > height) {
            width = MAX_IMAGE_WIDTH
            height = (MAX_IMAGE_WIDTH / ratio).toInt()
        } else {
            height = MAX_IMAGE_HEIGHT
            width = (MAX_IMAGE_HEIGHT * ratio).toInt()
        }
        
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}

