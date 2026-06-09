package com.tien.piholeconnect.ui.component

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tien.piholeconnect.R
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme
import com.tien.piholeconnect.ui.theme.success

/** One row of a [RankedListCard]: a [label], its [value], and optional [supporting] detail text. */
data class RankedListEntry(val label: String, val value: Int, val supporting: String? = null)

/**
 * A Material 3 card that ranks [entries] from highest to lowest, drawing a proportion bar under
 * each row (relative to the largest value) so magnitudes are comparable at a glance.
 */
@Composable
fun RankedListCard(
    title: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    entries: List<RankedListEntry>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
) {
    val maxValue = remember(entries) { entries.maxOfOrNull { it.value }?.coerceAtLeast(1) ?: 1 }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(Modifier.width(12.dp))
                ProvideTextStyle(MaterialTheme.typography.titleLarge) { title() }
            }

            if (entries.isEmpty()) {
                RankedListEmptyState()
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    entries.forEachIndexed { index, entry ->
                        RankedListRow(
                            rank = index + 1,
                            entry = entry,
                            fraction = entry.value.toFloat() / maxValue,
                            barColor = barColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RankedListRow(rank: Int, entry: RankedListEntry, fraction: Float, barColor: Color) {
    Row {
        Text(
            "$rank",
            Modifier.width(24.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row {
                Text(
                    entry.label,
                    Modifier.weight(1f).alignByBaseline(),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "%,d".format(entry.value),
                    Modifier.alignByBaseline(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            ProportionBar(fraction = fraction, color = barColor)
            entry.supporting?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** A thin determinate bar that fills [fraction] of the row width, animating on data change. */
@Composable
private fun ProportionBar(fraction: Float, color: Color, modifier: Modifier = Modifier) {
    val animated by
        animateFloatAsState(fraction.coerceIn(0f, 1f), label = "RankedListCardProportion")
    LinearProgressIndicator(
        progress = { animated },
        modifier = modifier.fillMaxWidth().height(6.dp),
        color = color,
        trackColor = color.copy(alpha = 0.16f),
        strokeCap = StrokeCap.Round,
        gapSize = 0.dp,
        drawStopIndicator = {},
    )
}

@Composable
private fun RankedListEmptyState() {
    Text(
        stringResource(R.string.ranked_list_empty),
        Modifier.fillMaxWidth().padding(vertical = 16.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RankedListCardPreview() {
    PiHoleConnectTheme {
        RankedListCard(
            title = { Text("Top permitted") },
            icon = {
                Icon(
                    Icons.Default.GppGood,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.success,
                )
            },
            barColor = MaterialTheme.colorScheme.success,
            entries =
                listOf(
                    RankedListEntry("debug.opendns.com", 2385),
                    RankedListEntry("ipv4only.arpa", 2382),
                    RankedListEntry("i-bl6p-cor004.api.p001.1drv.com", 1095),
                    RankedListEntry("gateway.fe.apple-dns.net", 796),
                    RankedListEntry("star-mini.c10r.facebook.com", 617),
                ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
