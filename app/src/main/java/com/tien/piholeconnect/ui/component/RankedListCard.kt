package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme
import com.tien.piholeconnect.ui.theme.success

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RankedListCard(
    title: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    valueMap: Map<String, Int>,
    modifier: Modifier = Modifier,
    elevation: Dp = 1.dp,
) {
    Card(modifier = modifier.fillMaxWidth(), elevation = elevation) {
        Column {
            Row(
                Modifier.padding(start = 15.dp, end = 15.dp, top = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()
                Spacer(Modifier.width(15.dp))
                ProvideTextStyle(value = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)) {
                    title()
                }
            }
            Column {
                valueMap.forEach {
                    ListItem(
                        text = {
                            Text(
                                it.key,
                                Modifier.horizontalScroll(rememberScrollState()),
                                maxLines = 1,
                                style = LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold)
                            )
                        },
                        trailing = { Text(it.value.toString()) })
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
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
                        tint = MaterialTheme.colors.success
                    )
                },
                valueMap = mapOf(
                    "debug.opendns.com" to 2385,
                    "ipv4only.arpa" to 2382,
                    "i-bl6p-cor004.api.p001.1drv.com" to 1095,
                    "gateway.fe.apple-dns.net" to 796,
                    "star-mini.c10r.facebook.com" to 617,
                    "e673.dsce9.akamaiedge.net" to 583,
                    "apac-au-courier-4.push-apple.com.akadns.net" to 553,
                    "www.google.com" to 475,
                    "e17437.dscb.akamaiedge.net" to 473,
                    "e6858.dscx.akamaiedge.net" to 431
                ),
                Modifier.padding(15.dp)
            )
        }
    }
}