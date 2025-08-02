package com.example.booknest.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.*

import java.util.*

data class ReadingGoal(
    val year: Int,
    val targetBooks: Int,
    val completedBooks: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val averageRating: Float,
    val totalPages: Int,
    val totalReadingTime: Int // in minutes
)

data class ReadingLog(
    val id: String,
    val bookId: String,
    val bookTitle: String,
    val author: String,
    val note: String,
    val rating: Float,
    val date: String,
    val logType: LogType,
    val isPublic: Boolean = true,
    val likes: Int = 0,
    val comments: Int = 0,
    val isLikedByUser: Boolean = false
)

enum class LogType(val displayName: String, val emoji: String) {
    THOUGHT("Thought", "💭"),
    REVIEW("Review", "⭐"),
    QUOTE("Quote", "📖"),
    PROGRESS("Progress", "📊")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onAddLogClick: () -> Unit = {
        // Default implementation - provide your own
    },
    onEditLogClick: (ReadingLog) -> Unit = { log ->
        // Default implementation - provide your own
    },
    onDeleteLog: (String) -> Unit = { logId ->
        // Default implementation - provide your own
    },
    onLikeLog: (String) -> Unit = { logId ->
        // Default implementation - provide your own
    },
    onShareLog: (ReadingLog) -> Unit = { log ->
        // Default implementation - provide your own
    }
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val readingGoal = ReadingGoal(
        year = currentYear,
        targetBooks = 24,
        completedBooks = 8,
        currentStreak = 15,
        longestStreak = 23,
        averageRating = 4.2f,
        totalPages = 2840,
        totalReadingTime = 3420 // 57 hours
    )

    val recentLogs = listOf(
        ReadingLog(
            id = "1",
            bookId = "book1",
            bookTitle = "The Midnight Library",
            author = "Matt Haig",
            note = "This book really made me think about life choices and regrets. The concept of infinite possibilities is fascinating and the way Haig explores the multiverse theory through personal growth is brilliant.",
            rating = 4.8f,
            date = "2024-01-15",
            logType = LogType.THOUGHT,
            isPublic = true,
            likes = 12,
            comments = 3,
            isLikedByUser = true
        ),
        ReadingLog(
            id = "2",
            bookId = "book2",
            bookTitle = "Educated",
            author = "Tara Westover",
            note = "A powerful memoir about education and family. The writing is incredible and the story is both heartbreaking and inspiring. Westover's journey from isolation to education is truly remarkable.",
            rating = 4.9f,
            date = "2024-01-10",
            logType = LogType.REVIEW,
            isPublic = true,
            likes = 8,
            comments = 5,
            isLikedByUser = false
        ),
        ReadingLog(
            id = "3",
            bookId = "book3",
            bookTitle = "Where the Crawdads Sing",
            author = "Delia Owens",
            note = "\"Sometimes she heard night-sounds she didn't know or jumped from lightning too close, but whenever she turned on the lamp and saw all her books lined up on the shelf, she felt safe again.\"",
            rating = 4.6f,
            date = "2024-01-08",
            logType = LogType.QUOTE,
            isPublic = false,
            likes = 0,
            comments = 0,
            isLikedByUser = false
        )
    )

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Public", "Private") + LogType.values().map { it.displayName }

    val filteredLogs = remember(recentLogs, selectedFilter) {
        when (selectedFilter) {
            "All" -> recentLogs
            "Public" -> recentLogs.filter { it.isPublic }
            "Private" -> recentLogs.filter { !it.isPublic }
            else -> recentLogs.filter { it.logType.displayName == selectedFilter }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddLogClick,
                containerColor = Color(0xFF6366F1),
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Reading Log",
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(20.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Reading Goals",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp
                            ),
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Track progress and share insights",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF64748B)
                        )
                    }

