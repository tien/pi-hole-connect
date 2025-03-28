package com.tien.piholeconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tien.piholeconnect.service.InAppPurchase
import com.tien.piholeconnect.ui.App
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var inAppPurchase: InAppPurchase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(inAppPurchase)

        enableEdgeToEdge()

        setContent { App() }
    }
}
