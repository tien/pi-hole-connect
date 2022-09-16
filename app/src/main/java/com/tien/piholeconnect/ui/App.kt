package com.tien.piholeconnect.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.*
import com.tien.piholeconnect.ui.component.BottomTab
import com.tien.piholeconnect.ui.component.OptionsMenu
import com.tien.piholeconnect.ui.component.TopBar
import com.tien.piholeconnect.ui.screen.filterrules.FilterRulesScreen
import com.tien.piholeconnect.ui.screen.filterrules.FilterRulesViewModel
import com.tien.piholeconnect.ui.screen.home.HomeScreen
import com.tien.piholeconnect.ui.screen.home.HomeViewModel
import com.tien.piholeconnect.ui.screen.log.LogScreen
import com.tien.piholeconnect.ui.screen.piholeconnection.PiHoleConnectionScreen
import com.tien.piholeconnect.ui.screen.piholeconnection.PiHoleConnectionViewModel
import com.tien.piholeconnect.ui.screen.preferences.PreferencesScreen
import com.tien.piholeconnect.ui.screen.preferences.PreferencesViewModel
import com.tien.piholeconnect.ui.screen.statistics.StatisticsScreen
import com.tien.piholeconnect.ui.screen.statistics.StatisticsViewModel
import com.tien.piholeconnect.ui.screen.tipjar.TipJarScreen
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme
import com.tien.piholeconnect.util.toKtorURLProtocol
import io.ktor.http.*
import kotlinx.coroutines.launch


@Composable
fun App(
    homeViewModel: HomeViewModel = viewModel(),
    preferencesViewModel: PreferencesViewModel = viewModel(),
    statisticsViewModel: StatisticsViewModel = viewModel(),
    filterRulesViewModel: FilterRulesViewModel = viewModel()
) {
    val context = LocalContext.current
    val userPreferences by preferencesViewModel.userPreferencesFlow.collectAsState(initial = null)
    val selectedPiHole by preferencesViewModel.selectedPiHoleFlow.collectAsState(initial = null)

    if (userPreferences == null) return

    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()

    val isDarkTheme = when (userPreferences!!.theme) {
        Theme.DARK -> true
        Theme.LIGHT -> false
        else -> isSystemInDarkTheme()
    }

    val optionsMenuItems = mutableSetOf(
        TopBarOptionsMenuItem(
            Screen.Preferences.route, Screen.Preferences.labelResourceId, Icons.TwoTone.Settings
        )
    )

    selectedPiHole?.let {
        optionsMenuItems.add(
            TopBarOptionsMenuItem(
                URLBuilder(
                    protocol = it.protocol.toKtorURLProtocol(),
                    host = it.host,
                    user = it.basicAuthUsername,
                    password = it.basicAuthPassword
                ).buildString(),
                R.string.options_menu_web_dashboard,
                Icons.TwoTone.OpenInNew,
                isExternalLink = true
            )
        )
    }
    optionsMenuItems.add(
        TopBarOptionsMenuItem(
            Screen.TipJar.route, Screen.TipJar.labelResourceId, Icons.TwoTone.Paid
        )
    )
    optionsMenuItems.add(
        TopBarOptionsMenuItem(
            stringResource(R.string.bug_report_url),
            R.string.options_menu_bug_report,
            Icons.TwoTone.BugReport,
            isExternalLink = true
        )
    )

    val tabItems = listOf(
        BottomTabItem(Screen.Home, Icons.TwoTone.Home),
        BottomTabItem(Screen.Statistics, Icons.TwoTone.Insights),
        BottomTabItem(Screen.FilterRules, Icons.TwoTone.Shield),
        BottomTabItem(Screen.Log, Icons.TwoTone.Analytics),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = currentRoute?.let(::screenForRoute)
    val title = currentRoute?.let { stringResource(screenForRoute(it).labelResourceId) }
        ?: stringResource(R.string.app_name)

    val defaultOptionsMenu = @Composable {
        OptionsMenu(
            selectedPiHoleConnectionId = userPreferences!!.selectedPiHoleConnectionId,
            piHoleConnections = userPreferences!!.piHoleConnectionsList,
            optionsMenuItems = optionsMenuItems,
            onOptionsMenuItemClick = {
                if (!it.isExternalLink) {
                    navController.navigate(it.key)
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.key))
                    runCatching { context.startActivity(intent) }
                }
            },
            onPiHoleConnectionClick = { piHoleConnection ->
                preferencesViewModel.viewModelScope.launch {
                    preferencesViewModel.updateUserPreferences {
                        it.toBuilder().setSelectedPiHoleConnectionId(piHoleConnection.id).build()
                    }
                }
            },
        )
    }

    PiHoleConnectTheme(darkTheme = isDarkTheme) {
        val themeColors = MaterialTheme.colors
        val elevationOverlay = LocalElevationOverlay.current
        val topAppBarColor = elevationOverlay?.apply(
            color = themeColors.primarySurface, elevation = AppBarDefaults.TopAppBarElevation
        ) ?: themeColors.primarySurface
        val bottomNavigationBackgroundColor = elevationOverlay?.apply(
            color = themeColors.primarySurface, elevation = BottomNavigationDefaults.Elevation
        ) ?: themeColors.primarySurface

        SideEffect {
            systemUiController.apply {
                setSystemBarsColor(color = topAppBarColor)
                setNavigationBarColor(color = bottomNavigationBackgroundColor)
            }
        }

        Scaffold(scaffoldState = scaffoldState, topBar = {
            if (currentScreen?.options?.showTopAppBar != false) {
                TopBar(title = title,
                    isBackButtonEnabled = currentScreen?.options?.showBackButton ?: false,
                    onBackButtonClick = { navController.navigateUp() },
                    actions = {
                        if (currentScreen?.options?.showMenus != false) {
                            defaultOptionsMenu()
                        }
                    })
            }
        }, bottomBar = {
            if (currentScreen?.options?.showTab != false) {
                BottomTab(items = tabItems,
                    currentRoute = currentRoute ?: Screen.Home.route,
                    onBottomTabItemClick = {
                        navController.navigate(it.screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    })
            }
        }) { padding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(viewModel = homeViewModel)
                }
                composable(Screen.Statistics.route) {
                    StatisticsScreen(viewModel = statisticsViewModel, scaffoldState = scaffoldState)
                }
                composable(Screen.Log.route) {
                    LogScreen(viewModel = hiltViewModel(), actions = { defaultOptionsMenu() })
                }
                composable(Screen.FilterRules.route) {
                    FilterRulesScreen(viewModel = filterRulesViewModel)
                }
                composable(Screen.Preferences.route) {
                    PreferencesScreen(
                        viewModel = preferencesViewModel, navController = navController
                    )
                }
                composable(
                    "${Screen.PiHoleConnection.route}?id={id}",
                    arguments = listOf(navArgument("id") { nullable = true })
                ) {
                    val piHoleConnectionViewModel = hiltViewModel<PiHoleConnectionViewModel>()
                    val id = it.arguments?.getString("id")

                    PiHoleConnectionScreen(
                        viewModel = piHoleConnectionViewModel,
                        connectionId = id,
                        navController = navController
                    )
                }
                composable(Screen.TipJar.route) {
                    TipJarScreen(viewModel = hiltViewModel())
                }
            }
        }
    }
}
