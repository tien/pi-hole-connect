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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.AsyncState
import com.tien.piholeconnect.model.ModifyFilterRuleResponse
import com.tien.piholeconnect.model.PiHoleLog
import com.tien.piholeconnect.model.RuleType
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.ui.component.LogItem
import com.tien.piholeconnect.ui.component.QueryDetail
import com.tien.piholeconnect.util.ChangedEffect
import com.tien.piholeconnect.util.SnackbarErrorEffect
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(actions: @Composable () -> Unit, viewModel: LogViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val scaffoldState = rememberBottomSheetScaffoldState()

    var searchActive by remember { mutableStateOf(false) }
    val query by viewModel.query.collectAsState()
    val logs by viewModel.logs.collectAsState(initial = listOf())
    val enabledStatuses by viewModel.enabledStatuses.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    var selectedLog: PiHoleLog? by remember { mutableStateOf(null) }

    viewModel.RefreshOnConnectionChangeEffect()

    SnackbarErrorEffect(viewModel.error, scaffoldState.snackbarHostState)

    ChangedEffect(viewModel.modifyFilterRuleState) {
        (viewModel.modifyFilterRuleState.second as? AsyncState.Settled)?.let {
            selectedLog = null
        }
    }

    ChangedEffect(scaffoldState.snackbarHostState, viewModel.modifyFilterRuleState) {
        when (viewModel.modifyFilterRuleState.second) {
            is AsyncState.Settled -> {
                (viewModel.modifyFilterRuleState.second as? AsyncState.Settled<ModifyFilterRuleResponse>)?.let { asyncState ->
                    when {
                        asyncState.result.isSuccess -> asyncState.result.getOrNull()?.message?.let {
                            scaffoldState.snackbarHostState.showSnackbar(it)
                        }

                        asyncState.result.isFailure -> asyncState.result.exceptionOrNull()?.localizedMessage?.let {
                            scaffoldState.snackbarHostState.showSnackbar(it)
                        }

                        else -> Unit
                    }
                }
            }

            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        viewModel.viewModelScope.launch {
            viewModel.apply {
                refresh()
            }
        }
    }

    LaunchedEffect(logs) {
        lazyListState.scrollToItem(0)
    }

    selectedLog?.let { logQuery ->
        QueryDetail(
            logQuery,
            onWhitelistClick = { viewModel.addToWhiteList(logQuery.requestedDomain) },
            onBlacklistClick = { viewModel.addToBlacklist(logQuery.requestedDomain) },
            onDismissRequest = { selectedLog = null },
            addToWhitelistLoading = viewModel.modifyFilterRuleState.first == RuleType.WHITE && viewModel.modifyFilterRuleState.second is AsyncState.Pending,
            addToBlacklistLoading = viewModel.modifyFilterRuleState.first == RuleType.BLACK && viewModel.modifyFilterRuleState.second is AsyncState.Pending
        )
    }

    @Composable
    fun LogList(state: LazyListState = rememberLazyListState()) {
        LazyColumn(state = state) {
            if (viewModel.hasBeenLoaded) {
                logs.forEachIndexed { index, log ->
                    item(key = index) {
                        LogItem(log, modifier = Modifier.clickable { selectedLog = log })
                    }
                }
            }
        }
    }

    BottomSheetScaffold(scaffoldState = scaffoldState,
        sheetPeekHeight = BottomSheetDefaults.SheetPeekHeight * 0.75f,
        topBar = {
            Box(Modifier.fillMaxWidth()) {
                SearchBar(modifier = Modifier.align(Alignment.TopCenter),
                    query = query,
                    onQueryChange = { viewModel.query.value = it },
                    onSearch = {
                        viewModel.query.value = it
                        searchActive = false
                    },
                    active = searchActive,
                    onActiveChange = { searchActive = it },
                    placeholder = { Text(stringResource(Screen.Log.labelResourceId)) },
                    leadingIcon = {
                        if (searchActive || query.isNotBlank()) {
                            IconButton(onClick = {
                                searchActive = false
                                viewModel.query.value = ""
                            }) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = stringResource(android.R.string.cancel)
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(android.R.string.search_go)
                            )
                        }
                    },
                    trailingIcon = {
                        if (!searchActive) {
                            actions()
                        }
                    }) {
                    LogList()
                }
            }
        },
        sheetContent = {
            val paddingModifier = Modifier.padding(horizontal = 16.dp)
            val styledDivider = @Composable {
                Divider(
                    paddingModifier.padding(vertical = 16.dp),
                    color = Color.White.copy(alpha = 0.12f)
                )
            }

            Text(
                stringResource(R.string.log_screen_number_of_queries), modifier = paddingModifier
            )
            viewModel.limits.forEach {
                ListItem(modifier = Modifier.selectable(
                    selected = viewModel.limit == it,
                    onClick = { viewModel.changeLimit(it) },
                    role = Role.RadioButton
                ), leadingContent = {
                    RadioButton(
                        selected = viewModel.limit == it,
                        onClick = null,
                    )
                }, headlineContent = { Text(it.toString()) })
            }
            styledDivider()
            Text(stringResource(R.string.log_screen_status), modifier = paddingModifier)
            LogViewModel.Status.values().forEach { status ->
                val checked = enabledStatuses.contains(status)
                ListItem(modifier = Modifier.selectable(
                    selected = checked, onClick = {
                        if (!checked) {
                            viewModel.enabledStatuses.value += status
                        } else {
                            viewModel.enabledStatuses.value -= status
                        }
                    }, role = Role.Checkbox
                ), leadingContent = {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = null,
                    )
                }, headlineContent = { Text(stringResource(status.labelResourceId)) })
            }
            styledDivider()
            Text(stringResource(R.string.log_screen_sort), modifier = paddingModifier)
            LogViewModel.Sort.values().forEach { sort ->
                val selected = sortBy == sort
                ListItem(modifier = Modifier.selectable(
                    selected = selected,
                    onClick = { viewModel.sortBy.value = sort },
                    role = Role.RadioButton
                ), leadingContent = {
                    RadioButton(
                        selected = selected, onClick = null
                    )
                }, headlineContent = { Text(stringResource(sort.labelResourceId)) })
            }
        },
        content = {
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
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (viewModel.hasBeenLoaded) {
                            Text(
                                pluralStringResource(
                                    R.plurals.log_screen_results, logs.count(), logs.count()
                                ),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { scope.launch { scaffoldState.bottomSheetState.expand() } }) {
                            Icon(
                                Icons.Default.Tune, contentDescription = null
                            )
                        }
                    }
                    Divider()
                    LogList(state = lazyListState)
                }
                PullToRefreshContainer(pullToRefreshState, Modifier.align(Alignment.TopCenter))
            }
        })
}
