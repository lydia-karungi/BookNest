package com.example.booknest.screens

import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PDFReadingScreen(
    bookId: String,
    bookTitle: String,
    pdfUri: Uri? = null, // URI to the PDF file
    pdfAssetPath: String? = null, // Path to PDF in assets (for testing)
    onBackClick: () -> Unit,
    onProgressUpdate: (Float, Int, Int) -> Unit = { _, _, _ -> }
) {
    var isReadingMode by remember { mutableStateOf(false) }
    var readingTimeMinutes by remember { mutableStateOf(0) }
    var sessionStartTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var isLoading by remember { mutableStateOf(pdfUri != null || pdfAssetPath != null) }

    val context = LocalContext.current

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
                // Reading timer toggle
                IconButton(
                    onClick = { isReadingMode = !isReadingMode }
                ) {
                    Icon(
                        if (isReadingMode) Icons.Filled.Add else Icons.Filled.PlayArrow,
                        contentDescription = if (isReadingMode) "Stop Reading" else "Start Reading Timer",
                        tint = if (isReadingMode) Color(0xFF10B981) else Color(0xFF64748B)
                    )
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

        // Loading indicator
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
                        text = "📖 Reading Active",
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

        // PDF Viewer using WebView
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (pdfUri != null || pdfAssetPath != null) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    errorCode: Int,
                                    description: String?,
                                    failingUrl: String?
                                ) {
                                    super.onReceivedError(view, errorCode, description, failingUrl)
                                    isLoading = false
                                }
                            }

                            settings.javaScriptEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            settings.setSupportZoom(true)
                            settings.builtInZoomControls = true
                            settings.displayZoomControls = false
                            settings.allowFileAccess = true
                            settings.allowContentAccess = true

                            // Load PDF
                            when {
                                pdfAssetPath != null -> {
                                    // For local assets, try direct loading first
                                    loadUrl("file:///android_asset/$pdfAssetPath")
                                }
                                pdfUri != null -> {
                                    // For external URIs, use Google Docs viewer
                                    val googleDocsUrl = "https://docs.google.com/gviewer?embedded=true&url=${pdfUri}"
                                    loadUrl(googleDocsUrl)
                                }
                                else -> {
                                    // Fallback
                                    loadUrl("file:///android_asset/sample.pdf")
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // No PDF available - show placeholder
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8FAFC)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📚",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "PDF Reader Ready",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No PDF file provided for this book.\n\nTo read books:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF94A3B8),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Column {
                            Text("• Upload PDF files", color = Color(0xFF64748B))
                            Text("• Download free books from Project Gutenberg", color = Color(0xFF64748B))
                            Text("• Connect with digital libraries", color = Color(0xFF64748B))
                            Text("• Import from your device storage", color = Color(0xFF64748B))
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                // TODO: Open file picker
                                // For now, let's set isLoading to false to show proper status
                                isLoading = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1)
                            )
                        ) {
                            Text("Upload PDF File")
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
                        pdfUri != null || pdfAssetPath != null -> "PDF Loaded"
                        else -> "No PDF Available"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )

                Text(
                    text = if (isReadingMode) "📖 Reading" else "⏹️ Stopped",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isReadingMode) Color(0xFF10B981) else Color(0xFF64748B)
                )
            }
        }
    }
}