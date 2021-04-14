package com.tien.piholeconnect.util

import android.content.Context
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import com.tien.piholeconnect.R
import io.ktor.client.features.*
import io.ktor.utils.io.errors.*

suspend fun SnackbarHostState.showGenericPiHoleConnectionError(
    context: Context,
    throwable: Throwable
) {
    val message = with(StringBuilder()) {
        when (throwable) {
            is ResponseException -> {
                append(throwable.response.status.toString())
                append(". ")
            }
            is IOException -> {
                throwable.localizedMessage?.let {
                    append(it)
                    append(". ")
                }
            }
            else -> {
                append(context.getString(R.string.error_pi_hole_connection_genericerror_pi_hole_connection_generic_prefix))
                append(", ")
            }
        }
        append(context.getString(R.string.error_pi_hole_connection_generic))
        toString()
    }

    showSnackbar(message = message, duration = SnackbarDuration.Long)
}