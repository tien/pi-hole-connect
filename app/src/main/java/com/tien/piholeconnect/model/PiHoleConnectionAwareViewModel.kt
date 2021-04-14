package com.tien.piholeconnect.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.repository.UserPreferencesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

abstract class PiHoleConnectionAwareViewModel constructor(private val userPreferencesRepository: UserPreferencesRepository) :
    RefreshableViewModel() {
    private var userPreferencesCollectionJob: Job? = null

    private fun startRefreshOnSelectedConnectionChangeJob() {
        userPreferencesCollectionJob?.cancel()
        userPreferencesCollectionJob = viewModelScope.launch {
            runCatching {
                userPreferencesRepository.userPreferencesFlow
                    .drop(1)
                    .distinctUntilChangedBy { it.selectedPiHoleConnectionId }
                    .collect {
                        hasBeenLoaded = false
                        refresh()
                    }
            }
        }
    }

    private fun cancelRefreshOnSelectedConnectionChangeJob() =
        userPreferencesCollectionJob?.cancel()

    @Composable
    @NonRestartableComposable
    fun RefreshOnConnectionChangeEffect() {
        DisposableEffect(Unit) {
            startRefreshOnSelectedConnectionChangeJob()
            onDispose { cancelRefreshOnSelectedConnectionChangeJob() }
        }
    }
}