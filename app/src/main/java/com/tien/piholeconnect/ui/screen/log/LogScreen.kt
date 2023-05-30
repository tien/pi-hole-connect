package com.tien.piholeconnect.ui.screen.log

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.BottomSheetScaffoldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.*
import com.tien.piholeconnect.ui.component.LogItem
import com.tien.piholeconnect.ui.component.QueryDetail
import com.tien.piholeconnect.util.ChangedEffect
import com.tien.piholeconnect.util.ConsumeAllNestedScroll
import com.tien.piholeconnect.util.showGenericPiHoleConnectionError
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(viewModel: LogViewModel = viewModel(), actions: @Composable () -> Unit) {
    val context = LocalContext.current

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

    if (viewModel.error != null) {
        LaunchedEffect(scaffoldState.snackbarHostState) {
            viewModel.error?.let {
                scaffoldState.snackbarHostState.showGenericPiHoleConnectionError(context, it)
            }
        }
    }

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
        QueryDetail(logQuery,
            onWhitelistClick = { viewModel.addToWhiteList(logQuery.requestedDomain) },
            onBlacklistClick = { viewModel.addToBlacklist(logQuery.requestedDomain) },
            onDismissRequest = { selectedLog = null },
            addToWhitelistLoading = viewModel.modifyFilterRuleState.first == RuleType.WHITE && viewModel.modifyFilterRuleState.second is AsyncState.Pending,
            addToBlacklistLoading = viewModel.modifyFilterRuleState.first == RuleType.BLACK && viewModel.modifyFilterRuleState.second is AsyncState.Pending
        )
    }


    val logList = @Composable {
        LazyColumn(state = lazyListState) {
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
        sheetPeekHeight = BottomSheetScaffoldDefaults.SheetPeekHeight * 0.75f,
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
                    logList()
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
            SwipeRefresh(
                state = rememberSwipeRefreshState(viewModel.isRefreshing), onRefresh = {
                    viewModel.viewModelScope.launch { viewModel.refresh() }
                }, modifier = Modifier.nestedScroll(ConsumeAllNestedScroll())
            ) {
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (viewModel.hasBeenLoaded) {
                            Text(
                                stringResource(R.string.log_screen_results).format(logs.count()),
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
                    logList()
                }
            }
        })
}
