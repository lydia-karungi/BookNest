package com.example.booknest.screens

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.booknest.data.database.BookNestDatabase
import com.example.booknest.data.entity.Book
import com.example.booknest.data.repository.BookRepository
import com.example.booknest.data.network.NetworkModule
import com.example.booknest.viewmodel.LibraryViewModel
import com.example.booknest.viewmodel.ViewModelFactory
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onAddBookClick: () -> Unit = {},
    onEditBookClick: (Book) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onReadBookClick: (Book) -> Unit = {}
) {
    // Get database instance and create repository
    val context = LocalContext.current
    val database = remember { BookNestDatabase.getDatabase(context) }

    val repository = remember {
        BookRepository(database.bookDao(), database.readingLogDao(), NetworkModule.bookApiService)
    }

    // Create ViewModel using factory
    val viewModel: LibraryViewModel = viewModel(
        factory = ViewModelFactory(repository)
    )

    // Collect books from ViewModel
    val allBooks by viewModel.books.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val categories = listOf("All", "Reading", "Finished", "Wishlist")
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    // Filter books based on search query and category
    val filteredBooks = remember(allBooks, searchQuery, selectedCategory) {
        allBooks.filter { book ->
            val matchesSearch = searchQuery.isEmpty() ||
                    book.title.contains(searchQuery, ignoreCase = true) ||
                    book.author.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == "All" || book.status == selectedCategory

            matchesSearch && matchesCategory
        }
    }

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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
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
                                    Icon(
                                        Icons.Default.Star, // or any book-related icon
                                        contentDescription = "BookNest",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "BookNest",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 28.sp
                                    ),
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Find Books Online Button - Navigate to Store
                            Surface(
                                onClick = onSearchClick,
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                color = Color(0xFF6366F1),
                                shadowElevation = 2.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Find Books Online",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

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

                            // Profile Avatar with gradient - simplified
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
                                Text(
                                    text = "BN",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
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
                                    text = "${allBooks.size} books",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF1E293B)
                                )
                                val avgProgress = if (allBooks.isNotEmpty()) {
                                    (allBooks.sumOf { it.progress.toDouble() } / allBooks.size * 100).toInt()
                                } else 0
                                Text(
                                    text = "$avgProgress% avg progress",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                            }

                            // Circular progress indicator
                            Box(contentAlignment = Alignment.Center) {
                                val progress = if (allBooks.isNotEmpty()) {
                                    (allBooks.sumOf { it.progress.toDouble() } / allBooks.size).toFloat()
                                } else 0f
                                CircularProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.size(60.dp),
                                    color = Color(0xFF6366F1),
                                    strokeWidth = 6.dp,
                                    trackColor = Color(0xFFE2E8F0)
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF1E293B),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Search Bar - Local library search
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
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
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFF6366F1),
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        ),
                        singleLine = true
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
                        text = "${if (searchQuery.isNotEmpty()) "Search Results" else "Your Library"}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "${filteredBooks.size} books",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Loading state
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF6366F1)
                        )
                    }
                }
            }

            // Books List
            items(filteredBooks) { book ->
                ModernBookCard(
                    book = book,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    onEditClick = { onEditBookClick(book) },
                    // In your ModernBookCard function, replace these lines:

                    onProgressUpdate = { newProgress ->
                        viewModel.updateBookProgress(book.id, newProgress)  // Remove .toInt()
                    },
                    onProgressUpdateByPages = { currentPage, totalPages ->
                        viewModel.updateBookProgressByPages(book.id, currentPage, totalPages)  // Remove .toInt()
                    },
                    onStatusUpdate = { newStatus ->
                        viewModel.updateBookStatus(book.id, newStatus)  // Remove .toInt()
                    },
                    onStartReading = {
                        viewModel.startReadingSession(book.id)  // Remove .toInt()
                    },
                    onReadBook = { onReadBookClick(book) }
                )
            }

            // Empty state
            if (!isLoading && filteredBooks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (searchQuery.isNotEmpty()) {
                            // Search results empty
                            Text(
                                text = "No books found for \"$searchQuery\"",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "Try a different search term",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF94A3B8)
                            )
                        } else if (allBooks.isEmpty()) {
                            // Library completely empty
                            Text(
                                text = "No books in your library yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "Find books online to get started!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = onSearchClick,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF6366F1)
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Find Books")
                                }
                                OutlinedButton(
                                    onClick = onAddBookClick,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF6366F1)
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Add Manually")
                                }
                            }
                        } else {
                            // Category filter shows no results
                            Text(
                                text = "No $selectedCategory books",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "Try selecting a different category",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
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
    onEditClick: () -> Unit = {},
    onProgressUpdate: (Float) -> Unit = {},
    onProgressUpdateByPages: (Int, Int) -> Unit = { _, _ -> },
    onStatusUpdate: (String) -> Unit = {},
    onStartReading: () -> Unit = {},
    onReadBook: () -> Unit = {}

) {
    var showProgressDialog by remember { mutableStateOf(false) }

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
                // Book Cover
                Card(
                    modifier = Modifier
                        .width(60.dp)
                        .height(80.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    if (!book.coverImagePath.isNullOrEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(book.coverImagePath.replace("http:", "https:"))
                                .crossfade(true)
                                .build(),
                            contentDescription = "Book cover",
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

                    // Progress Section (show if Reading or has progress)
                    if (book.status == "Reading" || book.progress > 0f) {
                        Spacer(modifier = Modifier.height(8.dp))

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

            // Divider
            HorizontalDivider(
                color = Color(0xFFE2E8F0),
                thickness = 1.dp
            )

            // Action Buttons
            // Action Buttons - Improved Design
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (book.status) {
                    "Wishlist" -> {
                        // Single full-width button for wishlist
                        Button(
                            onClick = {
                                onStatusUpdate("Reading")
                                onStartReading()
                                onReadBook()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Start Reading",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        // Edit button below
                        OutlinedButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF64748B)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Details")
                        }
                    }

                    "Reading" -> {
                        // Primary action - Resume reading
                        Button(
                            onClick = { onReadBook() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Continue Reading",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        // Secondary actions row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showProgressDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF6366F1)
                                ),
                                border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.3f))
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Update")
                            }

                            OutlinedButton(
                                onClick = onEditClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF64748B)
                                ),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit")
                            }
                        }
                    }

                    "Finished" -> {
                        // Primary action - Resume/Read again
                        Button(
                            onClick = {
                                onStatusUpdate("Reading")
                                onStartReading()
                                onReadBook()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Read Again",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        // Edit button below
                        OutlinedButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF64748B)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Details")
                        }
                    }
                }
            }
        }
    }

    // Progress Update Dialog
    if (showProgressDialog) {
        ProgressUpdateDialog(
            currentProgress = book.progress,
            currentPage = book.currentPage,
            totalPages = book.pageCount,
            onProgressUpdate = { newProgress ->
                onProgressUpdate(newProgress)
                showProgressDialog = false
            },
            onProgressUpdateByPages = { currentPage, totalPages ->
                onProgressUpdateByPages(currentPage, totalPages)
                showProgressDialog = false
            },
            onDismiss = { showProgressDialog = false }
        )
    }
}

