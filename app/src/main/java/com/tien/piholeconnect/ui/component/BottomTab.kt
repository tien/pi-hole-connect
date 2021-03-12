package com.tien.piholeconnect.ui.component

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.tien.piholeconnect.model.BottomTabItem
import com.tien.piholeconnect.model.Screen

@Composable
fun BottomTab(
    items: Iterable<BottomTabItem>,
    currentRoute: String,
    onBottomTabItemClick: (BottomTabItem) -> Unit
) {
    BottomNavigation {
        items.forEach {
            val label = stringResource(it.screen.labelResourceId)

            BottomNavigationItem(
                selected = it.screen.route == currentRoute,
                onClick = { onBottomTabItemClick(it) },
                icon = { Icon(it.icon, contentDescription = label) },
                label = { Text(label) },
                alwaysShowLabel = false
            )
        }
    }
}

@Preview
@Composable
fun BottomTabPreview() {
    var currentRoute by remember { mutableStateOf(Screen.Home.route) }

    val tabItems = listOf(
        BottomTabItem(Screen.Home, Icons.TwoTone.Home),
        BottomTabItem(Screen.Statistics, Icons.TwoTone.Insights),
        BottomTabItem(Screen.Log, Icons.TwoTone.Analytics),
        BottomTabItem(Screen.FilterRules, Icons.TwoTone.Shield),
        BottomTabItem(Screen.Preferences, Icons.TwoTone.Settings)
    )

    BottomTab(
        items = tabItems,
        currentRoute = currentRoute,
        onBottomTabItemClick = { currentRoute = it.screen.route })
}