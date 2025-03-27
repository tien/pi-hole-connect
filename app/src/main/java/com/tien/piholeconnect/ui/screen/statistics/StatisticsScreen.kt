package com.tien.piholeconnect.ui.screen.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tien.piholeconnect.R
import com.tien.piholeconnect.ui.component.RankedListCard
import com.tien.piholeconnect.ui.component.TopBarProgressIndicator
import com.tien.piholeconnect.ui.theme.info
import com.tien.piholeconnect.ui.theme.success

@OptIn(ExperimentalMaterial3Api::class)
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
        val topDomains by viewModel.topDomains.collectAsStateWithLifecycle()
        val topBlockedDomains by viewModel.topBlockedDomains.collectAsStateWithLifecycle()
        val topClients by viewModel.topClients.collectAsStateWithLifecycle()

        if (topDomains.data == null && topBlockedDomains.data == null && topClients.data == null) {
            return@PullToRefreshBox
        }

        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            RankedListCard(
                title = { Text(stringResource(R.string.statistics_top_permitted)) },
                icon = {
                    Icon(
                        Icons.Default.GppGood,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.success,
                    )
                },
                valueMap = topDomains.data ?: mapOf(),
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
                valueMap = topBlockedDomains.data ?: mapOf(),
            )
            RankedListCard(
                title = { Text(stringResource(R.string.statistics_top_client)) },
                icon = {
                    Icon(
                        Icons.Default.Devices,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.info,
                    )
                },
                valueMap = topClients.data ?: mapOf(),
            )
        }
    }
}
