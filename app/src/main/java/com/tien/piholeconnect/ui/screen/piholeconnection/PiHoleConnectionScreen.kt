package com.tien.piholeconnect.ui.screen.piholeconnection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.URLProtocol
import com.tien.piholeconnect.ui.LocalTopBarActions
import com.tien.piholeconnect.util.isNumericOrWhitespace
import com.tien.piholeconnect.util.toKtorURLProtocol
import io.ktor.http.URLProtocol.Companion.HTTP
import io.ktor.http.URLProtocol.Companion.HTTPS
import kotlinx.coroutines.launch

@Composable
fun PiHoleConnectionScreen(
    navController: NavController,
    connectionId: String? = null,
    viewModel: PiHoleConnectionViewModel = hiltViewModel(),
) {
    var isLoading by rememberSaveable { mutableStateOf(connectionId != null) }
    var showAdvanceOptions by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var isDeleteAlertDialogExpanded by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val nextActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
    val doneActions = KeyboardActions(onDone = { focusManager.clearFocus() })

    LaunchedEffect(Unit) {
        if (connectionId != null) {
            viewModel.viewModelScope.launch {
                viewModel.loadDataForId(connectionId)
                isLoading = false
            }
        }
    }

    if (isLoading) return

    // Publish the Save action into the shared top app bar while this screen is on-screen.
    val topBarActions = LocalTopBarActions.current
    DisposableEffect(Unit) {
        topBarActions.value = {
            IconButton(
                enabled = viewModel.isValid && !viewModel.isSaving,
                onClick = { viewModel.save { navController.navigateUp() } },
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = stringResource(R.string.pi_hole_connection_save),
                )
            }
        }
        onDispose { topBarActions.value = null }
    }

    var hostTouched by rememberSaveable { mutableStateOf(false) }
    var hostHasFocus by remember { mutableStateOf(false) }
    val hostError = hostTouched && viewModel.host.isBlank()

    if (isDeleteAlertDialogExpanded) {
        AlertDialog(
            title = { Text(stringResource(R.string.pi_hole_connection_remove_dialog_title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.viewModelScope.launch {
                            try {
                                viewModel.remove()
                                navController.navigateUp()
                            } catch (_: Exception) {}
                        }
                    }
                ) {
                    Text(stringResource(R.string.pi_hole_connection_remove_dialog_button_remove))
                }
            },
            dismissButton = {
                TextButton(onClick = { isDeleteAlertDialogExpanded = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
            onDismissRequest = { isDeleteAlertDialogExpanded = false },
        )
    }

    Column(
        Modifier.fillMaxWidth().imePadding().verticalScroll(scrollState).padding(25.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp),
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.pi_hole_connection_label_name)) },
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            keyboardActions = nextActions,
        )
        OutlinedTextField(
            modifier =
                Modifier.fillMaxWidth().onFocusChanged { focusState ->
                    if (hostHasFocus && !focusState.isFocused) hostTouched = true
                    hostHasFocus = focusState.isFocused
                },
            label = { Text(stringResource(R.string.pi_hole_connection_label_host)) },
            value = viewModel.host,
            onValueChange = { viewModel.host = it },
            isError = hostError,
            supportingText =
                if (hostError) {
                    { Text(stringResource(R.string.pi_hole_connection_error_host_required)) }
                } else {
                    null
                },
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next),
            keyboardActions = nextActions,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.pi_hole_connection_label_api_path)) },
            value = viewModel.apiPath,
            onValueChange = { viewModel.apiPath = it },
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next),
            keyboardActions = nextActions,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.pi_hole_connection_label_port)) },
            value = viewModel.port,
            onValueChange = {
                if (it.isNumericOrWhitespace()) {
                    viewModel.port = it
                }
            },
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = nextActions,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.pi_hole_connection_label_password)) },
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            visualTransformation = PasswordVisualTransformation(),
            supportingText = { Text(stringResource(R.string.pi_hole_connection_hint_scanner)) },
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (showAdvanceOptions) ImeAction.Next else ImeAction.Done,
                ),
            keyboardActions =
                KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    onDone = { focusManager.clearFocus() },
                ),
        )
        SwitchRow(
            label = stringResource(R.string.pi_hole_connection_label_show_advance_options),
            checked = showAdvanceOptions,
            onCheckedChange = { showAdvanceOptions = it },
        )
        if (showAdvanceOptions) {
            SwitchRow(
                label = stringResource(R.string.pi_hole_connection_label_use_https),
                checked = viewModel.protocol == URLProtocol.HTTPS,
                onCheckedChange = {
                    val protocol = if (it) URLProtocol.HTTPS else URLProtocol.HTTP
                    if (
                        (viewModel.protocol == URLProtocol.HTTP &&
                            viewModel.port == HTTP.defaultPort.toString()) ||
                            (viewModel.protocol == URLProtocol.HTTPS &&
                                viewModel.port == HTTPS.defaultPort.toString())
                    ) {
                        viewModel.port = protocol.toKtorURLProtocol().defaultPort.toString()
                    }
                    viewModel.protocol = protocol
                },
            )
            SwitchRow(
                label = stringResource(R.string.pi_hole_connection_label_trust_all_certificates),
                checked = viewModel.trustAllCertificates,
                onCheckedChange = { viewModel.trustAllCertificates = it },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(R.string.pi_hole_connection_label_basic_auth_username))
                },
                value = viewModel.basicAuthUsername,
                onValueChange = { viewModel.basicAuthUsername = it },
                singleLine = true,
                keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                keyboardActions = nextActions,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(R.string.pi_hole_connection_label_basic_auth_password))
                },
                value = viewModel.basicAuthPassword,
                onValueChange = { viewModel.basicAuthPassword = it },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next,
                    ),
                keyboardActions = nextActions,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(R.string.pi_hole_connection_label_basic_auth_realm))
                },
                value = viewModel.basicAuthRealm,
                onValueChange = { viewModel.basicAuthRealm = it },
                singleLine = true,
                keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                keyboardActions = doneActions,
            )
        }
        if (viewModel.shouldShowDeleteButton) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                onClick = { isDeleteAlertDialogExpanded = true },
            ) {
                Text(stringResource(R.string.pi_hole_connection_remove))
            }
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = null)
    }
}
