package com.tien.piholeconnect.ui.screen.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.LineChartData
import com.tien.piholeconnect.ui.component.DisableAdsBlockingAlertDialog
import com.tien.piholeconnect.ui.component.EnableAdsBlockingAlertDialog
import com.tien.piholeconnect.ui.component.LineChart
import com.tien.piholeconnect.ui.component.PiHoleSwitchFloatingActionButton
import com.tien.piholeconnect.ui.component.StatsCard
import com.tien.piholeconnect.ui.component.TopBarProgressIndicator
import com.tien.piholeconnect.ui.theme.infoContainer
import com.tien.piholeconnect.ui.theme.success
import com.tien.piholeconnect.ui.theme.successContainer
import com.tien.piholeconnect.ui.theme.warningContainer
import java.text.DateFormat
import java.text.DateFormat.getTimeInstance
import java.util.Date
import kotlin.math.max
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val metrics by viewModel.metricSummary.collectAsStateWithLifecycle()

    val totalQueries by
        animateIntAsState(metrics.data?.queries?.total ?: 0, label = "Total queries")

    val totalBlockedQueries by
        animateIntAsState(metrics.data?.queries?.blocked ?: 0, label = "Total blocked queries")

    val queryBlockingPercentage by
        animateFloatAsState(
            metrics.data?.queries?.percentBlocked?.toFloat() ?: 0f,
            label = "Query blocking percentage",
        )

    val blockedDomainListCount by
        animateIntAsState(
            metrics.data?.gravity?.domainsBeingBlocked ?: 0,
            label = "Blocked domain list count",
        )

    val successColor = MaterialTheme.colorScheme.success
    val errorColor = MaterialTheme.colorScheme.error

    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.SnackBarErrorEffect(snackbarHostState)

    DisposableEffect(Unit) {
        val job =
            viewModel.viewModelScope.launch {
                while (true) {
                    delay(10_000)
                    viewModel.backgroundRefresh()
                }
            }

        onDispose { job.cancel() }
    }

    val isAdsBlockingEnabled =
        viewModel.isAdsBlockingEnabled.collectAsStateWithLifecycle().value.data == true

    var isDisableDialogVisible by rememberSaveable { mutableStateOf(false) }

    if (isDisableDialogVisible) {
        if (isAdsBlockingEnabled) {
            DisableAdsBlockingAlertDialog(
                onDismissRequest = { isDisableDialogVisible = false },
                onDurationButtonClick = {
                    viewModel.viewModelScope.launch {
                        isDisableDialogVisible = false
                        viewModel.disable(it)
                    }
                },
            )
        } else {
            EnableAdsBlockingAlertDialog(
                onDismissRequest = { isDisableDialogVisible = false },
                onConfirmRequest = {
                    viewModel.viewModelScope.launch {
                        isDisableDialogVisible = false
                        viewModel.enable()
                    }
                },
            )
        }
    }

    val refreshing by viewModel.refreshing.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    TopBarProgressIndicator(visible = loading && !refreshing)

    Scaffold(
        Modifier.fillMaxHeight(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            PiHoleSwitchFloatingActionButton(
                isAdsBlockingEnabled = isAdsBlockingEnabled,
                isLoading = viewModel.isPiHoleSwitchLoading,
                onClick = { isDisableDialogVisible = true },
            )
        },
    ) {
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = refreshing,
            onRefresh = { viewModel.refresh() },
        ) {
            Column(
                Modifier.fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = 500.dp)
                    .padding(it)
                    .padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                Column {
                    Row {
                        StatsCard(
                            name = {
                                Text(
                                    buildAnnotatedString {
                                        append(stringResource(R.string.home_total_queries))
                                        append(" ")
                                        withStyle(
                                            SpanStyle(
                                                fontSize = 9.sp,
                                                baselineShift = BaselineShift.Superscript,
                                            )
                                        ) {
                                            (metrics.data?.clients?.total ?: 0).let {
                                                append(
                                                    pluralStringResource(
                                                        R.plurals.home_unique_clients,
                                                        it,
                                                        it,
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            statistics = "%,d".format(totalQueries),
                            backGroundColor = MaterialTheme.colorScheme.successContainer,
                            modifier = Modifier.padding(end = 2.5.dp).weight(1f),
                        )
                        StatsCard(
                            name = {
                                Text(
                                    stringResource(R.string.home_queries_blocked),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            statistics = "%,d".format(totalBlockedQueries),
                            backGroundColor = MaterialTheme.colorScheme.infoContainer,
                            modifier = Modifier.padding(start = 2.5.dp).weight(1f),
                        )
                    }
                    Row(Modifier.padding(top = 5.dp)) {
                        StatsCard(
                            name = {
                                Text(
                                    stringResource(R.string.home_percent_blocked),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            statistics = "%.2f%%".format(queryBlockingPercentage),
                            backGroundColor = MaterialTheme.colorScheme.warningContainer,
                            modifier = Modifier.padding(end = 2.5.dp).weight(1f),
                        )
                        StatsCard(
                            name = {
                                Text(
                                    stringResource(R.string.home_blocklist),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            statistics = "%,d".format(blockedDomainListCount),
                            backGroundColor = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.padding(start = 2.5.dp).weight(1f),
                        )
                    }
                }

                val history by viewModel.history.collectAsStateWithLifecycle()

                val queriesOverTimeLabel = stringResource(R.string.home_queries_over_time)
                val queriesOverTimeData =
                    remember(history.data) {
                        LineChartData(
                            label = queriesOverTimeLabel,
                            data =
                                history.data?.map {
                                    Pair(
                                        it.timestamp ?: 0,
                                        max((it.total ?: 0) - (it.blocked ?: 0), 0),
                                    )
                                } ?: listOf(),
                            color = successColor,
                        )
                    }

                val adsOverTimeLabel = stringResource(R.string.home_ads_over_time)
                val adsOverTimeData =
                    remember(history) {
                        LineChartData(
                            label = adsOverTimeLabel,
                            data =
                                history.data?.map { Pair(it.timestamp ?: 0, it.blocked ?: 0) }
                                    ?: listOf(),
                            color = errorColor,
                        )
                    }

                Card(Modifier.weight(1f)) {
                    Column {
                        Column(Modifier.padding(15.dp)) {
                            Text(
                                stringResource(R.string.home_queries_chart_title),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                        LineChart(
                            Modifier.fillMaxSize(),
                            data = listOf(queriesOverTimeData, adsOverTimeData),
                            xAxisFormatter =
                                remember { getTimeInstance(DateFormat.SHORT) }
                                    .let { dateTime ->
                                        { value -> dateTime.format(Date(value.toLong() * 1000)) }
                                    },
                        )
                    }
                }
            }
        }
    }
}
