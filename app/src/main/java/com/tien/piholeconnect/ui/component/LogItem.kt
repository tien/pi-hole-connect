package com.tien.piholeconnect.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tien.piholeconnect.model.AnswerType
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
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LogItem(log: QueryLog, modifier: Modifier = Modifier) {
    val dateFormat = remember { DateFormat.getTimeInstance() }
    val status =
        log.status?.let { QueryStatus.Companion.fromStatusString(it) } ?: QueryStatus.UNKNOWN
    val (icon, tint) =
        when (status.type) {
            QueryStatusType.BLOCK -> Pair(Icons.Default.GppBad, MaterialTheme.colorScheme.error)
            QueryStatusType.ALLOW -> Pair(Icons.Default.GppGood, MaterialTheme.colorScheme.success)
            QueryStatusType.CACHE -> Pair(Icons.Default.Cached, MaterialTheme.colorScheme.info)
            QueryStatusType.UNKNOWN ->
                Pair(Icons.AutoMirrored.Filled.Help, LocalContentColor.current.copy(alpha = 0.5f))
        }

    ListItem(
        modifier = modifier,
        leadingContent = {
            Icon(
                icon,
                tint = tint,
                contentDescription = log.status,
                modifier = Modifier.padding(top = 11.dp).size(35.dp),
            )
        },
        overlineContent = {
            if (log.status != null) {
                Text(log.status)
            }
        },
        headlineContent = {
            if (log.domain != null) {
                Text(log.domain)
            }
        },
        supportingContent = {
            if (log.client?.name != null) {
                Text(log.client.name)
            }
        },
        trailingContent = {
            Column {
                if (log.time != null) {
                    Text(dateFormat.format(log.time * 1000L))
                }
                if (log.reply?.time != null) {
                    Text(log.reply.time.milliseconds.toString())
                }
            }
        },
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LogItemPreview() {
    PiHoleConnectTheme {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            AnswerType.entries.forEach {
                LogItem(
                    QueryLog(
                        time = 1616407649532.0,
                        status = "FORWARDED",
                        type = "IPv6",
                        domain = "google.com",
                        client = QueryLogClient(name = "android.router"),
                        reply = QueryLogReply(time = 0.050610790252685547),
                    )
                )
            }
        }
    }
}
