package com.tien.piholeconnect.ui.screen.filterRules

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.model.*
import com.tien.piholeconnect.repository.IPiHoleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class FilterRulesViewModel @Inject constructor(private val piHoleRepository: IPiHoleRepository) :
    RefreshableViewModel() {
    enum class Tab { BLACK, WHITE }

    var rules: Iterable<PiHoleFilterRule> by mutableStateOf(listOf())
        private set
    var selectedTab by mutableStateOf(Tab.BLACK)

    var addRuleInputValue by mutableStateOf("")
    var addRuleIsWildcardChecked by mutableStateOf(false)

    override fun CoroutineScope.queueRefresh(): Job = launch {
        rules =
            RuleType.values().map { viewModelScope.async { piHoleRepository.getFilterRules(it) } }
                .awaitAll()
                .flatMap { it.data }
    }

    suspend fun addRule() {
        val ruleType = when (selectedTab) {
            Tab.WHITE -> if (addRuleIsWildcardChecked) RuleType.REGEX_WHITE else RuleType.WHITE
            Tab.BLACK -> if (addRuleIsWildcardChecked) RuleType.REGEX_BLACK else RuleType.BLACK
        }
        val trimmedDomain = addRuleInputValue.trim()
        val parsedDomain =
            if (addRuleIsWildcardChecked) "$WILDCARD_REGEX_PREFIX$trimmedDomain$WILDCARD_REGEX_SUFFIX" else trimmedDomain

        piHoleRepository.addFilterRule(parsedDomain, ruleType = ruleType)
        resetAddRuleDialogInputs()
        refresh()
    }

    suspend fun removeRule(rule: String, ruleType: RuleType) {
        piHoleRepository.removeFilterRule(rule, ruleType = ruleType)
        refresh()
    }

    fun resetAddRuleDialogInputs() {
        addRuleInputValue = ""
        addRuleIsWildcardChecked = false
    }
}