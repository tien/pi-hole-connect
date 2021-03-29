package com.tien.piholeconnect.extension

import android.content.Context
import androidx.compose.material.SnackbarHostState
import com.tien.piholeconnect.R

suspend fun SnackbarHostState.showGenericPiHoleConnectionError(context: Context) = showSnackbar(context.getString(R.string.error_pi_hole_connection_generic))