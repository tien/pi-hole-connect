package com.tien.piholeconnect.ui.screen.preferences

import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.model.PiHoleConnections
import com.tien.piholeconnect.model.UserPreferences
import com.tien.piholeconnect.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel
@Inject
constructor(
    piHoleConnectionsDataStore: DataStore<PiHoleConnections>,
    private val userPreferencesDataStore: DataStore<UserPreferences>,
) : BaseViewModel() {
    val piHoleConnections =
        piHoleConnectionsDataStore.data
            .map { it.connectionsMap }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null,
            )

    val userPreferences =
        userPreferencesDataStore.data.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

    fun updateUserPreferences(transform: suspend (UserPreferences) -> UserPreferences) {
        viewModelScope.launch { userPreferencesDataStore.updateData(transform) }
    }
}
