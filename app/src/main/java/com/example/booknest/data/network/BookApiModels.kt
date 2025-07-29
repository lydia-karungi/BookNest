package com.example.booknest.data.network

import com.google.gson.annotations.SerializedName

data class GoogleBooksResponse(
    @SerializedName("items")
    val items: List<GoogleBookItem>? = null,
    @SerializedName("totalItems")
    val totalItems: Int = 0
)

data class GoogleBookItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("volumeInfo")
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("authors")
    val authors: List<String>? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("pageCount")
    val pageCount: Int? = null,
    @SerializedName("categories")
    val categories: List<String>? = null,
    @SerializedName("averageRating")
    val averageRating: Float? = null,
    @SerializedName("imageLinks")
    val imageLinks: ImageLinks? = null,
    @SerializedName("publishedDate")
    val publishedDate: String? = null
)

data class ImageLinks(
    @SerializedName("thumbnail")
    val thumbnail: String? = null,
    @SerializedName("smallThumbnail")
    val smallThumbnail: String? = null
)