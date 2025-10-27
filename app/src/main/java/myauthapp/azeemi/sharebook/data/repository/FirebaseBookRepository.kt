package myauthapp.azeemi.sharebook.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import myauthapp.azeemi.sharebook.domain.model.Book
import myauthapp.azeemi.sharebook.domain.repository.BookRepository
import myauthapp.azeemi.sharebook.utils.ImageStorageHelper
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseBookRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val context: Context
) : BookRepository {
    
    override suspend fun getAllBooks(): Result<List<Book>> {
        return try {
            val snapshot = firestore.collection("books").get().await()
            val books = snapshot.documents.mapNotNull { it.toBook() }
            Result.success(books)
        } catch (e: Exception) {
            Log.e("FirebaseBook", "Get all books failed", e)
            Result.failure(e)
        }
    }
    
    override suspend fun getBooksByUser(userId: String): Result<List<Book>> {
        return try {
            val snapshot = firestore.collection("books")
                .whereEqualTo("ownerUid", userId)
                .get()
                .await()
            val books = snapshot.documents.mapNotNull { it.toBook() }
            Result.success(books)
        } catch (e: Exception) {
            Log.e("FirebaseBook", "Get books by user failed", e)
            Result.failure(e)
        }
    }
    
    override suspend fun getBooksByGenre(genre: String): Result<List<Book>> {
        return try {
            val snapshot = firestore.collection("books")
                .whereEqualTo("genre", genre)
                .get()
                .await()
            val books = snapshot.documents.mapNotNull { it.toBook() }
            Result.success(books)
        } catch (e: Exception) {
            Log.e("FirebaseBook", "Get books by genre failed", e)
            Result.failure(e)
        }
    }
    
    override suspend fun searchBooks(query: String): Result<List<Book>> {
        return try {
            val snapshot = firestore.collection("books")
                .whereGreaterThanOrEqualTo("title", query)
                .whereLessThanOrEqualTo("title", query + "\uf8ff")
                .get()
                .await()
            val books = snapshot.documents.mapNotNull { it.toBook() }
            Result.success(books)
        } catch (e: Exception) {
            Log.e("FirebaseBook", "Search books failed", e)
            Result.failure(e)
        }
    }
    
    override suspend fun getBookById(bookId: String): Result<Book> {
        return try {
            val snapshot = firestore.collection("books").document(bookId).get().await()
            val book = snapshot.toBook()
            if (book != null) {
                Result.success(book)
            } else {
                Result.failure(Exception("Book not found"))
            }
        } catch (e: Exception) {
            Log.e("FirebaseBook", "Get book by id failed", e)
            Result.failure(e)
        }
    }
    
    override suspend fun getUserBooks(userId: String): Result<List<Book>> {
        return getBooksByUser(userId)
    }
    
    override suspend fun addBook(book: Book): Result<String> {
        return addBook(book, null)
    }
    
    override suspend fun addBook(book: Book, imageUri: Uri?): Result<String> {
        return try {
            val bookId = UUID.randomUUID().toString()
            Log.d("FirebaseBook", "Starting to add book with ID: $bookId")
            
            // Save image to local storage if provided
            var localImagePath = ""
            var coverImageUrl = ""
            
            if (imageUri != null) {
                Log.d("FirebaseBook", "Saving image to local storage...")
                val savedPath = ImageStorageHelper.saveBookImage(context, imageUri, bookId)
                if (savedPath != null) {
                    localImagePath = savedPath
                    // Get file URI for display
                    val fileUri = ImageStorageHelper.getFileUri(context, savedPath)
                    coverImageUrl = fileUri?.toString() ?: ""
                    Log.d("FirebaseBook", "Image saved to: $localImagePath")
                }
            }
            
            // Use placeholder if no image was saved
            if (coverImageUrl.isEmpty()) {
                coverImageUrl = if (book.title.isNotEmpty()) {
                    "https://via.placeholder.com/300x400/4CAF50/FFFFFF?text=${book.title.take(10)}"
                } else {
                    "https://via.placeholder.com/300x400/9E9E9E/FFFFFF?text=Book"
                }
                Log.d("FirebaseBook", "Using placeholder image: $coverImageUrl")
            }
            
            val bookWithId = book.copy(
                id = bookId,
                coverImageUrl = coverImageUrl,
                localCoverImagePath = localImagePath,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            Log.d("FirebaseBook", "Saving book to Firestore: $bookWithId")
            firestore.collection("books")
                .document(bookId)
                .set(bookWithId.toFirestoreMap())
                .await()
            
            Log.d("FirebaseBook", "Book added successfully with ID: $bookId")
            Result.success(bookId)
        } catch (e: Exception) {
            Log.e("FirebaseBook", "Add book failed: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    override suspend fun updateBook(bookId: String, book: Book): Result<Unit> {
        return updateBook(bookId, book, null)
    }
    
    override suspend fun updateBook(bookId: String, book: Book, newImageUri: Uri?): Result<Unit> {
        return try {
            val coverImageUrl = if (newImageUri != null) {
                uploadBookCover(bookId, newImageUri)
            } else {
                book.coverImageUrl
            }
            
            val updatedBook = book.copy(
                id = bookId,
                coverImageUrl = coverImageUrl,
                updatedAt = System.currentTimeMillis()
            )
            
            // Update only specific fields
            val updateData: Map<String, Any> = mapOf(
                "title" to updatedBook.title,
                "author" to updatedBook.author,
                "description" to updatedBook.description,
                "isbn" to updatedBook.isbn,
                "genre" to updatedBook.genre,
                "coverImageUrl" to updatedBook.coverImageUrl,
                "isAvailable" to updatedBook.isAvailable,
                "updatedAt" to updatedBook.updatedAt
            )
            firestore.collection("books")
                .document(bookId)
                .update(updateData)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseBook", "Update book failed", e)
            Result.failure(e)
        }
    }
    
    override suspend fun deleteBook(bookId: String): Result<Unit> {
        return try {
            firestore.collection("books").document(bookId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseBook", "Delete book failed", e)
            Result.failure(e)
        }
    }
    
    override suspend fun markBookAsUnavailable(bookId: String): Result<Unit> {
        return try {
            firestore.collection("books").document(bookId)
                .update("isAvailable", false, "updatedAt", System.currentTimeMillis())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseBook", "Mark book as unavailable failed", e)
            Result.failure(e)
        }
    }
    
    override suspend fun getBookWithImages(bookId: String): Result<Book> {
        return getBookById(bookId)
    }
    
    private suspend fun uploadBookCover(bookId: String, imageUri: Uri): String {
        return try {
            val fileName = "book_cover_${bookId}.jpg"
            val storageRef = storage.reference.child("book_covers/$fileName")
            
            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("FirebaseBook", "Upload book cover failed: ${e.message}", e)
            // Return empty string if upload fails (book will save without image)
            Log.w("FirebaseBook", "Continuing without image - book will be saved without cover")
            ""
        }
    }
}

// Extension function to convert Book to Firestore Map
fun Book.toFirestoreMap(): HashMap<String, Any?> {
    return hashMapOf(
        "id" to id,
        "title" to title,
        "author" to author,
        "description" to description,
        "isbn" to isbn,
        "genre" to genre,
        "coverImageUrl" to coverImageUrl,
        "localCoverImagePath" to localCoverImagePath,
        "ownerUid" to ownerUid,
        "isAvailable" to isAvailable,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

// Extension function to convert Firestore document to Book
fun DocumentSnapshot.toBook(): Book? {
    return try {
        Book(
            id = getString("id") ?: "",
            title = getString("title") ?: "",
            author = getString("author") ?: "",
            description = getString("description") ?: "",
            isbn = getString("isbn") ?: "",
            genre = getString("genre") ?: "",
            coverImageUrl = getString("coverImageUrl") ?: "",
            localCoverImagePath = getString("localCoverImagePath") ?: "",
            ownerUid = getString("ownerUid") ?: "",
            isAvailable = getBoolean("isAvailable") ?: true,
            createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) {
        Log.e("FirebaseBook", "Failed to convert document to book", e)
        null
    }
}

