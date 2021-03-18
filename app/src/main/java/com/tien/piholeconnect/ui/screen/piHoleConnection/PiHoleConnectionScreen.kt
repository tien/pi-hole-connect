package com.tien.piholeconnect.ui.screen.piHoleConnection

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tien.piholeconnect.R
import com.tien.piholeconnect.ui.component.Scanner
import kotlinx.coroutines.launch

@Composable
fun PiHoleConnectionScreen(
    viewModel: PiHoleConnectionViewModel = viewModel(),
    connectionId: String? = null,
    navController: NavController
) {
    val context = LocalContext.current

    var isLoading by rememberSaveable { mutableStateOf(connectionId != null) }
    val scrollState = rememberScrollState()
    val positionMap = remember { mutableStateMapOf<String, Float>() }
    var isScannerExpanded by rememberSaveable { mutableStateOf(false) }

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
        AlertDialog(
            onDismissRequest = { isScannerExpanded = false },
            title = { Text(stringResource(R.string.pi_hole_connection_title_scanner)) },
            text = { Text(stringResource(R.string.pi_hole_connection_hint_scanner)) },
            buttons = {
                Scanner(
                    barcodeScanner = viewModel.barcodeScanner,
                    onBarcodeScanSuccess = {
                        it.firstOrNull()?.rawValue?.let { apiToken ->
                            viewModel.apiToken = apiToken
                            isScannerExpanded = false
                        }
                    })
            })
    }

    Column(
        Modifier
            .padding(25.dp)
            .verticalScroll(scrollState)
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
        Spacer(modifier = Modifier.height(25.dp))
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
        Spacer(modifier = Modifier.height(25.dp))
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
        Spacer(modifier = Modifier.height(25.dp))
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
            value = viewModel.port.toString(),
            onValueChange = { viewModel.port = it.toInt() },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(25.dp))
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
        Spacer(modifier = Modifier.height(25.dp))
        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
            viewModel.viewModelScope.launch {
                viewModel.save()
                navController.navigateUp()
            }
        }) {
            Text(stringResource(R.string.pi_hole_connection_save))
        }
        if (viewModel.shouldShowDeleteButton) {
            Spacer(modifier = Modifier.height(25.dp))
            Button(modifier = Modifier.fillMaxWidth(),
                colors = buttonColors(backgroundColor = MaterialTheme.colors.error), onClick = {
                    viewModel.viewModelScope.launch {
                        viewModel.remove()
                        navController.navigateUp()
                    }
                }) {
                Text(stringResource(R.string.pi_hole_connection_remove))
            }
        }
    }
}
