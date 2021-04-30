package com.tien.piholeconnect.repository

import androidx.datastore.core.DataStore
import com.tien.piholeconnect.di.DefaultHttpClient
import com.tien.piholeconnect.di.TrustAllCertificatesHttpClient
import com.tien.piholeconnect.model.*
import com.tien.piholeconnect.util.toKtorURLProtocol
import io.ktor.client.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration

class PiHoleRepositoryImpl @Inject constructor(
    @DefaultHttpClient private val defaultHttpClient: HttpClient,
    @TrustAllCertificatesHttpClient trustAllCertificatesHttpClient: HttpClient,
    userPreferencesDataStore: DataStore<UserPreferences>
) : PiHoleRepository {
    private val currentSelectedPiHoleFlow = userPreferencesDataStore.data.map { userPreferences ->
        userPreferences.piHoleConnectionsList.firstOrNull { it.id == userPreferences.selectedPiHoleConnectionId }
            ?: userPreferences.getPiHoleConnections(0)
    }

    private val baseRequestFlow: Flow<Pair<HttpClient, HttpRequestBuilder.() -> Unit>> =
        currentSelectedPiHoleFlow.map { piHoleConnection ->
            val httpClient =
                if (piHoleConnection.trustAllCertificates) trustAllCertificatesHttpClient else defaultHttpClient

            val requestBuilder: HttpRequestBuilder.() -> Unit = {
                url {
                    protocol = piHoleConnection.protocol.toKtorURLProtocol()
                    host = piHoleConnection.host
                    encodedPath = piHoleConnection.apiPath
                    port = piHoleConnection.port
                    parameters["auth"] = piHoleConnection.apiToken
                }
                if (piHoleConnection.basicAuthUsername.isNotBlank() || piHoleConnection.basicAuthPassword.isNotBlank()) {
                    val basicAuthProvider = BasicAuthProvider(
                        username = piHoleConnection.basicAuthUsername,
                        password = piHoleConnection.basicAuthPassword,
                        realm = if (piHoleConnection.basicAuthRealm.isBlank()) null else piHoleConnection.basicAuthRealm,
                        sendWithoutRequest = true
                    )
                    let {
                        // We know that BasicAuthProvider addRequestHeaders is synchronous, it's only a suspend function to conform to the AuthProvider Interface
                        @Suppress("BlockingMethodInNonBlockingContext")
                        runBlocking {
                            basicAuthProvider.addRequestHeaders(it)
                        }
                    }
                }
            }

            Pair(httpClient, requestBuilder)
        }

    override suspend fun getStatusSummary(): PiHoleSummary =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient.get {
                    requestBuilder(this)
                    url {
                        parameters.append("summaryRaw", true.toString())
                    }
                }
            }
        }

    override suspend fun getOverTimeData10Minutes(): PiHoleOverTimeData =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient.get {
                    requestBuilder(this)
                    url {
                        parameters.append("overTimeData10mins", true.toString())
                    }
                }
            }
        }

    override suspend fun getStatistics(): PiHoleStatistics =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient.get {
                    requestBuilder(this)
                    url {
                        parameters.append("getQueryTypes", true.toString())
                        parameters.append("topItems", true.toString())
                        parameters.append("topClients", true.toString())
                    }
                }
            }
        }

    override suspend fun getLogs(limit: Int): PiHoleLogs =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient.get {
                    requestBuilder(this)
                    url {
                        parameters.append("getAllQueries", limit.toString())
                    }
                }
            }
        }

    override suspend fun getFilterRules(ruleType: RuleType): PiHoleFilterRules =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient.get {
                    requestBuilder(this)
                    url {
                        parameters.append("list", ruleType.toString().toLowerCase(Locale.ENGLISH))
                    }
                }
            }
        }

    override suspend fun addFilterRule(
        rule: String,
        ruleType: RuleType
    ): ModifyFilterRuleResponse =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient.get {
                    requestBuilder(this)
                    url {
                        parameters.append("list", ruleType.toString().toLowerCase(Locale.ENGLISH))
                        parameters.append("add", rule)
                    }
                }
            }
        }

    override suspend fun removeFilterRule(
        rule: String,
        ruleType: RuleType
    ): ModifyFilterRuleResponse =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient.get {
                    requestBuilder(this)
                    url {
                        parameters.append("list", ruleType.toString().toLowerCase(Locale.ENGLISH))
                        parameters.append("sub", rule)
                    }
                }
            }
        }

    override suspend fun disable(duration: Duration): PiHoleEnableStatus =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient.get {
                    requestBuilder(this)
                    url {
                        parameters["disable"] =
                            if (duration == Duration.INFINITE) 0.toString() else duration.inSeconds.toString()
                    }
                }
            }
        }

    override suspend fun enable(): PiHoleEnableStatus =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient.get {
                    requestBuilder(this)
                    url {
                        parameters["enable"] = true.toString()
                    }
                }
            }
        }
}