package com.tien.piholeconnect.ui.screen.statistics

import com.tien.piholeconnect.model.BaseViewModel
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
) : BaseViewModel(userPreferencesRepository) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val topDomains =
        piHoleRepositoryProvider.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest {
                it.metricsApi
                    .getMetricsTopDomains(blocked = false)
                    .body()
                    .domains
                    ?.map { (it.domain ?: "") to (it.count ?: 0) }
                    ?.toMap()
            }
            .asViewFlowState()

    @OptIn(ExperimentalCoroutinesApi::class)
    val topBlockedDomains =
        piHoleRepositoryProvider.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest {
                it.metricsApi
                    .getMetricsTopDomains(blocked = true)
                    .body()
                    .domains
                    ?.map { (it.domain ?: "") to (it.count ?: 0) }
                    ?.toMap()
            }
            .asViewFlowState()

    @OptIn(ExperimentalCoroutinesApi::class)
    val topClients =
        piHoleRepositoryProvider.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest {
                it.metricsApi
                    .getMetricsTopClients()
                    .body()
                    .clients
                    ?.map { (it.name ?: "") to (it.count ?: 0) }
                    ?.toMap()
            }
            .asViewFlowState()
}
