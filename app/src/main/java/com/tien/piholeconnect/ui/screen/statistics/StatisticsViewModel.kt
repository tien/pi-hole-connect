package com.tien.piholeconnect.ui.screen.statistics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tien.piholeconnect.model.PiHoleConnectionAwareViewModel
import com.tien.piholeconnect.model.PiHoleStatistics
import com.tien.piholeconnect.repository.PiHoleRepository
import com.tien.piholeconnect.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel
@Inject
constructor(
    private val piHoleRepository: PiHoleRepository,
    val userPreferencesRepository: UserPreferencesRepository,
) : PiHoleConnectionAwareViewModel(userPreferencesRepository) {
    var statistics: PiHoleStatistics by mutableStateOf(PiHoleStatistics())
        private set

    override suspend fun queueRefresh() {
        val result = piHoleRepository.getStatistics()
        statistics = result.copy(topSources = result.topSources.mapKeys { it.key.split('|')[0] })
    }
}
