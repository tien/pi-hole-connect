package com.tien.piholeconnect.ui.screen.tipjar

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tien.piholeconnect.R


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TipJarScreen(viewModel: TipJarViewModel) {
    val activity = LocalContext.current as Activity

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Column {
        Text(
            stringResource(R.string.tip_jar_msg),
            modifier = Modifier.padding(15.dp)
        )
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