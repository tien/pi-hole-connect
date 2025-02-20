package com.tien.piholeconnect.repository

import com.tien.piholeconnect.model.PiHoleConnection
import com.tien.piholeconnect.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferencesFlow: Flow<UserPreferences>
    val selectedPiHoleFlow: Flow<PiHoleConnection?>

    suspend fun updateUserPreferences(transform: (UserPreferences) -> UserPreferences)

    suspend fun removePiHoleConnection(id: String)
}
