package com.example.booknest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.*

data class ReadingGoal(
    val year: Int,
    val targetBooks: Int,
    val completedBooks: Int,
    val currentStreak: Int
)

data class ReadingLog(
    val id: String,
    val bookTitle: String,
    val author: String,
    val note: String,
    val rating: Float,
    val date: String,
    val logType: LogType
)

enum class LogType {
    THOUGHT, REVIEW, QUOTE, PROGRESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onAddLogClick: () -> Unit = {}
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val readingGoal = ReadingGoal(
        year = currentYear,
        targetBooks = 24,
        completedBooks = 8,
        currentStreak = 15
    )

    val recentLogs = listOf(
        ReadingLog(
            id = "1",
            bookTitle = "The Midnight Library",
            author = "Matt Haig",
            note = "This book really made me think about life choices and regrets. The concept of infinite possibilities is fascinating.",
            rating = 4.8f,
            date = "2024-01-15",
            logType = LogType.THOUGHT
        ),
        ReadingLog(
            id = "2",
            bookTitle = "Educated",
            author = "Tara Westover",
            note = "A powerful memoir about education and family. The writing is incredible and the story is both heartbreaking and inspiring.",
            rating = 4.9f,
            date = "2024-01-10",
            logType = LogType.REVIEW
        ),
        ReadingLog(
            id = "3",
            bookTitle = "Where the Crawdads Sing",
            author = "Delia Owens",
            note = "\"Sometimes she heard night-sounds she didn't know or jumped from lightning too close, but whenever she turned on the lamp and saw all her books lined up on the shelf, she felt safe again.\"",
            rating = 4.6f,
            date = "2024-01-08",
            logType = LogType.QUOTE
        )
    )

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
                        text = "Track your progress and thoughts",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Reading Goal Card
            item {
                ReadingGoalCard(readingGoal = readingGoal)
            }

            // Stats Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatsCard(
                        title = "Reading Streak",
                        value = "${readingGoal.currentStreak}",
                        subtitle = "days",
                        icon = Icons.Default.Info,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )

                    StatsCard(
                        title = "Avg Rating",
                        value = "4.8",
                        subtitle = "stars",
                        icon = Icons.Default.Star,
                        color = Color(0xFFFBBF24),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Reading Journal Section Header
            item {
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
                            text = "Your thoughts and reflections",
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
            }

            // Recent Logs
            items(recentLogs) { log ->
                ReadingLogCard(log = log)
            }

            // Empty state or show more button
            if (recentLogs.isEmpty()) {
                item {
                    EmptyLogState(onAddLogClick = onAddLogClick)
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
fun ReadingGoalCard(readingGoal: ReadingGoal) {
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
                        text = "${readingGoal.year} Reading Goal",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "${readingGoal.completedBooks} of ${readingGoal.targetBooks} books",
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

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Books read",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
                val remaining = readingGoal.targetBooks - readingGoal.completedBooks
                Text(
                    text = "$remaining books to go",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF6366F1)
                )
            }
        }
    }
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
fun ReadingLogCard(log: ReadingLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = log.bookTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "by ${log.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
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
                            text = log.logType.name,
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

                    Spacer(modifier = Modifier.height(4.dp))

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
                }
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
                    text = log.rating.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF1E293B)
                )
            }
        }
    }
}

@Composable
fun EmptyLogState(onAddLogClick: () -> Unit) {
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
                text = "Start Your Reading Journal",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF1E293B)
            )

            Text(
                text = "Log your thoughts, quotes, and reviews",
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
                Text("Add First Log")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGoalsScreen() {
    MaterialTheme {
        GoalsScreen()
    }
}