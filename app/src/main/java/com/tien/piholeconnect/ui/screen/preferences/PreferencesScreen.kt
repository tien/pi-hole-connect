package com.tien.piholeconnect.ui.screen.preferences

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.model.Theme

@Composable
fun PreferencesScreen(
    navController: NavHostController,
    viewModel: PreferencesViewModel = hiltViewModel(),
) {
    val piHoleConnections by viewModel.piHoleConnections.collectAsStateWithLifecycle()
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val transparentListItemColors = ListItemDefaults.colors(containerColor = Color.Transparent)

    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // ---- My Pi-holes ----
        PreferenceSubheader(stringResource(R.string.preferences_my_pi_hole))
        PreferenceGroup {
            val connections = piHoleConnections.orEmpty()
            connections.entries.forEachIndexed { index, (id, connection) ->
                if (index > 0) HorizontalDivider(Modifier.padding(start = 16.dp))
                ListItem(
                    modifier =
                        Modifier.fillMaxWidth().clickable {
                            navController.navigate("${Screen.PiHoleConnection.route}?id=$id")
                        },
                    colors = transparentListItemColors,
                    leadingContent = {
                        Icon(
                            Icons.Default.Router,
                            contentDescription = "Pi-hole ${connection.metadata.name}",
                        )
                    },
                    headlineContent = { Text(connection.metadata.name.ifBlank { "Pi-hole" }) },
                    supportingContent = { Text(connection.configuration.host) },
                )
            }
            if (connections.size < 5) {
                if (connections.isNotEmpty()) HorizontalDivider(Modifier.padding(start = 16.dp))
                ListItem(
                    modifier =
                        Modifier.fillMaxWidth().clickable {
                            navController.navigate(Screen.PiHoleConnection.route)
                        },
                    colors = transparentListItemColors,
                    leadingContent = {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                    },
                    headlineContent = { Text(stringResource(R.string.preferences_add_pi_hole)) },
                )
            }
        }

        // ---- Appearance ----
        userPreferences?.let { prefs ->
            PreferenceSubheader(stringResource(R.string.preferences_appearance))
            PreferenceGroup {
                Text(
                    stringResource(R.string.preferences_theme),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                )
                val themes = remember { Theme.entries.filter { it != Theme.UNRECOGNIZED } }
                SingleChoiceSegmentedButtonRow(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    themes.forEachIndexed { index, theme ->
                        SegmentedButton(
                            selected = theme == prefs.theme,
                            onClick = {
                                viewModel.updateUserPreferences {
                                    it.toBuilder().setTheme(theme).build()
                                }
                            },
                            shape =
                                SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = themes.size,
                                ),
                            label = {
                                Text(
                                    stringResource(
                                        when (theme) {
                                            Theme.SYSTEM -> R.string.preferences_theme_system
                                            Theme.LIGHT -> R.string.preferences_theme_light
                                            Theme.DARK -> R.string.preferences_theme_dark
                                            else -> R.string.preferences_theme_system
                                        }
                                    )
                                )
                            },
                        )
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    HorizontalDivider(Modifier.padding(start = 16.dp))
                    ListItem(
                        modifier =
                            Modifier.fillMaxWidth()
                                .toggleable(
                                    value = prefs.useDynamicColor,
                                    role = Role.Switch,
                                    onValueChange = {
                                        viewModel.updateUserPreferences {
                                            it.toBuilder()
                                                .setUseDynamicColor(it.useDynamicColor.not())
                                                .build()
                                        }
                                    },
                                ),
                        colors = transparentListItemColors,
                        leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) },
                        headlineContent = {
                            Text(stringResource(R.string.preferences_dynamic_color))
                        },
                        trailingContent = {
                            Switch(checked = prefs.useDynamicColor, onCheckedChange = null)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun PreferenceSubheader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
    )
}

@Composable
private fun PreferenceGroup(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column { content() }
    }
}
