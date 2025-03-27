package com.tien.piholeconnect.repository

import kotlinx.coroutines.flow.Flow

interface PiHoleRepositoryManager {
    val selectedPiHoleRepository: Flow<PiHoleRepository?>

    suspend fun getSelectedPiHoleRepository(): PiHoleRepository?

    suspend fun setSelectedPiHole(id: String)
}
