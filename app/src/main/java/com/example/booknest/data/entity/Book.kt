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
    val dateFinished: Long? = null
)