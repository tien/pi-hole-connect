package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.model.TopBarOptionsMenuItem

@Composable
fun TopBar(
    title: String,
    isBackButtonEnabled: Boolean = false,
    onBackButtonClick: () -> Unit = {},
    optionsMenuItems: Iterable<TopBarOptionsMenuItem>,
    onOptionsMenuItemClick: (TopBarOptionsMenuItem) -> Unit
) {
    TopAppBar(title = { Text(title) }, navigationIcon = if (!isBackButtonEnabled) null else ({
        IconButton(onClick = onBackButtonClick) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.back_button_label)
            )
        }
    }), actions = {
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
                onDismissRequest = { isOptionsMenuExpanded = false }
            ) {
                optionsMenuItems.forEach {
                    DropdownMenuItem(onClick = {
                        isOptionsMenuExpanded = false
                        onOptionsMenuItemClick(it)
                    }) {
                        Text(stringResource(it.labelResourceId))
                    }
                }
            }

        }
    })
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar(
        title = "Pi-Hole Connect",
        optionsMenuItems = listOf(
            TopBarOptionsMenuItem(
                Screen.Preferences.route,
                Screen.Preferences.labelResourceId
            )
        ),
        onOptionsMenuItemClick = {},
        isBackButtonEnabled = true
    )
}