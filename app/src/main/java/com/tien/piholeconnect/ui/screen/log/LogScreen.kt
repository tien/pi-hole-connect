package com.tien.piholeconnect.ui.screen.log

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.*
import com.tien.piholeconnect.ui.component.LogItem
import com.tien.piholeconnect.ui.component.QueryDetail
import com.tien.piholeconnect.util.ChangedEffect
import com.tien.piholeconnect.util.ConsumeAllNestedScroll
import com.tien.piholeconnect.util.showGenericPiHoleConnectionError
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LogScreen(viewModel: LogViewModel = viewModel(), actions: @Composable RowScope.() -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    val themeColors = MaterialTheme.colors

    val lazyListState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val scaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed)
    val query by viewModel.query.collectAsState()
    val logs by viewModel.logs.collectAsState(initial = listOf())
    val enabledStatuses by viewModel.enabledStatuses.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    var selectedLog: PiHoleLog? by remember { mutableStateOf(null) }

    viewModel.RefreshOnConnectionChangeEffect()

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let {
            scaffoldState.snackbarHostState.showGenericPiHoleConnectionError(context, it)
        }
    }

    ChangedEffect(viewModel.modifyFilterRuleState) {
        (viewModel.modifyFilterRuleState.second as? AsyncState.Settled)?.let {
            selectedLog = null
        }
    }

    ChangedEffect(viewModel.modifyFilterRuleState) {
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
        Dialog(onDismissRequest = { selectedLog = null }) {
            QueryDetail(
                logQuery,
                onWhitelistClick = { viewModel.addToWhiteList(logQuery.requestedDomain) },
                onBlacklistClick = { viewModel.addToBlacklist(logQuery.requestedDomain) },
                onDismissRequest = { selectedLog = null },
                addToWhitelistLoading = viewModel.modifyFilterRuleState.first == RuleType.WHITE && viewModel.modifyFilterRuleState.second is AsyncState.Pending,
                addToBlacklistLoading = viewModel.modifyFilterRuleState.first == RuleType.BLACK && viewModel.modifyFilterRuleState.second is AsyncState.Pending
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        systemUiController.setStatusBarColor(color = themeColors.primaryVariant)
    }

    BackdropScaffold(
        scaffoldState = scaffoldState,
        backLayerBackgroundColor = MaterialTheme.colors.primaryVariant,
        appBar = {
            val focusRequester = remember { FocusRequester() }
            var isFocused by rememberSaveable { mutableStateOf(false) }

            TopAppBar(
                elevation = 0.dp,
                backgroundColor = Color.Transparent
            ) {
                if (isFocused || query.isNotEmpty()) {
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }

                    IconButton(onClick = {
                        viewModel.query.value = ""
                        focusManager.clearFocus()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = { viewModel.query.value = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        cursorBrush = SolidColor(Color.White),
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged { isFocused = it.isFocused }
                            .weight(1f))
                } else {
                    Row(
                        Modifier
                            .fillMaxHeight()
                            .padding(start = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProvideTextStyle(value = MaterialTheme.typography.h6) {
                            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                                Text(stringResource(Screen.Log.labelResourceId))
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {
                        coroutineScope.launch {
                            scaffoldState.conceal()
                            isFocused = true
                        }
                    }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                    actions()
                }
            }
        },
        backLayerContent = {
            val paddingModifier = Modifier.padding(horizontal = 16.dp)
            val radioButtonColors = RadioButtonDefaults.colors(
                selectedColor = Color.White,
                unselectedColor = Color.White.copy(alpha = 0.6f)
            )
            val styledDivider = @Composable {
                Divider(
                    paddingModifier.padding(vertical = 16.dp),
                    color = Color.White.copy(alpha = 0.12f)
                )
            }
            Column(
                Modifier
                    .padding(top = 5.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    stringResource(R.string.log_screen_number_of_queries),
                    modifier = paddingModifier
                )
                viewModel.limits.forEach {
                    ListItem(
                        Modifier.selectable(
                            selected = viewModel.limit == it,
                            onClick = { viewModel.changeLimit(it) },
                            role = Role.RadioButton
                        ),
                        icon = {
                            RadioButton(
                                selected = viewModel.limit == it,
                                onClick = null,
                                colors = radioButtonColors
                            )
                        },
                        text = { Text(it.toString()) })
                }
                styledDivider()
                Text(stringResource(R.string.log_screen_status), modifier = paddingModifier)
                LogViewModel.Status.values().forEach { status ->
                    val checked = enabledStatuses.contains(status)
                    ListItem(
                        Modifier.selectable(
                            selected = checked,
                            onClick = {
                                if (!checked) {
                                    viewModel.enabledStatuses.value += status
                                } else {
                                    viewModel.enabledStatuses.value -= status
                                }
                            },
                            role = Role.Checkbox
                        ),
                        icon = {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.White,
                                    uncheckedColor = Color.White.copy(alpha = 0.6f),
                                    checkmarkColor = MaterialTheme.colors.primaryVariant
                                )
                            )
                        },
                        text = { Text(stringResource(status.labelResourceId)) })
                }
                styledDivider()
                Text(stringResource(R.string.log_screen_sort), modifier = paddingModifier)
                LogViewModel.Sort.values().forEach { sort ->
                    val selected = sortBy == sort
                    ListItem(
                        Modifier.selectable(
                            selected = selected,
                            onClick = { viewModel.sortBy.value = sort },
                            role = Role.RadioButton
                        ),
                        icon = {
                            RadioButton(
                                selected = selected,
                                onClick = null,
                                colors = radioButtonColors
                            )
                        },
                        text = { Text(stringResource(sort.labelResourceId)) })
                }
            }
        },
        frontLayerContent = {
            SwipeRefresh(
                state = rememberSwipeRefreshState(viewModel.isRefreshing),
                onRefresh = {
                    viewModel.viewModelScope.launch { viewModel.refresh() }
                },
                modifier = Modifier.nestedScroll(ConsumeAllNestedScroll())
            ) {
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (viewModel.hasBeenLoaded) {
                            val labelResourceId =
                                if (scaffoldState.isRevealed) R.string.log_screen_see_results else R.string.log_screen_results
                            Text(
                                stringResource(labelResourceId).format(logs.count()),
                                style = MaterialTheme.typography.caption,
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { coroutineScope.launch { scaffoldState.reveal() } }) {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = null
                            )
                        }
                    }
                    Divider()
                    LazyColumn(state = lazyListState) {
                        if (viewModel.hasBeenLoaded) {
                            logs.forEachIndexed { index, log ->
                                item(key = index) {
                                    LogItem(
                                        log,
                                        modifier = Modifier.clickable { selectedLog = log })
                                }
                            }
                        }
                    }
                }
            }
        })
}
