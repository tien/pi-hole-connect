package com.tien.piholeconnect.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.tien.piholeconnect.R

sealed class Screen(val route: String, @StringRes val labelResourceId: Int) {
    object Home : Screen("home", R.string.label_home)
    object Statistics : Screen("statistics", R.string.label_statistics)
    object Log : Screen("log", R.string.label_log)
    object FilterRules : Screen("filterRules", R.string.label_lists)
    object Settings : Screen("settings", R.string.label_settings)
}

fun screenForRoute(route: String) = when (route) {
    Screen.Home.route -> Screen.Home
    Screen.Statistics.route -> Screen.Statistics
    Screen.Log.route -> Screen.Log
    Screen.FilterRules.route -> Screen.FilterRules
    Screen.Settings.route -> Screen.Settings
    else -> null
}

data class BottomTabItem(val screen: Screen, val icon: ImageVector)