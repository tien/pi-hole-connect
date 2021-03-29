package com.tien.piholeconnect.ui.screen.filterRules

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissValue.Default
import androidx.compose.material.DismissValue.DismissedToStart
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tien.piholeconnect.R
import com.tien.piholeconnect.extension.showGenericPiHoleConnectionError
import com.tien.piholeconnect.model.RuleType
import com.tien.piholeconnect.ui.component.AddFilterRuleDialog
import com.tien.piholeconnect.ui.component.SwipeToRefreshLayout
import com.tien.piholeconnect.ui.theme.contentColorFor
import kotlinx.coroutines.launch
import java.text.DateFormat

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
            Icon(Icons.Default.Add, contentDescription = "Add filter rule")
        }
    }) {
        SwipeToRefreshLayout(
            refreshingState = isRefreshing,
            onRefresh = {
                viewModel.viewModelScope.launch {
                    isRefreshing = true
                    viewModel.apply {
                        refresh()
                        error?.let {
                            scaffoldState.snackbarHostState.showGenericPiHoleConnectionError(context)
                        }
                    }
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
                LazyColumn {
                    viewModel.rules.filter {
                        when (viewModel.selectedTab) {
                            FilterRulesViewModel.Tab.BLACK -> blackListTabRules.contains(it.type)
                            FilterRulesViewModel.Tab.WHITE -> whiteListTabRules.contains(it.type)
                        }
                    }.forEach { rule ->
                        item(rule.id) {
                            val dismissState = rememberDismissState()
                            val dismissed = dismissState.isDismissed(EndToStart)

                            LaunchedEffect(dismissed) {
                                if (dismissed) viewModel.viewModelScope.launch {
                                    viewModel.removeRule(rule.domain, ruleType = rule.type)
                                }
                            }

                            SwipeToDismiss(
                                state = dismissState,
                                directions = setOf(EndToStart),
                                background = {
                                    val color by animateColorAsState(
                                        when (dismissState.targetValue) {
                                            Default -> Color.LightGray
                                            DismissedToStart -> MaterialTheme.colors.error
                                            else -> throw NotImplementedError()
                                        }
                                    )
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .background(color)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = contentColorFor(color)
                                        )
                                    }
                                }) {
                                ListItem(
                                    Modifier.background(MaterialTheme.colors.background),
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