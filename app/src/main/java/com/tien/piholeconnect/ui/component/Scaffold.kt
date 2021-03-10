package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Analytics
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Insights
import androidx.compose.material.icons.twotone.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tien.piholeconnect.model.BottomTabItem
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.model.TopBarOptionsMenuItem
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme

@Composable
fun Scaffold(
    title: String,
    optionsMenuItems: Iterable<TopBarOptionsMenuItem>,
    bottomTabItems: Iterable<BottomTabItem>,
    currentRoute: String,
    isBackButtonEnabled: Boolean,
    onBackButtonClick: () -> Unit,
    onBottomTabItemClick: (BottomTabItem) -> Unit,
    onOptionsMenuItemClick: (TopBarOptionsMenuItem) -> Unit,
    isAdsBlockingEnabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    androidx.compose.material.Scaffold(
        topBar = {
            TopBar(
                title = title,
                optionsMenuItems = optionsMenuItems,
                onOptionsMenuItemClick = onOptionsMenuItemClick,
                isBackButtonEnabled = isBackButtonEnabled,
                onBackButtonClick = onBackButtonClick
            )
        },
        bottomBar = {
            BottomTab(
                items = bottomTabItems,
                currentRoute = currentRoute,
                onBottomTabItemClick = onBottomTabItemClick
            )
        },
        floatingActionButton = { PiHoleSwitchFloatingActionButton(isAdsBlockingEnabled = isAdsBlockingEnabled) },
        modifier = modifier,
        content = content
    )
}

@Preview
@Composable
fun ScaffoldPreview(content: @Composable (PaddingValues) -> Unit = {}) {
    PiHoleConnectTheme {
        Scaffold(
            title = "Pi Hole Connect",
            optionsMenuItems = listOf(
                TopBarOptionsMenuItem(
                    Screen.Settings.route,
                    Screen.Settings.labelResourceId
                )
            ),
            bottomTabItems = listOf(
                BottomTabItem(Screen.Home, Icons.TwoTone.Home),
                BottomTabItem(Screen.Statistics, Icons.TwoTone.Insights),
                BottomTabItem(Screen.Log, Icons.TwoTone.Analytics),
                BottomTabItem(Screen.FilterRules, Icons.TwoTone.Shield)
            ),
            currentRoute = Screen.Home.route,
            onBottomTabItemClick = {},
            onOptionsMenuItemClick = {},
            isAdsBlockingEnabled = true,
            content = content,
            isBackButtonEnabled = false,
            onBackButtonClick = {}
        )
    }
}