package com.tien.piholeconnect.ui.screen.filterRules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.RuleType
import com.tien.piholeconnect.ui.component.SwipeToRefreshLayout
import kotlinx.coroutines.launch
import java.text.DateFormat

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterRulesScreen(
    modifier: Modifier = Modifier,
    viewModel: FilterRulesViewModel = viewModel()
) {
    val dateTimeInstance = remember { DateFormat.getDateInstance() }
    val whiteListTabRules = rememberSaveable { listOf(RuleType.WHITE, RuleType.REGEX_WHITE) }
    val blackListTabRules = rememberSaveable { listOf(RuleType.BLACK, RuleType.REGEX_BLACK) }
    var isRefreshing by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.viewModelScope.launch {
            viewModel.refresh()
        }
    }

    SwipeToRefreshLayout(
        refreshingState = isRefreshing,
        onRefresh = {
            viewModel.viewModelScope.launch {
                isRefreshing = true
                viewModel.refresh()
                isRefreshing = false
            }
        }) {
        Column(modifier) {
            TabRow(selectedTabIndex = viewModel.selectedTab.ordinal) {
                Tab(selected = viewModel.selectedTab == FilterRulesViewModel.Tab.WHITE,
                    onClick = { viewModel.selectedTab = FilterRulesViewModel.Tab.WHITE },
                    icon = { Icon(Icons.Default.Block, contentDescription = null) },
                    text = { Text(stringResource(R.string.filter_rules_black_list)) })
                Tab(selected = viewModel.selectedTab == FilterRulesViewModel.Tab.BLACK,
                    onClick = { viewModel.selectedTab = FilterRulesViewModel.Tab.BLACK },
                    icon = { Icon(Icons.Default.CheckCircleOutline, contentDescription = null) },
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
                        ListItem(
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