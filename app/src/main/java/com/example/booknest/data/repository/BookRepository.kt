package com.example.booknest.data.repository

import com.example.booknest.data.dao.BookDao
import com.example.booknest.data.dao.ReadingLogDao
import com.example.booknest.data.entity.Book
import com.example.booknest.data.entity.ReadingLog
import com.example.booknest.data.network.BookApiService
import com.example.booknest.data.network.GoogleBookItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.*

class BookRepository(
    private val bookDao: BookDao,
    private val readingLogDao: ReadingLogDao,
    private val bookApiService: BookApiService
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

    // EXISTING Reading log operations
    fun getAllLogs(): Flow<List<ReadingLog>> = readingLogDao.getAllLogs()

    fun getRecentLogs(limit: Int = 5): Flow<List<ReadingLog>> = readingLogDao.getRecentLogs(limit)

    suspend fun insertLog(log: ReadingLog) = readingLogDao.insertLog(log)

    suspend fun deleteLog(log: ReadingLog) = readingLogDao.deleteLog(log)

    // NEW: Enhanced Reading log operations for the improved Goals screen

    fun getPublicReadingLogs(): Flow<List<ReadingLog>> = readingLogDao.getPublicLogs()

    fun getPrivateReadingLogs(): Flow<List<ReadingLog>> = readingLogDao.getPrivateLogs()

    fun getReadingLogsByBook(bookId: String): Flow<List<ReadingLog>> =
        readingLogDao.getLogsByBook(bookId)

    fun getReadingLogsByType(logType: String): Flow<List<ReadingLog>> =
        readingLogDao.getLogsByType(logType)

    fun getFilteredReadingLogs(
        isPublic: Boolean? = null,
        logType: String? = null
    ): Flow<List<ReadingLog>> = readingLogDao.getFilteredLogs(isPublic, logType)

    suspend fun getReadingLogById(logId: String): ReadingLog? =
        readingLogDao.getLogById(logId)

    // NEW: Convenient method to create reading logs with proper formatting
    suspend fun insertReadingLog(
        bookId: String,
        bookTitle: String,
        author: String,
        logText: String,
        logType: String,
        rating: Float,
        isPublic: Boolean
    ): String {
        val logId = UUID.randomUUID().toString()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val readingLog = ReadingLog(
            id = logId,
            bookId = bookId,
            bookTitle = bookTitle,
            author = author,
            note = logText,
            logType = logType,
            rating = rating,
            date = currentDate,
            isPublic = isPublic,
            likes = 0,
            comments = 0,
            isLikedByUser = false
        )

        readingLogDao.insertLog(readingLog)
        return logId
    }

    suspend fun updateReadingLog(readingLog: ReadingLog) {
        readingLogDao.updateLog(readingLog)
    }

    suspend fun deleteReadingLogById(logId: String) =
        readingLogDao.deleteLogById(logId)

    suspend fun likeReadingLog(logId: String) {
        val log = readingLogDao.getLogById(logId)
        log?.let {
            if (!it.isLikedByUser) {
                readingLogDao.updateLikes(logId, it.likes + 1)
                readingLogDao.updateUserLike(logId, true)
            }
        }
    }

    suspend fun unlikeReadingLog(logId: String) {
        val log = readingLogDao.getLogById(logId)
        log?.let {
            if (it.isLikedByUser && it.likes > 0) {
                readingLogDao.updateLikes(logId, it.likes - 1)
                readingLogDao.updateUserLike(logId, false)
            }
        }
    }

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