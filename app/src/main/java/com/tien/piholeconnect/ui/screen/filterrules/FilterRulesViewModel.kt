package com.tien.piholeconnect.ui.screen.filterrules

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.repository.PiHoleRepositoryManager
import com.tien.piholeconnect.repository.apis.DomainManagementApi
import com.tien.piholeconnect.repository.models.GetDomainsInner
import com.tien.piholeconnect.repository.models.Post
import com.tien.piholeconnect.repository.models.ReplaceDomainRequest
import com.tien.piholeconnect.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterRulesViewModel
@Inject
constructor(private val piHoleRepositoryManager: PiHoleRepositoryManager) : BaseViewModel() {
    enum class Tab {
        BLACK,
        WHITE,
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val rules =
        piHoleRepositoryManager.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest { it.domainManagementApi.getDomains().body().domains ?: listOf() }
            .asViewFlowState()

    var selectedTab by mutableStateOf(Tab.BLACK)

    var addRuleInputValue by mutableStateOf("")
    var addRuleIsWildcardChecked by mutableStateOf(false)

    fun addRule() {
        viewModelScope.launch {
            try {
                piHoleRepositoryManager
                    .getSelectedPiHoleRepository()
                    ?.domainManagementApi
                    ?.addDomain(
                        type =
                            when (selectedTab) {
                                Tab.WHITE -> DomainManagementApi.TypeAddDomain.ALLOW
                                Tab.BLACK -> DomainManagementApi.TypeAddDomain.DENY
                            },
                        kind =
                            if (addRuleIsWildcardChecked) DomainManagementApi.KindAddDomain.REGEX
                            else DomainManagementApi.KindAddDomain.EXACT,
                        Post(domain = listOf(addRuleInputValue.trim())),
                    )

                resetAddRuleDialogInputs()
                doRefresh()
            } catch (error: Exception) {
                emitError(error)
            }
        }
    }

    fun removeRule(domain: GetDomainsInner) {
        if (domain.domain == null || domain.type == null || domain.kind == null) {
            return
        }

        viewModelScope.launch {
            removeRule(
                domain.domain,
                enumValueOf<DomainManagementApi.TypeDeleteDomain>(domain.type.value.uppercase()),
                enumValueOf<DomainManagementApi.KindDeleteDomain>(domain.kind.value.uppercase()),
            )
        }
    }

    private suspend fun removeRule(
        domain: String,
        type: DomainManagementApi.TypeDeleteDomain,
        kind: DomainManagementApi.KindDeleteDomain,
    ) {
        try {
            piHoleRepositoryManager
                .getSelectedPiHoleRepository()
                ?.domainManagementApi
                ?.deleteDomain(type, kind, domain)
            doRefresh()
        } catch (error: Exception) {
            emitError(error)
        }
    }

    fun toggleRule(domain: GetDomainsInner) {
        viewModelScope.launch { doToggleRule(domain) }
    }

    suspend fun doToggleRule(domain: GetDomainsInner) {
        if (
            domain.domain == null ||
                domain.type == null ||
                domain.kind == null ||
                domain.enabled == null
        ) {
            return
        }

        doToggleRule(
            domain.domain,
            enumValueOf<DomainManagementApi.TypeReplaceDomain>(domain.type.value.uppercase()),
            enumValueOf<DomainManagementApi.KindReplaceDomain>(domain.kind.value.uppercase()),
            !domain.enabled,
        )
    }

    private suspend fun doToggleRule(
        domain: String,
        type: DomainManagementApi.TypeReplaceDomain,
        kind: DomainManagementApi.KindReplaceDomain,
        enabled: Boolean,
    ) {
        try {
            piHoleRepositoryManager
                .getSelectedPiHoleRepository()
                ?.domainManagementApi
                ?.replaceDomain(type, kind, domain, ReplaceDomainRequest(enabled = enabled))
            doRefresh()
        } catch (error: Exception) {
            emitError(error)
        }
    }

    fun resetAddRuleDialogInputs() {
        addRuleInputValue = ""
        addRuleIsWildcardChecked = false
    }
}
