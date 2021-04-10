package com.tien.piholeconnect.ui.screen.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tien.piholeconnect.extension.showGenericPiHoleConnectionError
import com.tien.piholeconnect.ui.component.RankedListCard
import com.tien.piholeconnect.ui.component.SwipeToRefreshLayout
import com.tien.piholeconnect.ui.theme.info
import com.tien.piholeconnect.ui.theme.success
import kotlinx.coroutines.launch

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = viewModel(),
    scaffoldState: ScaffoldState
) {
    val context = LocalContext.current
    var isRefreshing by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.viewModelScope.launch {
            viewModel.apply {
                refresh()
                error?.let {
                    scaffoldState.snackbarHostState.showGenericPiHoleConnectionError(context)
                }
            }
        }
    }

    SwipeToRefreshLayout(
        refreshingState = isRefreshing,
        onRefresh = {
            viewModel.viewModelScope.launch {
                isRefreshing = true
                viewModel.apply {
                    refresh()
                    isRefreshing = false
                    error?.let {
                        scaffoldState.snackbarHostState.showGenericPiHoleConnectionError(context)
                    }
                }
            }
        }) {
        Column(
            modifier
                .verticalScroll(rememberScrollState())
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            RankedListCard(
                title = { Text("Top permitted") },
                icon = {
                    Icon(
                        Icons.Default.GppGood,
                        contentDescription = null,
                        tint = MaterialTheme.colors.success
                    )
                },
                valueMap = viewModel.statistics.topQueries
            )
            RankedListCard(
                title = { Text("Top blocked") },
                icon = {
                    Icon(
                        Icons.Default.GppBad,
                        contentDescription = null,
                        tint = MaterialTheme.colors.error
                    )
                },
                valueMap = viewModel.statistics.topAds
            )
            RankedListCard(
                title = { Text("Top client") },
                icon = {
                    Icon(
                        Icons.Default.Devices,
                        contentDescription = null,
                        tint = MaterialTheme.colors.info
                    )
                },
                valueMap = viewModel.statistics.topSources
            )
        }
    }
}