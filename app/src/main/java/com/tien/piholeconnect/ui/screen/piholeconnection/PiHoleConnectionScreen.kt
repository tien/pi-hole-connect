package com.tien.piholeconnect.ui.screen.piholeconnection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.URLProtocol
import com.tien.piholeconnect.util.isNumericOrWhitespace
import com.tien.piholeconnect.util.toKtorURLProtocol
import io.ktor.http.URLProtocol.Companion.HTTP
import io.ktor.http.URLProtocol.Companion.HTTPS
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun PiHoleConnectionScreen(
    navController: NavController,
    connectionId: String? = null,
    viewModel: PiHoleConnectionViewModel = hiltViewModel(),
) {
    var isLoading by rememberSaveable { mutableStateOf(connectionId != null) }
    var showAdvanceOptions by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val positionMap = remember { mutableStateMapOf<String, Float>() }
    var isDeleteAlertDialogExpanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (connectionId != null) {
            viewModel.viewModelScope.launch {
                viewModel.loadDataForId(connectionId)
                isLoading = false
            }
        }
    }

    if (isLoading) return

    if (isDeleteAlertDialogExpanded) {
        AlertDialog(
            text = { Text(stringResource(R.string.pi_hole_connection_remove_dialog_title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.viewModelScope.launch {
                            viewModel.remove()
                            navController.navigateUp()
                        }
                    }
                ) {
                    Text(
                        stringResource(R.string.pi_hole_connection_remove_dialog_button_remove)
                            .uppercase(Locale.getDefault())
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { isDeleteAlertDialogExpanded = false }) {
                    Text(stringResource(android.R.string.cancel).uppercase())
                }
            },
            onDismissRequest = { isDeleteAlertDialogExpanded = false },
        )
    }

    Column(
        Modifier.verticalScroll(scrollState).padding(25.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp),
    ) {
        OutlinedTextField(
            modifier =
                Modifier.fillMaxWidth()
                    .onGloballyPositioned {
                        positionMap[viewModel::name.name] = it.positionInParent().y
                    }
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            viewModel.viewModelScope.launch {
                                positionMap[viewModel::name.name]?.let {
                                    scrollState.scrollTo(it.toInt())
                                }
                            }
                        }
                    },
            label = { Text(stringResource(R.string.pi_hole_connection_label_name)) },
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )
        OutlinedTextField(
            modifier =
                Modifier.fillMaxWidth()
                    .onGloballyPositioned {
                        positionMap[viewModel::host.name] = it.positionInParent().y
                    }
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            viewModel.viewModelScope.launch {
                                positionMap[viewModel::host.name]?.let {
                                    scrollState.scrollTo(it.toInt())
                                }
                            }
                        }
                    },
            label = { Text(stringResource(R.string.pi_hole_connection_label_host)) },
            value = viewModel.host,
            onValueChange = { viewModel.host = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
        )
        OutlinedTextField(
            modifier =
                Modifier.fillMaxWidth()
                    .onGloballyPositioned {
                        positionMap[viewModel::apiPath.name] = it.positionInParent().y
                    }
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            viewModel.viewModelScope.launch {
                                positionMap[viewModel::apiPath.name]?.let {
                                    scrollState.scrollTo(it.toInt())
                                }
                            }
                        }
                    },
            label = { Text(stringResource(R.string.pi_hole_connection_label_api_path)) },
            value = viewModel.apiPath,
            onValueChange = { viewModel.apiPath = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
        )
        OutlinedTextField(
            modifier =
                Modifier.fillMaxWidth()
                    .onGloballyPositioned {
                        positionMap[viewModel::port.name] = it.positionInParent().y
                    }
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            viewModel.viewModelScope.launch {
                                positionMap[viewModel::port.name]?.let {
                                    scrollState.scrollTo(it.toInt())
                                }
                            }
                        }
                    },
            label = { Text(stringResource(R.string.pi_hole_connection_label_port)) },
            value = viewModel.port,
            onValueChange = {
                if (it.isNumericOrWhitespace()) {
                    viewModel.port = it
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        OutlinedTextField(
            modifier =
                Modifier.fillMaxWidth()
                    .onGloballyPositioned {
                        positionMap[viewModel::password.name] = it.positionInParent().y
                    }
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            viewModel.viewModelScope.launch {
                                positionMap[viewModel::password.name]?.let {
                                    scrollState.scrollTo(it.toInt())
                                }
                            }
                        }
                    },
            label = { Text(stringResource(R.string.pi_hole_connection_label_api_token)) },
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(R.string.pi_hole_connection_label_show_advance_options))
            Switch(checked = showAdvanceOptions, onCheckedChange = { showAdvanceOptions = it })
        }
        if (showAdvanceOptions) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.pi_hole_connection_label_use_https))
                Switch(
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
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.pi_hole_connection_label_trust_all_certificates))
                Switch(
                    checked = viewModel.trustAllCertificates,
                    onCheckedChange = { viewModel.trustAllCertificates = it },
                )
            }
            OutlinedTextField(
                modifier =
                    Modifier.fillMaxWidth()
                        .onGloballyPositioned {
                            positionMap[viewModel::basicAuthUsername.name] = it.positionInParent().y
                        }
                        .onFocusEvent { focusState ->
                            if (focusState.isFocused) {
                                viewModel.viewModelScope.launch {
                                    positionMap[viewModel::basicAuthUsername.name]?.let {
                                        scrollState.scrollTo(it.toInt())
                                    }
                                }
                            }
                        },
                label = {
                    Text(stringResource(R.string.pi_hole_connection_label_basic_auth_username))
                },
                value = viewModel.basicAuthUsername,
                onValueChange = { viewModel.basicAuthUsername = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            )
            OutlinedTextField(
                modifier =
                    Modifier.fillMaxWidth()
                        .onGloballyPositioned {
                            positionMap[viewModel::basicAuthPassword.name] = it.positionInParent().y
                        }
                        .onFocusEvent { focusState ->
                            if (focusState.isFocused) {
                                viewModel.viewModelScope.launch {
                                    positionMap[viewModel::basicAuthPassword.name]?.let {
                                        scrollState.scrollTo(it.toInt())
                                    }
                                }
                            }
                        },
                label = {
                    Text(stringResource(R.string.pi_hole_connection_label_basic_auth_password))
                },
                value = viewModel.basicAuthPassword,
                onValueChange = { viewModel.basicAuthPassword = it },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )
            OutlinedTextField(
                modifier =
                    Modifier.fillMaxWidth()
                        .onGloballyPositioned {
                            positionMap[viewModel::basicAuthRealm.name] = it.positionInParent().y
                        }
                        .onFocusEvent { focusState ->
                            if (focusState.isFocused) {
                                viewModel.viewModelScope.launch {
                                    positionMap[viewModel::basicAuthRealm.name]?.let {
                                        scrollState.scrollTo(it.toInt())
                                    }
                                }
                            }
                        },
                label = {
                    Text(stringResource(R.string.pi_hole_connection_label_basic_auth_realm))
                },
                value = viewModel.basicAuthRealm,
                onValueChange = { viewModel.basicAuthRealm = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            )
        }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.viewModelScope.launch {
                    viewModel.save()
                    navController.navigateUp()
                }
            },
        ) {
            Text(stringResource(R.string.pi_hole_connection_save))
        }
        if (viewModel.shouldShowDeleteButton) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                onClick = { isDeleteAlertDialogExpanded = true },
            ) {
                Text(stringResource(R.string.pi_hole_connection_remove))
            }
        }
    }
}
