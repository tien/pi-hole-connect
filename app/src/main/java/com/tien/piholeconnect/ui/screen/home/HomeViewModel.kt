package com.tien.piholeconnect.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tien.piholeconnect.model.ScreenViewModel
import com.tien.piholeconnect.repository.PiHoleRepositoryProvider
import com.tien.piholeconnect.repository.UserPreferencesRepository
import com.tien.piholeconnect.repository.models.GetBlocking200Response
import com.tien.piholeconnect.repository.models.SetBlockingRequest
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
constructor(
    private val piHoleRepositoryProvider: PiHoleRepositoryProvider,
    userPreferencesRepository: UserPreferencesRepository,
) : ScreenViewModel(userPreferencesRepository) {
    val isAdsBlockingEnabled =
        piHoleRepositoryProvider.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest {
                it.dnsControlApi.getBlocking().body().blocking ==
                    GetBlocking200Response.Blocking.ENABLED
            }
            .asRegisteredLoadState()

    val metricSummary =
        piHoleRepositoryProvider.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest { it.metricsApi.getMetricsSummary().body() }
            .asRegisteredLoadState()

    val history =
        piHoleRepositoryProvider.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest { it.metricsApi.getActivityMetrics().body().history ?: listOf() }
            .asRegisteredLoadState()

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

            refresh()
        } catch (error: Throwable) {
            addError(error)
        } finally {
            isPiHoleSwitchLoading = false
        }
    }
}
