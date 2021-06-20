package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QueryDetail(
    query: PiHoleLog,
    onWhitelistClick: () -> Unit,
    onBlacklistClick: () -> Unit,
    onDismissRequest: () -> Unit,
    addToWhitelistLoading: Boolean = false,
    addToBlacklistLoading: Boolean = false
) {
    val loading = addToWhitelistLoading || addToBlacklistLoading
    val dateFormat = remember { DateFormat.getDateTimeInstance() }
    val (icon, tint) = when (query.answerType.category) {
        AnswerCategory.BLOCK -> Pair(
            Icons.Default.GppBad,
            MaterialTheme.colors.error
        )
        AnswerCategory.ALLOW -> Pair(Icons.Default.GppGood, MaterialTheme.colors.success)
        AnswerCategory.CACHE -> Pair(Icons.Default.Cached, MaterialTheme.colors.info)
        AnswerCategory.UNKNOWN -> Pair(
            Icons.Default.Help,
            LocalContentColor.current.copy(alpha = 0.5f)
        )
    }

    Card {
        Column(Modifier.padding(top = 8.dp)) {
            ListItem(
                icon = { Icon(icon, tint = tint, contentDescription = null) },
                text = {
                    SelectionContainer {
                        Text(
                            query.answerType.name,
                            fontWeight = FontWeight.Bold,
                            color = tint
                        )
                    }
                })
            ListItem(
                icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                overlineText = { Text(stringResource(R.string.query_detail_timestamp)) },
                text = { SelectionContainer { Text(dateFormat.format(query.timestamp * 1000L)) } })
            ListItem(
                icon = { Icon(Icons.Default.Domain, contentDescription = null) },
                overlineText = { Text(stringResource(R.string.query_detail_requested_domain)) },
                text = { SelectionContainer { Text(query.requestedDomain) } })
            ListItem(
                icon = { Icon(Icons.Default.Devices, contentDescription = null) },
                overlineText = { Text(stringResource(R.string.query_detail_client)) },
                text = { SelectionContainer { Text(query.client) } })
            ListItem(
                icon = { Icon(Icons.Default.Dns, contentDescription = null) },
                overlineText = { Text(stringResource(R.string.query_detail_dns_record_type)) },
                text = { SelectionContainer { Text(query.queryType) } })
            ListItem(
                icon = { Icon(Icons.Default.HourglassBottom, contentDescription = null) },
                overlineText = { Text(stringResource(R.string.query_detail_response_time)) },
                text = {
                    SelectionContainer {
                        Text(
                            stringResource(R.string.query_detail_response_time_ms).format(
                                query.responseTime * 0.1
                            )
                        )
                    }
                })
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        top = 0.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    TextButton(
                        modifier = Modifier.composed {
                            if (addToWhitelistLoading) alpha(0f) else this
                        },
                        onClick = onWhitelistClick,
                        enabled = !loading
                    ) { Text(stringResource(R.string.query_detail_add_to_whitelist).uppercase()) }
                    if (addToWhitelistLoading) {
                        CircularProgressIndicator(Modifier.size(30.dp))
                    }
                }
                Box(contentAlignment = Alignment.Center) {
                    TextButton(
                        modifier = Modifier.composed {
                            if (addToBlacklistLoading) alpha(0f) else this
                        },
                        onClick = onBlacklistClick,
                        enabled = !loading
                    ) { Text(stringResource(R.string.query_detail_add_to_blacklist).uppercase()) }
                    if (addToBlacklistLoading) {
                        CircularProgressIndicator(Modifier.size(30.dp))
                    }
                }
                TextButton(onClick = onDismissRequest) { Text("Done".uppercase()) }
            }
        }
    }
}

@Preview
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
                responseTime = 450
            ),
            onWhitelistClick = {},
            onBlacklistClick = {},
            onDismissRequest = {}
        )
    }
}

@Preview
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
                responseTime = 450
            ),
            onWhitelistClick = {},
            onBlacklistClick = {},
            onDismissRequest = {},
            addToWhitelistLoading = true
        )
    }
}
