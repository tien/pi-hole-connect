package com.tien.piholeconnect.ui.screen.piHoleConnection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.tien.piholeconnect.extension.populateDefaultValues
import com.tien.piholeconnect.model.PiHoleConnection
import com.tien.piholeconnect.model.URLProtocol
import com.tien.piholeconnect.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PiHoleConnectionViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    val barcodeScanner: BarcodeScanner
) : ViewModel() {
    private val default = PiHoleConnection.newBuilder().populateDefaultValues()

    private var id: String? = null
    var name: String by mutableStateOf(default.name)
    var description: String by mutableStateOf(default.description)
    var protocol: URLProtocol by mutableStateOf(default.protocol)
    var host: String by mutableStateOf(default.host)
    var apiPath: String by mutableStateOf(default.apiPath)
    var port: Int by mutableStateOf(default.port)
    var apiToken: String by mutableStateOf(default.apiToken)
    var shouldShowDeleteButton: Boolean by mutableStateOf(false)
        private set

    suspend fun loadDataForId(piHoleConnectionId: String) {
        viewModelScope.launch {
            val preferences = userPreferencesRepository.userPreferencesFlow.first()
            val connection =
                preferences.piHoleConnectionsList.first { it.id == piHoleConnectionId }.toBuilder()
                    .build()

            id = connection.id
            name = connection.name
            description = connection.description
            host = connection.host
            apiPath = connection.apiPath
            port = connection.port
            apiToken = connection.apiToken
            shouldShowDeleteButton = preferences.piHoleConnectionsCount > 1
        }
    }

    suspend fun save() {
        userPreferencesRepository.updateUserPreferences { userPreferences ->
            val builder = userPreferences.toBuilder()

            val connectionBuilder = PiHoleConnection.newBuilder()
                .setId(id ?: UUID.randomUUID().toString())
                .setName(name)
                .setDescription(description)
                .setHost(host)
                .setApiPath(apiPath)
                .setPort(port)
                .setApiToken(apiToken)

            if (id == null) {
                return@updateUserPreferences builder.addPiHoleConnections(connectionBuilder).build()
            } else {
                val index =
                    userPreferences.piHoleConnectionsList.indexOfFirst { it.id == id }
                return@updateUserPreferences builder.setPiHoleConnections(index, connectionBuilder)
                    .build()
            }
        }
    }

    suspend fun remove() {
        if (id == null) return

        userPreferencesRepository.updateUserPreferences { userPreferences ->
            val builder = userPreferences.toBuilder()
            val index = builder.piHoleConnectionsList.indexOfFirst { it.id == id }

            builder.removePiHoleConnections(index).build()
        }
    }
}