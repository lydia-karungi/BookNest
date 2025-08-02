package com.example.booknest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.booknest.data.database.BookNestDatabase
import com.example.booknest.data.entity.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBookScreen(
    bookToEdit: Book? = null, // Pass the book when editing
    onBackClick: () -> Unit = {},
    onSaveClick: (title: String, author: String, pages: String, category: String, rating: Float) -> Unit = { _, _, _, _, _ -> }
) {
    // Get database instance
    val context = LocalContext.current
    val database = remember { BookNestDatabase.getDatabase(context) }
    val bookDao = database.bookDao()
    val scope = rememberCoroutineScope()

    // Determine if we're editing or adding
    val isEditing = bookToEdit != null

    // Pre-fill form fields if editing
    var title by remember { mutableStateOf(bookToEdit?.title ?: "") }
    var author by remember { mutableStateOf(bookToEdit?.author ?: "") }
    var pages by remember { mutableStateOf(bookToEdit?.pageCount?.toString() ?: "") }
    var category by remember { mutableStateOf(bookToEdit?.category ?: "Fiction") }
    var rating by remember { mutableStateOf(bookToEdit?.rating ?: 0f) }
    var expanded by remember { mutableStateOf(false) }

    val categories = listOf("Fiction", "Non-Fiction", "Biography", "Science", "History", "Romance", "Mystery", "Fantasy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Book" else "Add New Book",
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
                                        if (isEditing) Color(0xFF10B981) else Color(0xFF6366F1),
                                        if (isEditing) Color(0xFF059669) else Color(0xFF8B5CF6)
                                    )
                                ),
                                RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isEditing) Icons.Default.Edit else Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isEditing) "Edit Book Details" else "Create Your Book Entry",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1E293B)
                    )

                    Text(
                        text = if (isEditing)
                            "Update the information for \"${bookToEdit?.title}\""
                        else
                            "Fill in the details below to add a new book to your library",
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
                        text = "Book Information",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1E293B)
                    )

                    // Book Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Book Title *") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFF6366F1)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFF6366F1),
                            unfocusedLabelColor = Color(0xFF64748B),
                            focusedLabelColor = Color(0xFF6366F1)
                        )
                    )

                    // Author
                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Author *") },
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
                            unfocusedLabelColor = Color(0xFF64748B),
                            focusedLabelColor = Color(0xFF6366F1)
                        )
                    )

                    // Page Count
                    OutlinedTextField(
                        value = pages,
                        onValueChange = { pages = it },
                        label = { Text("Page Count") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF6366F1)
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFF6366F1),
                            unfocusedLabelColor = Color(0xFF64748B),
                            focusedLabelColor = Color(0xFF6366F1)
                        )
                    )

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Category") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = Color(0xFF6366F1)
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedLabelColor = Color(0xFF64748B),
                                focusedLabelColor = Color(0xFF6366F1)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        category = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Rating Section
                    Column {
                        Text(
                            text = "Rating",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(5) { index ->
                                IconButton(
                                    onClick = { rating = (index + 1).toFloat() }
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
                            }
                        }
                    }

                    // Show current status if editing
                    if (isEditing && bookToEdit != null) {
                        Column {
                            Text(
                                text = "Current Status",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color(0xFF64748B)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = when (bookToEdit.status) {
                                        "Reading" -> Color(0xFF6366F1).copy(alpha = 0.1f)
                                        "Finished" -> Color(0xFF10B981).copy(alpha = 0.1f)
                                        else -> Color(0xFF64748B).copy(alpha = 0.1f)
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = bookToEdit.status,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = when (bookToEdit.status) {
                                            "Reading" -> Color(0xFF6366F1)
                                            "Finished" -> Color(0xFF10B981)
                                            else -> Color(0xFF64748B)
                                        }
                                    )

                                    if (bookToEdit.progress > 0) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "• ${(bookToEdit.progress * 100).toInt()}% complete",
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
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFE2E8F0), Color(0xFFE2E8F0))
                        )
                    )
                ) {
                    Text(
                        "Cancel",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Button(
                    onClick = {
                        if (title.isNotBlank() && author.isNotBlank()) {
                            scope.launch {
                                if (isEditing && bookToEdit != null) {
                                    // Update existing book - keep the same ID and ID type
                                    val updatedBook = bookToEdit.copy(
                                        title = title,
                                        author = author,
                                        rating = rating,
                                        category = category,
                                        pageCount = pages.toIntOrNull() ?: bookToEdit.pageCount
                                    )
                                    bookDao.updateBook(updatedBook)
                                } else {
                                    // Save new book - let Room auto-generate the ID
                                    val newBook = Book(
                                        title = title,
                                        author = author,
                                        status = "Wishlist",
                                        progress = 0f,
                                        rating = rating,
                                        category = category,
                                        pageCount = pages.toIntOrNull() ?: 0
                                    )
                                    bookDao.insertBook(newBook)
                                }
                                // Go back after saving
                                onBackClick()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEditing) Color(0xFF10B981) else Color(0xFF6366F1),
                        contentColor = Color.White
                    ),
                    enabled = title.isNotBlank() && author.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isEditing) "Update Book" else "Save Book",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddBookScreen() {
    MaterialTheme {
        AddEditBookScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditBookScreen() {
    MaterialTheme {
        AddEditBookScreen(
            bookToEdit = Book(
                id = "1",  // Changed from 1 to "1"
                title = "Sample Book Title",
                author = "Sample Author",
                status = "Reading",
                progress = 0.45f,
                rating = 4f,
                category = "Fiction",
                pageCount = 250
            )
        )
    }
}