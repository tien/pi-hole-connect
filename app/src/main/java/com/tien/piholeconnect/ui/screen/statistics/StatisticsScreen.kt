package com.tien.piholeconnect.ui.screen.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tien.piholeconnect.R
import com.tien.piholeconnect.repository.models.GetMetricsSummary200Response
import com.tien.piholeconnect.ui.component.RankedListCard
import com.tien.piholeconnect.ui.component.RankedListEntry
import com.tien.piholeconnect.ui.component.TopBarProgressIndicator
import com.tien.piholeconnect.ui.theme.info
import com.tien.piholeconnect.ui.theme.success

@Composable
fun StatisticsScreen(
    snackbarHostState: SnackbarHostState,
    viewModel: StatisticsViewModel = hiltViewModel(),
) {
    viewModel.SnackBarErrorEffect(snackbarHostState)

    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val refreshing by viewModel.refreshing.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()

    TopBarProgressIndicator(visible = loading && !refreshing)

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = refreshing,
        onRefresh = { viewModel.refresh() },
    ) {
        val summary by viewModel.summary.collectAsStateWithLifecycle()
        val queryTypes by viewModel.queryTypes.collectAsStateWithLifecycle()
        val upstreams by viewModel.upstreams.collectAsStateWithLifecycle()
        val topDomains by viewModel.topDomains.collectAsStateWithLifecycle()
        val topBlockedDomains by viewModel.topBlockedDomains.collectAsStateWithLifecycle()
        val topClients by viewModel.topClients.collectAsStateWithLifecycle()

        val hasAnyData =
            summary.data != null ||
                !queryTypes.data.isNullOrEmpty() ||
                !upstreams.data.isNullOrEmpty() ||
                topDomains.data != null ||
                topBlockedDomains.data != null ||
                topClients.data != null

        if (!hasAnyData) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (loading) CircularProgressIndicator()
            }
            return@PullToRefreshBox
        }

        val successColor = MaterialTheme.colorScheme.success
        val infoColor = MaterialTheme.colorScheme.info
        val responseTimeFormat = stringResource(R.string.query_detail_response_time_ms)

        val queryTypeEntries = remember(queryTypes.data) { queryTypes.data.toRankedEntries() }
        val upstreamEntries =
            remember(upstreams.data, responseTimeFormat) {
                upstreams.data.orEmpty().map { upstream ->
                    RankedListEntry(
                        label = upstream.name,
                        value = upstream.count,
                        supporting = upstream.responseTimeMs?.let { responseTimeFormat.format(it) },
                    )
                }
            }
        val topDomainEntries = remember(topDomains.data) { topDomains.data.toRankedEntries() }
        val topBlockedEntries =
            remember(topBlockedDomains.data) { topBlockedDomains.data.toRankedEntries() }
        val topClientEntries = remember(topClients.data) { topClients.data.toRankedEntries() }

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            summary.data?.let { OverviewCard(it) }

            RankedListCard(
                title = { Text(stringResource(R.string.statistics_query_types)) },
                icon = {
                    Icon(
                        Icons.Default.Dns,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                },
                barColor = MaterialTheme.colorScheme.tertiary,
                entries = queryTypeEntries,
            )

            RankedListCard(
                title = { Text(stringResource(R.string.statistics_upstreams)) },
                icon = {
                    Icon(
                        Icons.Default.Router,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                },
                barColor = MaterialTheme.colorScheme.secondary,
                entries = upstreamEntries,
            )

            RankedListCard(
                title = { Text(stringResource(R.string.statistics_top_permitted)) },
                icon = {
                    Icon(Icons.Default.GppGood, contentDescription = null, tint = successColor)
                },
                barColor = successColor,
                entries = topDomainEntries,
            )

            RankedListCard(
                title = { Text(stringResource(R.string.statistics_top_blocked)) },
                icon = {
                    Icon(
                        Icons.Default.GppBad,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                },
                barColor = MaterialTheme.colorScheme.error,
                entries = topBlockedEntries,
            )

            RankedListCard(
                title = { Text(stringResource(R.string.statistics_top_client)) },
                icon = { Icon(Icons.Default.Devices, contentDescription = null, tint = infoColor) },
                barColor = infoColor,
                entries = topClientEntries,
            )
        }
    }
}

@Composable
private fun OverviewCard(summary: GetMetricsSummary200Response, modifier: Modifier = Modifier) {
    val queries = summary.queries
    val segments =
        listOf(
            CompositionSegment(
                stringResource(R.string.statistics_cached),
                queries?.cached ?: 0,
                MaterialTheme.colorScheme.success,
            ),
            CompositionSegment(
                stringResource(R.string.statistics_forwarded),
                queries?.forwarded ?: 0,
                MaterialTheme.colorScheme.info,
            ),
            CompositionSegment(
                stringResource(R.string.statistics_blocked),
                queries?.blocked ?: 0,
                MaterialTheme.colorScheme.error,
            ),
        )
    val total = segments.sumOf { it.value }

    Card(modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Insights,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    stringResource(R.string.statistics_overview),
                    Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    stringResource(R.string.statistics_last_24_hours),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            CompositionBar(segments)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                segments.forEach { CompositionLegendRow(it, total) }
            }

            HorizontalDivider()

            Row(
                Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                StatTile(
                    value = "%,d".format(queries?.uniqueDomains ?: 0),
                    label = stringResource(R.string.statistics_unique_domains),
                )
                VerticalDivider()
                StatTile(
                    value = "%,d".format(summary.clients?.active ?: 0),
                    label = stringResource(R.string.statistics_active_clients),
                )
                VerticalDivider()
                StatTile(
                    value = "%.1f".format(queries?.frequency ?: 0.0),
                    label = stringResource(R.string.statistics_queries_per_second),
                )
            }
        }
    }
}

/** A horizontal stacked bar showing each segment's share of the whole, as one rounded pill. */
@Composable
private fun CompositionBar(segments: List<CompositionSegment>, modifier: Modifier = Modifier) {
    val visible = segments.filter { it.value > 0 }
    Row(modifier.fillMaxWidth().height(14.dp).clip(CircleShape)) {
        visible.forEach { segment ->
            Box(Modifier.weight(segment.value.toFloat()).fillMaxHeight().background(segment.color))
        }
    }
}

@Composable
private fun CompositionLegendRow(segment: CompositionSegment, total: Int) {
    val percent = if (total > 0) segment.value * 100f / total else 0f
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(24.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.size(10.dp).clip(CircleShape).background(segment.color))
        }
        Spacer(Modifier.width(12.dp))
        Text(segment.label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Text("%,d".format(segment.value), style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.width(12.dp))
        Text(
            "%.1f%%".format(percent),
            Modifier.widthIn(min = 52.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun RowScope.StatTile(value: String, label: String) {
    Column(
        Modifier.weight(1f).padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, maxLines = 1)
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 2,
        )
    }
}

private data class CompositionSegment(val label: String, val value: Int, val color: Color)

private fun Map<String, Int>?.toRankedEntries(): List<RankedListEntry> =
    this?.map { (label, value) -> RankedListEntry(label, value) }.orEmpty()
