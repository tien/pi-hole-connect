package com.tien.piholeconnect

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.tien.piholeconnect.di.DefaultHttpClient
import com.tien.piholeconnect.di.TrustAllCertificatesHttpClient
import com.tien.piholeconnect.service.InAppPurchase
import com.tien.piholeconnect.ui.App
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var inAppPurchase: InAppPurchase

    @Inject
    @DefaultHttpClient
    lateinit var httpClient: HttpClient

    @Inject
    @TrustAllCertificatesHttpClient
    lateinit var trustAllCertificatesHttpClient: HttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(inAppPurchase)
        setContent { App() }
    }

    override fun onStop() {
        super.onStop()
        httpClient.close()
        trustAllCertificatesHttpClient.close()
    }
}