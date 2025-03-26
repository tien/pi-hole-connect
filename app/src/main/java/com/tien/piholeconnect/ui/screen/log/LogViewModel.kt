package com.tien.piholeconnect.ui.screen.log

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.QueryStatus
import com.tien.piholeconnect.model.QueryStatusType
import com.tien.piholeconnect.viewmodel.BaseViewModel
import com.tien.piholeconnect.model.UnitLoadState
import com.tien.piholeconnect.model.fromStatusString
import com.tien.piholeconnect.model.run
import com.tien.piholeconnect.repository.PiHoleRepositoryProvider
import com.tien.piholeconnect.repository.UserPreferencesRepository
import com.tien.piholeconnect.repository.apis.DomainManagementApi
import com.tien.piholeconnect.repository.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewModel
@Inject
constructor(
    private val piHoleRepositoryProvider: PiHoleRepositoryProvider,
    userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel(userPreferencesRepository) {
    enum class Sort(@StringRes val labelResourceId: Int) {
        DATE_DESC(R.string.log_screen_label_date_sort_desc),
        DATE_ASC(R.string.log_screen_label_date_sort_asc),
        RESPONSE_TIME_ASC(R.string.log_screen_label_response_time_sort_asc),
        RESPONSE_TIME_DESC(R.string.log_screen_label_response_time_sort_desc),
    }

    enum class Status(@StringRes val labelResourceId: Int, val contains: Set<QueryStatusType>) {
        ALLOWED(
            R.string.log_screen_label_allowed,
            setOf(QueryStatusType.ALLOW, QueryStatusType.CACHE),
        ),
        BLOCKED(
            R.string.log_screen_label_blocked,
            setOf(QueryStatusType.BLOCK, QueryStatusType.UNKNOWN),
        ),
    }

    val addToAllowlistLoadState = MutableStateFlow<UnitLoadState>(UnitLoadState.Idle)

    val addToDenyListLoadState = MutableStateFlow<UnitLoadState>(UnitLoadState.Idle)

    val query = MutableStateFlow("")
    val sortBy = MutableStateFlow(Sort.DATE_DESC)
    val limits = listOf(1000, 2500, 10000)
    var limit = MutableStateFlow(limits[0])

    var enabledStatuses = MutableStateFlow(setOf(Status.ALLOWED, Status.BLOCKED))

    @OptIn(ExperimentalCoroutinesApi::class)
    var logs =
        piHoleRepositoryProvider.selectedPiHoleRepository
            .filterNotNull()
            .combine(limit) { piHole, limit -> Pair(piHole, limit) }
            .mapLatest { (piHole, limit) ->
                piHole.metricsApi.getQueries(length = limit).body().queries ?: listOf()
            }
            .combine(enabledStatuses) { logs, statuses ->
                logs.filter { log ->
                    log.status != null &&
                        statuses
                            .flatMap { it.contains }
                            .contains(QueryStatus.Companion.fromStatusString(log.status).type)
                }
            }
            .combine(query) { logs, query ->
                if (query.isBlank()) {
                    logs
                } else {
                    logs.filter {
                        (it.domain != null && it.domain.contains(query, ignoreCase = true)) ||
                            (it.client?.name != null &&
                                it.client.name.contains(query, ignoreCase = true))
                    }
                }
            }
            .combine(sortBy) { logs, sortBy ->
                when (sortBy) {
                    Sort.DATE_DESC -> logs.sortedByDescending { it.time }
                    Sort.DATE_ASC -> logs.sortedBy { it.time }
                    Sort.RESPONSE_TIME_ASC -> logs.sortedBy { it.reply?.time }
                    Sort.RESPONSE_TIME_DESC -> logs.sortedByDescending { it.reply?.time }
                }
            }
            .asViewFlowState()

    fun addToWhiteList(domain: String) {
        addToAllowlistLoadState.run(scope = viewModelScope) {
            val body =
                piHoleRepositoryProvider
                    .getSelectedPiHoleRepository()
                    ?.domainManagementApi
                    ?.addDomain(
                        DomainManagementApi.TypeAddDomain.ALLOW,
                        DomainManagementApi.KindAddDomain.EXACT,
                        Post(domain = listOf(domain)),
                    )
                    ?.body()

            body?.processed?.errors?.firstOrNull()?.also { throw Error(it.error) }
        }
    }

    fun addToBlacklist(domain: String) {
        addToDenyListLoadState.run(scope = viewModelScope) {
            val body =
                piHoleRepositoryProvider
                    .getSelectedPiHoleRepository()
                    ?.domainManagementApi
                    ?.addDomain(
                        DomainManagementApi.TypeAddDomain.DENY,
                        DomainManagementApi.KindAddDomain.EXACT,
                        Post(domain = listOf(domain)),
                    )
                    ?.body()

            body?.processed?.errors?.firstOrNull()?.also { throw Error(it.error) }
        }
    }

    fun changeLimit(limit: Int) {
        viewModelScope.launch { this@LogViewModel.limit.value = limit }
    }
}
