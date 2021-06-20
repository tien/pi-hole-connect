package com.tien.piholeconnect.util

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.CoroutineScope

@Composable
@NonRestartableComposable
fun ChangedEffect(vararg keys: Any?, block: suspend CoroutineScope.() -> Unit) {
    var firstLaunch by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        firstLaunch = true
    }

    LaunchedEffect(*keys) {
        if (firstLaunch) {
            firstLaunch = false
            return@LaunchedEffect
        }
        block()
    }
}
