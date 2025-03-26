package com.tien.piholeconnect.service

import com.tien.piholeconnect.repository.PiHoleRepository
import com.tien.piholeconnect.repository.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class PiHoleRepositoryProviderImpl
@Inject
constructor(
    private val piHoleRepositoryFactory: PiHoleRepository.Factory,
    userPreferencesRepository: UserPreferencesRepository,
) : PiHoleRepositoryProvider {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val selectedPiHoleRepository =
        userPreferencesRepository.selectedPiHole
            .map { it?.let { piHoleRepositoryFactory.create(it) } }
            .stateIn(
                scope = MainScope(),
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )
            .mapLatest { it?.authenticate() }

    override suspend fun getSelectedPiHoleRepository(): PiHoleRepository? {
        return selectedPiHoleRepository.firstOrNull()
    }
}

