package com.tien.piholeconnect.ui.screen.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.model.PiHoleSummary
import com.tien.piholeconnect.repository.PiHoleRepository
import com.tien.piholeconnect.ui.component.ScaffoldPreview
import com.tien.piholeconnect.ui.component.StatsCard
import com.tien.piholeconnect.ui.component.SwipeToRefreshLayout
import com.tien.piholeconnect.ui.theme.info
import com.tien.piholeconnect.ui.theme.success
import com.tien.piholeconnect.ui.theme.warning
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    var isRefreshing by remember { mutableStateOf(false) }

    val totalQueries: Int by animateIntAsState(viewModel.totalQueries)
    val totalBlockedQueries: Int by animateIntAsState(viewModel.totalBlockedQueries)
    val queryBlockingPercentage: Float by animateFloatAsState(viewModel.queryBlockingPercentage.toFloat())
    val blockedDomainListCount: Int by animateIntAsState(viewModel.blockedDomainListCount)

    DisposableEffect(Unit) {
        val job = viewModel.viewModelScope.launch {
            while (true) {
                viewModel.refresh()
                delay(5000)
            }
        }

        onDispose {
            job.cancel()
        }
    }

    SwipeToRefreshLayout(
        refreshingState = isRefreshing,
        onRefresh = {
            viewModel.viewModelScope.launch {
                isRefreshing = true
                viewModel.refresh()
                isRefreshing = false
            }
        }) {
        Column(
            Modifier
                .padding(5.dp)
                .fillMaxHeight()
        ) {
            Row {
                StatsCard(
                    name = "Total Queries",
                    statistics = "%,d".format(totalQueries),
                    backGroundColor = MaterialTheme.colors.success,
                    modifier = Modifier
                        .padding(end = 2.5.dp)
                        .weight(1f)
                )
                StatsCard(
                    name = "Queries Blocked",
                    statistics = "%,d".format(totalBlockedQueries),
                    backGroundColor = MaterialTheme.colors.info,
                    modifier = Modifier
                        .padding(start = 2.5.dp)
                        .weight(1f)
                )
            }
            Row(Modifier.padding(top = 5.dp)) {
                StatsCard(
                    name = "Percent Blocked",
                    statistics = "%.2f%%".format(queryBlockingPercentage),
                    backGroundColor = MaterialTheme.colors.warning,
                    modifier = Modifier
                        .padding(end = 2.5.dp)
                        .weight(1f)
                )
                StatsCard(
                    name = "Blocklist",
                    statistics = "%,d".format(blockedDomainListCount),
                    backGroundColor = MaterialTheme.colors.error,
                    modifier = Modifier
                        .padding(start = 2.5.dp)
                        .weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ScaffoldPreview {
        HomeScreen(
            HomeViewModel(object : PiHoleRepository {
                override suspend fun getStatusSummary(): PiHoleSummary = PiHoleSummary(
                    dnsQueriesToday = 38972,
                    adsBlockedToday = 15428,
                    adsPercentageToday = (15428.0 / 38972.0) * 100.0,
                    domainsBeingBlocked = 83754
                )
            })
        )
    }
}