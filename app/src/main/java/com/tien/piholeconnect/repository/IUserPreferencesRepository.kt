package com.tien.piholeconnect.repository

import com.tien.piholeconnect.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface IUserPreferencesRepository {
    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun updateUserPreferences(transform: (UserPreferences) -> UserPreferences)
}