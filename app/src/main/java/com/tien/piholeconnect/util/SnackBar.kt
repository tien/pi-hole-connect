package com.tien.piholeconnect.util

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import com.tien.piholeconnect.R
import io.ktor.client.plugins.ResponseException
import java.util.concurrent.CancellationException

suspend fun SnackbarHostState.showGenericPiHoleConnectionError(
    error: Exception,
    context: Context,
): SnackbarResult? {
    if (error is CancellationException) return null

    val message =
        with(StringBuilder()) {
            when (error) {
                is ResponseException -> {
                    append(error.response.status.toString())
                    append(": ")
                }

                else -> {
                    append(
                        context.getString(R.string.error_pi_hole_connection_generic_error_prefix)
                    )
                    append(". ")
                }
            }

            append(context.getString(R.string.error_pi_hole_connection_generic))
            toString()
        }

    return showSnackbar(
        message = message,
        duration = SnackbarDuration.Long,
        actionLabel = context.getString(R.string.error_show_details),
    )
}
