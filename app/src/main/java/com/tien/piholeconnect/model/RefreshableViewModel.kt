package com.tien.piholeconnect.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class RefreshableViewModel : ViewModel() {
    private var refreshJob: Job? = null

    var error: Throwable? by mutableStateOf(null)
    var isRefreshing by mutableStateOf(false)
        protected set

    protected abstract suspend fun queueRefresh()

    suspend fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            kotlin.runCatching {
                error = null
                isRefreshing = true
                queueRefresh()
                isRefreshing = false
            }.onFailure { error = it }
        }
        refreshJob?.join()
    }
}