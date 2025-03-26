package com.tien.piholeconnect.service

import com.tien.piholeconnect.repository.PiHoleRepository
import kotlinx.coroutines.flow.Flow

interface PiHoleRepositoryProvider {
    val selectedPiHoleRepository: Flow<PiHoleRepository?>

    suspend fun getSelectedPiHoleRepository(): PiHoleRepository?
}
