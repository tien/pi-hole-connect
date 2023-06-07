package com.tien.piholeconnect.ui.component

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Analytics
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Insights
import androidx.compose.material.icons.twotone.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.tien.piholeconnect.model.BottomTabItem
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme

@Composable
fun BottomTab(
    items: Iterable<BottomTabItem>,
    currentRoute: String,
    onBottomTabItemClick: (BottomTabItem) -> Unit
) {
    NavigationBar {
        items.forEach {
            val label = stringResource(it.screen.labelResourceId)

            NavigationBarItem(selected = it.screen.route == currentRoute,
                onClick = { onBottomTabItemClick(it) },
                icon = { Icon(it.icon, contentDescription = label) },
                label = { Text(label) })
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BottomTabPreview() {
    var currentRoute by remember { mutableStateOf(Screen.Home.route) }

    val tabItems = listOf(
        BottomTabItem(Screen.Home, Icons.TwoTone.Home),
        BottomTabItem(Screen.Statistics, Icons.TwoTone.Insights),
        BottomTabItem(Screen.Log, Icons.TwoTone.Analytics),
        BottomTabItem(Screen.FilterRules, Icons.TwoTone.Shield)
    )

    PiHoleConnectTheme {
        BottomTab(items = tabItems,
            currentRoute = currentRoute,
            onBottomTabItemClick = { currentRoute = it.screen.route })
    }
}
