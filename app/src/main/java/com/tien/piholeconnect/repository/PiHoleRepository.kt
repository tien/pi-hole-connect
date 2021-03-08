package com.tien.piholeconnect.repository

import com.tien.piholeconnect.model.PiHoleSummary

interface PiHoleRepository {
    suspend fun getStatusSummary(): PiHoleSummary
}