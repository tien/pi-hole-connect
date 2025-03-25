package com.tien.piholeconnect.repository

import kotlinx.coroutines.flow.Flow

interface PiHoleRepositoryProvider {
    val selectedPiHoleRepositoryFlow: Flow<PiHoleV6Repository?>

    suspend fun getSelectedPiHoleRepository(): PiHoleV6Repository?
}
