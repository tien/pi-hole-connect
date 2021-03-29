package com.tien.piholeconnect.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.tien.piholeconnect.R

sealed class Screen(val route: String, @StringRes val labelResourceId: Int) {
    object Home : Screen("home", R.string.label_home)
    object Statistics : Screen("statistics", R.string.label_statistics)
    object Log : Screen("log", R.string.label_log)
    object FilterRules : Screen("filterRules", R.string.label_filter_rules)
    object Preferences : Screen("preferences", R.string.label_preferences)
    object PiHoleConnection : Screen("piHoleConnection", R.string.label_pi_hole_connection)
}

fun screenForRoute(route: String) =
    Screen::class.sealedSubclasses.first {
        it.objectInstance!!.route == route.split('?')[0]
    }.objectInstance!!

data class BottomTabItem(val screen: Screen, val icon: ImageVector)

data class TopBarOptionsMenuItem(val key: String, @StringRes val labelResourceId: Int)