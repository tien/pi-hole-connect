package com.tien.piholeconnect.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope

@Composable
fun ChangedEffect(vararg keys: Any?, block: suspend CoroutineScope.() -> Unit) {
  var isFirst by remember { mutableStateOf(true) }

  LaunchedEffect(*keys) {
    if (isFirst) {
      isFirst = false
      return@LaunchedEffect
    }
    block()
  }
}
