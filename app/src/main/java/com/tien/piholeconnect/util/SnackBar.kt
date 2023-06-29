package com.tien.piholeconnect.util

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.tien.piholeconnect.R
import io.ktor.client.plugins.ResponseException
import java.util.concurrent.CancellationException

suspend fun SnackbarHostState.showGenericPiHoleConnectionError(
    throwable: Throwable, context: Context
) {
    if (throwable is CancellationException) return

    val message = with(StringBuilder()) {
        when (throwable) {
            is ResponseException -> {
                append(throwable.response.status.toString())
                append(": ")
            }

            else -> {
                append(throwable.localizedMessage)
                append("\n")
            }
        }

        append(context.getString(R.string.error_pi_hole_connection_generic))
        toString()
    }

    showSnackbar(message = message, duration = SnackbarDuration.Long)
}

@Composable
fun SnackbarErrorEffect(error: Throwable?, snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current


    if (error != null) {
        LaunchedEffect(snackbarHostState) {
            error.let {
                snackbarHostState.showGenericPiHoleConnectionError(it, context)
            }
        }
    }
}
