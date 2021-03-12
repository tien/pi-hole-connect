package com.tien.piholeconnect.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.tien.piholeconnect.model.UserPreferences
import com.tien.piholeconnect.repository.IPiHoleRepository
import com.tien.piholeconnect.repository.IUserPreferencesRepository
import com.tien.piholeconnect.repository.PiHoleRepository
import com.tien.piholeconnect.repository.UserPreferencesRepository
import com.tien.piholeconnect.data.userPreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PiHoleConnectModule {
    companion object {
        @Provides
        fun provideHttpClient(): HttpClient {
            return HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
            }
        }

        @Provides
        @Singleton
        fun provideUserPreferencesDataStore(@ApplicationContext appContext: Context): DataStore<UserPreferences> =
            appContext.userPreferencesDataStore

        @Provides
        fun providePiHoleRepository(
            httpClient: HttpClient,
            userPreferencesDataStore: DataStore<UserPreferences>
        ): IPiHoleRepository =
            PiHoleRepository(httpClient, userPreferencesDataStore)

        @Provides
        fun provideUserPreferencesRepository(dataStore: DataStore<UserPreferences>): IUserPreferencesRepository =
            UserPreferencesRepository(dataStore)
    }
}