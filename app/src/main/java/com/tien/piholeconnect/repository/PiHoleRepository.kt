package com.tien.piholeconnect.repository

import androidx.datastore.core.DataStore
import com.tien.piholeconnect.extension.toKtorURLProtocol
import com.tien.piholeconnect.model.*
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

class PiHoleRepository constructor(
    private val httpClient: HttpClient,
    userPreferencesDataStore: DataStore<UserPreferences>
) : IPiHoleRepository {
    private val currentSelectedPiHoleFlow = userPreferencesDataStore.data.map {
        it.getPiHoleConnections(it.selectedPiHoleConnectionIndex)
    }

    private val baseRequestFlow: Flow<HttpRequestBuilder.() -> Unit> =
        currentSelectedPiHoleFlow.map { piHoleConnection ->
            {
                url {
                    protocol = piHoleConnection.protocol.toKtorURLProtocol()
                    host = piHoleConnection.host
                    encodedPath = piHoleConnection.apiPath
                    port = piHoleConnection.port
                    parameters["auth"] = piHoleConnection.apiToken
                }
            }
        }

    override suspend fun getStatusSummary(): PiHoleSummary =
        baseRequestFlow.first().let { requestBuilder ->
            httpClient.get {
                requestBuilder(this)
                url {
                    parameters.append("summaryRaw", true.toString())
                }
            }
        }

    override suspend fun getOverTimeData10Minutes(): PiHoleOverTimeData =
        baseRequestFlow.first().let { requestBuilder ->
            httpClient.get {
                requestBuilder(this)
                url {
                    parameters.append("overTimeData10mins", true.toString())
                }
            }
        }

    override suspend fun getStatistics(): PiHoleStatistics =
        baseRequestFlow.first().let { requestBuilder ->
            httpClient.get {
                requestBuilder(this)
                url {
                    parameters.append("getQueryTypes", true.toString())
                    parameters.append("topItems", true.toString())
                    parameters.append("topClients", true.toString())
                }
            }
        }

    override suspend fun getLogs(limit: Int): PiHoleLogs =
        baseRequestFlow.first().let { requestBuilder ->
            httpClient.get {
                requestBuilder(this)
                url {
                    parameters.append("getAllQueries", limit.toString())
                }
            }
        }

    override suspend fun getFilterRules(ruleType: RuleType): PiHoleFilterRules =
        baseRequestFlow.first().let { requestBuilder ->
            httpClient.get {
                requestBuilder(this)
                url {
                    parameters.append("list", ruleType.toString().toLowerCase(Locale.ENGLISH))
                }
            }
        }

    override suspend fun addFilterRules(
        rule: String,
        ruleType: RuleType
    ): ModifyFilterRuleResponse =
        baseRequestFlow.first().let { requestBuilder ->
            httpClient.get {
                requestBuilder(this)
                url {
                    parameters.append("list", ruleType.toString().toLowerCase(Locale.ENGLISH))
                    parameters.append("add", rule)
                }
            }
        }

    override suspend fun removeFilterRules(
        rule: String,
        ruleType: RuleType
    ): ModifyFilterRuleResponse =
        baseRequestFlow.first().let { requestBuilder ->
            httpClient.get {
                requestBuilder(this)
                url {
                    parameters.append("list", ruleType.toString().toLowerCase(Locale.ENGLISH))
                    parameters.append("sub", rule)
                }
            }
        }
}