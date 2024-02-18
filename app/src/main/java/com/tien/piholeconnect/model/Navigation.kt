package com.tien.piholeconnect.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.tien.piholeconnect.R

data class ScreenOptions(
    val showTab: Boolean,
    val showTopAppBar: Boolean,
    val showMenus: Boolean,
    val showBackButton: Boolean
)

private val DefaultScreenOptions =
    ScreenOptions(showTab = false, showTopAppBar = true, showMenus = false, showBackButton = true)
private val TabScreenOptions =
    ScreenOptions(showTab = true, showTopAppBar = true, showMenus = true, showBackButton = false)

sealed class Screen(
    val route: String,
    @StringRes val labelResourceId: Int,
    val options: ScreenOptions
) {
    data object Home : Screen("home", R.string.label_home, TabScreenOptions)
    data object Statistics : Screen("statistics", R.string.label_statistics, TabScreenOptions)
    data object Log : Screen("log", R.string.label_log, TabScreenOptions.copy(showTopAppBar = false))
    data object FilterRules : Screen("filterRules", R.string.label_filter_rules, TabScreenOptions)
    data object Preferences : Screen("preferences", R.string.label_preferences, DefaultScreenOptions)
    data object PiHoleConnection :
        Screen("piHoleConnection", R.string.label_pi_hole_connection, DefaultScreenOptions)

    data object TipJar : Screen("tipJar", R.string.label_tip_jar, DefaultScreenOptions)
}

fun screenForRoute(route: String) =
    Screen::class.sealedSubclasses.first {
        it.objectInstance!!.route == route.split('?')[0]
    }.objectInstance!!

data class BottomTabItem(val screen: Screen, val icon: ImageVector)

data class TopBarOptionsMenuItem(
    val key: String,
    @StringRes val labelResourceId: Int,
    val icon: ImageVector,
    val isExternalLink: Boolean = false
)
