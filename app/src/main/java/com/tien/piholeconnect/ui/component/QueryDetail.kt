package com.tien.piholeconnect.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.AnswerCategory
import com.tien.piholeconnect.model.AnswerType
import com.tien.piholeconnect.model.PiHoleLog
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme
import com.tien.piholeconnect.ui.theme.info
import com.tien.piholeconnect.ui.theme.success
import java.text.DateFormat

@Composable
fun QueryDetail(
    query: PiHoleLog,
    onWhitelistClick: () -> Unit,
    onBlacklistClick: () -> Unit,
    onDismissRequest: () -> Unit,
    addToWhitelistLoading: Boolean = false,
    addToBlacklistLoading: Boolean = false,
) {
    val loading = addToWhitelistLoading || addToBlacklistLoading
    val dateFormat = remember { DateFormat.getDateTimeInstance() }
    val (icon, tint) =
        when (query.answerType.category) {
            AnswerCategory.BLOCK -> Pair(Icons.Default.GppBad, MaterialTheme.colorScheme.error)

            AnswerCategory.ALLOW -> Pair(Icons.Default.GppGood, MaterialTheme.colorScheme.success)

            AnswerCategory.CACHE -> Pair(Icons.Default.Cached, MaterialTheme.colorScheme.info)

            AnswerCategory.UNKNOWN ->
                Pair(Icons.AutoMirrored.Filled.Help, LocalContentColor.current.copy(alpha = 0.5f))
        }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column(Modifier.padding(top = 8.dp)) {
                ListItem(
                    leadingContent = { Icon(icon, tint = tint, contentDescription = null) },
                    headlineContent = {
                        SelectionContainer {
                            Text(query.answerType.name, fontWeight = FontWeight.Bold, color = tint)
                        }
                    },
                )
                ListItem(
                    leadingContent = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    overlineContent = { Text(stringResource(R.string.query_detail_timestamp)) },
                    headlineContent = {
                        SelectionContainer { Text(dateFormat.format(query.timestamp * 1000L)) }
                    },
                )
                ListItem(
                    leadingContent = { Icon(Icons.Default.Domain, contentDescription = null) },
                    overlineContent = {
                        Text(stringResource(R.string.query_detail_requested_domain))
                    },
                    headlineContent = { SelectionContainer { Text(query.requestedDomain) } },
                )
                ListItem(
                    leadingContent = { Icon(Icons.Default.Devices, contentDescription = null) },
                    overlineContent = { Text(stringResource(R.string.query_detail_client)) },
                    headlineContent = { SelectionContainer { Text(query.client) } },
                )
                ListItem(
                    leadingContent = { Icon(Icons.Default.Dns, contentDescription = null) },
                    overlineContent = {
                        Text(stringResource(R.string.query_detail_dns_record_type))
                    },
                    headlineContent = { SelectionContainer { Text(query.queryType) } },
                )
                ListItem(
                    leadingContent = {
                        Icon(Icons.Default.HourglassBottom, contentDescription = null)
                    },
                    overlineContent = { Text(stringResource(R.string.query_detail_response_time)) },
                    headlineContent = {
                        SelectionContainer {
                            Text(
                                stringResource(R.string.query_detail_response_time_ms)
                                    .format(query.responseTime * 0.1)
                            )
                        }
                    },
                )
            }
        },
        confirmButton = {
            Column(
                Modifier.fillMaxWidth()
                    .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    TextButton(
                        modifier =
                            Modifier.composed { if (addToWhitelistLoading) alpha(0f) else this },
                        onClick = onWhitelistClick,
                        enabled = !loading,
                    ) {
                        Text(stringResource(R.string.query_detail_add_to_whitelist).uppercase())
                    }
                    if (addToWhitelistLoading) {
                        CircularProgressIndicator(Modifier.size(30.dp))
                    }
                }
                Box(contentAlignment = Alignment.Center) {
                    TextButton(
                        modifier =
                            Modifier.composed { if (addToBlacklistLoading) alpha(0f) else this },
                        onClick = onBlacklistClick,
                        enabled = !loading,
                    ) {
                        Text(stringResource(R.string.query_detail_add_to_blacklist).uppercase())
                    }
                    if (addToBlacklistLoading) {
                        CircularProgressIndicator(Modifier.size(30.dp))
                    }
                }
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(R.string.query_detail_done).uppercase())
                }
            }
        },
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun QueryDetailPreview() {
    PiHoleConnectTheme {
        QueryDetail(
            PiHoleLog(
                timestamp = 1616407649532,
                queryType = "AAAA",
                requestedDomain = "google.com",
                client = "android.router",
                answerType = AnswerType.UPSTREAM,
                responseTime = 450,
            ),
            onWhitelistClick = {},
            onBlacklistClick = {},
            onDismissRequest = {},
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun QueryDetailLoadingPreview() {
    PiHoleConnectTheme {
        QueryDetail(
            PiHoleLog(
                timestamp = 1616407649532,
                queryType = "AAAA",
                requestedDomain = "google.com",
                client = "android.router",
                answerType = AnswerType.UPSTREAM,
                responseTime = 450,
            ),
            onWhitelistClick = {},
            onBlacklistClick = {},
            onDismissRequest = {},
            addToWhitelistLoading = true,
        )
    }
}
