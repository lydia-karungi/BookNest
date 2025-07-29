package com.example.booknest.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "reading_logs")
data class ReadingLog(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val bookId: String,
    val bookTitle: String,
    val author: String,
    val logText: String,
    val logType: String, // "Thought", "Review", "Quote", "Progress"
    val rating: Float = 0f,
    val date: Long = System.currentTimeMillis()
)