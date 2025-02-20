package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tien.piholeconnect.ui.theme.contentColorFor
import com.tien.piholeconnect.ui.theme.success

@Composable
fun StatsCard(
    modifier: Modifier = Modifier,
    name: @Composable () -> Unit,
    statistics: String,
    backGroundColor: Color,
) {
    val defaultSize = MaterialTheme.typography
    var fontSize by remember { mutableStateOf(defaultSize.headlineMedium.fontSize) }

    Card(modifier, colors = CardDefaults.cardColors(containerColor = backGroundColor)) {
        Column(Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
            ProvideTextStyle(
                value =
                    MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.contentColorFor(backGroundColor)
                    )
            ) {
                name()
            }
            Text(
                statistics,
                color = MaterialTheme.colorScheme.contentColorFor(backGroundColor),
                style = MaterialTheme.typography.headlineMedium,
                fontSize = fontSize,
                maxLines = 1,
                softWrap = false,
                onTextLayout = {
                    if (it.didOverflowWidth) {
                        fontSize *= 0.9
                    }
                },
            )
        }
    }
}

@Preview
@Composable
fun StatsCardPreview() {
    StatsCard(
        Modifier.widthIn(max = 150.dp),
        name = { Text("Total Queries") },
        statistics = "23,456,756,456",
        backGroundColor = MaterialTheme.colorScheme.success,
    )
}
