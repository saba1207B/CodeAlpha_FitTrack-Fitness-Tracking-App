package com.example.fittrack.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    data object AddWorkout : Screen("add_workout", "Add Workout", Icons.Default.AddCircle)
    data object History : Screen("history", "History", Icons.Default.History)
    data object Statistics : Screen("statistics", "Statistics", Icons.Default.BarChart)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    companion object {
        val bottomNavItems = listOf(
            Dashboard,
            AddWorkout,
            History,
            Statistics,
            Settings
        )
    }
}
