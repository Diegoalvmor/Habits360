package com.example.habits360.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.habits360.features.goals.GoalsScreen
import com.example.habits360.features.habits.HabitsScreen
import com.example.habits360.features.progress.ProgressScreen
import com.example.habits360.features.settings.SettingsScreen
import com.example.habits360.features.stadistics.StatsScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = BottomNavItem.Habits.route, modifier = modifier) {
        composable(BottomNavItem.Habits.route) { HabitsScreen() }
        composable(BottomNavItem.Goals.route) { GoalsScreen() }
        composable(BottomNavItem.Progress.route) { ProgressScreen() }
        composable(BottomNavItem.Stats.route) { StatsScreen() }
        composable(BottomNavItem.Settings.route) { SettingsScreen(navController = navController) }
    }
}
