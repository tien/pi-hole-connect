package com.tien.piholeconnect.ui.screen.statistics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tien.piholeconnect.model.PiHoleStatistics
import com.tien.piholeconnect.model.RefreshableViewModel
import com.tien.piholeconnect.repository.PiHoleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val piHoleRepository: PiHoleRepository) :
    RefreshableViewModel() {
    var statistics: PiHoleStatistics by mutableStateOf(PiHoleStatistics())
        private set

    override fun CoroutineScope.queueRefresh() = launch {
        val result = piHoleRepository.getStatistics()
        statistics = result.copy(topSources = result.topSources.mapKeys { it.key.split('|')[0] })
    }
}