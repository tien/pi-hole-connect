package com.tien.piholeconnect.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.tien.piholeconnect.ui.component.StatsCard
import com.tien.piholeconnect.ui.theme.info
import com.tien.piholeconnect.ui.theme.success
import com.tien.piholeconnect.ui.theme.warning

class HomeViewModel : ViewModel {
    var totalQueries by mutableStateOf(0)
        private set
    var totalBlockedQueries by mutableStateOf(0)
        private set
    var queryBlockingPercentage by mutableStateOf(0f)
        private set
    var blockedDomainListCount by mutableStateOf(0)
        private set

    constructor() : super()

    constructor(
        totalQueries: Int,
        totalBlockedQueries: Int,
        queryBlockingPercentage: Float,
        blockedDomainListCount: Int
    ) : super() {
        this.totalQueries = totalQueries
        this.totalBlockedQueries = totalBlockedQueries
        this.queryBlockingPercentage = queryBlockingPercentage
        this.blockedDomainListCount = blockedDomainListCount
    }
}

@Composable
fun Home(viewModel: HomeViewModel = HomeViewModel()) {
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
fun HomePreview() {
    Home(
        HomeViewModel(
            totalQueries = 4627,
            totalBlockedQueries = 7893,
            queryBlockingPercentage = 4627f / 7893f * 100f,
            blockedDomainListCount = 85394
        )
    )
}