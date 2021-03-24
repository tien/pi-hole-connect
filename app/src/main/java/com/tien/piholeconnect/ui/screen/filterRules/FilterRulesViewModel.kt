package com.tien.piholeconnect.ui.screen.filterRules

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.model.PiHoleFilterRule
import com.tien.piholeconnect.model.RefreshableViewModel
import com.tien.piholeconnect.model.RuleType
import com.tien.piholeconnect.repository.IPiHoleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import javax.inject.Inject

@HiltViewModel
class FilterRulesViewModel @Inject constructor(private val piHoleRepository: IPiHoleRepository) :
    RefreshableViewModel() {
    enum class Tab {
        WHITE,
        BLACK
    }

    var rules: Iterable<PiHoleFilterRule> by mutableStateOf(listOf())
        private set
    var selectedTab: Tab by mutableStateOf(Tab.WHITE)

    override suspend fun queueRefresh() {
        rules =
            RuleType.values().map { viewModelScope.async { piHoleRepository.getFilterRules(it) } }
                .awaitAll()
                .flatMap { it.data }
    }
}