package com.tien.piholeconnect.ui.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.PiHoleConnection
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.model.TopBarOptionsMenuItem
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme
import com.tien.piholeconnect.util.populateDefaultValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    backButtonEnabled: Boolean,
    onBackButtonClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    if (backButtonEnabled) {
        TopAppBar(title = { Text(title) }, navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button_label)
                )
            }
        }, actions = actions
        )
    } else {
        CenterAlignedTopAppBar(title = { Text(title) }, actions = actions)
    }
}

@Composable
fun OptionsMenu(
    selectedPiHoleConnectionId: String,
    piHoleConnections: Iterable<PiHoleConnection>,
    optionsMenuItems: Iterable<TopBarOptionsMenuItem>,
    onOptionsMenuItemClick: (TopBarOptionsMenuItem) -> Unit,
    onPiHoleConnectionClick: (PiHoleConnection) -> Unit
) {
    Box {
        var isOptionsMenuExpanded by remember { mutableStateOf(false) }

        IconButton(onClick = { isOptionsMenuExpanded = true }) {
            Icon(
                Icons.Default.MoreVert, contentDescription = stringResource(
                    R.string.top_bar_more_options_label
                )
            )
        }
        DropdownMenu(expanded = isOptionsMenuExpanded, modifier = Modifier.composed {
            if (piHoleConnections.count() > 1) {
                fillMaxWidth()
            } else {
                this
            }
        }, onDismissRequest = { isOptionsMenuExpanded = false }) {
            Menu(piHoleConnections = piHoleConnections,
                selectedPiHoleConnectionId = selectedPiHoleConnectionId,
                optionsMenuItems = optionsMenuItems,
                onOptionsMenuItemClick = {
                    isOptionsMenuExpanded = false
                    onOptionsMenuItemClick(it)
                },
                onPiHoleConnectionClick = {
                    isOptionsMenuExpanded = false
                    onPiHoleConnectionClick(it)
                })
        }
    }
}

@Composable
private fun Menu(
    piHoleConnections: Iterable<PiHoleConnection>,
    selectedPiHoleConnectionId: String,
    optionsMenuItems: Iterable<TopBarOptionsMenuItem>,
    onOptionsMenuItemClick: (TopBarOptionsMenuItem) -> Unit,
    onPiHoleConnectionClick: (PiHoleConnection) -> Unit
) {
    val selectedId = selectedPiHoleConnectionId.ifBlank { piHoleConnections.firstOrNull()?.id }

    if (piHoleConnections.count() > 1) {
        piHoleConnections.forEach { connection ->
            DropdownMenuItem(leadingIcon = {
                RadioButton(
                    selected = connection.id == selectedId, onClick = null
                )
            }, text = {
                Text("${connection.name}@${connection.host}")
            }, onClick = { onPiHoleConnectionClick(connection) })
        }
        HorizontalDivider()
    }

    optionsMenuItems.forEach {
        DropdownMenuItem(leadingIcon = { Icon(imageVector = it.icon, contentDescription = null) },
            text = { Text(stringResource(it.labelResourceId)) },
            onClick = { onOptionsMenuItemClick(it) })
    }
}

@Composable
fun TopBarProgressIndicator(visible: Boolean) {
    AnimatedVisibility(
        visible,
        modifier = Modifier.zIndex(1f),
        enter = slideInVertically(),
        exit = slideOutVertically()
    ) {
        LinearProgressIndicator(
            Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RootTopAppBarPreview() {
    PiHoleConnectTheme {
        TopBar(
            title = "Pi-Hole Connect", backButtonEnabled = false
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NestedTopBarPreview() {
    PiHoleConnectTheme {
        TopBar(
            title = "Pi-Hole Connect", backButtonEnabled = true
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MenuPreview() {
    PiHoleConnectTheme {
        Column {
            val piHoleConnections = listOf((0..4)).flatten().map {
                PiHoleConnection.newBuilder().populateDefaultValues().build()
            }
            Menu(piHoleConnections = piHoleConnections,
                selectedPiHoleConnectionId = piHoleConnections.first().id,
                optionsMenuItems = listOf(
                    TopBarOptionsMenuItem(
                        Screen.Preferences.route,
                        Screen.Preferences.labelResourceId,
                        Icons.Default.Settings
                    )
                ),
                onOptionsMenuItemClick = {},
                onPiHoleConnectionClick = {})
        }
    }
}
