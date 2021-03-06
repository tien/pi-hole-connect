package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tien.piholeconnect.ui.theme.contentColorFor
import com.tien.piholeconnect.ui.theme.success

@Composable
fun StatsCard(
    name: String,
    statistics: String,
    backGroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(backgroundColor = backGroundColor, modifier = modifier) {
        Column(Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
            Text(
                name,
                color = contentColorFor(backGroundColor),
                style = MaterialTheme.typography.overline
            )
            Text(
                statistics,
                color = contentColorFor(backGroundColor),
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@Preview
@Composable
fun StatsCardPreview() {
    StatsCard(
        name = "Total Queries",
        statistics = "23,456",
        backGroundColor = MaterialTheme.colors.success
    )
}