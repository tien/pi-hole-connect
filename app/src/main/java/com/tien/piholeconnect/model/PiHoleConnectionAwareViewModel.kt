package com.tien.piholeconnect.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

abstract class PiHoleConnectionAwareViewModel(
    userPreferencesRepository: UserPreferencesRepository
) : RefreshableViewModel() {
    private val distinctPiHoleConnectionFlow =
        userPreferencesRepository.userPreferencesFlow.drop(1).distinctUntilChangedBy {
            it.selectedPiHoleConnectionId
        }

    var hasBeenLoaded by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            runCatching { distinctPiHoleConnectionFlow.collectLatest { hasBeenLoaded = false } }
        }
    }

    override fun onSuccess() {
        super.onSuccess()
        hasBeenLoaded = true
    }

    @Composable
    @NonRestartableComposable
    fun RefreshOnConnectionChangeEffect() {
        LaunchedEffect(Unit) { distinctPiHoleConnectionFlow.collectLatest { refresh() } }
    }
}
