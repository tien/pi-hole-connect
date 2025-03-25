// TODO: Remove after material3 make swipe-able public
@file:Suppress("INVISIBLE_MEMBER")

package com.tien.piholeconnect.ui.screen.filterrules

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.LoadState
import com.tien.piholeconnect.repository.models.GetDomainsInner
import com.tien.piholeconnect.ui.component.AddFilterRuleDialog
import kotlinx.coroutines.launch
import java.text.DateFormat
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilterRulesScreen(viewModel: FilterRulesViewModel = hiltViewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }

    val dateTimeInstance = remember { DateFormat.getDateInstance() }
    var isAddDialogVisible by rememberSaveable { mutableStateOf(false) }

    viewModel.SnackBarErrorEffect(snackbarHostState)

    LaunchedEffect(Unit) { viewModel.refresh() }

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
            },
        )
    }

    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { isAddDialogVisible = true }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.filter_rules_desc_add_filter),
                )
            }
        },
    ) {
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.viewModelScope.launch {
                    viewModel.refresh()
                    isRefreshing = false
                }
            },
        ) {
            Column(Modifier.padding(it)) {
                TabRow(selectedTabIndex = viewModel.selectedTab.ordinal) {
                    Tab(
                        selected = viewModel.selectedTab == FilterRulesViewModel.Tab.BLACK,
                        onClick = { viewModel.selectedTab = FilterRulesViewModel.Tab.BLACK },
                        icon = { Icon(Icons.Default.Block, contentDescription = null) },
                        text = { Text(stringResource(R.string.filter_rules_blacklist)) },
                    )
                    Tab(
                        selected = viewModel.selectedTab == FilterRulesViewModel.Tab.WHITE,
                        onClick = { viewModel.selectedTab = FilterRulesViewModel.Tab.WHITE },
                        icon = {
                            Icon(Icons.Default.CheckCircleOutline, contentDescription = null)
                        },
                        text = { Text(stringResource(R.string.filter_rules_whitelist)) },
                    )
                }

                val rulesState by viewModel.rules.collectAsStateWithLifecycle(LoadState.Loading())

                if (rulesState.data != null) {
                    LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                        rulesState.data
                            ?.filter {
                                when (viewModel.selectedTab) {
                                    FilterRulesViewModel.Tab.BLACK ->
                                        it.type === GetDomainsInner.Type.DENY
                                    FilterRulesViewModel.Tab.WHITE ->
                                        it.type === GetDomainsInner.Type.ALLOW
                                }
                            }
                            ?.forEach { rule ->
                                item(rule.id) {
                                    val localDensity = LocalDensity.current
                                    val iconSize = with(localDensity) { 48.dp.toPx() }
                                    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
                                    val anchoredDraggableState = remember {
                                        AnchoredDraggableState(
                                            initialValue = 0,
                                            anchors =
                                                DraggableAnchors {
                                                    0 at 0f
                                                    1 at -iconSize
                                                },
                                            positionalThreshold = { distance: Float ->
                                                distance * 0.5f
                                            },
                                            velocityThreshold = {
                                                with(localDensity) { 100.dp.toPx() }
                                            },
                                            snapAnimationSpec = tween(),
                                            decayAnimationSpec = decayAnimationSpec,
                                        )
                                    }

                                    Box(
                                        Modifier.anchoredDraggable(
                                            state = anchoredDraggableState,
                                            orientation = Orientation.Horizontal,
                                        )
                                    ) {
                                        Box(Modifier.matchParentSize()) {
                                            Row(
                                                Modifier.fillMaxSize()
                                                    .background(MaterialTheme.colorScheme.error),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                IconButton(
                                                    modifier = Modifier.fillMaxHeight(),
                                                    onClick = {
                                                        viewModel.viewModelScope.launch {
                                                            viewModel.removeRule(rule)
                                                        }
                                                    },
                                                ) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription =
                                                            stringResource(
                                                                R.string
                                                                    .filter_rules_desc_delete_filter
                                                            ),
                                                        tint =
                                                            contentColorFor(
                                                                MaterialTheme.colorScheme.error
                                                            ),
                                                    )
                                                }
                                            }
                                        }
                                        ListItem(
                                            modifier =
                                                Modifier.offset {
                                                        IntOffset(
                                                            anchoredDraggableState
                                                                .requireOffset()
                                                                .roundToInt(),
                                                            0,
                                                        )
                                                    }
                                                    .background(
                                                        MaterialTheme.colorScheme.background
                                                    ),
                                            overlineContent =
                                                when (rule.kind) {
                                                    GetDomainsInner.Kind.REGEX -> ({
                                                            Text(
                                                                stringResource(
                                                                    R.string.filter_rules_reg_exr
                                                                )
                                                            )
                                                        })
                                                    else -> null
                                                },
                                            headlineContent = {
                                                if (rule.domain != null) {
                                                    Text(rule.domain)
                                                }
                                            },
                                            supportingContent = {
                                                Text(
                                                    buildString {
                                                        rule.comment?.let { append(it) }
                                                        if (rule.enabled == false) {
                                                            if (rule.comment != null) append(" ")
                                                            append("(")
                                                            append(
                                                                Text(
                                                                    stringResource(
                                                                        R.string
                                                                            .filter_rules_disabled
                                                                    )
                                                                )
                                                            )
                                                            append(")")
                                                        }
                                                    }
                                                )
                                            },
                                            trailingContent = {
                                                if (rule.dateAdded != null) {
                                                    Text(
                                                        text =
                                                            dateTimeInstance.format(
                                                                rule.dateAdded * 1000L
                                                            )
                                                    )
                                                }
                                            },
                                        )
                                    }
                                }
                            }
                    }
                }
            }
        }
    }
}
