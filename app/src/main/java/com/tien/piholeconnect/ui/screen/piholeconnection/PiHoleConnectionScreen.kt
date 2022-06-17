package com.tien.piholeconnect.ui.screen.piholeconnection

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.URLProtocol
import com.tien.piholeconnect.ui.component.Scanner
import com.tien.piholeconnect.util.isNumericOrWhitespace
import com.tien.piholeconnect.util.toKtorURLProtocol
import io.ktor.http.URLProtocol.Companion.HTTP
import io.ktor.http.URLProtocol.Companion.HTTPS
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun PiHoleConnectionScreen(
    viewModel: PiHoleConnectionViewModel = viewModel(),
    connectionId: String? = null,
    navController: NavController
) {
    val context = LocalContext.current

    var isLoading by rememberSaveable { mutableStateOf(connectionId != null) }
    var showAdvanceOptions by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val positionMap = remember { mutableStateMapOf<String, Float>() }
    var isScannerExpanded by rememberSaveable { mutableStateOf(false) }
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

    if (isScannerExpanded) {
        Dialog(onDismissRequest = { isScannerExpanded = false }) {
            Card {
                Column {
                    Box(Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)) {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                            Text(
                                stringResource(R.string.pi_hole_connection_title_scanner),
                                style = MaterialTheme.typography.subtitle1
                            )
                        }
                    }
                    Box(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 28.dp)) {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                            Text(
                                stringResource(R.string.pi_hole_connection_hint_scanner),
                                style = MaterialTheme.typography.body2
                            )
                        }
                    }
                    BoxWithConstraints {
                        Scanner(
                            Modifier
                                .fillMaxWidth()
                                .height(maxWidth),
                            barcodeScanner = viewModel.barcodeScanner,
                            onBarcodeScanSuccess = {
                                it.firstOrNull()?.rawValue?.let { apiToken ->
                                    viewModel.apiToken = apiToken
                                    isScannerExpanded = false
                                }
                            })
                    }
                }
            }
        }
    }

    if (isDeleteAlertDialogExpanded) {
        AlertDialog(
            text = { Text(stringResource(R.string.pi_hole_connection_remove_dialog_title)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.viewModelScope.launch {
                        viewModel.remove()
                        navController.navigateUp()
                    }
                }) {
                    Text(
                        stringResource(R.string.pi_hole_connection_remove_dialog_button_remove).uppercase(
                            Locale.getDefault()
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isDeleteAlertDialogExpanded = false
                }) { Text(stringResource(android.R.string.cancel).uppercase()) }
            },
            onDismissRequest = { isDeleteAlertDialogExpanded = false })
    }

    Column(
        Modifier
            .verticalScroll(scrollState)
            .padding(25.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    positionMap[viewModel::name.name] = it.positionInParent().y
                }
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) {
                        viewModel.viewModelScope.launch {
                            positionMap[viewModel::name.name]?.let {
                                scrollState.scrollTo(
                                    it.toInt()
                                )
                            }
                        }
                    }
                },
            label = { Text(stringResource(R.string.pi_hole_connection_label_name)) },
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    positionMap[viewModel::host.name] = it.positionInParent().y
                }
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) {
                        viewModel.viewModelScope.launch {
                            positionMap[viewModel::host.name]?.let {
                                scrollState.scrollTo(
                                    it.toInt()
                                )
                            }
                        }
                    }
                },
            label = { Text(stringResource(R.string.pi_hole_connection_label_host)) },
            value = viewModel.host,
            onValueChange = { viewModel.host = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    positionMap[viewModel::apiPath.name] = it.positionInParent().y
                }
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) {
                        viewModel.viewModelScope.launch {
                            positionMap[viewModel::apiPath.name]?.let {
                                scrollState.scrollTo(
                                    it.toInt()
                                )
                            }
                        }
                    }
                },
            label = { Text(stringResource(R.string.pi_hole_connection_label_api_path)) },
            value = viewModel.apiPath,
            onValueChange = { viewModel.apiPath = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    positionMap[viewModel::port.name] = it.positionInParent().y
                }
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) {
                        viewModel.viewModelScope.launch {
                            positionMap[viewModel::port.name]?.let {
                                scrollState.scrollTo(
                                    it.toInt()
                                )
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    positionMap[viewModel::apiToken.name] = it.positionInParent().y
                }
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) {
                        viewModel.viewModelScope.launch {
                            positionMap[viewModel::apiToken.name]?.let {
                                scrollState.scrollTo(
                                    it.toInt()
                                )
                            }
                        }
                    }
                },
            label = { Text(stringResource(R.string.pi_hole_connection_label_api_token)) },
            trailingIcon = {
                IconButton(onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_DENIED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.CAMERA),
                            0
                        )
                    }
                    isScannerExpanded = true
                }) {
                    Icon(
                        Icons.Default.QrCode,
                        contentDescription = stringResource(R.string.pi_hole_connection_desc_scanner)
                    )
                }
            },
            value = viewModel.apiToken,
            onValueChange = { viewModel.apiToken = it },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.pi_hole_connection_label_show_advance_options))
            Switch(
                checked = showAdvanceOptions,
                onCheckedChange = { showAdvanceOptions = it }
            )
        }
        if (showAdvanceOptions) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.pi_hole_connection_label_use_https))
                Switch(
                    checked = viewModel.protocol == URLProtocol.HTTPS,
                    onCheckedChange = {
                        val protocol = if (it) URLProtocol.HTTPS else URLProtocol.HTTP
                        if ((viewModel.protocol == URLProtocol.HTTP && viewModel.port == HTTP.defaultPort.toString()) ||
                            (viewModel.protocol == URLProtocol.HTTPS && viewModel.port == HTTPS.defaultPort.toString())
                        ) {
                            viewModel.port = protocol.toKtorURLProtocol().defaultPort.toString()
                        }
                        viewModel.protocol = protocol
                    }
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.pi_hole_connection_label_trust_all_certificates))
                Switch(
                    checked = viewModel.trustAllCertificates,
                    onCheckedChange = { viewModel.trustAllCertificates = it }
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        positionMap[viewModel::basicAuthUsername.name] = it.positionInParent().y
                    }
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            viewModel.viewModelScope.launch {
                                positionMap[viewModel::basicAuthUsername.name]?.let {
                                    scrollState.scrollTo(
                                        it.toInt()
                                    )
                                }
                            }
                        }
                    },
                label = { Text(stringResource(R.string.pi_hole_connection_label_basic_auth_username)) },
                value = viewModel.basicAuthUsername,
                onValueChange = { viewModel.basicAuthUsername = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        positionMap[viewModel::basicAuthPassword.name] = it.positionInParent().y
                    }
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            viewModel.viewModelScope.launch {
                                positionMap[viewModel::basicAuthPassword.name]?.let {
                                    scrollState.scrollTo(
                                        it.toInt()
                                    )
                                }
                            }
                        }
                    },
                label = { Text(stringResource(R.string.pi_hole_connection_label_basic_auth_password)) },
                value = viewModel.basicAuthPassword,
                onValueChange = { viewModel.basicAuthPassword = it },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        positionMap[viewModel::basicAuthRealm.name] = it.positionInParent().y
                    }
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            viewModel.viewModelScope.launch {
                                positionMap[viewModel::basicAuthRealm.name]?.let {
                                    scrollState.scrollTo(
                                        it.toInt()
                                    )
                                }
                            }
                        }
                    },
                label = { Text(stringResource(R.string.pi_hole_connection_label_basic_auth_realm)) },
                value = viewModel.basicAuthRealm,
                onValueChange = { viewModel.basicAuthRealm = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }
        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
            viewModel.viewModelScope.launch {
                viewModel.save()
                navController.navigateUp()
            }
        }) {
            Text(stringResource(R.string.pi_hole_connection_save))
        }
        if (viewModel.shouldShowDeleteButton) {
            Button(modifier = Modifier.fillMaxWidth(),
                colors = buttonColors(backgroundColor = MaterialTheme.colors.error),
                onClick = { isDeleteAlertDialogExpanded = true }) {
                Text(stringResource(R.string.pi_hole_connection_remove))
            }
        }
    }
}
