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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tien.piholeconnect.repository.UserPreferencesRepository
import com.tien.piholeconnect.util.showGenericPiHoleConnectionError
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold

open class ScreenViewModel
@Inject
constructor(userPreferencesRepository: UserPreferencesRepository) : ViewModel() {
    protected var flows = MutableStateFlow(listOf<Flow<LoadState<*>>>())

    @OptIn(ExperimentalCoroutinesApi::class)
    protected fun <T> Flow<T>.asRegisteredLoadState(): Flow<LoadState<T>> {
        return refreshCount
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
                    addError(it.throwable)
                }
            }
            .also { this@ScreenViewModel.flows.value += it }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val loading =
        flows.flatMapLatest { combine(it) { values -> values.any { it is LoadState.Loading } } }

    private val refreshCount = MutableStateFlow(0)

    val refreshing = loading.combine(refreshCount) { loading, count -> loading && count >= 1 }

    fun refresh() {
        refreshCount.value++
    }

    private val errors = MutableStateFlow(listOf<Throwable>())

    protected fun addError(throwable: Throwable) {
        errors.value += throwable
    }

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

        var errorToDisplay by remember { mutableStateOf<Throwable?>(null) }

        val errors by errors.collectAsStateWithLifecycle(listOf())

        errors.lastOrNull()?.let {
            LaunchedEffect(snackbarHostState) {
                val snackbarResult = snackbarHostState.showGenericPiHoleConnectionError(it, context)

                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    errorToDisplay = it
                }
            }
        }

        errorToDisplay?.let {
            AlertDialog(
                onDismissRequest = { errorToDisplay = null },
                confirmButton = {
                    TextButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(sanitize(it.stackTraceToString())))
                            errorToDisplay = null
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
                    TextButton(onClick = { errorToDisplay = null }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                },
                text = { Text(it.localizedMessage?.let(sanitize) ?: "") },
            )
        }
    }
}
