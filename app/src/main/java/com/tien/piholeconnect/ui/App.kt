package com.tien.piholeconnect.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.*
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.*
import com.tien.piholeconnect.ui.component.BottomTab
import com.tien.piholeconnect.ui.component.TopBar
import com.tien.piholeconnect.ui.screen.filterrules.FilterRulesScreen
import com.tien.piholeconnect.ui.screen.filterrules.FilterRulesViewModel
import com.tien.piholeconnect.ui.screen.home.HomeScreen
import com.tien.piholeconnect.ui.screen.home.HomeViewModel
import com.tien.piholeconnect.ui.screen.log.LogScreen
import com.tien.piholeconnect.ui.screen.log.LogViewModel
import com.tien.piholeconnect.ui.screen.piholeconnection.PiHoleConnectionScreen
import com.tien.piholeconnect.ui.screen.piholeconnection.PiHoleConnectionViewModel
import com.tien.piholeconnect.ui.screen.preferences.PreferencesScreen
import com.tien.piholeconnect.ui.screen.preferences.PreferencesViewModel
import com.tien.piholeconnect.ui.screen.statistics.StatisticsScreen
import com.tien.piholeconnect.ui.screen.statistics.StatisticsViewModel
import com.tien.piholeconnect.ui.screen.tipjar.TipJarScreen
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme
import com.tien.piholeconnect.util.currentRouteAsState
import kotlinx.coroutines.launch


@Composable
fun App(
    homeViewModel: HomeViewModel = viewModel(),
    preferencesViewModel: PreferencesViewModel = viewModel(),
    statisticsViewModel: StatisticsViewModel = viewModel(),
    logViewModel: LogViewModel = viewModel(),
    filterRulesViewModel: FilterRulesViewModel = viewModel()
) {
    val userPreferences by preferencesViewModel.userPreferencesFlow.collectAsState(initial = null)
    if (userPreferences == null) return

    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()

    val optionsMenuItems =
        setOf(
            TopBarOptionsMenuItem(
                Screen.Preferences.route,
                Screen.Preferences.labelResourceId,
                Icons.TwoTone.Settings
            ),
            TopBarOptionsMenuItem(
                Screen.TipJar.route,
                Screen.TipJar.labelResourceId,
                Icons.TwoTone.Paid
            )
        )

    val tabItems = listOf(
        BottomTabItem(Screen.Home, Icons.TwoTone.Home),
        BottomTabItem(Screen.Statistics, Icons.TwoTone.Insights),
        BottomTabItem(Screen.Log, Icons.TwoTone.Analytics),
        BottomTabItem(Screen.FilterRules, Icons.TwoTone.Shield)
    )

    val currentRoute by navController.currentRouteAsState()
    val currentScreen = currentRoute?.let(::screenForRoute)
    val title = currentRoute?.let { stringResource(screenForRoute(it).labelResourceId) }
        ?: stringResource(R.string.app_name)

    PiHoleConnectTheme(
        darkTheme = when (userPreferences!!.theme) {
            Theme.DARK -> true
            Theme.LIGHT -> false
            else -> isSystemInDarkTheme()
        }
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopBar(
                    title = title,
                    selectedPiHoleConnectionId = userPreferences!!.selectedPiHoleConnectionId,
                    piHoleConnections = userPreferences!!.piHoleConnectionsList,
                    optionsMenuItems = optionsMenuItems,
                    isBackButtonEnabled = currentScreen?.options?.showBackButton ?: false,
                    isMenusButtonEnabled = currentScreen?.options?.showMenus ?: false,
                    onOptionsMenuItemClick = { navController.navigate(it.key) },
                    onPiHoleConnectionClick = { piHoleConnection ->
                        preferencesViewModel.viewModelScope.launch {
                            preferencesViewModel.updateUserPreferences {
                                it.toBuilder().setSelectedPiHoleConnectionId(piHoleConnection.id)
                                    .build()
                            }
                        }
                    },
                    onBackButtonClick = { navController.navigateUp() }
                )
            },
            bottomBar = {
                if (currentScreen?.options?.showTab != false) {
                    BottomTab(
                        items = tabItems,
                        currentRoute = currentRoute ?: Screen.Home.route,
                        onBottomTabItemClick = {
                            navController.navigate(it.screen.route) {
                                popUpTo = navController.graph.startDestination
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        ) { padding ->
            NavHost(navController = navController, startDestination = Screen.Home.route) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        Modifier.padding(padding),
                        viewModel = homeViewModel
                    )
                }
                composable(Screen.Statistics.route) {
                    StatisticsScreen(
                        Modifier.padding(padding),
                        viewModel = statisticsViewModel,
                        scaffoldState = scaffoldState
                    )
                }
                composable(Screen.Log.route) {
                    LogScreen(
                        Modifier.padding(padding),
                        viewModel = logViewModel,
                        scaffoldState = scaffoldState
                    )
                }
                composable(Screen.FilterRules.route) {
                    FilterRulesScreen(Modifier.padding(padding), viewModel = filterRulesViewModel)
                }
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
                composable(Screen.TipJar.route) {
                    TipJarScreen(viewModel = hiltNavGraphViewModel())
                }
            }
        }
    }
}