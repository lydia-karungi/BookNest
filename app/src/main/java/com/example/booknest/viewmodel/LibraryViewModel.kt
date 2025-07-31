package com.example.booknest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknest.data.entity.Book
import com.example.booknest.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            repository.getAllBooks().collect {
                _books.value = it
            }
        }
    }

    fun addBook(book: Book) {
        viewModelScope.launch {
            repository.insertBook(book)
        }
    }

    fun updateBook(book: Book) {
        viewModelScope.launch {
            repository.updateBook(book)
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }

    // NEW: Reading tracking methods

    fun updateBookProgress(bookId: String, newProgress: Float) {
        viewModelScope.launch {
            try {
                val currentBook = _books.value.find { it.id == bookId }
                currentBook?.let { book ->
                    val updatedBook = book.copy(
                        progress = newProgress.coerceIn(0f, 1f),
                        lastReadTime = System.currentTimeMillis(),
                        status = when {
                            newProgress >= 1.0f -> "Finished"
                            newProgress > 0f -> "Reading"
                            else -> book.status
                        },
                        dateFinished = if (newProgress >= 1.0f) System.currentTimeMillis() else book.dateFinished
                    )

                    repository.updateBook(updatedBook)
                }
            } catch (e: Exception) {
                // Handle error - you can add error state if needed
                e.printStackTrace()
            }
        }
    }

    fun updateBookProgressByPages(bookId: String, currentPage: Int, totalPages: Int) {
        viewModelScope.launch {
            try {
                val currentBook = _books.value.find { it.id == bookId }
                currentBook?.let { book ->
                    val newProgress = if (totalPages > 0) {
                        (currentPage.toFloat() / totalPages).coerceIn(0f, 1f)
                    } else {
                        book.progress
                    }

                    val updatedBook = book.copy(
                        progress = newProgress,
                        currentPage = currentPage.coerceAtLeast(0),
                        pageCount = if (totalPages > 0) totalPages else book.pageCount,
                        lastReadTime = System.currentTimeMillis(),
                        status = when {
                            newProgress >= 1.0f -> "Finished"
                            newProgress > 0f || currentPage > 0 -> "Reading"
                            else -> book.status
                        },
                        dateFinished = if (newProgress >= 1.0f) System.currentTimeMillis() else book.dateFinished
                    )

                    repository.updateBook(updatedBook)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateBookStatus(bookId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                val currentBook = _books.value.find { it.id == bookId }
                currentBook?.let { book ->
                    val updatedBook = book.copy(
                        status = newStatus,
                        lastReadTime = System.currentTimeMillis(),
                        dateFinished = when (newStatus) {
                            "Finished" -> System.currentTimeMillis()
                            else -> if (newStatus != "Finished") null else book.dateFinished
                        },
                        progress = when (newStatus) {
                            "Finished" -> 1.0f
                            "Wishlist" -> 0f
                            else -> book.progress
                        },
                        isCurrentlyReading = newStatus == "Reading"
                    )

                    repository.updateBook(updatedBook)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startReadingSession(bookId: String) {
        viewModelScope.launch {
            try {
                val currentBook = _books.value.find { it.id == bookId }
                currentBook?.let { book ->
                    val updatedBook = book.copy(
                        isCurrentlyReading = true,
                        lastReadTime = System.currentTimeMillis(),
                        status = if (book.status == "Wishlist") "Reading" else book.status,
                        readingSessions = book.readingSessions + 1
                    )

                    repository.updateBook(updatedBook)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun endReadingSession(bookId: String, sessionDurationMinutes: Int = 0) {
        viewModelScope.launch {
            try {
                val currentBook = _books.value.find { it.id == bookId }
                currentBook?.let { book ->
                    val updatedBook = book.copy(
                        isCurrentlyReading = false,
                        lastReadTime = System.currentTimeMillis(),
                        totalReadingTimeMinutes = book.totalReadingTimeMinutes + sessionDurationMinutes,
                        lastSessionDuration = sessionDurationMinutes
                    )

                    repository.updateBook(updatedBook)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Helper method to get reading statistics
    fun getReadingStats(): Map<String, Any> {
        val bookList = _books.value
        return mapOf(
            "totalBooks" to bookList.size,
            "booksReading" to bookList.count { it.status == "Reading" },
            "booksFinished" to bookList.count { it.status == "Finished" },
            "booksWishlist" to bookList.count { it.status == "Wishlist" },
            "averageProgress" to if (bookList.isNotEmpty()) {
                (bookList.sumOf { it.progress.toDouble() } / bookList.size * 100).toInt()
            } else 0,
            "totalReadingTime" to bookList.sumOf { it.totalReadingTimeMinutes },
            "currentlyReading" to bookList.any { it.isCurrentlyReading }
        )
    }

    // Method to update reading time (for future reading timer feature)
    fun updateReadingTime(bookId: String, additionalMinutes: Int) {
        viewModelScope.launch {
            try {
                val currentBook = _books.value.find { it.id == bookId }
                currentBook?.let { book ->
                    val updatedBook = book.copy(
                        totalReadingTimeMinutes = book.totalReadingTimeMinutes + additionalMinutes,
                        lastReadTime = System.currentTimeMillis()
                    )

                    repository.updateBook(updatedBook)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Method to add/update book notes
    fun updateBookNotes(bookId: String, notes: String) {
        viewModelScope.launch {
            try {
                val currentBook = _books.value.find { it.id == bookId }
                currentBook?.let { book ->
                    val updatedBook = book.copy(
                        notes = notes,
                        lastReadTime = System.currentTimeMillis()
                    )

                    repository.updateBook(updatedBook)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Method to quick-update progress (useful for reading interface)
    fun quickProgressUpdate(bookId: String, progressIncrement: Float) {
        viewModelScope.launch {
            try {
                val currentBook = _books.value.find { it.id == bookId }
                currentBook?.let { book ->
                    val newProgress = (book.progress + progressIncrement).coerceIn(0f, 1f)
                    updateBookProgress(bookId, newProgress)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}