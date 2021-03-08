package com.tien.piholeconnect.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tien.piholeconnect.model.PiHoleSummary
import com.tien.piholeconnect.repository.PiHoleRepository
import com.tien.piholeconnect.ui.component.ScaffoldPreview
import com.tien.piholeconnect.ui.component.StatsCard
import com.tien.piholeconnect.ui.theme.info
import com.tien.piholeconnect.ui.theme.success
import com.tien.piholeconnect.ui.theme.warning

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Column(Modifier.padding(5.dp)) {
        Row {
            StatsCard(
                name = "Total Queries",
                statistics = "%,d".format(viewModel.totalQueries),
                backGroundColor = MaterialTheme.colors.success,
                modifier = Modifier
                    .padding(end = 2.5.dp)
                    .weight(1f)
            )
            StatsCard(
                name = "Queries Blocked",
                statistics = "%,d".format(viewModel.totalBlockedQueries),
                backGroundColor = MaterialTheme.colors.info,
                modifier = Modifier
                    .padding(start = 2.5.dp)
                    .weight(1f)
            )
        }
        Row(Modifier.padding(top = 5.dp)) {
            StatsCard(
                name = "Percent Blocked",
                statistics = "%.2f%%".format(viewModel.queryBlockingPercentage),
                backGroundColor = MaterialTheme.colors.warning,
                modifier = Modifier
                    .padding(end = 2.5.dp)
                    .weight(1f)
            )
            StatsCard(
                name = "Blocklist",
                statistics = "%,d".format(viewModel.blockedDomainListCount),
                backGroundColor = MaterialTheme.colors.error,
                modifier = Modifier
                    .padding(start = 2.5.dp)
                    .weight(1f)
            )
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