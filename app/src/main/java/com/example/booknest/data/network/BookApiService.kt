package com.example.booknest.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface BookApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("key") apiKey: String = "" // We'll use it without API key for now
    ): GoogleBooksResponse

    companion object {
        const val BASE_URL = "https://www.googleapis.com/books/v1/"
    }
}