package com.example.booknest.data.dao

import androidx.room.*
import com.example.booknest.data.entity.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY dateAdded DESC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE status = :status ORDER BY dateAdded DESC")
    fun getBooksByStatus(status: String): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: String): Book?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT COUNT(*) FROM books WHERE status = 'Finished'")
    suspend fun getFinishedBooksCount(): Int

    @Query("SELECT COUNT(*) FROM books WHERE status = 'Reading'")
    suspend fun getCurrentlyReadingCount(): Int
}