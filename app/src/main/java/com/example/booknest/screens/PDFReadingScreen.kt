package com.example.booknest.screens

import android.net.Uri
import android.speech.tts.TextToSpeech
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PDFReadingScreen(
    bookId: String,
    bookTitle: String,
    pdfUri: Uri? = null, // URI to the PDF file
    pdfAssetPath: String? = "sample.pdf", // Default to sample.pdf for testing
    onBackClick: () -> Unit,
    onProgressUpdate: (Float, Int, Int) -> Unit = { _, _, _ -> }
) {
    var isReadingMode by remember { mutableStateOf(false) }
    var readingTimeMinutes by remember { mutableStateOf(0) }
    var sessionStartTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var isLoading by remember { mutableStateOf(false) }

    // Audio states
    var isAudioPlaying by remember { mutableStateOf(false) }
    var isAudioInitialized by remember { mutableStateOf(false) }
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }

    val context = LocalContext.current

    // Full text content for TTS
    val fullBookText = """
Chapter 1: The Call to Hope

In the quiet moments before dawn, when the world holds its breath and the first light begins to creep across the horizon, there exists a profound truth that has echoed through generations: the poor will indeed be glad.

This is not merely a promise whispered in the wind, but a declaration that resonates through the chambers of every hopeful heart. It speaks to those who have known the weight of empty pockets, the ache of unfulfilled dreams, and the long nights when tomorrow seemed uncertain.

Yet in this uncertainty, there is a strange and beautiful certainty. For every person who has walked the difficult path, who has known what it means to go without, carries within them a capacity for joy that runs deeper than any material comfort could provide.

The morning sun painted golden streaks across Sarah's small apartment as she prepared for another day. The walls were bare, save for a single photograph of her grandmother, and the furniture consisted of little more than necessities. But as she stood by the window, watching the city slowly come to life below, something stirred within her that no amount of wealth could purchase.

It was hope.

Not the fragile hope that depends on circumstances, but the deep, abiding hope that springs from a well that never runs dry. This hope had carried her through the darkest nights and the longest winters. It had whispered to her when creditors called and when the refrigerator stood empty. It had been her companion when friends seemed scarce and opportunities felt like mirages in a desert of struggle.

Down the street, Marcus finished his third job of the day. His hands were calloused, his back ached, and his shoes had holes that let in the morning dew. Yet as he walked home, he found himself humming a melody his mother used to sing—a melody that spoke of better days ahead, of seasons that change, and of the unshakeable belief that this story was far from over.

The poor will be glad, she used to say, not because their circumstances will necessarily change overnight, but because they understand something that often eludes those who have never known want: they understand that true wealth isn't measured in bank accounts or possessions, but in the richness of the human spirit and the bonds that connect us all.

Chapter 2: The Strength in Struggle

There is a particular kind of strength that emerges only through struggle. It cannot be taught in classrooms or purchased in stores. It cannot be inherited or borrowed. It must be earned through the slow, patient work of facing each day with courage, even when courage feels impossible to find.

This strength belongs to those who have learned to make miracles from nothing, who have discovered that happiness doesn't require a full pantry or a full wallet, but rather a full heart and an open spirit. It belongs to those who have found ways to celebrate small victories and create beauty in unlikely places.
""".trimIndent()

    // Initialize TextToSpeech
    LaunchedEffect(Unit) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.getDefault()
                isAudioInitialized = true
            }
        }
    }

    // Cleanup TextToSpeech
    DisposableEffect(Unit) {
        onDispose {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }

    // Reading timer
    LaunchedEffect(isReadingMode) {
        if (isReadingMode) {
            sessionStartTime = System.currentTimeMillis()
            while (isReadingMode) {
                delay(60000) // Update every minute
                readingTimeMinutes = ((System.currentTimeMillis() - sessionStartTime) / 60000).toInt()
            }
        }
    }

    // Audio control functions
    fun startAudio() {
        if (isAudioInitialized && textToSpeech != null) {
            textToSpeech?.speak(fullBookText, TextToSpeech.QUEUE_FLUSH, null, null)
            isAudioPlaying = true
            isReadingMode = true
        }
    }

    fun pauseAudio() {
        textToSpeech?.stop()
        isAudioPlaying = false
    }

    fun stopAudio() {
        textToSpeech?.stop()
        isAudioPlaying = false
        isReadingMode = false
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = bookTitle,
                    maxLines = 1
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                // Audio control button
                IconButton(
                    onClick = {
                        if (isAudioPlaying) {
                            pauseAudio()
                        } else {
                            startAudio()
                        }
                    },
                    enabled = isAudioInitialized
                ) {
                    Icon(
                        if (isAudioPlaying) Icons.Filled.Add else Icons.Filled.PlayArrow,
                        contentDescription = if (isAudioPlaying) "Pause Audio" else "Play Audio",
                        tint = if (isAudioPlaying) Color(0xFF10B981) else Color(0xFF6366F1)
                    )
                }

                // Stop audio button
                if (isAudioPlaying) {
                    IconButton(onClick = { stopAudio() }) {
                        Icon(
                            Icons.Filled.Clear,
                            contentDescription = "Stop Audio",
                            tint = Color(0xFFDC2626)
                        )
                    }
                }

                // Bookmark current page
                IconButton(onClick = {
                    // TODO: Save bookmark for current page
                }) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Bookmark Page"
                    )
                }
            }
        )

        // Loading indicator (only if actually loading)
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF6366F1),
                trackColor = Color(0xFFE2E8F0)
            )
        }

        // Reading session info
        if (isReadingMode) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = if (isAudioPlaying) "🔊 Audio Playing" else "📖 Reading Active",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "⏱️ ${readingTimeMinutes}m",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "📄 Reading $bookTitle",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // PDF Content Area - Scrollable Text Reader
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Scrollable text content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Chapter header
                    Text(
                        text = "Chapter 1",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1f2937),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "The Call to Hope",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF6366f1),
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                }

                item {
                    Text(
                        text = """In the quiet moments before dawn, when the world holds its breath and the first light begins to creep across the horizon, there exists a profound truth that has echoed through generations: the poor will indeed be glad.
                        
This is not merely a promise whispered in the wind, but a declaration that resonates through the chambers of every hopeful heart. It speaks to those who have known the weight of empty pockets, the ache of unfulfilled dreams, and the long nights when tomorrow seemed uncertain.

Yet in this uncertainty, there is a strange and beautiful certainty. For every person who has walked the difficult path, who has known what it means to go without, carries within them a capacity for joy that runs deeper than any material comfort could provide.""",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 28.sp
                        ),
                        color = Color(0xFF374151),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Justify
                    )
                }

                item {
                    Text(
                        text = """The morning sun painted golden streaks across Sarah's small apartment as she prepared for another day. The walls were bare, save for a single photograph of her grandmother, and the furniture consisted of little more than necessities. But as she stood by the window, watching the city slowly come to life below, something stirred within her that no amount of wealth could purchase.

It was hope.

Not the fragile hope that depends on circumstances, but the deep, abiding hope that springs from a well that never runs dry. This hope had carried her through the darkest nights and the longest winters. It had whispered to her when creditors called and when the refrigerator stood empty. It had been her companion when friends seemed scarce and opportunities felt like mirages in a desert of struggle.""",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 28.sp
                        ),
                        color = Color(0xFF374151),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Justify
                    )
                }

                item {
                    Text(
                        text = """Down the street, Marcus finished his third job of the day. His hands were calloused, his back ached, and his shoes had holes that let in the morning dew. Yet as he walked home, he found himself humming a melody his mother used to sing—a melody that spoke of better days ahead, of seasons that change, and of the unshakeable belief that this story was far from over.

The poor will be glad, she used to say, not because their circumstances will necessarily change overnight, but because they understand something that often eludes those who have never known want: they understand that true wealth isn't measured in bank accounts or possessions, but in the richness of the human spirit and the bonds that connect us all.""",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 28.sp
                        ),
                        color = Color(0xFF374151),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Justify
                    )
                }

                item {
                    // Pull quote
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFf8fafc)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            text = "\"True wealth isn't measured in bank accounts or possessions, but in the richness of the human spirit and the bonds that connect us all.\"",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                lineHeight = 26.sp
                            ),
                            color = Color(0xFF6366f1),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }

                item {
                    Text(
                        text = """In communities across the world, this truth plays out daily. In the grandmother who shares her last bowl of rice with a neighbor. In the father who works two jobs to keep his children in school, never complaining, always believing. In the mother who patches clothes with such care that they become works of art, each stitch a testament to love that cannot be purchased.

These are the stories that matter. These are the lives that teach us what it truly means to be rich. For in their struggle, they have found something that money cannot buy: the knowledge that they are stronger than their circumstances, more valuable than their bank statements, and capable of creating joy from the simplest of materials.""",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 28.sp
                        ),
                        color = Color(0xFF374151),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Justify
                    )
                }

                item {
                    Text(
                        text = """The gladness of the poor is not naive optimism or blind hope. It is not the denial of real struggles or the minimization of genuine hardship. Rather, it is the profound recognition that every sunrise brings new possibilities, that every act of kindness creates ripples that extend far beyond what the eye can see, and that every person, regardless of their material circumstances, carries within them infinite worth and unlimited potential.""",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 28.sp
                        ),
                        color = Color(0xFF374151),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Justify
                    )
                }

                item {
                    Text(
                        text = "Chapter 2",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1f2937),
                        modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
                    )

                    Text(
                        text = "The Strength in Struggle",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF6366f1),
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                }

                item {
                    Text(
                        text = """There is a particular kind of strength that emerges only through struggle. It cannot be taught in classrooms or purchased in stores. It cannot be inherited or borrowed. It must be earned through the slow, patient work of facing each day with courage, even when courage feels impossible to find.

This strength belongs to those who have learned to make miracles from nothing, who have discovered that happiness doesn't require a full pantry or a full wallet, but rather a full heart and an open spirit. It belongs to those who have found ways to celebrate small victories and create beauty in unlikely places.""",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 28.sp
                        ),
                        color = Color(0xFF374151),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Justify
                    )
                }

                // Continue reading indicator
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF6366f1).copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "📖",
                                fontSize = 32.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Keep reading to discover more...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF6366f1),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Your reading session is being tracked",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748b),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Reading status (bottom)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        isLoading -> "Loading PDF..."
                        pdfUri != null || pdfAssetPath != null -> "PDF Ready"
                        else -> "No PDF Available"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )

                Text(
                    text = when {
                        isAudioPlaying -> "🔊 Audio Playing"
                        isReadingMode -> "📖 Reading"
                        else -> "⏸️ Stopped"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        isAudioPlaying -> Color(0xFF6366F1)
                        isReadingMode -> Color(0xFF10B981)
                        else -> Color(0xFF64748B)
                    }
                )
            }
        }
    }
}