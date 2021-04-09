package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tien.piholeconnect.ui.theme.contentColorFor
import com.tien.piholeconnect.ui.theme.success

@Composable
fun StatsCard(
    modifier: Modifier = Modifier,
    name: String,
    statistics: String,
    backGroundColor: Color
) {
    val defaultSize = MaterialTheme.typography
    var fontSize by remember { mutableStateOf(defaultSize.h4.fontSize) }

    Card(modifier, backgroundColor = backGroundColor) {
        Column(Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
            Text(
                name,
                color = contentColorFor(backGroundColor),
                style = MaterialTheme.typography.subtitle1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                statistics,
                color = contentColorFor(backGroundColor),
                style = MaterialTheme.typography.h4,
                fontSize = fontSize,
                maxLines = 1,
                softWrap = false,
                onTextLayout = {
                    if (it.didOverflowWidth) {
                        fontSize *= 0.9
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun StatsCardPreview() {
    StatsCard(
        Modifier.widthIn(max = 150.dp),
        name = "Total Queries",
        statistics = "23,456,756,456",
        backGroundColor = MaterialTheme.colors.success
    )
}