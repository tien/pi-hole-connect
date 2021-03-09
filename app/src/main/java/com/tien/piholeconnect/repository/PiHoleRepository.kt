package com.tien.piholeconnect.repository

import com.tien.piholeconnect.model.PiHoleOverTimeData
import com.tien.piholeconnect.model.PiHoleSummary

interface PiHoleRepository {
    suspend fun getStatusSummary(): PiHoleSummary
    suspend fun getOverTimeData10Minutes(): PiHoleOverTimeData
}