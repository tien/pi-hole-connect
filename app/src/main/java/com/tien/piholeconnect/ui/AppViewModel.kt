package com.tien.piholeconnect.ui

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.model.LoadState
import com.tien.piholeconnect.model.PiHoleConnections
import com.tien.piholeconnect.model.UserPreferences
import com.tien.piholeconnect.model.asLoadState
import com.tien.piholeconnect.util.getSelectedConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel
@Inject
constructor(
    private val piHoleConnectionsDataStore: DataStore<PiHoleConnections>,
    userPreferencesDataStore: DataStore<UserPreferences>,
) : ViewModel() {
    val userPreferences =
        userPreferencesDataStore.data.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

    val piHoleConnections =
        piHoleConnectionsDataStore.data
            .map { it.connectionsMap }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = mapOf(),
            )

    val selectedPiHole =
        piHoleConnectionsDataStore.data
            .map { it.getSelectedConnection() }
            .asLoadState()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = LoadState.Loading(),
            )

    fun setSelectedPiHole(id: String) {
        viewModelScope.launch {
            piHoleConnectionsDataStore.updateData {
                require(it.containsConnections(id))
                it.toBuilder().setSelectedConnectionId(id).build()
            }
        }
    }
}
