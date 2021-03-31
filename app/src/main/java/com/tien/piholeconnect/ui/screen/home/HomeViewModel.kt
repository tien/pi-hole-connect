package com.tien.piholeconnect.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tien.piholeconnect.model.RefreshableViewModel
import com.tien.piholeconnect.repository.PiHoleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class HomeViewModel @Inject constructor(private val piHoleRepository: PiHoleRepository) :
    RefreshableViewModel() {
    var isPiHoleSwitchLoading by mutableStateOf(false)
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

    override fun CoroutineScope.queueRefresh() = launch {
        joinAll(
            launch {
                val summary = piHoleRepository.getStatusSummary()

                isAdsBlockingEnabled = summary.status == "enabled"
                totalQueries = summary.dnsQueriesToday
                totalBlockedQueries = summary.adsBlockedToday
                queryBlockingPercentage = summary.adsPercentageToday
                blockedDomainListCount = summary.domainsBeingBlocked
            },
            launch {
                val overTimeData = piHoleRepository.getOverTimeData10Minutes()

                queriesOverTime = overTimeData.domainsOverTime
                adsOverTime = overTimeData.adsOverTime
            }
        )
    }

    suspend fun disable(duration: Duration) {
        runCatching {
            isPiHoleSwitchLoading = true
            piHoleRepository.disable(duration)
            refresh()
        }.onFailure { error = it }
        isPiHoleSwitchLoading = false
    }

    suspend fun enable() {
        runCatching {
            isPiHoleSwitchLoading = true
            piHoleRepository.enable()
            refresh()
        }.onFailure { error = it }
        isPiHoleSwitchLoading = false
    }
}