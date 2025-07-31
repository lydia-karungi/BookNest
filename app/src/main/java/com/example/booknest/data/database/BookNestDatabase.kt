package com.example.booknest.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.example.booknest.data.dao.BookDao
import com.example.booknest.data.dao.ReadingLogDao
import com.example.booknest.data.entity.Book
import com.example.booknest.data.entity.ReadingLog

@Database(
    entities = [Book::class, ReadingLog::class],
    version = 2,
    exportSchema = false
)
abstract class BookNestDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun readingLogDao(): ReadingLogDao

    companion object {
        @Volatile
        private var INSTANCE: BookNestDatabase? = null

        // Migration from version 1 to 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns to books table
                database.execSQL("ALTER TABLE books ADD COLUMN currentPage INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE books ADD COLUMN lastReadTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE books ADD COLUMN totalReadingTimeMinutes INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE books ADD COLUMN averageReadingSpeed REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE books ADD COLUMN readingSessions INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE books ADD COLUMN lastSessionDuration INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE books ADD COLUMN bookmarks TEXT")
                database.execSQL("ALTER TABLE books ADD COLUMN notes TEXT")
                database.execSQL("ALTER TABLE books ADD COLUMN isCurrentlyReading INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE books ADD COLUMN estimatedTimeToFinish INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): BookNestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookNestDatabase::class.java,
                    "book_database"
                )
                    .addMigrations(MIGRATION_1_2) // Keep the migration for future reference
                    .fallbackToDestructiveMigration() // Add this to handle schema conflicts
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}