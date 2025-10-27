package myauthapp.azeemi.sharebook.domain.repository

import myauthapp.azeemi.sharebook.domain.model.Book

/**
 * Domain repository interface for book operations
 * This defines the contract for book management in the domain layer
 */
interface BookRepository {
    suspend fun getAllBooks(): Result<List<Book>>
    suspend fun getBooksByUser(userId: String): Result<List<Book>>
    suspend fun getBooksByGenre(genre: String): Result<List<Book>>
    suspend fun searchBooks(query: String): Result<List<Book>>
    suspend fun getBookById(bookId: String): Result<Book>
    suspend fun getUserBooks(userId: String): Result<List<Book>>
    suspend fun addBook(book: Book, imageUri: android.net.Uri? = null): Result<String>
    suspend fun addBook(book: Book): Result<String>
    suspend fun updateBook(bookId: String, book: Book, newImageUri: android.net.Uri? = null): Result<Unit>
    suspend fun updateBook(bookId: String, book: Book): Result<Unit>
    suspend fun deleteBook(bookId: String): Result<Unit>
    suspend fun markBookAsUnavailable(bookId: String): Result<Unit>
    suspend fun getBookWithImages(bookId: String): Result<Book>
}
