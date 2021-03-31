package com.tien.piholeconnect.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.tien.piholeconnect.data.userPreferencesDataStore
import com.tien.piholeconnect.model.UserPreferences
import com.tien.piholeconnect.repository.PiHoleRepository
import com.tien.piholeconnect.repository.UserPreferencesRepository
import com.tien.piholeconnect.repository.PiHoleRepositoryImpl
import com.tien.piholeconnect.repository.UserPreferencesRepositoryImpl
import com.tien.piholeconnect.service.InAppPurchase
import com.tien.piholeconnect.service.InAppPurchaseImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
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
        ): PiHoleRepository =
            PiHoleRepositoryImpl(httpClient, userPreferencesDataStore)

        @Provides
        fun provideUserPreferencesRepository(dataStore: DataStore<UserPreferences>): UserPreferencesRepository =
            UserPreferencesRepositoryImpl(dataStore)

        @Provides
        fun provideBarcodeScanner(): BarcodeScanner {
            val options =
                BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()
            return BarcodeScanning.getClient(options)
        }
    }
}

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class PiHoleConnectActivityModule {
    @Binds
    @ActivityRetainedScoped
    abstract fun bindInAppPurchase(inAppPurchaseImpl: InAppPurchaseImpl): InAppPurchase
}