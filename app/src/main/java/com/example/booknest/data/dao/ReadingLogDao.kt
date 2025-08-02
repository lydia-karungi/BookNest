package com.example.booknest.data.dao

import androidx.room.*
import com.example.booknest.data.entity.ReadingLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingLogDao {

    // Your existing methods
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

    // NEW: Additional methods for enhanced functionality

    @Query("SELECT * FROM reading_logs WHERE isPublic = 1 ORDER BY date DESC")
    fun getPublicLogs(): Flow<List<ReadingLog>>

    @Query("SELECT * FROM reading_logs WHERE isPublic = 0 ORDER BY date DESC")
    fun getPrivateLogs(): Flow<List<ReadingLog>>

    @Query("SELECT * FROM reading_logs WHERE logType = :logType ORDER BY date DESC")
    fun getLogsByType(logType: String): Flow<List<ReadingLog>>

    @Query("SELECT * FROM reading_logs WHERE id = :logId")
    suspend fun getLogById(logId: String): ReadingLog?

    @Query("DELETE FROM reading_logs WHERE id = :logId")
    suspend fun deleteLogById(logId: String)

    @Query("UPDATE reading_logs SET likes = :likes WHERE id = :logId")
    suspend fun updateLikes(logId: String, likes: Int)

    @Query("UPDATE reading_logs SET isLikedByUser = :isLiked WHERE id = :logId")
    suspend fun updateUserLike(logId: String, isLiked: Boolean)

    // Get filtered logs
    @Query("""
        SELECT * FROM reading_logs 
        WHERE (:isPublic IS NULL OR isPublic = :isPublic)
        AND (:logType IS NULL OR logType = :logType)
        ORDER BY date DESC
    """)
    fun getFilteredLogs(
        isPublic: Boolean? = null,
        logType: String? = null
    ): Flow<List<ReadingLog>>

    // Statistics
    @Query("SELECT COUNT(*) FROM reading_logs")
    suspend fun getTotalLogsCount(): Int

    @Query("SELECT COUNT(*) FROM reading_logs WHERE isPublic = 1")
    suspend fun getPublicLogsCount(): Int

    @Query("SELECT COUNT(*) FROM reading_logs WHERE logType = :logType")
    suspend fun getLogsCountByType(logType: String): Int
}