@Composable
fun ProgressUpdateDialog(
    currentProgress: Float,
    currentPage: Int,
    totalPages: Int,
    onProgressUpdate: (Float) -> Unit,
    onProgressUpdateByPages: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var progress by remember { mutableStateOf(currentProgress) }
    var pageInput by remember { mutableStateOf(if (currentPage > 0) currentPage.toString() else "") }
    var totalPagesInput by remember { mutableStateOf(if (totalPages > 0) totalPages.toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Reading Progress") },
        text = {
            Column {
                Text("Current progress: ${(currentProgress * 100).toInt()}%")

                Spacer(modifier = Modifier.height(16.dp))

                // Slider for percentage
                Text("Progress: ${(progress * 100).toInt()}%")
                Slider(
                    value = progress,
                    onValueChange = { progress = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Or input by pages
                Text("Or update by pages:")
                Row {
                    OutlinedTextField(
                        value = pageInput,
                        onValueChange = { pageInput = it },
                        label = { Text("Current Page") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = totalPagesInput,
                        onValueChange = { totalPagesInput = it },
                        label = { Text("Total Pages") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // Auto-calculate progress from pages
                LaunchedEffect(pageInput, totalPagesInput) {
                    val currentPageInt = pageInput.toIntOrNull()
                    val totalPagesInt = totalPagesInput.toIntOrNull()
                    if (currentPageInt != null && totalPagesInt != null && totalPagesInt > 0) {
                        progress = (currentPageInt.toFloat() / totalPagesInt).coerceIn(0f, 1f)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val currentPageInt = pageInput.toIntOrNull()
                    val totalPagesInt = totalPagesInput.toIntOrNull()

                    if (currentPageInt != null && totalPagesInt != null && totalPagesInt > 0) {
                        onProgressUpdateByPages(currentPageInt, totalPagesInt)
                    } else {
                        onProgressUpdate(progress)
                    }
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLibraryScreen() {
    MaterialTheme {
        LibraryScreen()
    }
}