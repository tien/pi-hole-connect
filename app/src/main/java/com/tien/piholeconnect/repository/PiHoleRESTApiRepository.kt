package com.tien.piholeconnect.repository

import com.tien.piholeconnect.model.PiHoleOverTimeData
import com.tien.piholeconnect.model.PiHoleSummary
import io.ktor.client.*
import io.ktor.client.request.*

class PiHoleRESTApiRepository constructor(private val httpClient: HttpClient) : PiHoleRepository {
    override suspend fun getStatusSummary(): PiHoleSummary = httpClient.get("http://pi.hole/admin/api.php")
    override suspend fun getOverTimeData10Minutes(): PiHoleOverTimeData = httpClient.get("http://pi.hole/admin/api.php?overTimeData10mins")
}