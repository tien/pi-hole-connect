package com.tien.piholeconnect.repository

import androidx.datastore.core.DataStore
import com.tien.piholeconnect.di.DefaultHttpClient
import com.tien.piholeconnect.di.TrustAllCertificatesHttpClient
import com.tien.piholeconnect.model.PiHoleConfiguration
import com.tien.piholeconnect.model.PiHoleConnections
import com.tien.piholeconnect.model.PiHoleSession
import com.tien.piholeconnect.repository.apis.ActionsApi
import com.tien.piholeconnect.repository.apis.AuthenticationApi
import com.tien.piholeconnect.repository.apis.ClientManagementApi
import com.tien.piholeconnect.repository.apis.DHCPApi
import com.tien.piholeconnect.repository.apis.DNSControlApi
import com.tien.piholeconnect.repository.apis.DocumentationApi
import com.tien.piholeconnect.repository.apis.DomainManagementApi
import com.tien.piholeconnect.repository.apis.FTLInformationApi
import com.tien.piholeconnect.repository.apis.GroupManagementApi
import com.tien.piholeconnect.repository.apis.ListManagementApi
import com.tien.piholeconnect.repository.apis.MetricsApi
import com.tien.piholeconnect.repository.apis.NetworkInformationApi
import com.tien.piholeconnect.repository.apis.PiHoleConfigurationApi
import com.tien.piholeconnect.repository.models.Password
import com.tien.piholeconnect.repository.models.SessionSession
import com.tien.piholeconnect.util.toKtorURLProtocol
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PiHoleRepository
@AssistedInject
constructor(
    @Assisted private val id: String,
    @Assisted private val configuration: PiHoleConfiguration,
    @DefaultHttpClient private val httpClient: HttpClient,
    @TrustAllCertificatesHttpClient private val trustAllCertificatesHttpClient: HttpClient,
    private val piHoleConnectionDataStore: DataStore<PiHoleConnections>,
) {
    @AssistedFactory
    interface Factory {
        fun create(id: String, configuration: PiHoleConfiguration): PiHoleRepository
    }

    private val client = run {
        val baseClient =
            if (configuration.trustAllCertificates) trustAllCertificatesHttpClient else httpClient

        if (
            configuration.basicAuthUsername.isBlank() && configuration.basicAuthPassword.isBlank()
        ) {
            return@run baseClient
        }

        baseClient.config {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = configuration.basicAuthUsername,
                            password = configuration.basicAuthPassword,
                        )
                    }

                    if (configuration.basicAuthRealm.isNotBlank()) {
                        realm = configuration.basicAuthRealm
                    }
                }
            }
        }
    }

    private val baseUrl =
        URLBuilder()
            .apply {
                protocol = configuration.protocol.toKtorURLProtocol()
                host = configuration.host
                encodedPath = configuration.apiPath
                port = configuration.port
            }
            .buildString()

    val actionsApi = ActionsApi(baseUrl, client)

    val authenticationApi = AuthenticationApi(baseUrl, client)

    val clientManagementApi = ClientManagementApi(baseUrl, client)

    val dhcpApi = DHCPApi(baseUrl, client)

    val dnsControlApi = DNSControlApi(baseUrl, client)

    val documentationApi = DocumentationApi(baseUrl, client)

    val domainManagementApi = DomainManagementApi(baseUrl, client)

    val ftlInformationApi = FTLInformationApi(baseUrl, client)

    val groupManagementApi = GroupManagementApi(baseUrl, client)

    val listManagementApi = ListManagementApi(baseUrl, client)

    val metricsApi = MetricsApi(baseUrl, client)

    val networkInformationApi = NetworkInformationApi(baseUrl, client)

    val piHoleConfigurationApi = PiHoleConfigurationApi(baseUrl, client)

    val authenticationMutex = Mutex()

    suspend fun authenticate(): PiHoleRepository {
        val alreadyLocked = !authenticationMutex.tryLock()

        if (alreadyLocked) {
            authenticationMutex.withLock {
                return this
            }
        }

        try {
            piHoleConnectionDataStore.data.first().connectionsMap[id]?.also {
                if (it.hasSession()) {
                    setSessionId(it.session.sid)
                }
            }

            val currentSessionResponse = authenticationApi.getAuth()

            val currentSession =
                if (!currentSessionResponse.success) null else currentSessionResponse.body().session

            val validSession =
                if (currentSession != null && currentSession.valid) currentSession else login()

            if (validSession.sid != null) {
                setSessionId(validSession.sid)
            }

            return this
        } finally {
            authenticationMutex.unlock()
        }
    }

    suspend fun login(): SessionSession {
        val sessionResponse = authenticationApi.addAuth(Password(configuration.password))

        if (!sessionResponse.success) {
            throw Exception("Unable to login")
        }

        val session = sessionResponse.body().session

        if (!session.valid) {
            throw Exception("Invalid session")
        }

        piHoleConnectionDataStore.updateData {
            it.toBuilder()
                .putConnections(
                    id,
                    it.getConnectionsOrThrow(id)
                        .toBuilder()
                        .setSession(PiHoleSession.newBuilder().setSid(session.sid))
                        .build(),
                )
                .build()
        }

        return session
    }

    private fun setSessionId(sid: String) {
        actionsApi.setApiKey(sid, "X-FTL-SID")
        authenticationApi.setApiKey(sid, "X-FTL-SID")
        clientManagementApi.setApiKey(sid, "X-FTL-SID")
        dhcpApi.setApiKey(sid, "X-FTL-SID")
        dnsControlApi.setApiKey(sid, "X-FTL-SID")
        documentationApi.setApiKey(sid, "X-FTL-SID")
        domainManagementApi.setApiKey(sid, "X-FTL-SID")
        ftlInformationApi.setApiKey(sid, "X-FTL-SID")
        groupManagementApi.setApiKey(sid, "X-FTL-SID")
        listManagementApi.setApiKey(sid, "X-FTL-SID")
        metricsApi.setApiKey(sid, "X-FTL-SID")
        networkInformationApi.setApiKey(sid, "X-FTL-SID")
        piHoleConfigurationApi.setApiKey(sid, "X-FTL-SID")
    }
}
