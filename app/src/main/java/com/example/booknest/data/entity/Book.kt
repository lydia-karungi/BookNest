package com.example.booknest.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val author: String,
    val status: String, // "Reading", "Finished", "Wishlist"
    val progress: Float = 0f,
    val rating: Float = 0f,
    val category: String,
    val pageCount: Int = 0,
    val coverImagePath: String? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val dateFinished: Long? = null,

    // NEW: Reading tracking fields
    val currentPage: Int = 0,
    val lastReadTime: Long = 0L,
    val totalReadingTimeMinutes: Int = 0,
    val averageReadingSpeed: Float = 0f, // pages per minute
    val readingSessions: Int = 0, // total number of reading sessions
    val lastSessionDuration: Int = 0, // minutes of last reading session
    val bookmarks: String? = null, // JSON string of bookmarked pages/notes
    val notes: String? = null, // User's notes about the book
    val isCurrentlyReading: Boolean = false, // true when user has an active reading session
    val estimatedTimeToFinish: Int = 0 // estimated minutes to finish based on reading speed
) {
    // Helper properties
    val progressPercentage: Int
        get() = (progress * 100).toInt()

    val pagesRemaining: Int
        get() = maxOf(0, pageCount - currentPage)

    val isFinished: Boolean
        get() = status == "Finished" || progress >= 1.0f

    val hasStartedReading: Boolean
        get() = currentPage > 0 || progress > 0f

    // Calculate reading progress from current page if pageCount is available
    fun calculateProgressFromPage(): Float {
        return if (pageCount > 0) {
            (currentPage.toFloat() / pageCount).coerceIn(0f, 1f)
        } else {
            progress
        }
    }

    // Update progress when current page changes
    fun updateProgressFromCurrentPage(): Book {
        return if (pageCount > 0) {
            copy(progress = calculateProgressFromPage())
        } else {
            this
        }
    }
}