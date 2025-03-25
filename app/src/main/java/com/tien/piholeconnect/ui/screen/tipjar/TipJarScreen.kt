package com.tien.piholeconnect.ui.screen.tipjar

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tien.piholeconnect.R

@Composable
fun TipJarScreen(viewModel: TipJarViewModel = hiltViewModel()) {
    val activity = LocalActivity.current as Activity

    LaunchedEffect(Unit) { viewModel.refresh() }

    Column {
        Text(stringResource(R.string.tip_jar_msg), modifier = Modifier.padding(15.dp))
        viewModel.tipOptions.forEach { tipOption ->
            ListItem(
                modifier = Modifier.clickable { viewModel.launchBillingFlow(activity, tipOption) },
                headlineContent = {
                    Text(tipOption.title.slice(IntRange(0, tipOption.title.indexOf(" ("))))
                },
                supportingContent = { Text(tipOption.description) },
                trailingContent = {
                    Text(tipOption.oneTimePurchaseOfferDetails?.formattedPrice ?: "")
                },
            )
        }
    }
}
