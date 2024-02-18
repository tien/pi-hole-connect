package com.tien.piholeconnect.ui.screen.filterrules

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tien.piholeconnect.model.*
import com.tien.piholeconnect.repository.PiHoleRepository
import com.tien.piholeconnect.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@HiltViewModel
class FilterRulesViewModel @Inject constructor(
    private val piHoleRepository: PiHoleRepository,
    val userPreferencesRepository: UserPreferencesRepository
) : PiHoleConnectionAwareViewModel(userPreferencesRepository) {
    enum class Tab { BLACK, WHITE }

    var rules: Iterable<PiHoleFilterRule> by mutableStateOf(listOf())
        private set
    var selectedTab by mutableStateOf(Tab.BLACK)

    var addRuleInputValue by mutableStateOf("")
    var addRuleIsWildcardChecked by mutableStateOf(false)

    override suspend fun queueRefresh() = coroutineScope {
        rules = RuleType.entries
            .map { async { piHoleRepository.getFilterRules(it) } }
            .awaitAll()
            .flatMap { it.data }
    }

    suspend fun addRule() {
        kotlin.runCatching {
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
        }.onFailure { error = it }
    }

    suspend fun removeRule(rule: String, ruleType: RuleType) {
        kotlin.runCatching {
            piHoleRepository.removeFilterRule(rule, ruleType = ruleType)
            refresh()
        }.onFailure { error = it }
    }

    fun resetAddRuleDialogInputs() {
        addRuleInputValue = ""
        addRuleIsWildcardChecked = false
    }
}
