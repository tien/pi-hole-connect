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
import com.tien.piholeconnect.model.QueryLog
import com.tien.piholeconnect.model.QueryLogClient
import com.tien.piholeconnect.model.QueryLogReply
import com.tien.piholeconnect.model.QueryStatus
import com.tien.piholeconnect.model.QueryStatusType
import com.tien.piholeconnect.model.fromStatusString
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme
import com.tien.piholeconnect.ui.theme.info
import com.tien.piholeconnect.ui.theme.success
import java.text.DateFormat

@Composable
fun QueryDetail(
    query: QueryLog,
    onWhitelistClick: () -> Unit,
    onBlacklistClick: () -> Unit,
    onDismissRequest: () -> Unit,
    addToWhitelistLoading: Boolean = false,
    addToBlacklistLoading: Boolean = false,
) {
    val loading = addToWhitelistLoading || addToBlacklistLoading
    val dateFormat = remember { DateFormat.getDateTimeInstance() }
    val status =
        query.status?.let { QueryStatus.Companion.fromStatusString(it) } ?: QueryStatus.UNKNOWN
    val (icon, tint) =
        when (status.type) {
            QueryStatusType.BLOCK -> Pair(Icons.Default.GppBad, MaterialTheme.colorScheme.error)
            QueryStatusType.ALLOW -> Pair(Icons.Default.GppGood, MaterialTheme.colorScheme.success)
            QueryStatusType.CACHE -> Pair(Icons.Default.Cached, MaterialTheme.colorScheme.info)
            QueryStatusType.UNKNOWN ->
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
                            Text(status.name, fontWeight = FontWeight.Bold, color = tint)
                        }
                    },
                )
                ListItem(
                    leadingContent = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    overlineContent = { Text(stringResource(R.string.query_detail_timestamp)) },
                    headlineContent = {
                        SelectionContainer {
                            if (query.time != null) {
                                Text(dateFormat.format(query.time * 1000))
                            }
                        }
                    },
                )
                ListItem(
                    leadingContent = { Icon(Icons.Default.Domain, contentDescription = null) },
                    overlineContent = {
                        Text(stringResource(R.string.query_detail_requested_domain))
                    },
                    headlineContent = {
                        if (query.domain != null) {
                            SelectionContainer { Text(query.domain) }
                        }
                    },
                )
                ListItem(
                    leadingContent = { Icon(Icons.Default.Devices, contentDescription = null) },
                    overlineContent = { Text(stringResource(R.string.query_detail_client)) },
                    headlineContent = {
                        if (query.client?.name != null) {
                            SelectionContainer { Text(query.client.name) }
                        }
                    },
                )
                ListItem(
                    leadingContent = { Icon(Icons.Default.Dns, contentDescription = null) },
                    overlineContent = {
                        Text(stringResource(R.string.query_detail_dns_record_type))
                    },
                    headlineContent = {
                        if (query.type != null) {
                            SelectionContainer { Text(query.type) }
                        }
                    },
                )
                ListItem(
                    leadingContent = {
                        Icon(Icons.Default.HourglassBottom, contentDescription = null)
                    },
                    overlineContent = { Text(stringResource(R.string.query_detail_response_time)) },
                    headlineContent = {
                        if (query.reply?.time != null) {
                            SelectionContainer {
                                Text(
                                    stringResource(R.string.query_detail_response_time_ms)
                                        .format(query.reply.time * 0.1)
                                )
                            }
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
            QueryLog(
                time = 1616407649532.0,
                type = "IPv6",
                status = "FORWARDED",
                domain = "google.com",
                client = QueryLogClient(name = "android.router"),
                reply = QueryLogReply(time = 450.0),
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
            QueryLog(
                time = 1616407649532.0,
                type = "IPv6",
                status = "FORWARDED",
                domain = "google.com",
                client = QueryLogClient(name = "android.router"),
                reply = QueryLogReply(time = 450.0),
            ),
            onWhitelistClick = {},
            onBlacklistClick = {},
            onDismissRequest = {},
            addToWhitelistLoading = true,
        )
    }
}
