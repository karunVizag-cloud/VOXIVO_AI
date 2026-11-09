package com.voxivo.ai.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Tasks : Screen("tasks", "Tasks", Icons.Default.CheckCircle)
    object Reports : Screen("reports", "Reports", Icons.Default.Insights)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)

    companion object {
        val bottomNavScreens = listOf(Home, Calendar, Tasks, Reports, Profile)
    }
}
