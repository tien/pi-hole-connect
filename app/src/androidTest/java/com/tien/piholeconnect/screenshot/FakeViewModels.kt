package com.tien.piholeconnect.screenshot

import androidx.datastore.core.DataStore
import com.tien.piholeconnect.model.LoadState
import com.tien.piholeconnect.model.PiHoleConnections
import com.tien.piholeconnect.model.QueryLog
import com.tien.piholeconnect.repository.PiHoleRepository
import com.tien.piholeconnect.repository.PiHoleRepositoryManager
import com.tien.piholeconnect.repository.models.GetDomainsInner
import com.tien.piholeconnect.repository.models.GetMetricsSummary200Response
import com.tien.piholeconnect.repository.models.TotalHistoryHistoryInner
import com.tien.piholeconnect.ui.screen.filterrules.FilterRulesViewModel
import com.tien.piholeconnect.ui.screen.home.HomeViewModel
import com.tien.piholeconnect.ui.screen.log.LogViewModel
import com.tien.piholeconnect.ui.screen.statistics.StatisticsViewModel
import com.tien.piholeconnect.ui.screen.tools.ToolsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

private val fakePiHoleRepositoryManager =
    object : PiHoleRepositoryManager {
        override val selectedPiHoleRepository: Flow<PiHoleRepository?> = flowOf(null)

        override suspend fun getSelectedPiHoleRepository(): PiHoleRepository? = null

        override suspend fun setSelectedPiHole(id: String) {}
    }

private val fakeDataStore =
    object : DataStore<PiHoleConnections> {
        override val data: Flow<PiHoleConnections> = flowOf(PiHoleConnections.getDefaultInstance())

        override suspend fun updateData(
            transform: suspend (t: PiHoleConnections) -> PiHoleConnections
        ): PiHoleConnections = PiHoleConnections.getDefaultInstance()
    }

private fun <T : com.tien.piholeconnect.viewmodel.BaseViewModel> T.configure(): T = apply {
    piHoleConnectionsDataStore = fakeDataStore
}

class FakeHomeViewModel(
    metricSummaryData: GetMetricsSummary200Response,
    historyData: List<TotalHistoryHistoryInner>,
    adsBlockingEnabled: Boolean = true,
) : HomeViewModel(fakePiHoleRepositoryManager) {
    override val metricSummary: StateFlow<LoadState<GetMetricsSummary200Response>> =
        MutableStateFlow(LoadState.Success(metricSummaryData))
    override val history: StateFlow<LoadState<List<TotalHistoryHistoryInner>>> =
        MutableStateFlow(LoadState.Success(historyData))
    override val isAdsBlockingEnabled: StateFlow<LoadState<Boolean>> =
        MutableStateFlow(LoadState.Success(adsBlockingEnabled))
    override val loading: StateFlow<Boolean> = MutableStateFlow(false)
    override val refreshing = MutableStateFlow(false)

    init {
        configure()
    }
}

class FakeStatisticsViewModel(
    topDomainsData: Map<String, Int>,
    topBlockedDomainsData: Map<String, Int>,
    topClientsData: Map<String, Int>,
) : StatisticsViewModel(fakePiHoleRepositoryManager) {
    override val topDomains: StateFlow<LoadState<Map<String, Int>?>> =
        MutableStateFlow(LoadState.Success(topDomainsData))
    override val topBlockedDomains: StateFlow<LoadState<Map<String, Int>?>> =
        MutableStateFlow(LoadState.Success(topBlockedDomainsData))
    override val topClients: StateFlow<LoadState<Map<String, Int>?>> =
        MutableStateFlow(LoadState.Success(topClientsData))
    override val loading: StateFlow<Boolean> = MutableStateFlow(false)
    override val refreshing = MutableStateFlow(false)

    init {
        configure()
    }
}

class FakeLogViewModel(logsData: List<QueryLog>) : LogViewModel(fakePiHoleRepositoryManager) {
    override var logs: StateFlow<LoadState<List<QueryLog>>> =
        MutableStateFlow(LoadState.Success(logsData))
    override val loading: StateFlow<Boolean> = MutableStateFlow(false)
    override val refreshing = MutableStateFlow(false)

    init {
        configure()
    }
}

class FakeFilterRulesViewModel(rulesData: List<GetDomainsInner>) :
    FilterRulesViewModel(fakePiHoleRepositoryManager) {
    override val rules: StateFlow<LoadState<List<GetDomainsInner>>> =
        MutableStateFlow(LoadState.Success(rulesData))
    override val loading: StateFlow<Boolean> = MutableStateFlow(false)
    override val refreshing = MutableStateFlow(false)

    init {
        configure()
    }
}

class FakeToolsViewModel(gravityUpdatedAtData: Long?) :
    ToolsViewModel(fakePiHoleRepositoryManager) {
    override val gravityUpdatedAt: StateFlow<LoadState<Long?>> =
        MutableStateFlow(LoadState.Success(gravityUpdatedAtData))
    override val operationLoadState =
        MutableStateFlow<LoadState<ToolsViewModel.Tool>>(LoadState.Idle())
    override val loading: StateFlow<Boolean> = MutableStateFlow(false)
    override val refreshing = MutableStateFlow(false)

    init {
        configure()
    }
}
