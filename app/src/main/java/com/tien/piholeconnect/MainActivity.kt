package com.tien.piholeconnect

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tien.piholeconnect.repository.PiHoleRESTApiRepository
import com.tien.piholeconnect.repository.PiHoleRepository
import com.tien.piholeconnect.ui.App
import com.tien.piholeconnect.ui.screen.home.HomeViewModel
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*

@Suppress("UNCHECKED_CAST")
class MainActivity : AppCompatActivity() {
    private val httpClient = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }
    private val piHoleRepository: PiHoleRepository = PiHoleRESTApiRepository(httpClient)

    private val homeViewModel by viewModels<HomeViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                HomeViewModel(piHoleRepository) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App(homeViewModel) }
    }
}