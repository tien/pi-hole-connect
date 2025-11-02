package com.tien.piholeconnect.ui.screen.tipjar

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.LoadState

@Composable
fun TipJarScreen(viewModel: TipJarViewModel = hiltViewModel()) {
    val activity = LocalActivity.current as Activity
    val tipOptions by viewModel.tipOptions.collectAsStateWithLifecycle()

    Column {
        Text(stringResource(R.string.tip_jar_msg), modifier = Modifier.padding(15.dp))
        when (val tipOptions = tipOptions) {
            is LoadState.Success ->
                tipOptions.data.forEach { tipOption ->
                    ListItem(
                        modifier =
                            Modifier.clickable { viewModel.launchBillingFlow(activity, tipOption) },
                        headlineContent = {
                            Text(tipOption.title.slice(IntRange(0, tipOption.title.indexOf(" ("))))
                        },
                        supportingContent = { Text(tipOption.description) },
                        trailingContent = {
                            Text(tipOption.oneTimePurchaseOfferDetails?.formattedPrice ?: "")
                        },
                    )
                }
            else -> Unit
        }
    }
}
