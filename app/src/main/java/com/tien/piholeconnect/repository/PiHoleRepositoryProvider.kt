package com.tien.piholeconnect.repository

import kotlinx.coroutines.flow.Flow

interface PiHoleRepositoryProvider {
    val selectedPiHoleRepository: Flow<PiHoleV6Repository?>

    suspend fun getSelectedPiHoleRepository(): PiHoleV6Repository?
}
