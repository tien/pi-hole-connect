package com.tien.piholeconnect.repository

import com.tien.piholeconnect.model.PiHoleLogs
import com.tien.piholeconnect.model.PiHoleOverTimeData
import com.tien.piholeconnect.model.PiHoleStatistics
import com.tien.piholeconnect.model.PiHoleSummary

interface IPiHoleRepository {
    suspend fun getStatusSummary(): PiHoleSummary
    suspend fun getOverTimeData10Minutes(): PiHoleOverTimeData
    suspend fun getStatistics(): PiHoleStatistics
    suspend fun getLogs(limit: Int): PiHoleLogs
}