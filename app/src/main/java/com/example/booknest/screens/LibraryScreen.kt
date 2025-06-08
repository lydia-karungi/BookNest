package com.example.booknest.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.example.booknest.R

data class Book(
    val title: String,
    val author: String,
    val status: String,
    val progress: Float,
    val rating: Float,
    val category: String,
    val coverColor: Color,
    val pageCount: Int,
    val readDate: String,
    val coverImageRes: Int? = null // Add this for image resource
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen() {
    val books = listOf(
        Book("The Midnight Library", "Matt Haig", "Reading", 0.65f, 4.8f, "Fiction", Color.Black, 288, "2024-01-15", R.drawable.midnight_library_cover), // Set to null for now
        Book("Educated", "Tara Westover", "Finished", 1.0f, 4.9f, "Memoir", Color.Black, 334, "2024-01-10", R.drawable.educated),
        Book("Where the Crawdads Sing", "Delia Owens", "Reading", 0.35f, 4.6f, "Fiction", Color.Black, 368, "2024-01-12", R.drawable.where_the_crwadads_sing),
        Book("Klara and the Sun", "Kazuo Ishiguro", "Unread", 0.0f, 4.5f, "Fiction", Color.Black, 303, "", R.drawable.klara_and_the_sun),
        Book("The Seven Husbands of Evelyn Hugo", "Taylor Jenkins Reid", "Finished", 1.0f, 4.7f, "Romance", Color.Black, 400, "2024-01-08", R.drawable.the_seven_husbands)
    )

    val categories = listOf("All", "Reading", "Finished", "Wishlist")
    val readingStats = "3 books • 67% avg progress"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Modern Header with Glass Effect
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 0.dp
        ) {
            Column {
                // Top Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BookNest",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 32.sp
                            ),
                            color = Color.Black
                        )
                        Text(
                            text = readingStats,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    }

                    // Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color(0xFFFFBBE7),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "LK",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }

                // Enhanced Search Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black.copy(alpha = 0.04f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.Black.copy(alpha = 0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Search your library...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black.copy(alpha = 0.4f)
                            )
                        }
                    }

                    // Filter Button
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFBBE7)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Filter",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Modern Category Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    items(categories.size) { index ->
                        ModernCategoryPill(
                            category = categories[index],
                            isSelected = index == 0
                        )
                    }
                }
            }
        }

        // Books List with Modern Cards
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
        ) {
            items(books) { book ->
                ModernBookCard(book = book)
            }
        }
    }
}

@Composable
fun ModernCategoryPill(category: String, isSelected: Boolean) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color.Black else Color.Transparent,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.Black.copy(alpha = 0.1f)
        ) else null
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ModernBookCard(book: Book) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.02f),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Enhanced Book Cover
            Surface(
                modifier = Modifier
                    .width(60.dp)
                    .height(80.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.Black
            ) {
                if (book.coverImageRes != null) {
                    // Use real book cover image
                    Image(
                        painter = painterResource(id = book.coverImageRes),
                        contentDescription = "Cover of ${book.title}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback to text-based cover
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = book.title.split(" ").take(2).joinToString("\n"),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            lineHeight = 11.sp,
                            maxLines = 3
                        )

                        Text(
                            text = book.author.split(" ").last().uppercase(),
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 7.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Book Information
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "by ${book.author}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (book.status) {
                        "Reading" -> Color(0xFFFFBBE7).copy(alpha = 0.2f)
                        "Finished" -> Color.Black.copy(alpha = 0.08f)
                        else -> Color.Black.copy(alpha = 0.04f)
                    }
                ) {
                    Text(
                        text = book.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = when (book.status) {
                            "Reading" -> Color(0xFFFFBBE7)
                            else -> Color.Black.copy(alpha = 0.7f)
                        }
                    )
                }

                if (book.status == "Reading") {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Section
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progress",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color.Black.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${(book.progress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { book.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFFFBBE7),
                            trackColor = Color.Black.copy(alpha = 0.08f)
                        )
                    }
                }
            }

            // Rating and Menu
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFBBE7),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = book.rating.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = Color.Black.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLibraryScreen() {
    MaterialTheme {
        LibraryScreen()
    }
}