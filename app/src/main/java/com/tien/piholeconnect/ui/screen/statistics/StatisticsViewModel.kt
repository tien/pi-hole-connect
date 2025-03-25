package com.tien.piholeconnect.ui.screen.statistics

import com.tien.piholeconnect.model.ScreenViewModel
import com.tien.piholeconnect.repository.PiHoleRepositoryProvider
import com.tien.piholeconnect.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel
@Inject
constructor(
    piHoleRepositoryProvider: PiHoleRepositoryProvider,
    userPreferencesRepository: UserPreferencesRepository,
) : ScreenViewModel(userPreferencesRepository) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val topDomains =
        piHoleRepositoryProvider.selectedPiHoleRepositoryFlow
            .filterNotNull()
            .mapLatest {
                it.metricsApi
                    .getMetricsTopDomains(blocked = false)
                    .body()
                    .domains
                    ?.map { (it.domain ?: "") to (it.count ?: 0) }
                    ?.toMap()
            }
            .asRegisteredLoadState()

    @OptIn(ExperimentalCoroutinesApi::class)
    val topBlockedDomains =
        piHoleRepositoryProvider.selectedPiHoleRepositoryFlow
            .filterNotNull()
            .mapLatest {
                it.metricsApi
                    .getMetricsTopDomains(blocked = true)
                    .body()
                    .domains
                    ?.map { (it.domain ?: "") to (it.count ?: 0) }
                    ?.toMap()
            }
            .asRegisteredLoadState()

    @OptIn(ExperimentalCoroutinesApi::class)
    val topClients =
        piHoleRepositoryProvider.selectedPiHoleRepositoryFlow
            .filterNotNull()
            .mapLatest {
                it.metricsApi
                    .getMetricsTopClients()
                    .body()
                    .clients
                    ?.map { (it.name ?: "") to (it.count ?: 0) }
                    ?.toMap()
            }
            .asRegisteredLoadState()
}
