package com.tien.piholeconnect.ui.screen.piholeconnection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.tien.piholeconnect.model.PiHoleConfiguration
import com.tien.piholeconnect.model.PiHoleConnection
import com.tien.piholeconnect.model.PiHoleConnections
import com.tien.piholeconnect.model.PiHoleMetadata
import com.tien.piholeconnect.model.URLProtocol
import com.tien.piholeconnect.util.populateDefaultValues
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PiHoleConnectionViewModel
@Inject
constructor(private val piHoleConnectionsDataStore: DataStore<PiHoleConnections>) : ViewModel() {
    private val default = PiHoleConnection.newBuilder().populateDefaultValues()

    private var id: String? = null
    var name: String by mutableStateOf(default.metadata.name)
    var description: String by mutableStateOf(default.metadata.description)
    var protocol: URLProtocol by mutableStateOf(default.configuration.protocol)
    var host: String by mutableStateOf(default.configuration.host)
    var apiPath: String by mutableStateOf(default.configuration.apiPath)
    var port: String by mutableStateOf(default.configuration.port.toString())
    var password: String by mutableStateOf(default.configuration.password)
    var basicAuthUsername: String by mutableStateOf(default.configuration.basicAuthUsername)
    var basicAuthPassword: String by mutableStateOf(default.configuration.basicAuthPassword)
    var basicAuthRealm: String by mutableStateOf(default.configuration.basicAuthRealm)
    var trustAllCertificates: Boolean by mutableStateOf(default.configuration.trustAllCertificates)
    var shouldShowDeleteButton: Boolean by mutableStateOf(false)
        private set

    suspend fun loadDataForId(piHoleConnectionId: String) {
        val connection =
            piHoleConnectionsDataStore.data
                .first()
                .getConnectionsOrThrow(piHoleConnectionId)
                .toBuilder()
                .build()

        id = piHoleConnectionId
        name = connection.metadata.name
        description = connection.metadata.description
        protocol = connection.configuration.protocol
        host = connection.configuration.host
        apiPath = connection.configuration.apiPath
        port = connection.configuration.port.toString()
        password = connection.configuration.password
        basicAuthUsername = connection.configuration.basicAuthUsername
        basicAuthPassword = connection.configuration.basicAuthPassword
        basicAuthRealm = connection.configuration.basicAuthRealm
        trustAllCertificates = connection.configuration.trustAllCertificates
        shouldShowDeleteButton = true
    }

    suspend fun save() {
        piHoleConnectionsDataStore.updateData {
            it.toBuilder()
                .putConnections(
                    id ?: UUID.randomUUID().toString(),
                    PiHoleConnection.newBuilder()
                        .setMetadata(
                            PiHoleMetadata.newBuilder().setName(name).let {
                                if (description.isNotBlank()) it.setDescription(description) else it
                            }
                        )
                        .setConfiguration(
                            PiHoleConfiguration.newBuilder()
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
                                    if (basicAuthRealm.isNotBlank())
                                        it.setBasicAuthRealm(basicAuthRealm)
                                    else it
                                }
                        )
                        .build(),
                )
                .build()
        }
    }

    suspend fun remove() {
        require(id !== null)
        piHoleConnectionsDataStore.updateData { it.toBuilder().removeConnections(id).build() }
    }
}
