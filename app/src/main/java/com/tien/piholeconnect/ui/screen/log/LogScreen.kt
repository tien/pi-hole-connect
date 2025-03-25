package com.tien.piholeconnect.ui.screen.log

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.LoadState
import com.tien.piholeconnect.model.QueryLog
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.ui.component.LogItem
import com.tien.piholeconnect.ui.component.QueryDetail
import com.tien.piholeconnect.ui.component.TopBarProgressIndicator
import com.tien.piholeconnect.util.ChangedEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(actions: @Composable () -> Unit, viewModel: LogViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val scaffoldState = rememberBottomSheetScaffoldState()

    var searchActive by remember { mutableStateOf(false) }
    val query by viewModel.query.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val enabledStatuses by viewModel.enabledStatuses.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()

    var selectedLog: QueryLog? by remember { mutableStateOf(null) }

    viewModel.SnackBarErrorEffect(scaffoldState.snackbarHostState)

    val addRuleSuccessMessage = stringResource(R.string.log_screen_add_filter_rule_success)
    val addRuleFailureMessage = stringResource(R.string.log_screen_add_filter_rule_failure)

    suspend fun handleLoadState(loadState: LoadState<*>) {
        when (loadState) {
            is LoadState.Success -> {
                selectedLog = null
                scaffoldState.snackbarHostState.showSnackbar(addRuleSuccessMessage)
            }
            is LoadState.Failure -> {
                selectedLog = null
                scaffoldState.snackbarHostState.showSnackbar(addRuleFailureMessage)
            }
            else -> Unit
        }
    }

    val addToAllowListLoadState by viewModel.addToAllowlistLoadState.collectAsStateWithLifecycle()

    ChangedEffect(addToAllowListLoadState) { handleLoadState(addToAllowListLoadState) }

    val addToDenyListLoadState by viewModel.addToDenyListLoadState.collectAsStateWithLifecycle()

    ChangedEffect(addToDenyListLoadState) { handleLoadState(addToDenyListLoadState) }

    LaunchedEffect(Unit) { viewModel.backgroundRefresh() }

    LaunchedEffect(logs) { lazyListState.scrollToItem(0) }

    selectedLog?.let { logQuery ->
        QueryDetail(
            logQuery,
            onWhitelistClick = {
                if (logQuery.domain != null) {
                    viewModel.addToWhiteList(logQuery.domain)
                }
            },
            onBlacklistClick = {
                if (logQuery.domain != null) {
                    viewModel.addToBlacklist(logQuery.domain)
                }
            },
            onDismissRequest = { selectedLog = null },
            addToWhitelistLoading = addToAllowListLoadState is LoadState.Loading,
            addToBlacklistLoading = addToDenyListLoadState is LoadState.Loading,
        )
    }

    @Composable
    fun LogList(state: LazyListState = rememberLazyListState()) {
        LazyColumn(state = state) {
            if (logs is LoadState.Success) {
                (logs as LoadState.Success).data.forEachIndexed { index, log ->
                    item(key = index) {
                        LogItem(log, modifier = Modifier.clickable { selectedLog = log })
                    }
                }
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = BottomSheetDefaults.SheetPeekHeight * 0.75f,
        topBar = {
            Box(Modifier.fillMaxWidth()) {
                SearchBar(
                    modifier = Modifier.align(Alignment.TopCenter),
                    expanded = searchActive,
                    onExpandedChange = { searchActive = it },
                    inputField =
                        @Composable {
                            SearchBarDefaults.InputField(
                                query = query,
                                onQueryChange = { viewModel.query.value = it },
                                onSearch = {
                                    viewModel.query.value = it
                                    searchActive = false
                                },
                                expanded = searchActive,
                                onExpandedChange = { searchActive = it },
                                placeholder = { Text(stringResource(Screen.Log.labelResourceId)) },
                                leadingIcon = {
                                    if (searchActive || query.isNotBlank()) {
                                        IconButton(
                                            onClick = {
                                                searchActive = false
                                                viewModel.query.value = ""
                                            }
                                        ) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription =
                                                    stringResource(android.R.string.cancel),
                                            )
                                        }
                                    } else {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription =
                                                stringResource(android.R.string.search_go),
                                        )
                                    }
                                },
                                trailingIcon = {
                                    if (!searchActive) {
                                        actions()
                                    }
                                },
                            )
                        },
                ) {
                    LogList()
                }
            }
        },
        sheetContent = {
            val paddingModifier = Modifier.padding(horizontal = 16.dp)
            val styledDivider =
                @Composable { HorizontalDivider(paddingModifier.padding(vertical = 16.dp)) }

            Text(stringResource(R.string.log_screen_number_of_queries), modifier = paddingModifier)

            val limit by viewModel.limit.collectAsStateWithLifecycle()

            viewModel.limits.forEach {
                ListItem(
                    modifier =
                        Modifier.selectable(
                            selected = limit == it,
                            onClick = { viewModel.changeLimit(it) },
                            role = Role.RadioButton,
                        ),
                    leadingContent = { RadioButton(selected = limit == it, onClick = null) },
                    headlineContent = { Text(it.toString()) },
                )
            }
            styledDivider()
            Text(stringResource(R.string.log_screen_status), modifier = paddingModifier)
            LogViewModel.Status.entries.forEach { status ->
                val checked = enabledStatuses.contains(status)
                ListItem(
                    modifier =
                        Modifier.selectable(
                            selected = checked,
                            onClick = {
                                if (!checked) {
                                    viewModel.enabledStatuses.value += status
                                } else {
                                    viewModel.enabledStatuses.value -= status
                                }
                            },
                            role = Role.Checkbox,
                        ),
                    leadingContent = { Checkbox(checked = checked, onCheckedChange = null) },
                    headlineContent = { Text(stringResource(status.labelResourceId)) },
                )
            }
            styledDivider()
            Text(stringResource(R.string.log_screen_sort), modifier = paddingModifier)
            LogViewModel.Sort.entries.forEach { sort ->
                val selected = sortBy == sort
                ListItem(
                    modifier =
                        Modifier.selectable(
                            selected = selected,
                            onClick = { viewModel.sortBy.value = sort },
                            role = Role.RadioButton,
                        ),
                    leadingContent = { RadioButton(selected = selected, onClick = null) },
                    headlineContent = { Text(stringResource(sort.labelResourceId)) },
                )
            }
        },
        content = {
            val loading by viewModel.loading.collectAsStateWithLifecycle()
            val refreshing by viewModel.refreshing.collectAsStateWithLifecycle()
            val pullToRefreshState = rememberPullToRefreshState()

            TopBarProgressIndicator(visible = loading && !refreshing)

            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = refreshing,
                onRefresh = { viewModel.refresh() },
            ) {
                Column {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (logs is LoadState.Success) {
                            val loadedLogs = logs as LoadState.Success
                            Text(
                                pluralStringResource(
                                    R.plurals.log_screen_results,
                                    loadedLogs.data.count(),
                                    loadedLogs.data.count(),
                                ),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = { scope.launch { scaffoldState.bottomSheetState.expand() } }
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null)
                        }
                    }
                    HorizontalDivider()
                    LogList(state = lazyListState)
                }
            }
        },
    )
}
