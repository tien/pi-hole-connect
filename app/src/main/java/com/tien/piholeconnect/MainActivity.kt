package com.tien.piholeconnect

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tien.piholeconnect.ui.App
import com.tien.piholeconnect.ui.screen.home.HomeViewModel
import com.tien.piholeconnect.ui.screen.preferences.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}