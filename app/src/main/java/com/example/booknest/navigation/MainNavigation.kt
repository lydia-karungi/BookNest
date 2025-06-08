package com.example.booknest.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import com.example.booknest.screens.LibraryScreen

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Library : BottomNavItem("library", "Library", Icons.Filled.Home)
    object Goals : BottomNavItem("goals", "Goals", Icons.Filled.Star)
    object Stats : BottomNavItem("stats", "Stats", Icons.Filled.Info)
    object Store : BottomNavItem("store", "Store", Icons.Filled.Search)
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
                com.example.booknest.screens.LibraryScreen()
            }
            composable(BottomNavItem.Goals.route) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Goals Coming Soon", style = MaterialTheme.typography.headlineMedium)
                }
            }
            composable(BottomNavItem.Stats.route) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Stats Coming Soon", style = MaterialTheme.typography.headlineMedium)
                }
            }
            composable(BottomNavItem.Store.route) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Store Coming Soon", style = MaterialTheme.typography.headlineMedium)
                }
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
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
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