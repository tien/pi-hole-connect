package com.tien.piholeconnect.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tien.piholeconnect.repository.PiHoleRepositoryManager
import com.tien.piholeconnect.repository.models.GetBlocking200Response
import com.tien.piholeconnect.repository.models.SetBlockingRequest
import com.tien.piholeconnect.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel
@Inject
constructor(private val piHoleRepositoryManager: PiHoleRepositoryManager) : BaseViewModel() {
    val isAdsBlockingEnabled =
        piHoleRepositoryManager.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest {
                it.dnsControlApi.getBlocking().body().blocking ==
                    GetBlocking200Response.Blocking.ENABLED
            }
            .asViewFlowState()

    val metricSummary =
        piHoleRepositoryManager.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest { it.metricsApi.getMetricsSummary().body() }
            .asViewFlowState()

    val history =
        piHoleRepositoryManager.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest { it.metricsApi.getActivityMetrics().body().history ?: listOf() }
            .asViewFlowState()

    var isPiHoleSwitchLoading by mutableStateOf(false)
        private set

    suspend fun disable(duration: Duration) {
        toggle(false, duration)
    }

    suspend fun enable() {
        toggle(true, Duration.INFINITE)
    }

    private suspend fun toggle(state: Boolean, duration: Duration) {
        try {
            isPiHoleSwitchLoading = true

            piHoleRepositoryManager
                .getSelectedPiHoleRepository()
                ?.dnsControlApi
                ?.setBlocking(
                    SetBlockingRequest(
                        state,
                        if (duration.isInfinite()) null else duration.inWholeSeconds.toDouble(),
                    )
                )

            backgroundRefresh()
        } catch (error: Exception) {
            emitError(error)
        } finally {
            isPiHoleSwitchLoading = false
        }
    }
}
