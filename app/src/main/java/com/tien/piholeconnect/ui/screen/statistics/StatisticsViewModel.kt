package com.tien.piholeconnect.ui.screen.statistics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.model.PiHoleStatistics
import com.tien.piholeconnect.repository.IPiHoleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val piHoleRepository: IPiHoleRepository) :
    ViewModel() {
    private var refreshJob: Job? = null

    var error: Throwable? by mutableStateOf(null)
        private set
    var isRefreshing by mutableStateOf(false)
        private set
    var statistics: PiHoleStatistics by mutableStateOf(PiHoleStatistics())
        private set

    suspend fun refresh() {
        refreshJob?.cancel()

        refreshJob = viewModelScope.launch {
            isRefreshing = true
            runCatching {
                val result = piHoleRepository.getStatistics()
                statistics =
                    result.copy(topSources = result.topSources.mapKeys { it.key.split('|')[0] })
            }.onFailure {
                error = it
            }
            isRefreshing = false
        }

        refreshJob?.join()
    }
}