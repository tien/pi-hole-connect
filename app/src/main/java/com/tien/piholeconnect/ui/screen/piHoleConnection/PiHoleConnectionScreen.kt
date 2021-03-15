package com.tien.piholeconnect.ui.screen.piHoleConnection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun PiHoleConnectionScreen(
    viewModel: PiHoleConnectionViewModel = viewModel(),
    connectionId: String? = null
) {
    var isLoading by rememberSaveable { mutableStateOf(connectionId != null) }
    val scrollState = rememberScrollState()
    val positionMap = remember { mutableStateMapOf<String, Float>() }

    LaunchedEffect(Unit) {
        if (connectionId != null) {
            viewModel.viewModelScope.launch {
                viewModel.loadDataForId(connectionId)
                isLoading = false
            }
        }
    }

    if (isLoading) return

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
            label = { Text("Name") },
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
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
            label = { Text("Host") },
            value = viewModel.host,
            onValueChange = { viewModel.host = it },
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
            label = { Text("API Path") },
            value = viewModel.apiPath,
            onValueChange = { viewModel.apiPath = it },
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
            label = { Text("Port") },
            value = viewModel.port.toString(),
            onValueChange = { viewModel.port = it.toInt() },
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
            label = { Text("API Token") },
            value = viewModel.apiToken,
            onValueChange = { viewModel.apiToken = it },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(25.dp))
        Button(onClick = { viewModel.viewModelScope.launch { viewModel.save() } }) {
            Text("Save")
        }
    }
}

@Preview
@Composable
fun PiHoleConnectionScreenPreview() {
    PiHoleConnectionScreen(Any() as PiHoleConnectionViewModel)
}