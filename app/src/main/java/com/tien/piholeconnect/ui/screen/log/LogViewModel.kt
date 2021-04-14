package com.tien.piholeconnect.ui.screen.log

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tien.piholeconnect.model.PiHoleConnectionAwareViewModel
import com.tien.piholeconnect.model.PiHoleLog
import com.tien.piholeconnect.repository.PiHoleRepository
import com.tien.piholeconnect.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val piHoleRepository: PiHoleRepository,
    val userPreferencesRepository: UserPreferencesRepository
) : PiHoleConnectionAwareViewModel(userPreferencesRepository) {
    var logs: Iterable<PiHoleLog> by mutableStateOf(listOf())
        private set

    override fun CoroutineScope.queueRefresh() = launch {
        logs = piHoleRepository.getLogs(200).data
    }
}