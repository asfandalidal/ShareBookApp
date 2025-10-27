package myauthapp.azeemi.sharebook.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import myauthapp.azeemi.sharebook.domain.model.Book
import myauthapp.azeemi.sharebook.domain.usecase.BookUseCase
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookUseCase: BookUseCase
) : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _userBooks = MutableStateFlow<List<Book>>(emptyList())
    val userBooks: StateFlow<List<Book>> = _userBooks.asStateFlow()

    private val _filteredBooks = MutableStateFlow<List<Book>>(emptyList())
    val filteredBooks: StateFlow<List<Book>> = _filteredBooks.asStateFlow()

    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _selectedGenre.asStateFlow()

    init {
        loadAllBooks()
    }

    /** ---------------- LOAD FUNCTIONS ---------------- **/

    // Load all books (for general browsing)
    fun loadAllBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            bookUseCase.getAllBooks()
                .onSuccess { books ->
                    _books.value = books
                    _filteredBooks.value = books
                }
                .onFailure { e ->
                    _errorMessage.value = e.message ?: "Failed to load books"
                }
            _isLoading.value = false
        }
    }

    // Load only current user's books
    fun loadMyBooks(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            bookUseCase.getBooksByUser(userId)
                .onSuccess { books ->
                    _books.value = books
                    _filteredBooks.value = books
                }
                .onFailure { e ->
                    _errorMessage.value = e.message ?: "Failed to load your books"
                }
            _isLoading.value = false
        }
    }

    fun loadBooksByGenre(genre: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _selectedGenre.value = genre

            bookUseCase.getBooksByGenre(genre)
                .onSuccess { books ->
                    _filteredBooks.value = books
                }
                .onFailure { e ->
                    _errorMessage.value = e.message ?: "Failed to load books"
                }

            _isLoading.value = false
        }
    }

    fun getBookById(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            bookUseCase.getBookById(bookId)
                .onSuccess { book ->
                    _selectedBook.value = book
                }
                .onFailure { e ->
                    _errorMessage.value = e.message ?: "Failed to load book details"
                }

            _isLoading.value = false
        }
    }

    fun loadUserBooks(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d("BookViewModel", "Loading books for user: $userId")
                val result = bookUseCase.getUserBooks(userId)
                if (result.isSuccess) {
                    val books = result.getOrThrow()
                    Log.d("BookViewModel", "Loaded ${books.size} books for user")
                    _userBooks.value = books
                    updateFilteredBooks()
                } else {
                    Log.e("BookViewModel", "Error loading user books: ${result.exceptionOrNull()?.message}")
                    _errorMessage.value = "Failed to load books: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error loading user books", e)
                _errorMessage.value = "Failed to load books: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** ---------------- CRUD OPERATIONS ---------------- **/

    fun addBook(book: Book) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d("BookViewModel", "Adding book: $book")
                val result = bookUseCase.addBook(book)
                if (result.isSuccess) {
                    val bookId = result.getOrThrow()
                    Log.d("BookViewModel", "Book added successfully with ID: $bookId")
                    // Reload all books to show in HomeScreen
                    loadAllBooks()
                    // Reload user books after successful addition
                    loadUserBooks(book.ownerUid)
                } else {
                    Log.e("BookViewModel", "Error adding book: ${result.exceptionOrNull()?.message}")
                    _errorMessage.value = "Failed to add book: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error adding book", e)
                _errorMessage.value = "Failed to add book: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addBook(book: Book, imageUri: android.net.Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d("BookViewModel", "Adding book with image: $book")
                val result = bookUseCase.addBook(book, imageUri)
                if (result.isSuccess) {
                    val bookId = result.getOrThrow()
                    Log.d("BookViewModel", "Book added successfully with ID: $bookId")
                    // Reload all books to show in HomeScreen
                    loadAllBooks()
                    // Reload user books after successful addition
                    loadUserBooks(book.ownerUid)
                } else {
                    Log.e("BookViewModel", "Error adding book: ${result.exceptionOrNull()?.message}")
                    _errorMessage.value = "Failed to add book: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error adding book", e)
                _errorMessage.value = "Failed to add book: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun updateBook(bookId: String, updatedBook: Book) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            bookUseCase.updateBook(bookId, updatedBook)
                .onSuccess {
                    // ✅ Replace the updated book in the lists
                    _books.value = _books.value.map { if (it.id == bookId) updatedBook else it }
                    _userBooks.value = _userBooks.value.map { if (it.id == bookId) updatedBook else it }
                    _filteredBooks.value = _filteredBooks.value.map { if (it.id == bookId) updatedBook else it }

                    // Update selected book if it's the one being updated
                    if (_selectedBook.value?.id == bookId) {
                        _selectedBook.value = updatedBook
                    }
                }
                .onFailure { e ->
                    _errorMessage.value = e.message ?: "Failed to update book"
                }

            _isLoading.value = false
        }
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            bookUseCase.deleteBook(bookId)
                .onSuccess {
                    // ✅ Remove from all local caches
                    _books.value = _books.value.filterNot { it.id == bookId }
                    _userBooks.value = _userBooks.value.filterNot { it.id == bookId }
                    _filteredBooks.value = _filteredBooks.value.filterNot { it.id == bookId }

                    // Clear selected book if it's the one being deleted
                    if (_selectedBook.value?.id == bookId) {
                        _selectedBook.value = null
                    }
                }
                .onFailure { e ->
                    _errorMessage.value = e.message ?: "Failed to delete book"
                }

            _isLoading.value = false
        }
    }

    fun markBookAsUnavailable(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            bookUseCase.markBookAsUnavailable(bookId)
                .onSuccess {
                    _books.value = _books.value.map {
                        if (it.id == bookId) it.copy(isAvailable = false) else it
                    }
                    _userBooks.value = _userBooks.value.map {
                        if (it.id == bookId) it.copy(isAvailable = false) else it
                    }
                    _filteredBooks.value = _filteredBooks.value.map {
                        if (it.id == bookId) it.copy(isAvailable = false) else it
                    }

                    // Update selected book if it's the one being modified
                    if (_selectedBook.value?.id == bookId) {
                        _selectedBook.value = _selectedBook.value?.copy(isAvailable = false)
                    }
                }
                .onFailure { e ->
                    _errorMessage.value = e.message ?: "Failed to mark unavailable"
                }

            _isLoading.value = false
        }
    }

    /** ---------------- SEARCH & FILTER ---------------- **/

    // Filter books by search query (client-side filtering)
    fun filterBooks(query: String) {
        _searchQuery.value = query
        _filteredBooks.value = if (query.isBlank()) {
            _books.value
        } else {
            _books.value.filter { book ->
                book.title.contains(query, ignoreCase = true) ||
                        book.author.contains(query, ignoreCase = true) ||
                        book.genre.contains(query, ignoreCase = true)
            }
        }
    }

    // Server-side search
    fun searchBooks(query: String) {
        _searchQuery.value = query

        if (query.isBlank()) {
            _filteredBooks.value = _books.value
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            bookUseCase.searchBooks(query)
                .onSuccess { books ->
                    _filteredBooks.value = books
                }
                .onFailure { e ->
                    _errorMessage.value = e.message ?: "Search failed"
                }

            _isLoading.value = false
        }
    }

    // Add this method to filter user books
    private fun updateFilteredBooks() {
        _filteredBooks.value = _userBooks.value
    }

    // Clear filters and show all books
    fun clearFilters() {
        _filteredBooks.value = _books.value
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _selectedGenre.value = null
        _filteredBooks.value = _books.value
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSelectedBook() {
        _selectedBook.value = null
    }

    // Add this to clear user books when needed
    fun clearUserBooks() {
        _userBooks.value = emptyList()
    }
}