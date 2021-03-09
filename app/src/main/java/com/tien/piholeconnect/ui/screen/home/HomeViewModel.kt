package com.tien.piholeconnect.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.repository.PiHoleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class HomeViewModel constructor(private val piHoleRepository: PiHoleRepository) : ViewModel() {
    private var refreshJob: Job? = null

    var error: Throwable? = null
        private set
    var isRefreshing by mutableStateOf(false)
        private set

    var isAdsBlockingEnabled by mutableStateOf(true)
        private set
    var totalQueries by mutableStateOf(0)
        private set
    var totalBlockedQueries by mutableStateOf(0)
        private set
    var queryBlockingPercentage by mutableStateOf(.0)
        private set
    var blockedDomainListCount by mutableStateOf(0)
        private set

    var queriesOverTime by mutableStateOf(mapOf<Int, Int>())
        private set
    var adsOverTime by mutableStateOf(mapOf<Int, Int>())
        private set

    suspend fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            runCatching {
                isRefreshing = true

                joinAll(
                    viewModelScope.launch {
                        val summary = piHoleRepository.getStatusSummary()

                        isAdsBlockingEnabled = summary.status == "enabled"
                        totalQueries = summary.dnsQueriesToday
                        totalBlockedQueries = summary.adsBlockedToday
                        queryBlockingPercentage = summary.adsPercentageToday
                        blockedDomainListCount = summary.domainsBeingBlocked
                    },
                    viewModelScope.launch {
                        val overTimeData = piHoleRepository.getOverTimeData10Minutes()

                        queriesOverTime = overTimeData.domainsOverTime
                        adsOverTime = overTimeData.adsOverTime
                    }
                )
            }.onFailure {
                error = it
            }
            isRefreshing = false
        }
        refreshJob?.join()
    }
}