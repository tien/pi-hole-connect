package com.tien.piholeconnect.ui.screen.log

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tien.piholeconnect.ui.component.LogItem
import com.tien.piholeconnect.ui.component.SwipeToRefreshLayout
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LogScreen(modifier: Modifier = Modifier, viewModel: LogViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.viewModelScope.launch {
            viewModel.refresh()
        }
    }

    if (viewModel.logs.count() == 0) return

    SwipeToRefreshLayout(
        refreshingState = viewModel.isRefreshing,
        onRefresh = { viewModel.viewModelScope.launch { viewModel.refresh() } }) {
        LazyColumn(modifier) {
            viewModel.logs.forEachIndexed { index, log ->
                item(key = index) {
                    LogItem(log)
                }
            }
        }
    }
}