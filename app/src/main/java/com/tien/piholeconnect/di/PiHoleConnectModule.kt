package com.tien.piholeconnect.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.tien.piholeconnect.data.userPreferencesDataStore
import com.tien.piholeconnect.model.PiHoleSerializer
import com.tien.piholeconnect.model.UserPreferences
import com.tien.piholeconnect.repository.PiHoleRepository
import com.tien.piholeconnect.repository.PiHoleRepositoryImpl
import com.tien.piholeconnect.repository.UserPreferencesRepository
import com.tien.piholeconnect.repository.UserPreferencesRepositoryImpl
import com.tien.piholeconnect.service.InAppPurchase
import com.tien.piholeconnect.service.InAppPurchaseImpl
import com.tien.piholeconnect.util.Ipv4FirstDns
import com.tien.piholeconnect.util.NaiveTrustManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import okhttp3.OkHttpClient
import org.apache.http.conn.ssl.AllowAllHostnameVerifier
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.net.ssl.SSLContext

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TrustAllCertificatesHttpClient

@Module
@InstallIn(SingletonComponent::class)
abstract class PiHoleConnectModule {
    @Binds
    abstract fun bindPiHoleRepository(piHoleRepositoryImpl: PiHoleRepositoryImpl): PiHoleRepository

    companion object {
        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient = OkHttpClient
            .Builder()
            .dns(Ipv4FirstDns())
            .build()

        @Provides
        @DefaultHttpClient
        @Singleton
        fun provideDefaultHttpClient(okHttpClient: OkHttpClient): HttpClient =
            HttpClient(OkHttp) {
                engine {
                    preconfigured = okHttpClient
                }
                install(JsonFeature) {
                    serializer = KotlinxSerializer(PiHoleSerializer.DefaultJson)
                }
            }

        @SuppressLint("AllowAllHostnameVerifier")
        @TrustAllCertificatesHttpClient
        @Provides
        @Singleton
        fun provideAllowSelfSignedCertificateHttpClient(okHttpClient: OkHttpClient): HttpClient =
            HttpClient(OkHttp) {
                engine {
                    val trustManager = NaiveTrustManager()
                    val sslContext = SSLContext.getInstance("TLS").apply {
                        init(null, arrayOf(trustManager), null)
                    }
                    preconfigured = okHttpClient.newBuilder()
                        .sslSocketFactory(
                            sslSocketFactory = sslContext.socketFactory,
                            trustManager = trustManager
                        )
                        .hostnameVerifier(AllowAllHostnameVerifier())
                        .build()
                }
                install(JsonFeature) {
                    serializer = KotlinxSerializer(PiHoleSerializer.DefaultJson)
                }
            }

        @Provides
        @Singleton
        fun provideUserPreferencesDataStore(@ApplicationContext appContext: Context): DataStore<UserPreferences> =
            appContext.userPreferencesDataStore

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
