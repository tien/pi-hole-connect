package com.tien.piholeconnect

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.tien.piholeconnect.service.InAppPurchase
import com.tien.piholeconnect.ui.App
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var inAppPurchase: InAppPurchase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(inAppPurchase)
        setContent { App() }
    }
}
