package com.tien.piholeconnect.repository

import androidx.datastore.core.DataStore
import com.tien.piholeconnect.di.DefaultHttpClient
import com.tien.piholeconnect.di.TrustAllCertificatesHttpClient
import com.tien.piholeconnect.model.Authentication
import com.tien.piholeconnect.model.PiHoleConnection
import com.tien.piholeconnect.model.Session
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class PiHoleRepositoryProviderImpl
@Inject
constructor(
    private val piHoleRepositoryFactory: PiHoleV6Repository.Factory,
    userPreferencesRepository: UserPreferencesRepository,
) : PiHoleRepositoryProvider {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val selectedPiHoleRepository =
        userPreferencesRepository.selectedPiHole
            .map { it?.let { piHoleRepositoryFactory.create(it) } }
            .stateIn(
                scope = MainScope(),
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )
            .filterNotNull()
            .mapLatest { it.authenticate() }

    override suspend fun getSelectedPiHoleRepository(): PiHoleV6Repository? {
        return selectedPiHoleRepository.firstOrNull()
    }
}

class PiHoleV6Repository
@AssistedInject
constructor(
    @DefaultHttpClient private val httpClient: HttpClient,
    @TrustAllCertificatesHttpClient private val trustAllCertificatesHttpClient: HttpClient,
    @Assisted private val piHoleConnection: PiHoleConnection,
    private val authenticationDataStore: DataStore<Authentication>,
) {
    @AssistedFactory
    interface Factory {
        fun create(piHoleConnection: PiHoleConnection): PiHoleV6Repository
    }

    private val client = run {
        val baseClient =
            if (piHoleConnection.trustAllCertificates) trustAllCertificatesHttpClient
            else httpClient

        if (
            piHoleConnection.basicAuthUsername.isBlank() &&
                piHoleConnection.basicAuthPassword.isBlank()
        ) {
            return@run baseClient
        }

        baseClient.config {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = piHoleConnection.basicAuthUsername,
                            password = piHoleConnection.basicAuthPassword,
                        )
                    }

                    if (piHoleConnection.basicAuthRealm.isNotBlank()) {
                        realm = piHoleConnection.basicAuthRealm
                    }
                }
            }
        }
    }

    private val baseUrl =
        URLBuilder()
            .apply {
                protocol = piHoleConnection.protocol.toKtorURLProtocol()
                host = piHoleConnection.host
                encodedPath = piHoleConnection.apiPath
                port = piHoleConnection.port
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

    suspend fun authenticate(): PiHoleV6Repository {
        val alreadyLocked = !authenticationMutex.tryLock()

        if (alreadyLocked) {
            authenticationMutex.withLock {
                return this
            }
        }

        try {
            authenticationDataStore.data
                .firstOrNull()
                ?.getSessionsOrDefault(piHoleConnection.id, null)
                ?.also { setSessionId(it.sid) }

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
        val sessionResponse = authenticationApi.addAuth(Password(piHoleConnection.password))

        if (!sessionResponse.success) {
            throw Error("Unable to login")
        }

        val session = sessionResponse.body().session

        if (!session.valid) {
            throw Error("Invalid session")
        }

        authenticationDataStore.updateData {
            it.toBuilder()
                .putSessions(piHoleConnection.id, Session.newBuilder().setSid(session.sid).build())
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
