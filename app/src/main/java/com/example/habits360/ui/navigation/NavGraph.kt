package com.example.habits360.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.habits360.features.goals.GoalsScreen
import com.example.habits360.features.habits.HabitsScreen
import com.example.habits360.features.progress.ProgressScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = BottomNavItem.Habits.route, modifier = modifier) {
        composable(BottomNavItem.Habits.route) { HabitsScreen() }
        composable(BottomNavItem.Goals.route) { GoalsScreen() }
        composable(BottomNavItem.Progress.route) { ProgressScreen() }
    }
}
