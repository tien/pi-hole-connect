package com.tien.piholeconnect.repository

import com.tien.piholeconnect.di.DefaultHttpClient
import com.tien.piholeconnect.di.TrustAllCertificatesHttpClient
import com.tien.piholeconnect.model.*
import com.tien.piholeconnect.util.toKtorURLProtocol
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*
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
    userPreferencesRepository: UserPreferencesRepository
) : PiHoleRepository {
    private val baseRequestFlow: Flow<Pair<HttpClient, HttpRequestBuilder.() -> Unit>> =
        userPreferencesRepository.selectedPiHoleFlow.map { piHoleConnection ->
            val httpClient =
                if (piHoleConnection.trustAllCertificates) trustAllCertificatesHttpClient else defaultHttpClient

            val requestBuilder: HttpRequestBuilder.() -> Unit = {
                url {
                    protocol = piHoleConnection.protocol.toKtorURLProtocol()
                    host = piHoleConnection.host
                    encodedPath = piHoleConnection.apiPath
                    port = piHoleConnection.port
                    if (piHoleConnection.apiToken.isNotBlank()) {
                        parameters["auth"] = piHoleConnection.apiToken
                    }
                }
                if (piHoleConnection.basicAuthUsername.isNotBlank() || piHoleConnection.basicAuthPassword.isNotBlank()) {
                    val basicAuthProvider = BasicAuthProvider(credentials = {
                        BasicAuthCredentials(
                            username = piHoleConnection.basicAuthUsername,
                            password = piHoleConnection.basicAuthPassword
                        )
                    },
                        realm = piHoleConnection.basicAuthRealm.ifBlank { null },
                        sendWithoutRequestCallback = { true })
                    let {
                        // We know that BasicAuthProvider addRequestHeaders is synchronous, it's only a suspend function to conform to the AuthProvider Interface
                        @Suppress("BlockingMethodInNonBlockingContext") runBlocking {
                            basicAuthProvider.addRequestHeaders(it)
                        }
                    }
                }
            }

            Pair(httpClient, requestBuilder)
        }

    override suspend fun getStatusSummary(): PiHoleSummary = withContext(Dispatchers.IO) {
        baseRequestFlow.first().let { (httpClient, requestBuilder) ->
            httpClient.get {
                requestBuilder(this)
                url {
                    parameters["summaryRaw"] = true.toString()
                }
            }.body()
        }
    }

    override suspend fun getOverTimeData10Minutes(): PiHoleOverTimeData =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient.get {
                    requestBuilder(this)
                    url {
                        parameters["overTimeData10mins"] = true.toString()
                    }
                }.body()
            }
        }

    override suspend fun getStatistics(): PiHoleStatistics = withContext(Dispatchers.IO) {
        baseRequestFlow.first().let { (httpClient, requestBuilder) ->
            httpClient.get {
                requestBuilder(this)
                url {
                    parameters["getQueryTypes"] = true.toString()
                    parameters["topItems"] = true.toString()
                    parameters["topClients"] = true.toString()
                }
            }.body()
        }
    }

    override suspend fun getLogs(limit: Int): PiHoleLogs = withContext(Dispatchers.IO) {
        baseRequestFlow.first().let { (httpClient, requestBuilder) ->
            httpClient.get {
                requestBuilder(this)
                url {
                    parameters["getAllQueries"] = limit.toString()
                }
            }.body()
        }
    }

    override suspend fun getFilterRules(ruleType: RuleType): PiHoleFilterRules =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient.get {
                    requestBuilder(this)
                    url {
                        parameters["list"] = ruleType.toString().lowercase(Locale.ENGLISH)
                    }
                }.body()
            }
        }

    override suspend fun addFilterRule(
        rule: String, ruleType: RuleType
    ): ModifyFilterRuleResponse = withContext(Dispatchers.IO) {
        baseRequestFlow.first().let { (httpClient, requestBuilder) ->
            httpClient.get {
                requestBuilder(this)
                url {
                    parameters["list"] = ruleType.toString().lowercase(Locale.ENGLISH)
                    parameters["add"] = rule
                }
            }.body()
        }
    }

    override suspend fun removeFilterRule(
        rule: String, ruleType: RuleType
    ): ModifyFilterRuleResponse = withContext(Dispatchers.IO) {
        baseRequestFlow.first().let { (httpClient, requestBuilder) ->
            httpClient.get {
                requestBuilder(this)
                url {
                    parameters["list"] = ruleType.toString().lowercase(Locale.ENGLISH)
                    parameters["sub"] = rule
                }
            }.body()
        }
    }

    override suspend fun disable(duration: Duration): PiHoleStatusResponse =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient.get {
                    requestBuilder(this)
                    url {
                        parameters["disable"] =
                            if (duration.isInfinite()) 0.toString() else duration.inWholeSeconds.toString()
                    }
                }.body()
            }
        }

    override suspend fun enable(): PiHoleStatusResponse = withContext(Dispatchers.IO) {
        baseRequestFlow.first().let { (httpClient, requestBuilder) ->
            httpClient.get {
                requestBuilder(this)
                url {
                    parameters["enable"] = true.toString()
                }
            }.body()
        }
    }
}
