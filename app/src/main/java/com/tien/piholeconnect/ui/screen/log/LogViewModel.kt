package com.tien.piholeconnect.ui.screen.log

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.AnswerType
import com.tien.piholeconnect.model.PiHoleConnectionAwareViewModel
import com.tien.piholeconnect.model.PiHoleLog
import com.tien.piholeconnect.repository.PiHoleRepository
import com.tien.piholeconnect.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val piHoleRepository: PiHoleRepository,
    val userPreferencesRepository: UserPreferencesRepository
) : PiHoleConnectionAwareViewModel(userPreferencesRepository) {
    enum class Sort(@StringRes val labelResourceId: Int) {
        DATE_DESC(R.string.log_screen_label_date_sort_desc),
        DATE_ASC(R.string.log_screen_label_date_sort_asc),
        RESPONSE_TIME_ASC(R.string.log_screen_label_response_time_sort_asc),
        RESPONSE_TIME_DESC(R.string.log_screen_label_response_time_sort_desc)
    }

    enum class Status(@StringRes val labelResourceId: Int, val contains: Set<AnswerType>) {
        ALLOWED(
            R.string.log_screen_label_allowed,
            setOf(AnswerType.UPSTREAM, AnswerType.LOCAL_CACHE, AnswerType.UNKNOWN)
        ),
        BLOCKED(
            R.string.log_screen_label_blocked,
            setOf(AnswerType.GRAVITY_BLOCK, AnswerType.WILD_CARD_BLOCK)
        )
    }

    private var rawLogs = MutableStateFlow(listOf<PiHoleLog>())

    val query = MutableStateFlow("")
    val sortBy = MutableStateFlow(Sort.DATE_DESC)
    val limits = listOf(1000, 2500, 10000)
    var limit by mutableStateOf(limits[0])
        private set
    var enabledStatuses = MutableStateFlow(setOf(Status.ALLOWED, Status.BLOCKED))

    var logs = rawLogs
        .combine(enabledStatuses) { logs, statuses ->
            logs.filter { log -> statuses.flatMap { it.contains }.contains(log.answerType) }
        }.combine(query) { logs, query ->
            if (query.isBlank()) {
                logs
            } else {
                logs.filter {
                    it.requestedDomain.contains(query, ignoreCase = true) ||
                            it.client.contains(query, ignoreCase = true)
                }
            }
        }.combine(sortBy) { logs, sortBy ->
            when (sortBy) {
                Sort.DATE_DESC -> logs.sortedByDescending { it.timestamp }
                Sort.DATE_ASC -> logs.sortedBy { it.timestamp }
                Sort.RESPONSE_TIME_ASC -> logs.sortedBy { it.responseTime }
                Sort.RESPONSE_TIME_DESC -> logs.sortedByDescending { it.responseTime }
            }
        }

    override suspend fun queueRefresh() {
        rawLogs.value = piHoleRepository.getLogs(limit).data
    }

    fun changeLimit(limit: Int) {
        viewModelScope.launch {
            this@LogViewModel.limit = limit
            refresh()
        }
    }
}