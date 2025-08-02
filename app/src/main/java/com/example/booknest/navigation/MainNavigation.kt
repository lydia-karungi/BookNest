package com.example.booknest.navigation

import android.widget.Toast
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import kotlinx.coroutines.launch
import com.example.booknest.screens.AddEditBookScreen
import com.example.booknest.screens.AddLogScreen
import com.example.booknest.screens.GoalsScreen
import com.example.booknest.screens.LibraryScreen
import com.example.booknest.screens.BookSearchScreen
import com.example.booknest.screens.PDFReadingScreen
import com.example.booknest.screens.LoginScreen
import com.example.booknest.screens.SignUpScreen
import com.example.booknest.screens.AccountScreen
import com.example.booknest.screens.ReadingLog
import com.example.booknest.screens.LogType
import com.example.booknest.data.entity.Book
import com.example.booknest.data.database.BookNestDatabase
import com.example.booknest.data.repository.BookRepository
import com.example.booknest.data.network.NetworkModule
import com.example.booknest.viewmodel.LibraryViewModel
import com.example.booknest.viewmodel.ViewModelFactory
import com.example.booknest.screens.StatsScreen

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Library : BottomNavItem("library", "Library", Icons.Filled.Home)
    object Goals : BottomNavItem("goals", "Goals", Icons.Filled.Star)
    object Stats : BottomNavItem("stats", "Stats", Icons.Filled.Info)
    object Store : BottomNavItem("store", "Store", Icons.Filled.Search)
    object AddBook : BottomNavItem("addBook", "Add Book", Icons.Filled.Add)
}

