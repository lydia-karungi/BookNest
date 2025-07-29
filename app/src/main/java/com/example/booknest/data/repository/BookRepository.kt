package com.example.booknest.data.repository

import com.example.booknest.data.dao.BookDao
import com.example.booknest.data.dao.ReadingLogDao
import com.example.booknest.data.entity.Book
import com.example.booknest.data.entity.ReadingLog
import com.example.booknest.data.network.BookApiService
import com.example.booknest.data.network.GoogleBookItem
import kotlinx.coroutines.flow.Flow

class BookRepository(
    private val bookDao: BookDao,
    private val readingLogDao: ReadingLogDao,
    private val bookApiService: BookApiService // Add this
) {
    // Book operations
    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()

    fun getBooksByStatus(status: String): Flow<List<Book>> = bookDao.getBooksByStatus(status)

    suspend fun getBookById(id: String): Book? = bookDao.getBookById(id)

    suspend fun insertBook(book: Book) = bookDao.insertBook(book)

    suspend fun updateBook(book: Book) = bookDao.updateBook(book)

    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)

    suspend fun getFinishedBooksCount(): Int = bookDao.getFinishedBooksCount()

    suspend fun getCurrentlyReadingCount(): Int = bookDao.getCurrentlyReadingCount()

    // Reading log operations
    fun getAllLogs(): Flow<List<ReadingLog>> = readingLogDao.getAllLogs()

    fun getRecentLogs(limit: Int = 5): Flow<List<ReadingLog>> = readingLogDao.getRecentLogs(limit)

    suspend fun insertLog(log: ReadingLog) = readingLogDao.insertLog(log)

    suspend fun deleteLog(log: ReadingLog) = readingLogDao.deleteLog(log)

    // Network operations
    suspend fun searchBooksOnline(query: String): Result<List<GoogleBookItem>> {
        return try {
            val response = bookApiService.searchBooks(query)
            Result.success(response.items ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addBookFromApi(googleBook: GoogleBookItem): Result<Book> {
        return try {
            val book = Book(
                title = googleBook.volumeInfo.title ?: "Unknown Title",
                author = googleBook.volumeInfo.authors?.joinToString(", ") ?: "Unknown Author",
                status = "Wishlist",
                progress = 0f,
                rating = googleBook.volumeInfo.averageRating ?: 0f,
                category = googleBook.volumeInfo.categories?.firstOrNull() ?: "Fiction",
                pageCount = googleBook.volumeInfo.pageCount ?: 0,
                coverImagePath = googleBook.volumeInfo.imageLinks?.thumbnail
            )
            insertBook(book)
            Result.success(book)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}