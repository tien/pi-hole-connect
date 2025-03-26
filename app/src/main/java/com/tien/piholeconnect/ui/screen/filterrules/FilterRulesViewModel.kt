package com.tien.piholeconnect.ui.screen.filterrules

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tien.piholeconnect.model.*
import com.tien.piholeconnect.repository.PiHoleRepositoryProvider
import com.tien.piholeconnect.repository.UserPreferencesRepository
import com.tien.piholeconnect.repository.apis.DomainManagementApi
import com.tien.piholeconnect.repository.models.GetDomainsInner
import com.tien.piholeconnect.repository.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel
class FilterRulesViewModel
@Inject
constructor(
    private val piHoleRepositoryProvider: PiHoleRepositoryProvider,
    val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel(userPreferencesRepository) {
    enum class Tab {
        BLACK,
        WHITE,
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val rules =
        piHoleRepositoryProvider.selectedPiHoleRepository
            .filterNotNull()
            .mapLatest { it.domainManagementApi.getDomains().body().domains ?: listOf() }
            .asViewFlowState()

    var selectedTab by mutableStateOf(Tab.BLACK)

    var addRuleInputValue by mutableStateOf("")
    var addRuleIsWildcardChecked by mutableStateOf(false)

    suspend fun addRule() {
        try {
            piHoleRepositoryProvider
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
            backgroundRefresh()
        } catch (error: Exception) {
            emitError(error)
        }
    }

    suspend fun removeRule(domain: GetDomainsInner) {
        if (domain.domain == null || domain.type == null || domain.kind == null) {
            return
        }

        return removeRule(
            domain.domain,
            type = enumValueOf<DomainManagementApi.TypeDeleteDomain>(domain.type.value.uppercase()),
            kind = enumValueOf<DomainManagementApi.KindDeleteDomain>(domain.kind.value.uppercase()),
        )
    }

    suspend fun removeRule(
        domain: String,
        type: DomainManagementApi.TypeDeleteDomain,
        kind: DomainManagementApi.KindDeleteDomain,
    ) {
        try {
            piHoleRepositoryProvider
                .getSelectedPiHoleRepository()
                ?.domainManagementApi
                ?.deleteDomain(type, kind, domain)
            backgroundRefresh()
        } catch (error: Exception) {
            emitError(error)
        }
    }

    fun resetAddRuleDialogInputs() {
        addRuleInputValue = ""
        addRuleIsWildcardChecked = false
    }
}
