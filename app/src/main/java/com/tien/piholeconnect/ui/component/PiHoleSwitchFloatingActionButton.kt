package com.tien.piholeconnect.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tien.piholeconnect.R
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme
import com.tien.piholeconnect.ui.theme.contentColorFor
import com.tien.piholeconnect.ui.theme.successContainer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun PiHoleSwitchFloatingActionButton(
    isAdsBlockingEnabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    val containerColor =
        if (isAdsBlockingEnabled) MaterialTheme.colorScheme.successContainer
        else MaterialTheme.colorScheme.errorContainer
    FloatingActionButton(containerColor = containerColor, onClick = onClick) {
        if (isLoading) {
            CircularProgressIndicator(color = LocalContentColor.current)
        } else {
            Icon(
                if (isAdsBlockingEnabled) Icons.Default.GppGood else Icons.Default.GppBad,
                contentDescription =
                    stringResource(
                        if (isAdsBlockingEnabled) R.string.label_disable_blocking
                        else R.string.label_enable_blocking
                    ),
                tint = MaterialTheme.colorScheme.contentColorFor(containerColor),
            )
        }
    }
}

@Composable
fun DisableAdsBlockingAlertDialog(
    onDismissRequest: () -> Unit,
    onDurationButtonClick: (Duration) -> Unit,
) {
    var isDurationPickerVisible by rememberSaveable { mutableStateOf(false) }

    if (isDurationPickerVisible) {
        DurationPickerDialog(
            onDurationConfirm = onDurationButtonClick,
            onDismissRequest = onDismissRequest,
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = { Icon(Icons.Default.GppBad, null) },
            title = { Text(stringResource(R.string.disable_dialog_title)) },
            text = { Text(stringResource(R.string.disable_dialog_msg)) },
            confirmButton = {
                Column(
                    Modifier.fillMaxWidth().padding(8.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TextButton(onClick = { onDurationButtonClick(Duration.INFINITE) }) {
                        Text(stringResource(R.string.disable_dialog_button_permanent).uppercase())
                    }
                    TextButton(onClick = { onDurationButtonClick(10.seconds) }) {
                        Text(stringResource(R.string.disable_dialog_button_10_seconds).uppercase())
                    }
                    TextButton(onClick = { onDurationButtonClick(30.seconds) }) {
                        Text(stringResource(R.string.disable_dialog_button_30_seconds).uppercase())
                    }
                    TextButton(onClick = { onDurationButtonClick(5.minutes) }) {
                        Text(stringResource(R.string.disable_dialog_button_5_minutes).uppercase())
                    }
                    TextButton(onClick = { isDurationPickerVisible = true }) {
                        Text(stringResource(R.string.disable_dialog_button_custom_time).uppercase())
                    }
                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(R.string.disable_dialog_button_cancel).uppercase())
                    }
                }
            },
        )
    }
}

@Composable
fun EnableAdsBlockingAlertDialog(onConfirmRequest: () -> Unit, onDismissRequest: () -> Unit) {
    AlertDialog(
        icon = { Icon(Icons.Default.GppGood, null) },
        title = { Text(stringResource(R.string.enable_dialog_title)) },
        text = { Text(stringResource(R.string.enable_dialog_msg)) },
        confirmButton = {
            TextButton(onClick = onConfirmRequest) {
                Text(stringResource(R.string.enable_dialog_button_confirm).uppercase())
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.enable_dialog_button_dismiss).uppercase())
            }
        },
        onDismissRequest = onDismissRequest,
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EnableAdsBlockingAlertDialogPreview() {
    PiHoleConnectTheme {
        EnableAdsBlockingAlertDialog(onConfirmRequest = {}, onDismissRequest = {})
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DisableAdsBlockingAlertDialogPreview() {
    PiHoleConnectTheme {
        DisableAdsBlockingAlertDialog(onDismissRequest = {}, onDurationButtonClick = {})
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PiHoleSwitchFloatingActionButtonDisablePreview() {
    PiHoleConnectTheme {
        PiHoleSwitchFloatingActionButton(
            isAdsBlockingEnabled = false,
            isLoading = false,
            onClick = {},
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PiHoleSwitchFloatingActionButtonEnablePreview() {
    PiHoleConnectTheme {
        PiHoleSwitchFloatingActionButton(
            isAdsBlockingEnabled = true,
            isLoading = false,
            onClick = {},
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PiHoleSwitchFloatingActionButtonEnableLoadingPreview() {
    PiHoleConnectTheme {
        PiHoleSwitchFloatingActionButton(
            isAdsBlockingEnabled = true,
            isLoading = true,
            onClick = {},
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PiHoleSwitchFloatingActionButtonDisableLoadingPreview() {
    PiHoleConnectTheme {
        PiHoleSwitchFloatingActionButton(
            isAdsBlockingEnabled = false,
            isLoading = true,
            onClick = {},
        )
    }
}
