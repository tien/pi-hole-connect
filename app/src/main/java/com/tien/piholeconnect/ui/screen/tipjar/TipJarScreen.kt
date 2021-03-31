package com.tien.piholeconnect.ui.screen.tipjar

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TipJarScreen(viewModel: TipJarViewModel) {
    val activity = LocalContext.current as Activity

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Column {
        viewModel.tipOptions.forEach { tipOption ->
            ListItem(
                Modifier.clickable {
                    viewModel.launchBillingFlow(activity, tipOption)
                },
                text = {
                    Text(tipOption.title.slice(IntRange(0, tipOption.title.indexOf(" ("))))
                },
                secondaryText = { Text(tipOption.description) },
                trailing = { Text(tipOption.price) })
        }
    }
}