package com.tien.piholeconnect.repository

import com.tien.piholeconnect.model.PiHoleConnection
import com.tien.piholeconnect.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>

    val selectedPiHole: Flow<PiHoleConnection?>

    suspend fun updateUserPreferences(transform: (UserPreferences) -> UserPreferences)

    suspend fun removePiHoleConnection(id: String)
}
