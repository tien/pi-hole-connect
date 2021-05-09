package com.tien.piholeconnect.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.PiHoleConnection
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.model.TopBarOptionsMenuItem
import com.tien.piholeconnect.util.populateDefaultValues

@Composable
fun TopBar(
    title: String,
    isBackButtonEnabled: Boolean,
    onBackButtonClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) }, navigationIcon = if (!isBackButtonEnabled) null else ({
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_button_label)
                )
            }
        }), actions = actions
    )
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
        DropdownMenu(
            expanded = isOptionsMenuExpanded,
            modifier = Modifier.composed {
                if (piHoleConnections.count() > 1) {
                    fillMaxWidth()
                } else {
                    this
                }
            },
            onDismissRequest = { isOptionsMenuExpanded = false }
        ) {
            Menu(
                piHoleConnections = piHoleConnections,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Menu(
    piHoleConnections: Iterable<PiHoleConnection>,
    selectedPiHoleConnectionId: String,
    optionsMenuItems: Iterable<TopBarOptionsMenuItem>,
    onOptionsMenuItemClick: (TopBarOptionsMenuItem) -> Unit,
    onPiHoleConnectionClick: (PiHoleConnection) -> Unit
) {
    val selectedId =
        if (selectedPiHoleConnectionId.isBlank()) piHoleConnections.firstOrNull()?.id else selectedPiHoleConnectionId

    if (piHoleConnections.count() > 1) {
        piHoleConnections.forEach { connection ->
            DropdownMenuItem(onClick = { onPiHoleConnectionClick(connection) }) {
                ListItem(
                    icon = {
                        RadioButton(
                            selected = connection.id == selectedId,
                            onClick = null
                        )
                    },
                    text = { Text(connection.name) },
                    secondaryText = { Text(connection.host) })
            }
        }
        Divider()
    }
    optionsMenuItems.forEach {
        DropdownMenuItem(onClick = { onOptionsMenuItemClick(it) }) {
            ListItem(
                icon = { Icon(imageVector = it.icon, contentDescription = null) },
                text = { Text(stringResource(it.labelResourceId)) })
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TopBarProgressIndicator(visible: Boolean) {
    AnimatedVisibility(
        visible,
        modifier = Modifier.zIndex(1f),
        enter = slideInVertically(),
        exit = slideOutVertically()
    ) {
        LinearProgressIndicator(
            Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.secondary
        )
    }
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar(
        title = "Pi-Hole Connect",
        isBackButtonEnabled = true
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun MenuPreview() {
    Column {
        val piHoleConnections = listOf((0..4)).flatten().map {
            PiHoleConnection.newBuilder().populateDefaultValues().build()
        }
        Menu(
            piHoleConnections = piHoleConnections,
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