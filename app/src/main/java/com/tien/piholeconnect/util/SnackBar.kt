package com.tien.piholeconnect.util

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.tien.piholeconnect.R
import io.ktor.client.plugins.ResponseException
import java.util.concurrent.CancellationException

suspend fun SnackbarHostState.showGenericPiHoleConnectionError(
    throwable: Throwable, context: Context
): SnackbarResult? {
    if (throwable is CancellationException) return null

    val message = with(StringBuilder()) {
        when (throwable) {
            is ResponseException -> {
                append(throwable.response.status.toString())
                append(": ")
            }

            else -> {
                append(context.getString(R.string.error_pi_hole_connection_generic_error_prefix))
                append(". ")
            }
        }

        append(context.getString(R.string.error_pi_hole_connection_generic))
        toString()
    }

    return showSnackbar(
        message = message, duration = SnackbarDuration.Long, actionLabel = context.getString(R.string.error_show_details)
    )
}

@Composable
fun SnackbarErrorEffect(error: Throwable?, snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var errorToDisplay by remember { mutableStateOf<Throwable?>(null) }

    if (error != null) {
        LaunchedEffect(snackbarHostState) {
            error.let {
                val snackbarResult = snackbarHostState.showGenericPiHoleConnectionError(it, context)

                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    errorToDisplay = it
                }
            }
        }
    }
    
    errorToDisplay?.let {
        AlertDialog(onDismissRequest = { errorToDisplay = null }, confirmButton = {
            TextButton(onClick = {
                clipboard.setText(AnnotatedString(it.stackTraceToString()))
                errorToDisplay = null
            }) {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = stringResource(android.R.string.copy)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(android.R.string.copy))
            }
        }, dismissButton = {
            TextButton(onClick = { errorToDisplay = null }) {
                Text(stringResource(android.R.string.cancel))
            }
        }, text = { Text(it.localizedMessage ?: "") })
    }
}
