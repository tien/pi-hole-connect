package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.tien.piholeconnect.model.AnswerCategory
import com.tien.piholeconnect.model.AnswerType
import com.tien.piholeconnect.model.PiHoleLog
import com.tien.piholeconnect.ui.theme.info
import com.tien.piholeconnect.ui.theme.success
import java.text.DateFormat

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LogItem(log: PiHoleLog, modifier: Modifier = Modifier) {
    val dateFormat = remember { DateFormat.getTimeInstance() }
    val (icon, tint) = when (log.answerType.category) {
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

    ListItem(
        modifier,
        icon = {
            Icon(
                icon,
                tint = tint,
                contentDescription = log.answerType.toString(),
                modifier = Modifier
                    .padding(top = 11.dp)
                    .size(35.dp)
            )
        },
        overlineText = { Text(log.answerType.toString()) },
        text = { Text(log.requestedDomain) },
        secondaryText = { Text(log.client) },
        trailing = {
            Column {
                Text(dateFormat.format(log.timestamp * 1000L))
                Text("%.1f ms".format(log.responseTime * 0.1))
            }
        })
}

@Preview(showBackground = true, showSystemUi = false, backgroundColor = 0xFFFFFFFF)
@Composable
fun LogItemPreview() {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        AnswerType.values().forEach {
            LogItem(
                PiHoleLog(
                    timestamp = 1616407649532,
                    queryType = "IPv6",
                    requestedDomain = "google.com",
                    client = "android.router",
                    answerType = it,
                    responseTime = 450
                )
            )
        }
    }
}