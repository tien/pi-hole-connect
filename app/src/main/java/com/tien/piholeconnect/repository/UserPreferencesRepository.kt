package com.tien.piholeconnect.repository

import com.tien.piholeconnect.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun updateUserPreferences(transform: (UserPreferences) -> UserPreferences)
    suspend fun removePiHoleConnection(id: String)
}