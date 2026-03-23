package com.tien.piholeconnect.ui.screen.tools

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.DeleteSweep
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material.icons.twotone.Router
import androidx.compose.material.icons.twotone.SystemUpdateAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.LoadState
import com.tien.piholeconnect.util.rememberRelativeTime

@Composable
fun ToolsScreen(viewModel: ToolsViewModel = hiltViewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val gravityUpdatedAt by viewModel.gravityUpdatedAt.collectAsStateWithLifecycle()
    val operationLoadState by viewModel.operationLoadState.collectAsStateWithLifecycle()

    val successMessage = stringResource(R.string.tools_operation_success)
    val errorMessage = stringResource(R.string.tools_operation_error)

    LaunchedEffect(Unit) {
        viewModel.operationLoadState.collect {
            when (it) {
                is LoadState.Success -> snackbarHostState.showSnackbar(successMessage)
                is LoadState.Failure -> snackbarHostState.showSnackbar(errorMessage)
                else -> Unit
            }
        }
    }

    viewModel.SnackBarErrorEffect(snackbarHostState)

    val isAnyLoading = operationLoadState is LoadState.Loading

    val refreshing by viewModel.refreshing.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = refreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                ToolItem(
                    label = stringResource(R.string.tools_update_gravity),
                    supportingContent = {
                        gravityUpdatedAt.data?.let {
                            Text(
                                stringResource(
                                    R.string.tools_gravity_last_updated,
                                    rememberRelativeTime(it).value,
                                )
                            )
                        }
                    },
                    icon = Icons.TwoTone.SystemUpdateAlt,
                    isLoading =
                        operationLoadState is LoadState.Loading &&
                            operationLoadState.data == ToolsViewModel.Tool.UPDATE_GRAVITY,
                    isAnyLoading = isAnyLoading,
                    onClick = viewModel::updateGravity,
                )
                HorizontalDivider()
                ToolItem(
                    label = stringResource(R.string.tools_restart_dns),
                    icon = Icons.TwoTone.Refresh,
                    isLoading =
                        operationLoadState is LoadState.Loading &&
                            operationLoadState.data == ToolsViewModel.Tool.RESTART_DNS,
                    isAnyLoading = isAnyLoading,
                    onClick = viewModel::restartDNS,
                )
                HorizontalDivider()
                ToolItem(
                    label = stringResource(R.string.tools_flush_network_table),
                    icon = Icons.TwoTone.Router,
                    isLoading =
                        operationLoadState is LoadState.Loading &&
                            operationLoadState.data == ToolsViewModel.Tool.FLUSH_NETWORK_TABLE,
                    isAnyLoading = isAnyLoading,
                    onClick = viewModel::flushNetworkTable,
                )
                HorizontalDivider()
                ToolItem(
                    label = stringResource(R.string.tools_flush_logs),
                    icon = Icons.TwoTone.DeleteSweep,
                    isLoading =
                        operationLoadState is LoadState.Loading &&
                            operationLoadState.data == ToolsViewModel.Tool.FLUSH_LOG,
                    isAnyLoading = isAnyLoading,
                    onClick = viewModel::flushLog,
                )
            }
        }
    }
}

@Composable
private fun ToolItem(
    label: String,
    supportingContent: @Composable (() -> Unit)? = null,
    icon: ImageVector,
    isLoading: Boolean,
    isAnyLoading: Boolean,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(label) },
        supportingContent = supportingContent,
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            }
        },
        modifier =
            Modifier.alpha(if (isAnyLoading && !isLoading) 0.38f else 1f)
                .clickable(enabled = !isAnyLoading, onClick = onClick),
    )
}
