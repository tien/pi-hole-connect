package com.tien.piholeconnect.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.tien.piholeconnect.model.UserPreferences

val Context.userPreferencesDataStore: DataStore<UserPreferences> by dataStore(
    fileName = "userPreferences.pb",
    serializer = UserPreferencesSerializer
)