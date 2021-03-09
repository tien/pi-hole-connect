package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tien.piholeconnect.R
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme

@Composable
fun PiHoleSwitchFloatingActionButton(isAdsBlockingEnabled: Boolean) {
    var isDisableAlertVisible: Boolean by remember { mutableStateOf(false) }

    if (isDisableAlertVisible) {
        DisableAdsBlockingAlertDialog(onDismiss = { isDisableAlertVisible = false })
    }

    FloatingActionButton(
        backgroundColor = if (isAdsBlockingEnabled) MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.primaryVariant,
        onClick = { isDisableAlertVisible = true }) {
        Icon(
            imageVector = if (isAdsBlockingEnabled) Icons.Default.GppGood else Icons.Default.GppBad,
            contentDescription = stringResource(if (isAdsBlockingEnabled) R.string.label_disable_blocking else R.string.label_enable_blocking)
        )
    }
}

@Composable
fun DisableAdsBlockingAlertDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Disable ads blocking") },
        text = { Text("This will disable Pi-Hole ads blocking for the selected period") },
        buttons = {
            Column(Modifier.padding(8.dp)) {
                val buttonModifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)

                Button(onClick = { }, buttonModifier) { Text("Permanently") }
                Button(onClick = { }, buttonModifier) { Text("30 seconds") }
                Button(onClick = { }, buttonModifier) { Text("1 minutes") }
                Button(onClick = { }, buttonModifier) { Text("5 minutes") }
                Button(onClick = { }, buttonModifier) { Text("Custom time") }
            }
        })
}

@Preview
@Composable
fun DisableAdsBlockingAlertDialogPreview() {
    DisableAdsBlockingAlertDialog(onDismiss = {})
}

@Preview
@Composable
fun PiHoleSwitchFloatingActionButtonDisabledPreview() {
    PiHoleConnectTheme {
        PiHoleSwitchFloatingActionButton(isAdsBlockingEnabled = false)
    }
}

@Preview
@Composable
fun PiHoleSwitchFloatingActionButtonEnabledPreview() {
    PiHoleConnectTheme {
        PiHoleSwitchFloatingActionButton(isAdsBlockingEnabled = true)
    }
}