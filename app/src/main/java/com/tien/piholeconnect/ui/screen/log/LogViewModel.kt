package com.tien.piholeconnect.ui.screen.log

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.model.PiHoleLog
import com.tien.piholeconnect.repository.IPiHoleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(private val piHoleRepository: IPiHoleRepository) :
    ViewModel() {
    private var refreshJob: Job? = null

    var error: Throwable? by mutableStateOf(null)
        private set
    var isRefreshing by mutableStateOf(false)
        private set
    var logs: Iterable<PiHoleLog> by mutableStateOf(listOf())
        private set

    suspend fun refresh() {
        refreshJob?.cancel()

        refreshJob = viewModelScope.launch {
            isRefreshing = true
            runCatching {
                logs = piHoleRepository.getLogs(200).data
            }.onFailure {
                error = it
            }
            isRefreshing = false
        }

        refreshJob?.join()
    }
}