package com.tien.piholeconnect.util

import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun rememberRelativeTime(timestampMillis: Long): State<CharSequence> {
    val relativeTime =
        remember(timestampMillis) {
            mutableStateOf(
                DateUtils.getRelativeTimeSpanString(
                    timestampMillis,
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS,
                )
            )
        }

    LaunchedEffect(timestampMillis) {
        while (currentCoroutineContext().isActive) {
            val ageSeconds = (System.currentTimeMillis() - timestampMillis) / 1000
            val interval =
                when {
                    ageSeconds < 60 -> 1.seconds
                    ageSeconds < 3600 -> 30.seconds
                    ageSeconds < 86400 -> 5.minutes
                    else -> 1.hours
                }
            delay(interval)
            relativeTime.value =
                DateUtils.getRelativeTimeSpanString(
                    timestampMillis,
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS,
                )
        }
    }

    return relativeTime
}
