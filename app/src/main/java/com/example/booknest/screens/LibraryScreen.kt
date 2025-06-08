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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
    val coverImageRes: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onAddBookClick: () -> Unit = {},
    onEditBookClick: (Book) -> Unit = {}
) {
    val books = listOf(
        Book("The Midnight Library", "Matt Haig", "Reading", 0.65f, 4.8f, "Fiction", Color.Black, 288, "2024-01-15", R.drawable.midnight_library_cover),
        Book("Educated", "Tara Westover", "Finished", 1.0f, 4.9f, "Memoir", Color.Black, 334, "2024-01-10", R.drawable.educated),
        Book("Where the Crawdads Sing", "Delia Owens", "Reading", 0.35f, 4.6f, "Fiction", Color.Black, 368, "2024-01-12", R.drawable.where_the_crwadads_sing),
        Book("Klara and the Sun", "Kazuo Ishiguro", "Unread", 0.0f, 4.5f, "Fiction", Color.Black, 303, "", R.drawable.klara_and_the_sun),
        Book("The Seven Husbands of Evelyn Hugo", "Taylor Jenkins Reid", "Finished", 1.0f, 4.7f, "Romance", Color.Black, 400, "2024-01-08", R.drawable.the_seven_husbands)
    )

    val categories = listOf("All", "Reading", "Finished", "Wishlist")
    var selectedCategory by remember { mutableStateOf("All") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBookClick,
                containerColor = Color(0xFF6366F1),
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Book",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8FAFC),
                            Color.White
                        )
                    )
                )
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Modern Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Status bar space
                    Spacer(modifier = Modifier.height(8.dp))

                    // Top header with greeting
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Good morning! ☀️",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF64748B),
                                fontSize = 16.sp
                            )
                            Text(
                                text = "BookNest",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 28.sp
                                ),
                                color = Color(0xFF1E293B)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Notification icon
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 2.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Profile Avatar with gradient
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF6366F1),
                                                Color(0xFF8B5CF6)
                                            )
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.your_profile_photo), // Replace with your image file name
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                            }
                        }
                    }

                    // Reading Stats Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Reading Progress",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF64748B),
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "3 books",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "67% avg progress",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                            }

                            // Circular progress indicator
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { 0.67f },
                                    modifier = Modifier.size(60.dp),
                                    color = Color(0xFF6366F1),
                                    strokeWidth = 6.dp,
                                    trackColor = Color(0xFFE2E8F0)
                                )
                                Text(
                                    text = "67%",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF1E293B),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Search Bar
                    OutlinedTextField(
                        value = "",
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        placeholder = {
                            Text(
                                "Search your library...",
                                color = Color(0xFF94A3B8)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = "Filter",
                                    tint = Color(0xFF6366F1),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFF6366F1),
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )

                    // Category Pills
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        items(categories) { category ->
                            ModernCategoryPill(
                                category = category,
                                isSelected = selectedCategory == category,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Books Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Library",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "${books.size} books",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Books List
            items(books) { book ->
                ModernBookCard(
                    book = book,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    onEditClick = { onEditBookClick(book) }
                )
            }

            // Bottom spacing for FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ModernCategoryPill(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(25.dp),
        color = if (isSelected) Color(0xFF6366F1) else Color.White,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFE2E8F0)
        ) else null,
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = if (isSelected) Color.White else Color(0xFF64748B)
        )
    }
}
@Composable
fun ModernBookCard(
    book: Book,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Main book content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Enhanced Book Cover
                Card(
                    modifier = Modifier
                        .width(60.dp)
                        .height(80.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    if (book.coverImageRes != null) {
                        Image(
                            painter = painterResource(id = book.coverImageRes),
                            contentDescription = "Cover of ${book.title}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF6366F1),
                                            Color(0xFF8B5CF6)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = book.title.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Book Information
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1E293B),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "by ${book.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status and Rating Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Status Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (book.status) {
                                "Reading" -> Color(0xFF6366F1).copy(alpha = 0.1f)
                                "Finished" -> Color(0xFF10B981).copy(alpha = 0.1f)
                                else -> Color(0xFF64748B).copy(alpha = 0.1f)
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
                                    "Reading" -> Color(0xFF6366F1)
                                    "Finished" -> Color(0xFF10B981)
                                    else -> Color(0xFF64748B)
                                }
                            )
                        }

                        // Rating
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = book.rating.toString(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF1E293B)
                            )
                        }
                    }

                    if (book.status == "Reading") {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress Section
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Progress",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "${(book.progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF1E293B)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { book.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFF6366F1),
                                trackColor = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }
            }

            // Divider and Edit Button (moved outside the Row, inside the Column)
            HorizontalDivider(
                color = Color(0xFFE2E8F0),
                thickness = 1.dp
            )

            // Edit Button
            TextButton(
                onClick = onEditClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF6366F1)
                )
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Edit Book",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
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