package com.tien.piholeconnect.repository

import androidx.datastore.core.DataStore
import com.tien.piholeconnect.extension.toKtorURLProtocol
import com.tien.piholeconnect.model.PiHoleOverTimeData
import com.tien.piholeconnect.model.PiHoleSummary
import com.tien.piholeconnect.model.UserPreferences
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PiHoleRepository constructor(
    private val httpClient: HttpClient,
    userPreferencesDataStore: DataStore<UserPreferences>
) : IPiHoleRepository {
    private val currentSelectedPiHoleFlow = userPreferencesDataStore.data.map {
        it.getPiHoleConnections(it.selectedPiHoleConnectionIndex)
    }

    private val baseUrlFlow: Flow<URLBuilder.(URLBuilder) -> Unit> =
        currentSelectedPiHoleFlow.map { piHoleConnection ->
            {
                protocol = piHoleConnection.protocol.toKtorURLProtocol()
                host = piHoleConnection.host
                encodedPath = piHoleConnection.apiPath
                port = piHoleConnection.port
            }
        }

    override suspend fun getStatusSummary(): PiHoleSummary =
        baseUrlFlow.first().let { urlBuilder ->
            httpClient.get {
                url {
                    urlBuilder(this)
                }
            }
        }

    override suspend fun getOverTimeData10Minutes(): PiHoleOverTimeData =
        baseUrlFlow.first().let { urlBuilder ->
            httpClient.get {
                url {
                    urlBuilder(this)
                    parameters.append("overTimeData10mins", true.toString())
                }
            }
        }
}