package com.tien.piholeconnect.ui.screen.piholeconnection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tien.piholeconnect.model.PiHoleConnection
import com.tien.piholeconnect.model.URLProtocol
import com.tien.piholeconnect.repository.UserPreferencesRepository
import com.tien.piholeconnect.util.populateDefaultValues
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PiHoleConnectionViewModel
@Inject
constructor(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {
    private val default = PiHoleConnection.newBuilder().populateDefaultValues()

    private var id: String? = null
    var name: String by mutableStateOf(default.name)
    var description: String by mutableStateOf(default.description)
    var protocol: URLProtocol by mutableStateOf(default.protocol)
    var host: String by mutableStateOf(default.host)
    var apiPath: String by mutableStateOf(default.apiPath)
    var port: String by mutableStateOf(default.port.toString())
    var password: String by mutableStateOf(default.password)
    var basicAuthUsername: String by mutableStateOf(default.basicAuthUsername)
    var basicAuthPassword: String by mutableStateOf(default.basicAuthPassword)
    var basicAuthRealm: String by mutableStateOf(default.basicAuthRealm)
    var trustAllCertificates: Boolean by mutableStateOf(default.trustAllCertificates)
    var shouldShowDeleteButton: Boolean by mutableStateOf(false)
        private set

    suspend fun loadDataForId(piHoleConnectionId: String) {
        val preferences = userPreferencesRepository.userPreferences.first()
        val connection =
            preferences.piHoleConnectionsList
                .first { it.id == piHoleConnectionId }
                .toBuilder()
                .build()

        id = connection.id
        name = connection.name
        description = connection.description
        protocol = connection.protocol
        host = connection.host
        apiPath = connection.apiPath
        port = connection.port.toString()
        password = connection.password
        basicAuthUsername = connection.basicAuthUsername
        basicAuthPassword = connection.basicAuthPassword
        basicAuthRealm = connection.basicAuthRealm
        trustAllCertificates = connection.trustAllCertificates
        shouldShowDeleteButton = true
    }

    suspend fun save() {
        userPreferencesRepository.updateUserPreferences { userPreferences ->
            val builder = userPreferences.toBuilder()

            var connectionBuilder =
                PiHoleConnection.newBuilder()
                    .setId(id ?: UUID.randomUUID().toString())
                    .setName(name)
                    .let { if (description.isNotBlank()) it.setDescription(description) else it }
                    .setProtocol(protocol)
                    .setHost(host)
                    .setApiPath(apiPath)
                    .let { port.toIntOrNull()?.let { port -> it.setPort(port) } ?: it }
                    .let { if (password.isNotBlank()) it.setPassword(password) else it }
                    .let {
                        if (it.trustAllCertificates != trustAllCertificates)
                            it.setTrustAllCertificates(trustAllCertificates)
                        else it
                    }
                    .let {
                        if (basicAuthUsername.isNotBlank())
                            it.setBasicAuthUsername(basicAuthUsername)
                        else it
                    }
                    .let {
                        if (basicAuthPassword.isNotBlank())
                            it.setBasicAuthPassword(basicAuthPassword)
                        else it
                    }
                    .let {
                        if (basicAuthRealm.isNotBlank()) it.setBasicAuthRealm(basicAuthRealm)
                        else it
                    }

            if (id == null) {
                return@updateUserPreferences builder.addPiHoleConnections(connectionBuilder).build()
            } else {
                val index = userPreferences.piHoleConnectionsList.indexOfFirst { it.id == id }
                return@updateUserPreferences builder
                    .setPiHoleConnections(index, connectionBuilder)
                    .build()
            }
        }
    }

    suspend fun remove() {
        id?.let { runCatching { userPreferencesRepository.removePiHoleConnection(it) } }
    }
}
