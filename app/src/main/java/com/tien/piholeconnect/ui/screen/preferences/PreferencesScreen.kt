package com.tien.piholeconnect.ui.screen.preferences

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.model.Theme
import com.tien.piholeconnect.model.UserPreferences
import kotlinx.coroutines.launch

private val PreferenceItemModifier = Modifier.fillMaxWidth().height(56.dp)

@Composable
fun PreferencesScreen(
    navController: NavHostController,
    viewModel: PreferencesViewModel = hiltViewModel(),
) {
    val userPreferences by
        viewModel.userPreferencesFlow.collectAsState(initial = UserPreferences.getDefaultInstance())

    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        ListItem(
            leadingContent = {
                Text(
                    stringResource(R.string.preferences_my_pi_hole),
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            headlineContent = {},
        )
        userPreferences.piHoleConnectionsList.forEach {
            ListItem(
                modifier =
                    Modifier.clickable {
                        navController.navigate("${Screen.PiHoleConnection.route}?id=${it.id}")
                    },
                leadingContent = {
                    Icon(Icons.Default.Router, contentDescription = "Pi-hole ${it.name}")
                },
                headlineContent = { Text(it.name) },
                supportingContent = { Text(it.host) },
            )
        }
        if (userPreferences.piHoleConnectionsCount < 5) {
            ListItem(
                modifier =
                    Modifier.clickable { navController.navigate(Screen.PiHoleConnection.route) },
                leadingContent = {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                },
                headlineContent = { Text(stringResource(R.string.preferences_add_pi_hole)) },
            )
        }
        Column(Modifier.selectableGroup()) {
            ListItem(
                leadingContent = {
                    Text(
                        stringResource(R.string.preferences_theme),
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
                headlineContent = {},
            )
            Theme.entries
                .filter { it != Theme.UNRECOGNIZED }
                .forEach { theme ->
                    ListItem(
                        modifier =
                            PreferenceItemModifier.selectable(
                                selected = theme == userPreferences.theme,
                                onClick = {
                                    viewModel.viewModelScope.launch {
                                        viewModel.updateUserPreferences {
                                            it.toBuilder().setTheme(theme).build()
                                        }
                                    }
                                },
                                role = Role.RadioButton,
                            ),
                        leadingContent = {
                            RadioButton(selected = theme == userPreferences.theme, onClick = null)
                        },
                        headlineContent = {
                            Text(text = theme.name.lowercase().replaceFirstChar { it.titlecase() })
                        },
                    )
                }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ListItem(
                modifier =
                    PreferenceItemModifier.selectable(
                        selected = userPreferences.useDynamicColor,
                        onClick = {
                            viewModel.viewModelScope.launch {
                                viewModel.updateUserPreferences {
                                    it.toBuilder().setUseDynamicColor(!it.useDynamicColor).build()
                                }
                            }
                        },
                        role = Role.Switch,
                    ),
                leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) },
                trailingContent = {
                    Switch(checked = userPreferences.useDynamicColor, onCheckedChange = null)
                },
                headlineContent = { Text(stringResource(R.string.preferences_dynamic_color)) },
            )
        }
    }
}
