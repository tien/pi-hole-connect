package com.tien.piholeconnect.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.tien.piholeconnect.data.PiHoleConnectionsSerializer
import com.tien.piholeconnect.data.UserPreferencesSerializer
import com.tien.piholeconnect.fixtures.MockResponseRegistry
import com.tien.piholeconnect.fixtures.buildMockEngine
import com.tien.piholeconnect.model.PiHoleConnections
import com.tien.piholeconnect.model.PiHoleSerializer
import com.tien.piholeconnect.model.UserPreferences
import com.tien.piholeconnect.repository.PiHoleRepositoryManager
import com.tien.piholeconnect.repository.PiHoleRepositoryManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [PiHoleConnectModule::class])
abstract class TestPiHoleConnectModule {
    @Binds
    @Singleton
    abstract fun bindPiHoleRepositoryManager(
        piHoleRepositoryManager: PiHoleRepositoryManagerImpl
    ): PiHoleRepositoryManager

    companion object {
        private fun mockClient(registry: MockResponseRegistry): HttpClient =
            HttpClient(buildMockEngine(registry)) {
                install(ContentNegotiation) { json(PiHoleSerializer.DefaultJson) }
            }

        @Provides
        @DefaultHttpClient
        @Singleton
        fun provideDefaultHttpClient(registry: MockResponseRegistry): HttpClient =
            mockClient(registry)

        @Provides
        @TrustAllCertificatesHttpClient
        @Singleton
        fun provideTrustAllHttpClient(registry: MockResponseRegistry): HttpClient =
            mockClient(registry)

        @Provides
        @Singleton
        fun provideUserPreferencesDataStore(
            @ApplicationContext appContext: Context
        ): DataStore<UserPreferences> = TestDataStores.userPreferences(appContext)

        @Provides
        @Singleton
        fun providePiHoleConnectionsDataStore(
            @ApplicationContext appContext: Context
        ): DataStore<PiHoleConnections> = TestDataStores.connections(appContext)
    }
}

/**
 * Process-wide singletons so successive Hilt [SingletonComponent]s share the same DataStore
 * instance — `androidx.datastore` rejects a second active instance for the same file, and
 * `HiltAndroidRule` builds a fresh component per test method.
 */
private object TestDataStores {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Volatile private var connections: DataStore<PiHoleConnections>? = null
    @Volatile private var userPreferences: DataStore<UserPreferences>? = null

    fun connections(appContext: Context): DataStore<PiHoleConnections> =
        connections
            ?: synchronized(this) {
                connections
                    ?: DataStoreFactory.create(
                            serializer = PiHoleConnectionsSerializer,
                            migrations = listOf(),
                            scope = scope,
                            produceFile = {
                                appContext.dataStoreFile("test-pi-hole-connections.pb")
                            },
                        )
                        .also { connections = it }
            }

    fun userPreferences(appContext: Context): DataStore<UserPreferences> =
        userPreferences
            ?: synchronized(this) {
                userPreferences
                    ?: DataStoreFactory.create(
                            serializer = UserPreferencesSerializer,
                            migrations = listOf(),
                            scope = scope,
                            produceFile = { appContext.dataStoreFile("test-user-preferences.pb") },
                        )
                        .also { userPreferences = it }
            }
}
