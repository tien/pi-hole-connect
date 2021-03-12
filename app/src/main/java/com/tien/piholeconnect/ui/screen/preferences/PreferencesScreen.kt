package com.tien.piholeconnect.ui.screen.preferences

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tien.piholeconnect.model.TemperatureUnit
import com.tien.piholeconnect.model.Theme
import com.tien.piholeconnect.ui.component.ScaffoldPreview
import kotlinx.coroutines.launch
import androidx.compose.ui.text.intl.Locale.Companion as Locale

@Composable
fun PreferencesScreen(viewModel: PreferencesViewModel = viewModel()) {
    val userPreferences by viewModel.userPreferencesFlow.collectAsState(initial = null)

    if (userPreferences == null) return

    Column(Modifier.padding(horizontal = 20.dp, vertical = 15.dp)) {
        Text("My Pi-holes", style = MaterialTheme.typography.caption)
        Column(
            Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalArrangement = Arrangement.Center
        ) {
            userPreferences!!.piHoleConnectionsList.forEach {
                Text(it.name)
            }
        }
        Column(Modifier.selectableGroup()) {
            Text("Theme", style = MaterialTheme.typography.caption)
            Theme.values()
                .filter { it != Theme.UNRECOGNIZED }
                .forEach { theme ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = theme == userPreferences!!.theme,
                                onClick = {
                                    viewModel.viewModelScope.launch {
                                        viewModel.updateUserPreferences {
                                            it
                                                .toBuilder()
                                                .setTheme(theme)
                                                .build()
                                        }
                                    }
                                },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            modifier = Modifier.padding(end = 40.dp),
                            selected = theme == userPreferences!!.theme,
                            onClick = null
                        )
                        Text(
                            text = theme.name.toLowerCase(Locale.current)
                                .capitalize(Locale.current)
                        )
                    }
                }
        }
        Column(Modifier.selectableGroup()) {
            Text("Temperature display", style = MaterialTheme.typography.caption)
            TemperatureUnit.values()
                .filter { it != TemperatureUnit.UNRECOGNIZED }
                .forEach { temperatureUnit ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = temperatureUnit == userPreferences!!.temperatureUnit,
                                onClick = {
                                    viewModel.viewModelScope.launch {
                                        viewModel.updateUserPreferences {
                                            it
                                                .toBuilder()
                                                .setTemperatureUnit(temperatureUnit)
                                                .build()
                                        }
                                    }
                                },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            modifier = Modifier.padding(end = 40.dp),
                            selected = temperatureUnit == userPreferences!!.temperatureUnit,
                            onClick = null
                        )
                        Text(
                            text = temperatureUnit.name.toLowerCase(Locale.current)
                                .capitalize(Locale.current)
                        )
                    }
                }
        }
    }
}

@Preview
@Composable
fun PreferencesScreenPreview() {
    ScaffoldPreview {
        PreferencesScreen()
    }
}
