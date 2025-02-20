package com.tien.piholeconnect.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme
import com.tien.piholeconnect.ui.theme.success

@Composable
fun RankedListCard(
    title: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    valueMap: Map<String, Int>,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column {
            Row(
                Modifier.padding(start = 15.dp, end = 15.dp, top = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                icon()
                Spacer(Modifier.width(15.dp))
                ProvideTextStyle(
                    value =
                        MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                ) {
                    title()
                }
            }
            Column {
                valueMap.forEach {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = {
                            Text(
                                it.key,
                                Modifier.horizontalScroll(rememberScrollState()),
                                maxLines = 1,
                                style =
                                    LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold),
                            )
                        },
                        trailingContent = { Text(it.value.toString()) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RankedListCardPreview() {
    PiHoleConnectTheme {
        Column {
            RankedListCard(
                title = { Text("Top Queries") },
                icon = {
                    Icon(
                        Icons.Default.GppGood,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.success,
                    )
                },
                valueMap =
                    mapOf(
                        "debug.opendns.com" to 2385,
                        "ipv4only.arpa" to 2382,
                        "i-bl6p-cor004.api.p001.1drv.com" to 1095,
                        "gateway.fe.apple-dns.net" to 796,
                        "star-mini.c10r.facebook.com" to 617,
                        "e673.dsce9.akamaiedge.net" to 583,
                        "apac-au-courier-4.push-apple.com.akadns.net" to 553,
                        "www.google.com" to 475,
                        "e17437.dscb.akamaiedge.net" to 473,
                        "e6858.dscx.akamaiedge.net" to 431,
                    ),
                Modifier.padding(15.dp),
            )
        }
    }
}
