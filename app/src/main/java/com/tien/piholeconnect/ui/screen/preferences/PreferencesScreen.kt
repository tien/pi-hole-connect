package com.tien.piholeconnect.ui.screen.preferences

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
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.model.Theme
import com.tien.piholeconnect.model.UserPreferences
import kotlinx.coroutines.launch

private val PreferenceItemModifier = Modifier
    .fillMaxWidth()
    .height(56.dp)

@Composable
fun PreferencesScreen(
    viewModel: PreferencesViewModel = viewModel(), navController: NavHostController
) {
    val userPreferences by viewModel.userPreferencesFlow.collectAsState(initial = UserPreferences.getDefaultInstance())

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        ListItem(leadingContent = {
            Text(
                stringResource(R.string.preferences_my_pi_hole),
                style = MaterialTheme.typography.bodySmall
            )
        }, headlineContent = {})
        userPreferences.piHoleConnectionsList.forEach {
            ListItem(modifier = Modifier.clickable { navController.navigate("${Screen.PiHoleConnection.route}?id=${it.id}") },
                leadingContent = {
                    Icon(
                        Icons.Default.Router, contentDescription = "Pi-hole ${it.name}"
                    )
                },
                headlineContent = { Text(it.name) },
                supportingContent = { Text(it.host) })
        }
        if (userPreferences.piHoleConnectionsCount < 5) {
            ListItem(modifier = Modifier.clickable { navController.navigate(Screen.PiHoleConnection.route) },
                leadingContent = {
                    Icon(
                        Icons.Default.AddCircleOutline, contentDescription = null
                    )
                },
                headlineContent = { Text(stringResource(R.string.preferences_add_pi_hole)) })
        }
        Column(
            Modifier.selectableGroup()
        ) {
            ListItem(leadingContent = {
                Text(
                    stringResource(R.string.preferences_theme),
                    style = MaterialTheme.typography.bodySmall
                )
            }, headlineContent = {})
            Theme.values().filter { it != Theme.UNRECOGNIZED }.forEach { theme ->
                ListItem(modifier = PreferenceItemModifier.selectable(
                    selected = theme == userPreferences.theme, onClick = {
                        viewModel.viewModelScope.launch {
                            viewModel.updateUserPreferences {
                                it.toBuilder().setTheme(theme).build()
                            }
                        }
                    }, role = Role.RadioButton
                ), leadingContent = {
                    RadioButton(
                        selected = theme == userPreferences.theme, onClick = null
                    )
                }, headlineContent = {
                    Text(text = theme.name.lowercase().replaceFirstChar { it.titlecase() })
                })
            }
        }
//        Column(
//            Modifier
//                .padding(horizontal = 15.dp)
//                .selectableGroup()
//        ) {
//            Text(
//                stringResource(R.string.preferences_temperature),
//                style = MaterialTheme.typography.caption
//            )
//            TemperatureUnit.values()
//                .filter { it != TemperatureUnit.UNRECOGNIZED }
//                .forEach { temperatureUnit ->
//                    Row(
//                        PreferenceItemModifier
//                            .selectable(
//                                selected = temperatureUnit == userPreferences.temperatureUnit,
//                                onClick = {
//                                    viewModel.viewModelScope.launch {
//                                        viewModel.updateUserPreferences {
//                                            it
//                                                .toBuilder()
//                                                .setTemperatureUnit(temperatureUnit)
//                                                .build()
//                                        }
//                                    }
//                                },
//                                role = Role.RadioButton
//                            ),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        RadioButton(
//                            modifier = Modifier.padding(end = 32.dp),
//                            selected = temperatureUnit == userPreferences.temperatureUnit,
//                            onClick = null
//                        )
//                        Text(
//                            text = temperatureUnit.name.toLowerCase(Locale.current)
//                                .capitalize(Locale.current)
//                        )
//                    }
//                }
//        }
    }
}
