package com.tien.piholeconnect.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

abstract class RefreshableViewModel : ViewModel() {
    private var refreshJob: Job? = null

    var error: Throwable? by mutableStateOf(null)
    var hasBeenLoaded: Boolean by mutableStateOf(false)
        protected set
    var isRefreshing by mutableStateOf(false)
        protected set

    protected abstract fun CoroutineScope.queueRefresh(): Job

    suspend fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            kotlin.runCatching {
                error = null
                isRefreshing = true
                coroutineScope {
                    queueRefresh().join()
                }
                hasBeenLoaded = true
                isRefreshing = false
            }.onFailure { error = it }
        }
        refreshJob?.join()
    }
}