                    IconButton(
                        onClick = { /* Open goal settings */ }
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Goal Settings",
                            tint = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Reading Goal Card
            item {
                EnhancedReadingGoalCard(readingGoal = readingGoal)
            }

            // Enhanced Stats Cards
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatsCard(
                            title = "Current Streak",
                            value = "${readingGoal.currentStreak}",
                            subtitle = "days",
                            icon = Icons.Default.Star,
                            color = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )

                        StatsCard(
                            title = "Avg Rating",
                            value = "${readingGoal.averageRating}",
                            subtitle = "stars",
                            icon = Icons.Default.Star,
                            color = Color(0xFFFBBF24),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatsCard(
                            title = "Pages Read",
                            value = "${readingGoal.totalPages}",
                            subtitle = "pages",
                            icon = Icons.Default.Info,
                            color = Color(0xFF6366F1),
                            modifier = Modifier.weight(1f)
                        )

                        StatsCard(
                            title = "Reading Time",
                            value = "${readingGoal.totalReadingTime / 60}h",
                            subtitle = "hours",
                            icon = Icons.Default.DateRange,
                            color = Color(0xFF8B5CF6),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Reading Journal Section Header with Filters
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Reading Journal",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "${filteredLogs.size} ${if (selectedFilter == "All") "logs" else selectedFilter.lowercase() + " logs"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF64748B)
                            )
                        }

                        TextButton(
                            onClick = { /* Navigate to full journal */ }
                        ) {
                            Text(
                                "View All",
                                color = Color(0xFF6366F1)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filter Pills
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filters) { filter ->
                            FilterPill(
                                text = filter,
                                isSelected = selectedFilter == filter,
                                onClick = { selectedFilter = filter }
                            )
                        }
                    }
                }
            }

            // Reading Logs
            if (filteredLogs.isEmpty()) {
                item {
                    EmptyLogState(
                        onAddLogClick = onAddLogClick,
                        selectedFilter = selectedFilter
                    )
                }
            } else {
                items(filteredLogs) { log ->
                    EnhancedReadingLogCard(
                        log = log,
                        onEditClick = { onEditLogClick(log) },
                        onDeleteClick = { onDeleteLog(log.id) },
                        onLikeClick = { onLikeLog(log.id) },
                        onShareClick = { onShareLog(log) }
                    )
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
fun EnhancedReadingGoalCard(readingGoal: ReadingGoal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${readingGoal.year} Reading Challenge",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "${readingGoal.completedBooks} of ${readingGoal.targetBooks} books completed",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF64748B)
                    )
                }

                // Circular Progress
                Box(contentAlignment = Alignment.Center) {
                    val progress = readingGoal.completedBooks.toFloat() / readingGoal.targetBooks.toFloat()
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(80.dp),
                        color = Color(0xFF6366F1),
                        strokeWidth = 8.dp,
                        trackColor = Color(0xFFE2E8F0)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { readingGoal.completedBooks.toFloat() / readingGoal.targetBooks.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF6366F1),
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Goal insights
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    val remaining = readingGoal.targetBooks - readingGoal.completedBooks
                    Text(
                        text = "$remaining books to go",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color(0xFF6366F1)
                    )

                    val daysLeft = 365 - Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                    val booksPerMonth = if (daysLeft > 0) (remaining.toFloat() / (daysLeft / 30f)) else 0f
                    Text(
                        text = "${String.format("%.1f", booksPerMonth)} books/month needed",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Longest streak: ${readingGoal.longestStreak} days",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "You're ${if (readingGoal.completedBooks > readingGoal.targetBooks * 0.33) "ahead" else "behind"} schedule",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (readingGoal.completedBooks > readingGoal.targetBooks * 0.33) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterPill(
    text: String,
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
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = if (isSelected) Color.White else Color(0xFF64748B)
        )
    }
}

