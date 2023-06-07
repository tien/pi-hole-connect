package com.tien.piholeconnect.ui.screen.home

import android.view.MotionEvent
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.tien.piholeconnect.R
import com.tien.piholeconnect.ui.component.DisableAdsBlockingAlertDialog
import com.tien.piholeconnect.ui.component.EnableAdsBlockingAlertDialog
import com.tien.piholeconnect.ui.component.LineChart
import com.tien.piholeconnect.ui.component.LineChartData
import com.tien.piholeconnect.ui.component.PiHoleSwitchFloatingActionButton
import com.tien.piholeconnect.ui.component.SelectedValue
import com.tien.piholeconnect.ui.component.StatsCard
import com.tien.piholeconnect.ui.component.TopBarProgressIndicator
import com.tien.piholeconnect.ui.theme.infoContainer
import com.tien.piholeconnect.ui.theme.success
import com.tien.piholeconnect.ui.theme.successContainer
import com.tien.piholeconnect.ui.theme.toColorInt
import com.tien.piholeconnect.ui.theme.warningContainer
import com.tien.piholeconnect.util.showGenericPiHoleConnectionError
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DateFormat.getTimeInstance

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    var isDisableDialogVisible by rememberSaveable { mutableStateOf(false) }

    val totalQueries: Int by animateIntAsState(viewModel.totalQueries)
    val totalBlockedQueries: Int by animateIntAsState(viewModel.totalBlockedQueries)
    val queryBlockingPercentage: Float by animateFloatAsState(viewModel.queryBlockingPercentage.toFloat())
    val blockedDomainListCount: Int by animateIntAsState(viewModel.blockedDomainListCount)

    val successColorInt = MaterialTheme.colorScheme.success.toColorInt()
    val errorColorInt = MaterialTheme.colorScheme.error.toColorInt()

    val queriesOverTimeLabel = stringResource(R.string.home_queries_over_time)
    val queriesOverTimeData = remember(viewModel.queriesOverTime) {
        LineChartData(label = queriesOverTimeLabel,
            viewModel.queriesOverTime.map { Pair(it.key.toFloat() * 1000L, it.value.toFloat()) }) {
            color = successColorInt
            fillColor = successColorInt
        }
    }

    val adsOverTimeLabel = stringResource(R.string.home_ads_over_time)
    val adsOverTimeData = remember(viewModel.adsOverTime) {
        LineChartData(label = adsOverTimeLabel,
            viewModel.adsOverTime.map { Pair(it.key.toFloat() * 1000L, it.value.toFloat()) }) {
            color = errorColorInt
            fillColor = errorColorInt
        }
    }

    viewModel.RefreshOnConnectionChangeEffect()

    if (viewModel.error != null) {
        LaunchedEffect(snackbarHostState) {
            viewModel.error?.let {
                snackbarHostState.showGenericPiHoleConnectionError(context, it)
            }
        }
    }

    DisposableEffect(Unit) {
        val job = viewModel.viewModelScope.launch {
            while (true) {
                viewModel.refresh()
                delay(10_000)
            }
        }

        onDispose {
            job.cancel()
        }
    }

    TopBarProgressIndicator(visible = !viewModel.hasBeenLoaded && viewModel.isRefreshing)

    if (isDisableDialogVisible) {
        if (viewModel.isAdsBlockingEnabled) {
            DisableAdsBlockingAlertDialog(onDismissRequest = { isDisableDialogVisible = false },
                onDurationButtonClick = {
                    viewModel.viewModelScope.launch {
                        isDisableDialogVisible = false
                        viewModel.disable(it)
                    }
                })
        } else {
            EnableAdsBlockingAlertDialog(onDismissRequest = { isDisableDialogVisible = false },
                onConfirmRequest = {
                    viewModel.viewModelScope.launch {
                        isDisableDialogVisible = false
                        viewModel.enable()
                    }
                })
        }
    }

    Scaffold(Modifier.fillMaxHeight(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            PiHoleSwitchFloatingActionButton(isAdsBlockingEnabled = viewModel.isAdsBlockingEnabled,
                isLoading = viewModel.isPiHoleSwitchLoading,
                onClick = { isDisableDialogVisible = true })
        }) {
        var isScrollEnabled by remember { mutableStateOf(true) }
        SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing), onRefresh = {
            viewModel.viewModelScope.launch {
                isRefreshing = true
                viewModel.refresh()
                isRefreshing = false
            }
        }) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState(), enabled = isScrollEnabled)
                    .heightIn(min = 500.dp)
                    .padding(it)
                    .padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Column {
                    Row {
                        StatsCard(
                            name = {
                                Text(buildAnnotatedString {
                                    append(stringResource(R.string.home_total_queries))
                                    append(" ")
                                    withStyle(
                                        SpanStyle(
                                            fontSize = 9.sp,
                                            baselineShift = BaselineShift.Superscript
                                        )
                                    ) {
                                        append(
                                            stringResource(R.string.home_unique_clients).format(
                                                viewModel.uniqueClients
                                            )
                                        )
                                    }
                                }, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            },
                            statistics = "%,d".format(totalQueries),
                            backGroundColor = MaterialTheme.colorScheme.successContainer,
                            modifier = Modifier
                                .padding(end = 2.5.dp)
                                .weight(1f)
                        )
                        StatsCard(
                            name = {
                                Text(
                                    stringResource(R.string.home_queries_blocked),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            statistics = "%,d".format(totalBlockedQueries),
                            backGroundColor = MaterialTheme.colorScheme.infoContainer,
                            modifier = Modifier
                                .padding(start = 2.5.dp)
                                .weight(1f)
                        )
                    }
                    Row(Modifier.padding(top = 5.dp)) {
                        StatsCard(
                            name = {
                                Text(
                                    stringResource(R.string.home_percent_blocked),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            statistics = "%.2f%%".format(queryBlockingPercentage),
                            backGroundColor = MaterialTheme.colorScheme.warningContainer,
                            modifier = Modifier
                                .padding(end = 2.5.dp)
                                .weight(1f)
                        )
                        StatsCard(
                            name = {
                                Text(
                                    stringResource(R.string.home_blocklist),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            statistics = "%,d".format(blockedDomainListCount),
                            backGroundColor = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier
                                .padding(start = 2.5.dp)
                                .weight(1f)
                        )
                    }
                }
                Card(Modifier.weight(1f)) {
                    var value: Iterable<SelectedValue> by remember { mutableStateOf(listOf()) }
                    val permittedQueriesCount =
                        value.firstOrNull { it.label == queriesOverTimeData.label }?.value?.second?.toInt()
                    val blockedQueriesCount =
                        value.firstOrNull { it.label == adsOverTimeData.label }?.value?.second?.toInt()
                    val dateFormat = remember { getTimeInstance() }
                    val startTime = value.firstOrNull { it.value != null }?.value?.first
                    val endTime = startTime?.let { it + 600_000 }
                    val formattedStartTime = startTime?.let { dateFormat.format(it) }
                    val formattedEndTime = endTime?.let { dateFormat.format(endTime) }

                    Column {
                        Column(Modifier.padding(15.dp)) {
                            Text(
                                stringResource(R.string.home_queries_chart_title),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Column(Modifier.alpha(if (permittedQueriesCount != null && blockedQueriesCount != null) 1f else 0f)) {
                                Text(
                                    String.format(
                                        stringResource(
                                            R.string.home_queries_chart_time_info,
                                            formattedStartTime ?: "",
                                            formattedEndTime ?: ""
                                        )
                                    ), style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "%s: %d".format(
                                        stringResource(R.string.home_queries_chart_permitted_queries_label),
                                        permittedQueriesCount
                                    ), style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "%s: %d".format(
                                        stringResource(R.string.home_queries_chart_blocked_queries_label),
                                        blockedQueriesCount
                                    ), style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        val listener = remember {
                            object : OnChartGestureListener {
                                override fun onChartGestureStart(
                                    me: MotionEvent?,
                                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                                ) {
                                    isScrollEnabled = false
                                }

                                override fun onChartGestureEnd(
                                    me: MotionEvent?,
                                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                                ) {
                                    isScrollEnabled = true
                                }

                                override fun onChartLongPressed(me: MotionEvent?) = Unit

                                override fun onChartDoubleTapped(me: MotionEvent?) = Unit

                                override fun onChartSingleTapped(me: MotionEvent?) = Unit

                                override fun onChartFling(
                                    me1: MotionEvent?,
                                    me2: MotionEvent?,
                                    velocityX: Float,
                                    velocityY: Float
                                ) = Unit

                                override fun onChartScale(
                                    me: MotionEvent?, scaleX: Float, scaleY: Float
                                ) = Unit

                                override fun onChartTranslate(
                                    me: MotionEvent?, dX: Float, dY: Float
                                ) = Unit
                            }
                        }
                        LineChart(
                            lineData = listOf(queriesOverTimeData, adsOverTimeData),
                            onValueSelected = { value = it },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            xAxis.labelCount = 5
                            xAxis.valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String =
                                    dateFormat.format(value)
                            }
                            axisLeft.axisMinimum = 0f
                            axisRight.axisMinimum = 0f
                            onChartGestureListener = listener
                        }
                    }
                }
            }
        }
    }
}
