package com.example.booknest.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "reading_logs",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["bookId"])]
)
data class ReadingLog(
    @PrimaryKey
    val id: String = "",
    val bookId: String,
    val bookTitle: String,
    val author: String,
    val note: String, // Your existing field name
    val logType: String, // "Thought", "Review", "Quote", "Progress"
    val rating: Float = 0f,
    val date: String, // Your existing field name
    val isPublic: Boolean = true,
    val likes: Int = 0,
    val comments: Int = 0,
    val isLikedByUser: Boolean = false
)