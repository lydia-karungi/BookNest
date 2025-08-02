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
    version = 3, // Increased version for ReadingLog changes
    exportSchema = false
)
abstract class BookNestDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun readingLogDao(): ReadingLogDao

    companion object {
        @Volatile
        private var INSTANCE: BookNestDatabase? = null

        // Migration from version 1 to 2 (Book table changes)
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

        // Migration from version 2 to 3 (ReadingLog table changes)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Check if reading_logs table exists, if not create it
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS reading_logs_new (
                        id TEXT PRIMARY KEY NOT NULL,
                        bookId TEXT NOT NULL,
                        bookTitle TEXT NOT NULL,
                        author TEXT NOT NULL,
                        note TEXT NOT NULL,
                        logType TEXT NOT NULL,
                        rating REAL NOT NULL DEFAULT 0.0,
                        date TEXT NOT NULL,
                        isPublic INTEGER NOT NULL DEFAULT 1,
                        likes INTEGER NOT NULL DEFAULT 0,
                        comments INTEGER NOT NULL DEFAULT 0,
                        isLikedByUser INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(bookId) REFERENCES books(id) ON DELETE CASCADE
                    )
                """)

                // Copy data from old table if it exists
                database.execSQL("""
                    INSERT INTO reading_logs_new (id, bookId, bookTitle, author, note, logType, rating, date, isPublic, likes, comments, isLikedByUser)
                    SELECT 
                        id, 
                        bookId, 
                        bookTitle, 
                        author, 
                        note, 
                        logType, 
                        rating, 
                        date,
                        1 as isPublic,
                        0 as likes,
                        0 as comments,
                        0 as isLikedByUser
                    FROM reading_logs
                """)

                // Drop old table and rename new one
                database.execSQL("DROP TABLE IF EXISTS reading_logs")
                database.execSQL("ALTER TABLE reading_logs_new RENAME TO reading_logs")

                // Create index
                database.execSQL("CREATE INDEX IF NOT EXISTS index_reading_logs_bookId ON reading_logs(bookId)")
            }
        }

        fun getDatabase(context: Context): BookNestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookNestDatabase::class.java,
                    "book_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Add both migrations
                    .fallbackToDestructiveMigration() // Keep this for development safety
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}