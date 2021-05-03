package com.tien.piholeconnect.ui.screen.filterrules

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.RuleType
import com.tien.piholeconnect.ui.component.AddFilterRuleDialog
import com.tien.piholeconnect.ui.component.TopBarProgressIndicator
import com.tien.piholeconnect.util.showGenericPiHoleConnectionError
import kotlinx.coroutines.launch
import java.text.DateFormat
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterRulesScreen(
    modifier: Modifier = Modifier,
    viewModel: FilterRulesViewModel = viewModel()
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()

    val dateTimeInstance = remember { DateFormat.getDateInstance() }
    val whiteListTabRules = rememberSaveable { listOf(RuleType.WHITE, RuleType.REGEX_WHITE) }
    val blackListTabRules = rememberSaveable { listOf(RuleType.BLACK, RuleType.REGEX_BLACK) }
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    var isAddDialogVisible by rememberSaveable { mutableStateOf(false) }

    viewModel.RefreshOnConnectionChangeEffect()

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let {
            scaffoldState.snackbarHostState.showGenericPiHoleConnectionError(context, it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    TopBarProgressIndicator(visible = !viewModel.hasBeenLoaded && viewModel.isRefreshing)

    if (isAddDialogVisible) {
        AddFilterRuleDialog(
            value = viewModel.addRuleInputValue,
            onValueChange = { viewModel.addRuleInputValue = it },
            isWildcardChecked = viewModel.addRuleIsWildcardChecked,
            onIsWildcardCheckedChanged = { viewModel.addRuleIsWildcardChecked = it },
            onDismissRequest = { isAddDialogVisible = false },
            onConfirmClick = {
                isAddDialogVisible = false
                viewModel.viewModelScope.launch { viewModel.addRule() }
            },
            onCancelClick = {
                isAddDialogVisible = false
                viewModel.resetAddRuleDialogInputs()
            })
    }

    Scaffold(modifier, scaffoldState = scaffoldState, floatingActionButton = {
        FloatingActionButton(onClick = { isAddDialogVisible = true }) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(R.string.filter_rules_desc_add_filter)
            )
        }
    }) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                viewModel.viewModelScope.launch {
                    isRefreshing = true
                    viewModel.refresh()
                    isRefreshing = false
                }
            }) {
            Column {
                TabRow(selectedTabIndex = viewModel.selectedTab.ordinal) {
                    Tab(selected = viewModel.selectedTab == FilterRulesViewModel.Tab.BLACK,
                        onClick = { viewModel.selectedTab = FilterRulesViewModel.Tab.BLACK },
                        icon = { Icon(Icons.Default.Block, contentDescription = null) },
                        text = { Text(stringResource(R.string.filter_rules_black_list)) })
                    Tab(selected = viewModel.selectedTab == FilterRulesViewModel.Tab.WHITE,
                        onClick = { viewModel.selectedTab = FilterRulesViewModel.Tab.WHITE },
                        icon = {
                            Icon(
                                Icons.Default.CheckCircleOutline,
                                contentDescription = null
                            )
                        },
                        text = { Text(stringResource(R.string.filter_rules_white_list)) })
                }

                if (viewModel.hasBeenLoaded) {
                    LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                        viewModel.rules.filter {
                            when (viewModel.selectedTab) {
                                FilterRulesViewModel.Tab.BLACK -> blackListTabRules.contains(it.type)
                                FilterRulesViewModel.Tab.WHITE -> whiteListTabRules.contains(it.type)
                            }
                        }.forEach { rule ->
                            item(rule.id) {
                                val swipeableState = rememberSwipeableState(0)
                                val iconSize = with(LocalDensity.current) { 48.dp.toPx() }

                                Box(
                                    Modifier.swipeable(
                                        state = swipeableState,
                                        anchors = mapOf(0f to 0, -iconSize to 1),
                                        orientation = Orientation.Horizontal
                                    )
                                ) {
                                    Box(Modifier.matchParentSize()) {
                                        Row(
                                            Modifier
                                                .fillMaxSize()
                                                .background(MaterialTheme.colors.error),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(modifier = Modifier.fillMaxHeight(),
                                                onClick = {
                                                    viewModel.viewModelScope.launch {
                                                        viewModel.removeRule(
                                                            rule.domain,
                                                            ruleType = rule.type
                                                        )
                                                    }
                                                }) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = stringResource(R.string.filter_rules_desc_delete_filter),
                                                    tint = contentColorFor(MaterialTheme.colors.error)
                                                )
                                            }
                                        }
                                    }
                                    ListItem(
                                        Modifier
                                            .offset {
                                                IntOffset(
                                                    swipeableState.offset.value.roundToInt(),
                                                    0
                                                )
                                            }
                                            .background(MaterialTheme.colors.background),
                                        overlineText = when (rule.type) {
                                            RuleType.REGEX_BLACK, RuleType.REGEX_WHITE -> ({ Text("RegExr") })
                                            else -> null
                                        },
                                        text = { Text(rule.domain) },
                                        secondaryText = rule.comment?.let { { Text(it) } },
                                        trailing = {
                                            Text(
                                                text = dateTimeInstance.format(rule.dateAdded * 1000L)
                                            )
                                        })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}