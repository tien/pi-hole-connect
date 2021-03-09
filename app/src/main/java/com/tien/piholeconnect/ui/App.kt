package com.tien.piholeconnect.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Analytics
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Insights
import androidx.compose.material.icons.twotone.Shield
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.tien.piholeconnect.extension.currentRouteAsState
import com.tien.piholeconnect.model.BottomTabItem
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.model.screenForRoute
import com.tien.piholeconnect.ui.component.Scaffold
import com.tien.piholeconnect.ui.screen.home.HomeScreen
import com.tien.piholeconnect.ui.screen.home.HomeViewModel
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme


@Composable
fun App(homeViewModel: HomeViewModel) {
    val navController = rememberNavController()

    val tabItems = listOf(
        BottomTabItem(Screen.Home, Icons.TwoTone.Home),
        BottomTabItem(Screen.Statistics, Icons.TwoTone.Insights),
        BottomTabItem(Screen.Log, Icons.TwoTone.Analytics),
        BottomTabItem(Screen.FilterRules, Icons.TwoTone.Shield)
    )

    val currentRoute by navController.currentRouteAsState()
    val title = currentRoute?.let { screenForRoute(it) }?.let { stringResource(it.labelResourceId) }
        ?: "Pi Hole Connect"

    PiHoleConnectTheme {
        Scaffold(
            bottomTabItems = tabItems,
            title = title,
            currentRoute = currentRoute ?: Screen.Home.route,
            onBottomTabItemClick = {
                navController.navigate(it.screen.route) {
                    popUpTo = navController.graph.startDestination
                    launchSingleTop = true
                }
            },
            isAdsBlockingEnabled = homeViewModel.isAdsBlockingEnabled
        ) {
            NavHost(navController = navController, startDestination = Screen.Home.route) {
                composable(Screen.Home.route) { HomeScreen(homeViewModel) }
                composable(Screen.Statistics.route) {}
                composable(Screen.Log.route) {}
                composable(Screen.FilterRules.route) {}
            }
        }
    }
}