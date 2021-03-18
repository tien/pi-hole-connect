package com.tien.piholeconnect.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Analytics
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Insights
import androidx.compose.material.icons.twotone.Shield
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.tien.piholeconnect.extension.currentRouteAsState
import com.tien.piholeconnect.model.*
import com.tien.piholeconnect.ui.component.Scaffold
import com.tien.piholeconnect.ui.screen.home.HomeScreen
import com.tien.piholeconnect.ui.screen.home.HomeViewModel
import com.tien.piholeconnect.ui.screen.piHoleConnection.PiHoleConnectionScreen
import com.tien.piholeconnect.ui.screen.piHoleConnection.PiHoleConnectionViewModel
import com.tien.piholeconnect.ui.screen.preferences.PreferencesScreen
import com.tien.piholeconnect.ui.screen.preferences.PreferencesViewModel
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme


@Composable
fun App(
    homeViewModel: HomeViewModel = viewModel(),
    preferencesViewModel: PreferencesViewModel = viewModel(),
) {
    val preferences by preferencesViewModel.userPreferencesFlow.collectAsState(initial = null)
    if (preferences == null) return

    val navController = rememberNavController()

    val optionsMenuItems =
        setOf(TopBarOptionsMenuItem(Screen.Preferences.route, Screen.Preferences.labelResourceId))

    val tabItems = listOf(
        BottomTabItem(Screen.Home, Icons.TwoTone.Home),
        BottomTabItem(Screen.Statistics, Icons.TwoTone.Insights),
        BottomTabItem(Screen.Log, Icons.TwoTone.Analytics),
        BottomTabItem(Screen.FilterRules, Icons.TwoTone.Shield)
    )

    val currentRoute by navController.currentRouteAsState()
    val title = currentRoute?.let { stringResource(screenForRoute(it).labelResourceId) }
        ?: "Pi Hole Connect"

    PiHoleConnectTheme(
        darkTheme = when (preferences!!.theme) {
            Theme.DARK -> true
            Theme.LIGHT -> false
            else -> isSystemInDarkTheme()
        }
    ) {
        Scaffold(
            optionsMenuItems = optionsMenuItems,
            bottomTabItems = tabItems,
            title = title,
            currentRoute = currentRoute ?: Screen.Home.route,
            isBottomTabEnabled = currentRoute != Screen.Preferences.route && currentRoute != Screen.PiHoleConnection.route,
            isBackButtonEnabled = currentRoute == Screen.Preferences.route || currentRoute == Screen.PiHoleConnection.route,
            onBackButtonClick = { navController.navigateUp() },
            onBottomTabItemClick = {
                navController.navigate(it.screen.route) {
                    popUpTo = navController.graph.startDestination
                    launchSingleTop = true
                }
            },
            onOptionsMenuItemClick = { navController.navigate(it.key) },
            isAdsBlockingEnabled = homeViewModel.isAdsBlockingEnabled,
        ) { padding ->
            NavHost(navController = navController, startDestination = Screen.Home.route) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        Modifier.padding(padding),
                        viewModel = homeViewModel
                    )
                }
                composable(Screen.Statistics.route) {}
                composable(Screen.Log.route) {}
                composable(Screen.FilterRules.route) {}
                composable(Screen.FilterRules.route) {}
                composable(Screen.Preferences.route) {
                    PreferencesScreen(
                        viewModel = preferencesViewModel,
                        navController = navController
                    )
                }
                composable(
                    "${Screen.PiHoleConnection.route}?id={id}", arguments = listOf(
                        navArgument("id") { nullable = true })
                ) {
                    val piHoleConnectionViewModel =
                        hiltNavGraphViewModel<PiHoleConnectionViewModel>()
                    val id = it.arguments?.getString("id")

                    PiHoleConnectionScreen(
                        viewModel = piHoleConnectionViewModel,
                        connectionId = id,
                        navController = navController
                    )
                }
            }
        }
    }
}