package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tien.piholeconnect.R
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme
import com.tien.piholeconnect.ui.theme.success
import java.util.*
import kotlin.time.Duration
import kotlin.time.minutes
import kotlin.time.seconds

@Composable
fun PiHoleSwitchFloatingActionButton(
    isAdsBlockingEnabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    FloatingActionButton(
        backgroundColor = if (isAdsBlockingEnabled) MaterialTheme.colors.success else MaterialTheme.colors.error,
        onClick = onClick
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = LocalContentColor.current)
        } else {
            Icon(
                imageVector = if (isAdsBlockingEnabled) Icons.Default.GppGood else Icons.Default.GppBad,
                contentDescription = stringResource(if (isAdsBlockingEnabled) R.string.label_disable_blocking else R.string.label_enable_blocking)
            )
        }
    }
}

@Composable
fun DisableAdsBlockingAlertDialog(
    onDismissRequest: () -> Unit,
    onDurationButtonClick: (Duration) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.disable_dialog_title)) },
        text = { Text(stringResource(R.string.disable_dialog_msg)) },
        buttons = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(onClick = { onDurationButtonClick(Duration.INFINITE) }) {
                    Text(
                        stringResource(R.string.disable_dialog_button_permanent).toUpperCase(Locale.ROOT)
                    )
                }
                TextButton(onClick = { onDurationButtonClick(30.seconds) }) { Text("30 SECONDS") }
                TextButton(onClick = { onDurationButtonClick(1.minutes) }) { Text("1 MINUTES") }
                TextButton(onClick = { onDurationButtonClick(5.minutes) }) { Text("5 MINUTES") }
                TextButton(onClick = onDismissRequest) {
                    Text(
                        stringResource(R.string.disable_dialog_button_cancel).toUpperCase(
                            Locale.ROOT
                        )
                    )
                }
            }
        })
}

@Composable
fun EnableAdsBlockingAlertDialog(onConfirmRequest: () -> Unit, onDismissRequest: () -> Unit) {
    AlertDialog(
        text = { Text(stringResource(R.string.enable_dialog_title)) },
        confirmButton = {
            TextButton(onClick = onConfirmRequest) {
                Text(
                    stringResource(R.string.enable_dialog_button_confirm).toUpperCase(
                        Locale.ROOT
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    stringResource(R.string.enable_dialog_button_dismiss).toUpperCase(
                        Locale.ROOT
                    )
                )
            }
        },
        onDismissRequest = onDismissRequest,
    )
}

@Preview
@Composable
fun EnableAdsBlockingAlertDialogPreview() {
    EnableAdsBlockingAlertDialog(onConfirmRequest = {}, onDismissRequest = {})
}

@Preview
@Composable
fun DisableAdsBlockingAlertDialogPreview() {
    DisableAdsBlockingAlertDialog(onDismissRequest = {}, onDurationButtonClick = {})
}

@Preview
@Composable
fun PiHoleSwitchFloatingActionButtonDisablePreview() {
    PiHoleConnectTheme {
        PiHoleSwitchFloatingActionButton(
            isAdsBlockingEnabled = false,
            isLoading = false,
            onClick = {})
    }
}

@Preview
@Composable
fun PiHoleSwitchFloatingActionButtonEnablePreview() {
    PiHoleConnectTheme {
        PiHoleSwitchFloatingActionButton(
            isAdsBlockingEnabled = true,
            isLoading = false,
            onClick = {})
    }
}

@Preview
@Composable
fun PiHoleSwitchFloatingActionButtonEnableLoadingPreview() {
    PiHoleConnectTheme {
        PiHoleSwitchFloatingActionButton(
            isAdsBlockingEnabled = true,
            isLoading = true,
            onClick = {})
    }
}

@Preview
@Composable
fun PiHoleSwitchFloatingActionButtonDisableLoadingPreview() {
    PiHoleConnectTheme {
        PiHoleSwitchFloatingActionButton(
            isAdsBlockingEnabled = true,
            isLoading = true,
            onClick = {})
    }
}