package com.tien.piholeconnect.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tien.piholeconnect.viewmodel.BaseViewModel
import com.tien.piholeconnect.repository.PiHoleRepositoryProvider
import com.tien.piholeconnect.repository.UserPreferencesRepository
import com.tien.piholeconnect.repository.models.GetBlocking200Response
import com.tien.piholeconnect.repository.models.SetBlockingRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val piHoleRepositoryProvider: PiHoleRepositoryProvider,
    userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel(userPreferencesRepository) {
    val isAdsBlockingEnabled =
        piHoleRepositoryProvider.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest {
                it.dnsControlApi.getBlocking().body().blocking ==
                    GetBlocking200Response.Blocking.ENABLED
            }
            .asViewFlowState()

    val metricSummary =
        piHoleRepositoryProvider.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest { it.metricsApi.getMetricsSummary().body() }
            .asViewFlowState()

    val history =
        piHoleRepositoryProvider.selectedPiHoleRepository
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

            piHoleRepositoryProvider
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
