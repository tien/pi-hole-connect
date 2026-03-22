package com.tien.piholeconnect.ui.screen.statistics

import com.tien.piholeconnect.repository.PiHoleRepositoryManager
import com.tien.piholeconnect.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel
open class StatisticsViewModel
@Inject
constructor(piHoleRepositoryManager: PiHoleRepositoryManager) : BaseViewModel() {
  @OptIn(ExperimentalCoroutinesApi::class)
  open val topDomains =
      piHoleRepositoryManager.selectedPiHoleRepository
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
  open val topBlockedDomains =
      piHoleRepositoryManager.selectedPiHoleRepository
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
  open val topClients =
      piHoleRepositoryManager.selectedPiHoleRepository
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
