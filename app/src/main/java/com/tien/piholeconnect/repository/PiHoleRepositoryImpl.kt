package com.tien.piholeconnect.repository

import com.tien.piholeconnect.di.DefaultHttpClient
import com.tien.piholeconnect.di.TrustAllCertificatesHttpClient
import com.tien.piholeconnect.model.ModifyFilterRuleResponse
import com.tien.piholeconnect.model.PiHoleFilterRules
import com.tien.piholeconnect.model.PiHoleLogs
import com.tien.piholeconnect.model.PiHoleOverTimeData
import com.tien.piholeconnect.model.PiHoleStatistics
import com.tien.piholeconnect.model.PiHoleStatusResponse
import com.tien.piholeconnect.model.PiHoleSummary
import com.tien.piholeconnect.model.RuleType
import com.tien.piholeconnect.util.toKtorURLProtocol
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.BasicAuthProvider
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.encodedPath
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PiHoleRepositoryImpl
@Inject
constructor(
    @DefaultHttpClient private val defaultHttpClient: HttpClient,
    @TrustAllCertificatesHttpClient trustAllCertificatesHttpClient: HttpClient,
    userPreferencesRepository: UserPreferencesRepository,
) : PiHoleRepository {
    private val baseRequestFlow: Flow<Pair<HttpClient, HttpRequestBuilder.() -> Unit>> =
        userPreferencesRepository.selectedPiHoleFlow.map { piHoleConnection ->
            piHoleConnection ?: throw Exception("Pi-hole connection hasn't been setup")

            val httpClient =
                if (piHoleConnection.trustAllCertificates) trustAllCertificatesHttpClient
                else defaultHttpClient

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
                if (
                    piHoleConnection.basicAuthUsername.isNotBlank() ||
                        piHoleConnection.basicAuthPassword.isNotBlank()
                ) {
                    val basicAuthProvider =
                        BasicAuthProvider(
                            credentials = {
                                BasicAuthCredentials(
                                    username = piHoleConnection.basicAuthUsername,
                                    password = piHoleConnection.basicAuthPassword,
                                )
                            },
                            realm = piHoleConnection.basicAuthRealm.ifBlank { null },
                            sendWithoutRequestCallback = { true },
                        )
                    let { runBlocking { basicAuthProvider.addRequestHeaders(it) } }
                }
            }

            Pair(httpClient, requestBuilder)
        }

    override suspend fun getStatusSummary(): PiHoleSummary =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient
                    .get {
                        requestBuilder(this)
                        url { parameters["summaryRaw"] = true.toString() }
                    }
                    .body()
            }
        }

    override suspend fun getOverTimeData10Minutes(): PiHoleOverTimeData =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient
                    .get {
                        requestBuilder(this)
                        url { parameters["overTimeData10mins"] = true.toString() }
                    }
                    .body()
            }
        }

    override suspend fun getStatistics(): PiHoleStatistics =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient
                    .get {
                        requestBuilder(this)
                        url {
                            parameters["getQueryTypes"] = true.toString()
                            parameters["topItems"] = true.toString()
                            parameters["topClients"] = true.toString()
                        }
                    }
                    .body()
            }
        }

    override suspend fun getLogs(limit: Int): PiHoleLogs =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient
                    .get {
                        requestBuilder(this)
                        url { parameters["getAllQueries"] = limit.toString() }
                    }
                    .body()
            }
        }

    override suspend fun getFilterRules(ruleType: RuleType): PiHoleFilterRules =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient
                    .get {
                        requestBuilder(this)
                        url { parameters["list"] = ruleType.toString().lowercase(Locale.ENGLISH) }
                    }
                    .body()
            }
        }

    override suspend fun addFilterRule(rule: String, ruleType: RuleType): ModifyFilterRuleResponse =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient
                    .get {
                        requestBuilder(this)
                        url {
                            parameters["list"] = ruleType.toString().lowercase(Locale.ENGLISH)
                            parameters["add"] = rule
                        }
                    }
                    .body()
            }
        }

    override suspend fun removeFilterRule(
        rule: String,
        ruleType: RuleType,
    ): ModifyFilterRuleResponse =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient
                    .get {
                        requestBuilder(this)
                        url {
                            parameters["list"] = ruleType.toString().lowercase(Locale.ENGLISH)
                            parameters["sub"] = rule
                        }
                    }
                    .body()
            }
        }

    override suspend fun disable(duration: Duration): PiHoleStatusResponse =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient
                    .get {
                        requestBuilder(this)
                        url {
                            parameters["disable"] =
                                if (duration.isInfinite()) 0.toString()
                                else duration.inWholeSeconds.toString()
                        }
                    }
                    .body()
            }
        }

    override suspend fun enable(): PiHoleStatusResponse =
        withContext(Dispatchers.IO) {
            baseRequestFlow.first().let { (httpClient, requestBuilder) ->
                httpClient
                    .get {
                        requestBuilder(this)
                        url { parameters["enable"] = true.toString() }
                    }
                    .body()
            }
        }
}
