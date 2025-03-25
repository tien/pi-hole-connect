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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
class FilterRulesViewModel
@Inject
constructor(
    private val piHoleRepositoryProvider: PiHoleRepositoryProvider,
    val userPreferencesRepository: UserPreferencesRepository,
) : ScreenViewModel(userPreferencesRepository) {
    enum class Tab {
        BLACK,
        WHITE,
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val rules =
        piHoleRepositoryProvider.selectedPiHoleRepositoryFlow
            .filterNotNull()
            .mapLatest { it.domainManagementApi.getDomains().body().domains ?: listOf() }
            .asRegisteredLoadState()

    var selectedTab by mutableStateOf(Tab.BLACK)

    var addRuleInputValue by mutableStateOf("")
    var addRuleIsWildcardChecked by mutableStateOf(false)

    suspend fun addRule() {
        kotlin
            .runCatching {
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
                refresh()
            }
            .onFailure(this::addError)
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
        kotlin
            .runCatching {
                piHoleRepositoryProvider
                    .getSelectedPiHoleRepository()
                    ?.domainManagementApi
                    ?.deleteDomain(type, kind, domain)
                refresh()
            }
            .onFailure(this::addError)
    }

    fun resetAddRuleDialogInputs() {
        addRuleInputValue = ""
        addRuleIsWildcardChecked = false
    }
}
