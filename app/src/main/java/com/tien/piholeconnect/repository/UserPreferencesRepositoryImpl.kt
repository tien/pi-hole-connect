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

    override suspend fun removePiHoleConnection(id: String) {
        dataStore.updateData { userPreferences ->
            val index = userPreferences.piHoleConnectionsList.indexOfFirst { it.id == id }

            if (index == -1) {
                throw IndexOutOfBoundsException()
            } else {
                val builder = userPreferences.toBuilder()

                if (id == builder.selectedPiHoleConnectionId) {
                    builder.selectedPiHoleConnectionId =
                        userPreferences.piHoleConnectionsList.firstOrNull { it.id != id }?.id
                }

                return@updateData builder.removePiHoleConnections(index).build()
            }
        }
    }
}