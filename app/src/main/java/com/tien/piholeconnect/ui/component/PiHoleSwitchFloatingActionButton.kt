package com.tien.piholeconnect.ui.component

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.tien.piholeconnect.R
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme

@Composable
fun PiHoleSwitchFloatingActionButton(isAdsBlockingEnabled: Boolean) {
    FloatingActionButton(
        backgroundColor = if (isAdsBlockingEnabled) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.secondaryVariant,
        onClick = { /*TODO*/ }) {
        Icon(
            imageVector = if (isAdsBlockingEnabled) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = stringResource(if (isAdsBlockingEnabled) R.string.label_disable_blocking else R.string.label_enable_blocking)
        )
    }
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