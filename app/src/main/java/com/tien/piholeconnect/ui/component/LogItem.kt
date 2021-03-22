package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Help
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tien.piholeconnect.model.AnswerType
import com.tien.piholeconnect.model.PiHoleLog
import com.tien.piholeconnect.ui.theme.info
import com.tien.piholeconnect.ui.theme.success
import java.text.DateFormat

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LogItem(log: PiHoleLog) {
    val dateFormat = remember { DateFormat.getTimeInstance() }
    val (icon, tint) = when (log.answerType) {
        AnswerType.GRAVITY_BLOCK, AnswerType.WILD_CARD_BLOCK -> Pair(
            Icons.Default.GppBad,
            MaterialTheme.colors.error
        )
        AnswerType.UPSTREAM -> Pair(Icons.Default.GppGood, MaterialTheme.colors.success)
        AnswerType.LOCAL_CACHE -> Pair(Icons.Default.Cached, MaterialTheme.colors.info)
        AnswerType.UNKNOWN -> Pair(
            Icons.Default.Help,
            LocalContentColor.current.copy(alpha = 0.5f)
        )
    }

    ListItem(
        icon = {
            Icon(
                icon,
                tint = tint,
                contentDescription = log.answerType.toString(),
                modifier = Modifier.padding(top = 11.dp).size(35.dp)
            )
        },
        overlineText = { Text(log.answerType.toString()) },
        text = { Text(log.requestedDomain) },
        secondaryText = { Text(log.client) },
        trailing = {
            Column {
                Text(dateFormat.format(log.timestamp * 1000L))
                Text("${log.responseTime} ms")
            }
        })
}

@Preview(showBackground = true, showSystemUi = false, backgroundColor = 0xFFFFFFFF)
@Composable
fun LogItemPreview() {
    Column {
        LogItem(
            PiHoleLog(
                timestamp = 1616407649532,
                queryType = "IPv6",
                requestedDomain = "google.com",
                client = "android.router",
                answerType = AnswerType.GRAVITY_BLOCK,
                responseTime = 450
            )
        )
        LogItem(
            PiHoleLog(
                timestamp = 1616407649532,
                queryType = "IPv6",
                requestedDomain = "google.com",
                client = "android.router",
                answerType = AnswerType.UPSTREAM,
                responseTime = 450
            )
        )
        LogItem(
            PiHoleLog(
                timestamp = 1616407649532,
                queryType = "IPv6",
                requestedDomain = "google.com",
                client = "android.router",
                answerType = AnswerType.LOCAL_CACHE,
                responseTime = 450
            )
        )
        LogItem(
            PiHoleLog(
                timestamp = 1616407649532,
                queryType = "IPv6",
                requestedDomain = "google.com",
                client = "android.router",
                answerType = AnswerType.WILD_CARD_BLOCK,
                responseTime = 450
            )
        )
        LogItem(
            PiHoleLog(
                timestamp = 1616407649532,
                queryType = "IPv6",
                requestedDomain = "google.com",
                client = "android.router",
                answerType = AnswerType.UNKNOWN,
                responseTime = 450
            )
        )
    }
}