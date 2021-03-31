package com.tien.piholeconnect.repository

import androidx.datastore.core.DataStore
import com.tien.piholeconnect.model.UserPreferences

class UserPreferencesRepositoryImpl constructor(private val dataStore: DataStore<UserPreferences>) :
    UserPreferencesRepository {
    override val userPreferencesFlow = dataStore.data

    override suspend fun updateUserPreferences(transform: (UserPreferences) -> UserPreferences) {
        dataStore.updateData {
            transform(it)
        }
    }
}