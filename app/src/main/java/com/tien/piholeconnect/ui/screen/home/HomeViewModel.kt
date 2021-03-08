package com.tien.piholeconnect.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tien.piholeconnect.repository.PiHoleRepository

class HomeViewModel constructor(private val piHoleRepository: PiHoleRepository) : ViewModel() {
    var totalQueries by mutableStateOf(0)
        private set
    var totalBlockedQueries by mutableStateOf(0)
        private set
    var queryBlockingPercentage by mutableStateOf(.0)
        private set
    var blockedDomainListCount by mutableStateOf(0)
        private set

    suspend fun refresh() {
        val summary = piHoleRepository.getStatusSummary()

        totalQueries = summary.dnsQueriesToday
        totalBlockedQueries = summary.adsBlockedToday
        queryBlockingPercentage = summary.adsPercentageToday
        blockedDomainListCount = summary.domainsBeingBlocked
    }
}