@Composable
fun EnhancedReadingLogCard(
    log: ReadingLog,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = log.bookTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF1E293B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        // Fixed Visibility indicator
                        Icon(
                            if (log.isPublic) Icons.Default.Share else Icons.Default.Lock,
                            contentDescription = if (log.isPublic) "Public" else "Private",
                            tint = if (log.isPublic) Color(0xFF10B981) else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "by ${log.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Log Type Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (log.logType) {
                            LogType.THOUGHT -> Color(0xFF6366F1).copy(alpha = 0.1f)
                            LogType.REVIEW -> Color(0xFF10B981).copy(alpha = 0.1f)
                            LogType.QUOTE -> Color(0xFFF59E0B).copy(alpha = 0.1f)
                            LogType.PROGRESS -> Color(0xFF8B5CF6).copy(alpha = 0.1f)
                        }
                    ) {
                        Text(
                            text = "${log.logType.emoji} ${log.logType.displayName}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = when (log.logType) {
                                LogType.THOUGHT -> Color(0xFF6366F1)
                                LogType.REVIEW -> Color(0xFF10B981)
                                LogType.QUOTE -> Color(0xFFF59E0B)
                                LogType.PROGRESS -> Color(0xFF8B5CF6)
                            }
                        )
                    }

                    // Menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    showMenu = false
                                    onEditClick()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                }
                            )
                            // Removed the condition - share is now available for all logs
                            DropdownMenuItem(
                                text = { Text("Share") },
                                onClick = {
                                    showMenu = false
                                    onShareClick()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Share, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = log.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Note/Quote
            Text(
                text = log.note,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF374151),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rating and Engagement Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating
                if (log.rating > 0) {
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
                            text = log.rating.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                // Engagement (only for public logs)
                if (log.isPublic) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Like button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onLikeClick() }
                        ) {
                            Icon(
                                if (log.isLikedByUser) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (log.isLikedByUser) Color(0xFFEF4444) else Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                            if (log.likes > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = log.likes.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        // Comments - Fixed icon
                        if (log.comments > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Comments",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = log.comments.toString(),
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
}

fun shareReadingLog(context: Context, log: ReadingLog) {
    val shareText = when (log.logType) {
        LogType.QUOTE -> "\"${log.note}\"\n\n- ${log.bookTitle} by ${log.author}\n\nShared from BookNest 📚"
        LogType.REVIEW -> "📚 ${log.bookTitle} by ${log.author}\n\n⭐ ${log.rating}/5\n\n${log.note}\n\nShared from BookNest"
        LogType.THOUGHT -> "💭 My thoughts on ${log.bookTitle}:\n\n${log.note}\n\nShared from BookNest 📚"
        LogType.PROGRESS -> "📊 Reading ${log.bookTitle}:\n\n${log.note}\n\nShared from BookNest 📚"
    }

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        putExtra(Intent.EXTRA_SUBJECT, "Check out: ${log.bookTitle}")
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share reading log"))
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF1E293B)
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun EmptyLogState(
    onAddLogClick: () -> Unit,
    selectedFilter: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Color(0xFF6366F1).copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (selectedFilter == "All")
                    "Start Your Reading Journal"
                else
                    "No $selectedFilter Logs Yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF1E293B)
            )

            Text(
                text = if (selectedFilter == "All")
                    "Share your thoughts, quotes, and reviews with the community"
                else
                    "Create your first ${selectedFilter.lowercase()} log",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddLogClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6366F1)
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Log")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGoalsScreen() {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    MaterialTheme {
        GoalsScreen(
            onAddLogClick = {
                Toast.makeText(context, "Navigate to add log", Toast.LENGTH_SHORT).show()
            },
            onEditLogClick = { log ->
                Toast.makeText(context, "Edit: ${log.bookTitle}", Toast.LENGTH_SHORT).show()
            },
            onShareLog = { log ->
                shareReadingLog(context, log)
            },
            onDeleteLog = { logId ->
                showDeleteDialog = logId
            },
            onLikeLog = { logId ->
                Toast.makeText(context, "Liked log!", Toast.LENGTH_SHORT).show()
            }
        )

        // Delete confirmation dialog
        showDeleteDialog?.let { logId ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Delete Log") },
                text = { Text("Are you sure you want to delete this reading log?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = null
                            Toast.makeText(context, "Log deleted", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}