// Additional routes that aren't in bottom navigation
object Routes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val ACCOUNT = "account"
    const val EDIT_BOOK = "edit_book"
    const val QUOTE_LOG = "quoteLog"
    const val EDIT_LOG = "edit_log"
    const val READING = "reading"
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    // Authentication state - in real app, use a proper authentication manager
    var isLoggedIn by remember { mutableStateOf(false) }

    // Create shared ViewModel for book operations
    val context = LocalContext.current
    val database = remember { BookNestDatabase.getDatabase(context) }
    val repository = remember {
        BookRepository(database.bookDao(), database.readingLogDao(), NetworkModule.bookApiService)
    }
    val sharedViewModel: LibraryViewModel = viewModel(
        factory = ViewModelFactory(repository)
    )

    // Determine start destination based on authentication state
    val startDestination = if (isLoggedIn) BottomNavItem.Library.route else Routes.LOGIN

    Scaffold(
        bottomBar = {
            // Only show bottom navigation if user is logged in
            if (isLoggedIn) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(if (isLoggedIn) innerPadding else PaddingValues(0.dp))
        ) {
            // Authentication Routes
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        isLoggedIn = true
                        navController.navigate(BottomNavItem.Library.route) {
                            // Clear login screen from back stack
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp = {
                        navController.navigate(Routes.SIGNUP)
                    }
                )
            }

            composable(Routes.SIGNUP) {
                SignUpScreen(
                    onSignUpSuccess = {
                        isLoggedIn = true
                        navController.navigate(BottomNavItem.Library.route) {
                            // Clear authentication screens from back stack
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.ACCOUNT) {
                AccountScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        isLoggedIn = false
                        navController.navigate(Routes.LOGIN) {
                            // Clear all screens from back stack
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Main App Routes (only accessible when logged in)
            composable(BottomNavItem.Library.route) {
                LibraryScreen(
                    onAddBookClick = {
                        navController.navigate(BottomNavItem.AddBook.route)
                    },
                    onEditBookClick = { book ->
                        // Store the book in the ViewModel temporarily for editing
                        sharedViewModel.setBookToEdit(book)
                        navController.navigate(Routes.EDIT_BOOK)
                    },
                    onSearchClick = {
                        // Navigate to Store tab for online book search
                        navController.navigate(BottomNavItem.Store.route)
                    },
                    onReadBookClick = { book ->
                        // Navigate to reading screen
                        navController.navigate("${Routes.READING}/${book.id}/${book.title}")
                    },
                    onAccountClick = {  // Add this new parameter
                        navController.navigate(Routes.ACCOUNT)
                    }
                )
            }

            // Reading Screen
            composable(
                route = "${Routes.READING}/{bookId}/{bookTitle}",
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType },
                    navArgument("bookTitle") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val bookIdString = backStackEntry.arguments?.getString("bookId") ?: ""
                val bookTitle = backStackEntry.arguments?.getString("bookTitle") ?: "Book"

                PDFReadingScreen(
                    bookId = bookIdString,
                    bookTitle = bookTitle,
                    pdfAssetPath = "sample.pdf", // For testing with sample PDF
                    onBackClick = {
                        navController.popBackStack() // Return to library
                    },
                    onProgressUpdate = { progress, currentPage, totalPages ->
                        // Pass the string ID directly to the ViewModel
                        sharedViewModel.updateBookProgressByPages(bookIdString, currentPage, totalPages)
                    }
                )
            }

            composable(BottomNavItem.Goals.route) {
                // Add these for the working functionality
                val context = LocalContext.current
                var showDeleteDialog by remember { mutableStateOf<String?>(null) }

                GoalsScreen(
                    onAddLogClick = {
                        navController.navigate(Routes.QUOTE_LOG)
                    },
                    onEditLogClick = { readingLog ->
                        // Show toast for now (you can add edit navigation later)
                        Toast.makeText(context, "Edit: ${readingLog.bookTitle}", Toast.LENGTH_SHORT).show()
                    },
                    onDeleteLog = { logId ->
                        // Show confirmation dialog
                        showDeleteDialog = logId
                    },
                    onLikeLog = { logId ->
                        // Show feedback to user
                        Toast.makeText(context, "Liked log!", Toast.LENGTH_SHORT).show()
                    },
                    onShareLog = { readingLog ->
                        // Actually share the log
                        shareReadingLog(context, readingLog)
                    }
                )

                // Add the delete confirmation dialog
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
                                Text("Delete", color = MaterialTheme.colorScheme.error)
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

            composable(Routes.QUOTE_LOG) {
                // Create the coroutine scope at the composable level
                val scope = rememberCoroutineScope()

                AddLogScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveClick = { bookId, bookTitle, author, logText, logType, rating, isPublic ->
                        // Use the scope created at the composable level
                        scope.launch {
                            try {
                                // Save the reading log to database using the repository
                                val logId = repository.insertReadingLog(
                                    bookId = bookId,
                                    bookTitle = bookTitle,
                                    author = author,
                                    logText = logText,
                                    logType = logType,
                                    rating = rating,
                                    isPublic = isPublic
                                )

                                // Log saved successfully
                                println("✅ Reading log saved successfully with ID: $logId")

                                // Navigate back to goals screen
                                navController.popBackStack()

                            } catch (e: Exception) {
                                // Handle any database errors
                                println("❌ Error saving reading log: ${e.message}")
                                e.printStackTrace()

                                // You could show a Toast or Snackbar here for user feedback
                                // For now, still navigate back
                                navController.popBackStack()
                            }
                        }
                    }
                )
            }

            composable(BottomNavItem.Stats.route) {
                StatsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(BottomNavItem.Store.route) {
                BookSearchScreen()
            }

            // Add Book Screen (for new books)
            composable(BottomNavItem.AddBook.route) {
                AddEditBookScreen(
                    bookToEdit = null, // null means we're adding a new book
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveClick = { title, author, pages, category, rating ->
                        // The save logic is handled inside AddEditBookScreen
                        navController.popBackStack() // Go back after saving
                    }
                )
            }

            // Edit Book Screen (for existing books)
            composable(Routes.EDIT_BOOK) {
                val bookToEdit by sharedViewModel.bookToEdit.collectAsState()

                AddEditBookScreen(
                    bookToEdit = bookToEdit, // Pass the book to edit
                    onBackClick = {
                        sharedViewModel.clearBookToEdit() // Clear the stored book
                        navController.popBackStack()
                    },
                    onSaveClick = { title, author, pages, category, rating ->
                        // The update logic is handled inside AddEditBookScreen
                        sharedViewModel.clearBookToEdit() // Clear the stored book
                        navController.popBackStack() // Go back after saving
                    }
                )
            }
        }
    }
}

// Share functionality
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
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Library,
        BottomNavItem.Goals,
        BottomNavItem.Stats,
        BottomNavItem.Store
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainNavigationPreview() {
    MaterialTheme {
        MainNavigation()
    }
}