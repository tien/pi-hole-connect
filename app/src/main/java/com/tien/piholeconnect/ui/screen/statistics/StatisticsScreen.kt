package com.tien.piholeconnect.ui.screen.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.R
import com.tien.piholeconnect.ui.component.RankedListCard
import com.tien.piholeconnect.ui.component.TopBarProgressIndicator
import com.tien.piholeconnect.ui.theme.info
import com.tien.piholeconnect.ui.theme.success
import com.tien.piholeconnect.util.SnackbarErrorEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    snackbarHostState: SnackbarHostState, viewModel: StatisticsViewModel = hiltViewModel()
) {
    var isRefreshing by rememberSaveable { mutableStateOf(false) }

    viewModel.RefreshOnConnectionChangeEffect()

    SnackbarErrorEffect(viewModel.error, snackbarHostState)

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    TopBarProgressIndicator(visible = !viewModel.hasBeenLoaded && viewModel.isRefreshing)

    if (!viewModel.hasBeenLoaded) return

    val pullToRefreshState = rememberPullToRefreshState()

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.viewModelScope.launch {
                viewModel.refresh()
                pullToRefreshState.endRefresh()
            }
        }
    }

    Box(Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            RankedListCard(
                title = { Text(stringResource(R.string.statistics_top_permitted)) },
                icon = {
                    Icon(
                        Icons.Default.GppGood,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.success
                    )
                },
                valueMap = viewModel.statistics.topQueries
            )
            RankedListCard(
                title = { Text(stringResource(R.string.statistics_top_blocked)) },
                icon = {
                    Icon(
                        Icons.Default.GppBad,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                valueMap = viewModel.statistics.topAds
            )
            RankedListCard(
                title = { Text(stringResource(R.string.statistics_top_client)) },
                icon = {
                    Icon(
                        Icons.Default.Devices,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.info
                    )
                },
                valueMap = viewModel.statistics.topSources
            )
        }
        PullToRefreshContainer(pullToRefreshState, Modifier.align(Alignment.TopCenter))
    }
}
