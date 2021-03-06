package com.tien.piholeconnect

import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Analytics
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Insights
import androidx.compose.material.icons.twotone.Shield
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.tien.piholeconnect.extension.currentRouteAsState
import com.tien.piholeconnect.model.BottomTabItem
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.ui.component.BottomTab
import com.tien.piholeconnect.ui.component.TopBar
import com.tien.piholeconnect.ui.screen.Home
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme


@Composable
fun App() {
    val navController = rememberNavController()

    val tabItems = listOf(
        BottomTabItem(Screen.Home, Icons.TwoTone.Home),
        BottomTabItem(Screen.Statistics, Icons.TwoTone.Insights),
        BottomTabItem(Screen.Log, Icons.TwoTone.Analytics),
        BottomTabItem(Screen.FilterRules, Icons.TwoTone.Shield)
    )

    val currentRoute by navController.currentRouteAsState()

    PiHoleConnectTheme {
        Scaffold(
            topBar = { TopBar() },
            bottomBar = {
                BottomTab(
                    items = tabItems,
                    currentRoute = currentRoute ?: Screen.Home.route,
                    onNavigationItemClick = {
                        navController.navigate(it.screen.route) {
                            popUpTo = navController.graph.startDestination
                            launchSingleTop = true
                        }
                    }
                )
            }
        ) {
            NavHost(navController = navController, startDestination = Screen.Home.route) {
                composable(route = Screen.Home.route) { Home() }
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}