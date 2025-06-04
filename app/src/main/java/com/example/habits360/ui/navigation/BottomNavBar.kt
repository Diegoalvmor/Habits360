package com.example.habits360.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Habits,
        BottomNavItem.Goals,
        BottomNavItem.Progress,
        BottomNavItem.Stats,
        BottomNavItem.Settings

    )
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
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

sealed class BottomNavItem(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String) {
    object Habits : BottomNavItem("habits", Icons.Default.CheckCircle, "Hábitos")
    object Goals : BottomNavItem("goals", Icons.Default.FitnessCenter, "Objetivos")
    object Progress : BottomNavItem("progress", Icons.Default.SelfImprovement, "Informe")
    object Stats : BottomNavItem("stadistics", Icons.Default.Leaderboard , "Progreso")
    object Settings : BottomNavItem("settings", Icons.Default.Settings, "Ajustes")
}
