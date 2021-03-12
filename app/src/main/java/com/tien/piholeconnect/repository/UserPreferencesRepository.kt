package com.tien.piholeconnect.repository

import androidx.datastore.core.DataStore
import com.tien.piholeconnect.model.UserPreferences

class UserPreferencesRepository constructor(private val dataStore: DataStore<UserPreferences>) :
    IUserPreferencesRepository {
    override val userPreferencesFlow = dataStore.data

    override suspend fun updateUserPreferences(transform: (UserPreferences) -> UserPreferences) {
        dataStore.updateData {
            transform(it)
        }
    }
}