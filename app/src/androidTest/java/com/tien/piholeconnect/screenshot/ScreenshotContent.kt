package com.tien.piholeconnect.screenshot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.twotone.Analytics
import androidx.compose.material.icons.twotone.Construction
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Insights
import androidx.compose.material.icons.twotone.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tien.piholeconnect.model.BottomTabItem
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.ui.component.BottomTab
import com.tien.piholeconnect.ui.component.TopBar
import com.tien.piholeconnect.ui.screen.filterrules.FilterRulesScreen
import com.tien.piholeconnect.ui.screen.filterrules.FilterRulesViewModel
import com.tien.piholeconnect.ui.screen.home.HomeScreen
import com.tien.piholeconnect.ui.screen.home.HomeViewModel
import com.tien.piholeconnect.ui.screen.log.LogScreen
import com.tien.piholeconnect.ui.screen.log.LogViewModel
import com.tien.piholeconnect.ui.screen.statistics.StatisticsScreen
import com.tien.piholeconnect.ui.screen.statistics.StatisticsViewModel
import com.tien.piholeconnect.ui.screen.tools.ToolsScreen
import com.tien.piholeconnect.ui.screen.tools.ToolsViewModel
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme

private val tabItems =
    listOf(
        BottomTabItem(Screen.Home, Icons.TwoTone.Home),
        BottomTabItem(Screen.Statistics, Icons.TwoTone.Insights),
        BottomTabItem(Screen.FilterRules, Icons.TwoTone.Shield),
        BottomTabItem(Screen.Log, Icons.TwoTone.Analytics),
        BottomTabItem(Screen.Tools, Icons.TwoTone.Construction),
    )

@Composable
private fun ScreenshotScaffold(screen: Screen, content: @Composable (PaddingValues) -> Unit) {
    PiHoleConnectTheme {
        Scaffold(
            topBar = {
                TopBar(
                    title = stringResource(screen.labelResourceId),
                    backButtonEnabled = false,
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }
                    },
                )
            },
            bottomBar = {
                BottomTab(items = tabItems, currentRoute = screen.route, onBottomTabItemClick = {})
            },
            content = content,
        )
    }
}

@Composable
fun ScreenshotHomeScreen(viewModel: HomeViewModel) {
    ScreenshotScaffold(screen = Screen.Home) { padding ->
        Box(Modifier.padding(padding)) { HomeScreen(viewModel = viewModel) }
    }
}

@Composable
fun ScreenshotStatisticsScreen(viewModel: StatisticsViewModel) {
    ScreenshotScaffold(screen = Screen.Statistics) { padding ->
        Box(Modifier.padding(padding)) {
            StatisticsScreen(
                snackbarHostState = remember { SnackbarHostState() },
                viewModel = viewModel,
            )
        }
    }
}

@Composable
fun ScreenshotFilterRulesScreen(viewModel: FilterRulesViewModel) {
    ScreenshotScaffold(screen = Screen.FilterRules) { padding ->
        Box(Modifier.padding(padding)) { FilterRulesScreen(viewModel = viewModel) }
    }
}

@Composable
fun ScreenshotLogScreen(viewModel: LogViewModel) {
    ScreenshotScaffold(screen = Screen.Log) { padding ->
        Box(Modifier.padding(padding)) { LogScreen(actions = {}, viewModel = viewModel) }
    }
}

@Composable
fun ScreenshotToolsScreen(viewModel: ToolsViewModel) {
    ScreenshotScaffold(screen = Screen.Tools) { padding ->
        Box(Modifier.padding(padding)) { ToolsScreen(viewModel = viewModel) }
    }
}
