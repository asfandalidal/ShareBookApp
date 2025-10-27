package myauthapp.azeemi.sharebook.domain.usecase

import myauthapp.azeemi.sharebook.domain.model.Book
import myauthapp.azeemi.sharebook.domain.repository.BookRepository
import javax.inject.Inject

/**
 * Use case for book operations
 * This encapsulates the business logic for book management
 */
class BookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    
    suspend fun getAllBooks(): Result<List<Book>> {
        return bookRepository.getAllBooks()
    }
    
    suspend fun getBooksByUser(userId: String): Result<List<Book>> {
        if (userId.isBlank()) {
            return Result.failure(Exception("User ID cannot be empty"))
        }
        return bookRepository.getBooksByUser(userId)
    }
    
    suspend fun getBooksByGenre(genre: String): Result<List<Book>> {
        if (genre.isBlank()) {
            return Result.failure(Exception("Genre cannot be empty"))
        }
        return bookRepository.getBooksByGenre(genre)
    }
    
    suspend fun searchBooks(query: String): Result<List<Book>> {
        if (query.isBlank()) {
            return Result.failure(Exception("Search query cannot be empty"))
        }
        
        if (query.length < 2) {
            return Result.failure(Exception("Search query must be at least 2 characters"))
        }
        
        return bookRepository.searchBooks(query)
    }
    
    suspend fun getBookById(bookId: String): Result<Book> {
        if (bookId.isBlank()) {
            return Result.failure(Exception("Book ID cannot be empty"))
        }
        return bookRepository.getBookById(bookId)
    }
    
    suspend fun getUserBooks(userId: String): Result<List<Book>> {
        return getBooksByUser(userId)
    }
    
    suspend fun addBook(book: Book, imageUri: android.net.Uri? = null): Result<String> {
        // Add business logic validation
        if (!book.isValid) {
            return Result.failure(Exception("Book data is invalid"))
        }
        
        return bookRepository.addBook(book, imageUri)
    }
    
    suspend fun addBook(book: Book): Result<String> {
        return addBook(book, null)
    }
    
    suspend fun updateBook(bookId: String, book: Book, newImageUri: android.net.Uri? = null): Result<Unit> {
        if (bookId.isBlank()) {
            return Result.failure(Exception("Book ID cannot be empty"))
        }
        
        if (!book.isValid) {
            return Result.failure(Exception("Book data is invalid"))
        }
        
        return bookRepository.updateBook(bookId, book, newImageUri)
    }
    
    suspend fun updateBook(bookId: String, book: Book): Result<Unit> {
        return updateBook(bookId, book, null)
    }
    
    suspend fun deleteBook(bookId: String): Result<Unit> {
        if (bookId.isBlank()) {
            return Result.failure(Exception("Book ID cannot be empty"))
        }
        return bookRepository.deleteBook(bookId)
    }
    
    suspend fun markBookAsUnavailable(bookId: String): Result<Unit> {
        if (bookId.isBlank()) {
            return Result.failure(Exception("Book ID cannot be empty"))
        }
        return bookRepository.markBookAsUnavailable(bookId)
    }
    
    suspend fun getBookWithImages(bookId: String): Result<Book> {
        return bookRepository.getBookWithImages(bookId)
    }
}
