package com.tien.piholeconnect.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.tien.piholeconnect.data.AuthenticationSerializer
import com.tien.piholeconnect.data.UserPreferencesSerializer
import com.tien.piholeconnect.model.Authentication
import com.tien.piholeconnect.model.PiHoleSerializer
import com.tien.piholeconnect.model.UserPreferences
import com.tien.piholeconnect.service.PiHoleRepositoryProvider
import com.tien.piholeconnect.service.PiHoleRepositoryProviderImpl
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
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import org.apache.http.conn.ssl.AllowAllHostnameVerifier
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.net.ssl.SSLContext

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class DefaultHttpClient

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class TrustAllCertificatesHttpClient

@Module
@InstallIn(SingletonComponent::class)
abstract class PiHoleConnectModule {
    @Binds
    @Singleton
    abstract fun bindPiHoleRepositoryProvider(
        piHoleRepositoryProviderImpl: PiHoleRepositoryProviderImpl
    ): PiHoleRepositoryProvider

    companion object {
        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().dns(Ipv4FirstDns()).build()

        @Provides
        @DefaultHttpClient
        @Singleton
        fun provideDefaultHttpClient(okHttpClient: OkHttpClient): HttpClient =
            HttpClient(OkHttp) {
                engine { preconfigured = okHttpClient }
                install(ContentNegotiation) { json(PiHoleSerializer.DefaultJson) }
            }

        @SuppressLint("AllowAllHostnameVerifier")
        @TrustAllCertificatesHttpClient
        @Provides
        @Singleton
        fun provideAllowSelfSignedCertificateHttpClient(okHttpClient: OkHttpClient): HttpClient =
            HttpClient(OkHttp) {
                engine {
                    val trustManager = NaiveTrustManager()
                    val sslContext =
                        SSLContext.getInstance("TLS").apply {
                            init(null, arrayOf(trustManager), null)
                        }
                    preconfigured =
                        okHttpClient
                            .newBuilder()
                            .sslSocketFactory(
                                sslSocketFactory = sslContext.socketFactory,
                                trustManager = trustManager,
                            )
                            .hostnameVerifier(AllowAllHostnameVerifier())
                            .build()
                }
                install(ContentNegotiation) { json(PiHoleSerializer.DefaultJson) }
            }

        @Provides
        @Singleton
        fun provideUserPreferencesDataStore(
            @ApplicationContext appContext: Context
        ): DataStore<UserPreferences> =
            DataStoreFactory.create(
                serializer = UserPreferencesSerializer,
                migrations = listOf(),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { appContext.dataStoreFile("preferences.pb") },
            )

        @Provides
        @Singleton
        fun provideAuthenticationDataStore(
            @ApplicationContext appContext: Context
        ): DataStore<Authentication> =
            DataStoreFactory.create(
                serializer = AuthenticationSerializer,
                migrations = listOf(),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { appContext.dataStoreFile("authentication.pb") },
            )

        @Provides
        @Singleton
        fun provideUserPreferencesRepository(
            dataStore: DataStore<UserPreferences>
        ): UserPreferencesRepository = UserPreferencesRepositoryImpl(dataStore)
    }
}

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class PiHoleConnectActivityModule {
    @Binds
    @ActivityRetainedScoped
    abstract fun bindInAppPurchase(inAppPurchaseImpl: InAppPurchaseImpl): InAppPurchase
}
