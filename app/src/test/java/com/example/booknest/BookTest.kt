package com.example.booknest

import com.example.booknest.data.entity.Book
import org.junit.Test
import org.junit.Assert.*

class BookTest {

    @Test
    fun testBookCreation() {
        // Create a simple book
        val book = Book(
            id = "1",
            title = "Test Book",
            author = "Test Author",
            coverImagePath = null,
            pageCount = 100,
            currentPage = 50,
            status = "Reading",
            category = "Fiction",
            rating = 4.0f,
            progress = 0.5f  // Need to set progress explicitly
        )

        // Test basic properties
        assertEquals("1", book.id)
        assertEquals("Test Book", book.title)
        assertEquals("Test Author", book.author)
        assertEquals(100, book.pageCount)
        assertEquals(50, book.currentPage)
        assertEquals(0.5f, book.progress, 0.01f)
    }

    @Test
    fun testBookProgress() {
        // Test with different progress values
        val book1 = Book(
            id = "1",
            title = "Half Read",
            author = "Author",
            coverImagePath = null,
            pageCount = 100,
            currentPage = 50,
            status = "Reading",
            category = "Fiction",
            rating = 3.0f
            // progress defaults to 0f if not set
        )

        // Test the stored progress value (which is 0f by default)
        assertEquals(0f, book1.progress, 0.01f)

        // Test the calculated progress using the helper method
        val calculatedProgress = book1.calculateProgressFromPage()
        assertEquals(0.5f, calculatedProgress, 0.01f)

        // Test updating progress from current page
        val updatedBook = book1.updateProgressFromCurrentPage()
        assertEquals(0.5f, updatedBook.progress, 0.01f)

        // Test helper properties
        assertEquals(50, book1.pagesRemaining)
        assertTrue(book1.hasStartedReading)
        assertFalse(book1.isFinished)
    }

    @Test
    fun testEmptyBook() {
        val book = Book(
            id = "2",
            title = "New Book",
            author = "Author",
            coverImagePath = null,
            pageCount = 0,
            currentPage = 0,
            status = "Wishlist",
            category = "Fiction",
            rating = 0.0f
        )

        // Book with 0 pages
        assertEquals(0f, book.progress, 0.01f)
        assertEquals(0, book.progressPercentage)
        assertFalse(book.hasStartedReading)
        assertFalse(book.isFinished)
    }

    @Test
    fun testFinishedBook() {
        val book = Book(
            id = "3",
            title = "Finished Book",
            author = "Author",
            coverImagePath = null,
            pageCount = 200,
            currentPage = 200,
            status = "Finished",
            category = "Non-Fiction",
            rating = 5.0f,
            progress = 1.0f
        )

        assertTrue(book.isFinished)
        assertEquals(0, book.pagesRemaining)
        assertEquals(100, book.progressPercentage)
    }
}