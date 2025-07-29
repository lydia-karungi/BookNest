package com.example.booknest.data.dao

import androidx.room.*
import com.example.booknest.data.entity.ReadingLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingLogDao {

    @Query("SELECT * FROM reading_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<ReadingLog>>

    @Query("SELECT * FROM reading_logs WHERE bookId = :bookId ORDER BY date DESC")
    fun getLogsByBook(bookId: String): Flow<List<ReadingLog>>

    @Query("SELECT * FROM reading_logs ORDER BY date DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 5): Flow<List<ReadingLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ReadingLog)

    @Update
    suspend fun updateLog(log: ReadingLog)

    @Delete
    suspend fun deleteLog(log: ReadingLog)
}