package com.example.booknest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booknest.data.database.BookNestDatabase
import com.example.booknest.data.entity.Book
import com.example.booknest.data.network.NetworkModule
import com.example.booknest.data.repository.BookRepository
import com.example.booknest.viewmodel.LibraryViewModel
import com.example.booknest.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: (bookId: String, bookTitle: String, author: String, logText: String, logType: String, rating: Float, isPublic: Boolean) -> Unit = { _, _, _, _, _, _, _ -> }
) {
    // Get books from library
    val context = LocalContext.current
    val database = remember { BookNestDatabase.getDatabase(context) }
    val repository = remember {
        BookRepository(database.bookDao(), database.readingLogDao(), NetworkModule.bookApiService)
    }
    val viewModel: LibraryViewModel = viewModel(
        factory = ViewModelFactory(repository)
    )

    val allBooks by viewModel.books.collectAsState()
    // Filter to only show books that are currently being read
    val readingBooks = remember(allBooks) {
        allBooks.filter { it.status == "Reading" }
    }

    // State variables
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var logText by remember { mutableStateOf("") }
    var logType by remember { mutableStateOf("Thought") }
    var rating by remember { mutableStateOf(0f) }
    var isPublic by remember { mutableStateOf(true) }
    var showBookSelector by remember { mutableStateOf(false) }
    var logTypeExpanded by remember { mutableStateOf(false) }

    val logTypes = listOf(
        "Thought" to "💭 Personal reflection or insight",
        "Review" to "⭐ Detailed book review",
        "Quote" to "📖 Memorable quote or passage",
        "Progress" to "📊 Reading progress update"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Create Reading Log",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1E293B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF64748B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
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
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF6366F1),
                                        Color(0xFF8B5CF6)
                                    )
                                ),
                                RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Share Your Reading Journey",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1E293B)
                    )

                    Text(
                        text = "Create a log that you can share with the BookNest community",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Log Details",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1E293B)
                    )

                    // Book Selection
                    Column {
                        Text(
                            text = "Select Book *",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFF374151)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (readingBooks.isEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFEF3C7)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "No books currently being read",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = Color(0xFF92400E)
                                    )
                                    Text(
                                        text = "Start reading a book from your library to create logs",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFA16207)
                                    )
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = selectedBook?.title ?: "",
                                onValueChange = { },
                                readOnly = true,
                                placeholder = { Text("Choose a book you're currently reading...") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = Color(0xFF6366F1)
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showBookSelector = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedBorderColor = Color(0xFF6366F1)
                                )
                            )
                        }
                    }

                    // Author (auto-filled when book is selected)
                    if (selectedBook != null) {
                        OutlinedTextField(
                            value = selectedBook?.author ?: "",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Author") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF6366F1)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedBorderColor = Color(0xFF6366F1),
                                disabledBorderColor = Color(0xFFE2E8F0),
                                disabledTextColor = Color(0xFF64748B)
                            )
                        )
                    }

                    // Log Type Dropdown
                    ExposedDropdownMenuBox(
                        expanded = logTypeExpanded,
                        onExpandedChange = { logTypeExpanded = !logTypeExpanded }
                    ) {
                        OutlinedTextField(
                            value = logTypes.find { it.first == logType }?.second ?: logType,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Log Type") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = Color(0xFF6366F1)
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = logTypeExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedBorderColor = Color(0xFF6366F1)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = logTypeExpanded,
                            onDismissRequest = { logTypeExpanded = false }
                        ) {
                            logTypes.forEach { (type, description) ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = description,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    },
                                    onClick = {
                                        logType = type
                                        logTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Log Text (Large text field)
                    OutlinedTextField(
                        value = logText,
                        onValueChange = { logText = it },
                        label = { Text("Your ${logType} *") },
                        placeholder = {
                            Text(
                                when (logType) {
                                    "Thought" -> "What insights or reflections do you have about this book? Share your personal thoughts..."
                                    "Review" -> "Write an honest review of the book. What did you like or dislike? Would you recommend it?"
                                    "Quote" -> "Share a memorable quote or passage that resonated with you..."
                                    "Progress" -> "How is your reading going? Any interesting developments or updates..."
                                    else -> "Share your thoughts about this book..."
                                },
                                color = Color(0xFF94A3B8)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFF6366F1)
                        ),
                        maxLines = 8
                    )

                    // Rating Section
                    Column {
                        Text(
                            text = "Rating ${if (logType == "Review") "*" else "(Optional)"}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFF374151)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(5) { index ->
                                IconButton(
                                    onClick = {
                                        rating = if (rating == (index + 1).toFloat()) 0f else (index + 1).toFloat()
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = "Rate ${index + 1} stars",
                                        tint = if (index < rating) Color(0xFFFBBF24) else Color(0xFFE2E8F0),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            if (rating > 0) {
                                Text(
                                    text = "${rating.toInt()}/5",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF1E293B)
                                )
                            } else {
                                Text(
                                    text = "Tap to rate",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }

                    // Privacy Settings
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF8FAFC)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Sharing Settings",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF1E293B)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Public option
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { isPublic = true },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isPublic) Color(0xFF6366F1).copy(alpha = 0.1f) else Color.White
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isPublic) Color(0xFF6366F1) else Color(0xFFE2E8F0)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.Share,
                                            contentDescription = null,
                                            tint = if (isPublic) Color(0xFF6366F1) else Color(0xFF64748B),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Public",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = if (isPublic) Color(0xFF6366F1) else Color(0xFF64748B)
                                        )
                                        Text(
                                            text = "Share with community",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                // Private option
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { isPublic = false },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (!isPublic) Color(0xFF64748B).copy(alpha = 0.1f) else Color.White
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (!isPublic) Color(0xFF64748B) else Color(0xFFE2E8F0)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = if (!isPublic) Color(0xFF64748B) else Color(0xFF64748B),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Private",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = if (!isPublic) Color(0xFF64748B) else Color(0xFF64748B)
                                        )
                                        Text(
                                            text = "Personal only",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF64748B)
                    )
                ) {
                    Text(
                        "Cancel",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                val isFormValid = selectedBook != null &&
                        logText.isNotBlank() &&
                        (logType != "Review" || rating > 0)

                Button(
                    onClick = {
                        selectedBook?.let { book ->
                            onSaveClick(
                                book.id,
                                book.title,
                                book.author,
                                logText,
                                logType,
                                rating,
                                isPublic
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6366F1),
                        contentColor = Color.White
                    ),
                    enabled = isFormValid
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isPublic) "Share Log" else "Save Log",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Book Selection Dialog
    if (showBookSelector) {
        AlertDialog(
            onDismissRequest = { showBookSelector = false },
            title = {
                Text(
                    "Select Book",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                LazyColumn {
                    items(readingBooks) { book ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedBook = book
                                    showBookSelector = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedBook?.id == book.id)
                                    Color(0xFF6366F1).copy(alpha = 0.1f) else Color.White
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedBook?.id == book.id) Color(0xFF6366F1) else Color(0xFFE2E8F0)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = book.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "by ${book.author}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "${(book.progress * 100).toInt()}% complete",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF6366F1)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBookSelector = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddLogScreen() {
    MaterialTheme {
        AddLogScreen()
    }
}