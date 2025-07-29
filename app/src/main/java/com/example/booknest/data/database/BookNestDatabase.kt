package com.example.booknest.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.booknest.data.dao.BookDao
import com.example.booknest.data.dao.ReadingLogDao
import com.example.booknest.data.entity.Book
import com.example.booknest.data.entity.ReadingLog

@Database(
    entities = [Book::class, ReadingLog::class],
    version = 1,
    exportSchema = false
)
abstract class BookNestDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun readingLogDao(): ReadingLogDao

    companion object {
        @Volatile
        private var INSTANCE: BookNestDatabase? = null

        fun getDatabase(context: Context): BookNestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookNestDatabase::class.java,
                    "booknest_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}