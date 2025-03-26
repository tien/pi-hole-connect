package com.tien.piholeconnect.model

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.repository.UserPreferencesRepository
import com.tien.piholeconnect.util.showGenericPiHoleConnectionError
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class BaseViewModel
@Inject
constructor(userPreferencesRepository: UserPreferencesRepository) : ViewModel() {
    protected var flows = MutableStateFlow(listOf<Flow<LoadState<*>>>())

    @OptIn(ExperimentalCoroutinesApi::class)
    protected fun <T> Flow<T>.asViewFlowState(
        initialValue: LoadState<T> = LoadState.Idle()
    ): StateFlow<LoadState<T>> {
        return this.asViewFlowState()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = initialValue,
            )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> Flow<T>.asViewFlowState(): Flow<LoadState<T>> {
        return refreshTrigger
            .onStart { emit(Unit) }
            .flatMapLatest { this.asLoadState() }
            .runningFold(LoadState.Loading<T>() as LoadState<T>) { prev, curr ->
                when (curr) {
                    is LoadState.Loading -> prev.asLoading()
                    is LoadState.Failure -> prev.asFailure(curr.throwable)
                    is LoadState.Success,
                    is LoadState.Idle -> curr
                }
            }
            .onEach {
                if (it is LoadState.Failure) {
                    when (it.throwable) {
                        is Error -> throw it.throwable
                        is Exception -> emitError(it.throwable)
                    }
                }
            }
            .also { this@BaseViewModel.flows.value += it }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val loadingFlow =
        flows.flatMapLatest { combine(it) { values -> values.any { it is LoadState.Loading } } }

    val loading =
        loadingFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false,
        )

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 0)

    val refreshing = MutableStateFlow(false)

    fun refresh() {
        refreshing.value = true
        viewModelScope.launch {
            doRefresh()
            refreshing.value = false
        }
    }

    fun backgroundRefresh() {
        viewModelScope.launch { doRefresh() }
    }

    private suspend fun doRefresh() {
        refreshTrigger.emit(Unit)
        loadingFlow.first { !it }
    }

    private val error = MutableSharedFlow<Exception>()

    protected suspend fun emitError(error: Exception) {
        this.error.emit(error)
    }

    private val errorToDisplay = MutableStateFlow<Exception?>(null)

    private val sensitiveData =
        userPreferencesRepository.userPreferences.map { preferences ->
            preferences.piHoleConnectionsList
                .flatMap { listOf(it.password, it.basicAuthPassword) }
                .map { it.trim() }
                .filter { it.isNotBlank() }
        }

    @Composable
    fun SnackBarErrorEffect(snackbarHostState: SnackbarHostState) {
        val context = LocalContext.current
        val clipboard = LocalClipboardManager.current

        val sensitiveData by sensitiveData.collectAsStateWithLifecycle(listOf())
        val sanitize = { string: String ->
            sensitiveData.fold(string) { acc, curr -> acc.replace(curr, "*".repeat(curr.length)) }
        }

        val currentError by error.collectAsStateWithLifecycle(null)

        currentError?.let {
            LaunchedEffect(snackbarHostState) {
                val snackbarResult = snackbarHostState.showGenericPiHoleConnectionError(it, context)

                when (snackbarResult) {
                    SnackbarResult.ActionPerformed -> {
                        errorToDisplay.value = it
                    }
                    SnackbarResult.Dismissed,
                    null -> Unit
                }
            }
        }

        val errorToDisplay by errorToDisplay.collectAsStateWithLifecycle()

        errorToDisplay?.let {
            AlertDialog(
                onDismissRequest = { this.errorToDisplay.value = null },
                confirmButton = {
                    TextButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(sanitize(it.stackTraceToString())))
                            this.errorToDisplay.value = null
                        }
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = stringResource(android.R.string.copy),
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(android.R.string.copy))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { this.errorToDisplay.value = null }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                },
                text = { Text(it.localizedMessage?.let(sanitize) ?: "") },
            )
        }
    }
}
