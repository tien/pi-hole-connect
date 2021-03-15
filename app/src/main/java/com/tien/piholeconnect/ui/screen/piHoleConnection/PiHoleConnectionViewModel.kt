package com.tien.piholeconnect.ui.screen.piHoleConnection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.extension.populateDefaultValues
import com.tien.piholeconnect.model.PiHoleConnection
import com.tien.piholeconnect.repository.IUserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PiHoleConnectionViewModel @Inject constructor(
    private val userPreferencesRepository: IUserPreferencesRepository
) : ViewModel() {
    private val default = PiHoleConnection.newBuilder().populateDefaultValues()

    private var id: String? = null
    var name: String by mutableStateOf(default.name)
    var description: String by mutableStateOf(default.description)
    var host: String by mutableStateOf(default.host)
    var apiPath: String by mutableStateOf(default.apiPath)
    var port: Int by mutableStateOf(default.port)
    var apiToken: String by mutableStateOf(default.apiToken)

    suspend fun loadDataForId(piHoleConnectionId: String) {
        viewModelScope.launch {
            val connection =
                userPreferencesRepository.userPreferencesFlow.first().piHoleConnectionsList.first { it.id == piHoleConnectionId }
                    .toBuilder().build()

            id = connection.id
            name = connection.name
            description = connection.description
            host = connection.host
            apiPath = connection.apiPath
            port = connection.port
            apiToken = connection.apiToken
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
}