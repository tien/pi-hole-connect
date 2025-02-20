package com.tien.piholeconnect.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tien.piholeconnect.model.PiHoleConnectionAwareViewModel
import com.tien.piholeconnect.model.PiHoleStatus
import com.tien.piholeconnect.repository.PiHoleRepository
import com.tien.piholeconnect.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val piHoleRepository: PiHoleRepository,
    userPreferencesRepository: UserPreferencesRepository,
) : PiHoleConnectionAwareViewModel(userPreferencesRepository) {
    var isPiHoleSwitchLoading by mutableStateOf(false)
        private set

    var isAdsBlockingEnabled by mutableStateOf(true)
        private set

    var totalQueries by mutableIntStateOf(0)
        private set

    var totalBlockedQueries by mutableIntStateOf(0)
        private set

    var queryBlockingPercentage by mutableDoubleStateOf(.0)
        private set

    var blockedDomainListCount by mutableIntStateOf(0)
        private set

    var uniqueClients by mutableIntStateOf(0)

    var queriesOverTime by mutableStateOf(mapOf<Int, Int>())
        private set

    var adsOverTime by mutableStateOf(mapOf<Int, Int>())
        private set

    override suspend fun queueRefresh() = coroutineScope {
        val deferredSummary = async { piHoleRepository.getStatusSummary() }
        val deferredOverTimeData = async { piHoleRepository.getOverTimeData10Minutes() }

        awaitAll(deferredSummary, deferredOverTimeData)

        deferredSummary.await().let { summary ->
            isAdsBlockingEnabled = summary.status == "enabled"
            totalQueries = summary.dnsQueriesToday
            totalBlockedQueries = summary.adsBlockedToday
            queryBlockingPercentage = summary.adsPercentageToday
            blockedDomainListCount = summary.domainsBeingBlocked
            uniqueClients = summary.uniqueClients
        }
        deferredOverTimeData.await().let { overTimeData ->
            queriesOverTime = overTimeData.domainsOverTime
            adsOverTime = overTimeData.adsOverTime
        }
    }

    suspend fun disable(duration: Duration) {
        toggle(false, duration)
    }

    suspend fun enable() {
        toggle(true, Duration.INFINITE)
    }

    private suspend fun toggle(state: Boolean, duration: Duration) {
        runCatching {
                isPiHoleSwitchLoading = true

                val result =
                    if (state) piHoleRepository.enable() else piHoleRepository.disable(duration)

                isAdsBlockingEnabled =
                    when (result.status) {
                        PiHoleStatus.ENABLED -> true
                        PiHoleStatus.DISABLED -> false
                        else -> isAdsBlockingEnabled
                    }
            }
            .onFailure { error = it }
        isPiHoleSwitchLoading = false
    }
}
