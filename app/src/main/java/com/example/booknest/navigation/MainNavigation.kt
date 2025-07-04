package com.example.booknest.navigation

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.booknest.screens.AddBookScreen
import com.example.booknest.screens.AddLogScreen
import com.example.booknest.screens.GoalsScreen
import com.example.booknest.screens.LibraryScreen

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Library : BottomNavItem("library", "Library", Icons.Filled.Home)
    object Goals : BottomNavItem("goals", "Goals", Icons.Filled.Star)
    object Stats : BottomNavItem("stats", "Stats", Icons.Filled.Info)
    object Store : BottomNavItem("store", "Store", Icons.Filled.Search)
    object AddBook : BottomNavItem("addBook", "Add Book", Icons.Filled.Add)
}

// Additional routes that aren't in bottom navigation
object Routes {
    const val EDIT_BOOK = "edit_book"
    const val QUOTE_LOG = "quoteLog"
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Library.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Library.route) {
                LibraryScreen(
                    onAddBookClick = {
                        navController.navigate(BottomNavItem.AddBook.route)
                    },
                    onEditBookClick = { book ->
                        // Navigate to edit book screen
                        // You can pass book data through navigation arguments or use a shared state
                        navController.navigate(Routes.EDIT_BOOK)
                    }
                )
            }

            composable(BottomNavItem.Goals.route) {
                GoalsScreen(
                    onAddLogClick = {
                        navController.navigate(Routes.QUOTE_LOG)
                    }
                )
            }

            composable(Routes.QUOTE_LOG) {
                AddLogScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveClick = { bookTitle, author, logText, logType, rating ->
                        // Handle saving the log here
                        // You can save to database or shared state
                        navController.popBackStack() // Go back after saving
                    }
                )
            }

            composable(BottomNavItem.Stats.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Stats Coming Soon", style = MaterialTheme.typography.headlineMedium)
                }
            }

            composable(BottomNavItem.Store.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Store Coming Soon", style = MaterialTheme.typography.headlineMedium)
                }
            }

            composable(BottomNavItem.AddBook.route) {
                AddBookScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveClick = { title, author, pages, category, rating ->
                        // Handle saving the new book here
                        // You can save to database or shared state
                        navController.popBackStack() // Go back after saving
                    }
                )
            }

            // Edit Book Screen (reuses AddBookScreen with different parameters)
            composable(Routes.EDIT_BOOK) {
                AddBookScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveClick = { title, author, pages, category, rating ->
                        // Handle updating the existing book here
                        // You can update database or shared state
                        navController.popBackStack() // Go back after saving
                    }
                )
            }
        }
    }
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