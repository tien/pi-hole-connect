package com.tien.piholeconnect.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tien.piholeconnect.R
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme
import com.tien.piholeconnect.util.isNumericOrWhitespace
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

enum class TimeTextFieldType { PRIMARY, SECONDARY }

@Composable
fun TimeTextField(
    value: String,
    modifier: Modifier = Modifier,
    type: TimeTextFieldType,
    onValueChange: (value: String) -> Unit
) {
    var focusState: FocusState? by rememberSaveable { mutableStateOf(null) }

    val colors = when (type) {
        TimeTextFieldType.PRIMARY -> {
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                errorContainerColor = MaterialTheme.colorScheme.primaryContainer,
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                errorTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        }

        TimeTextFieldType.SECONDARY -> OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp),
            disabledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp),
            errorContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp),
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    }

    TextField(
        value,
        placeholder = {
            if (focusState?.isFocused == false) {
                Text(
                    "00",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center)
                )
            }
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
        onValueChange = {
            if (it.length <= 2) {
                onValueChange(it)
            }
        },
        colors = colors,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .width(96.dp)
            .onFocusChanged { focusState = it },
    )
}

@Composable
fun DurationPickerDialog(onDismissRequest: () -> Unit, onDurationConfirm: (Duration) -> Unit) {
    var hours by rememberSaveable { mutableStateOf("") }
    var minutes by rememberSaveable { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        DurationPicker(
            hours = hours,
            minutes = minutes,
            onHoursChange = { hours = it },
            onMinutesChange = { minutes = it },
            onOkayClick = onDurationConfirm,
            onCancelClick = onDismissRequest,
            autoFocus = true
        )
    }
}

@Composable
fun DurationPicker(
    autoFocus: Boolean = false,
    hours: String,
    minutes: String,
    onHoursChange: (hours: String) -> Unit,
    onMinutesChange: (minutes: String) -> Unit,
    onOkayClick: (Duration) -> Unit,
    onCancelClick: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (autoFocus) {
            focusRequester.requestFocus()
        }
    }

    Card {
        Column(Modifier.width(IntrinsicSize.Max)) {
            Column(
                Modifier
                    .paddingFromBaseline(top = 28.dp)
                    .padding(top = 0.dp, end = 24.dp, bottom = 24.dp, start = 24.dp)
            ) {
                Text(
                    stringResource(R.string.duration_picker_enter_time).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Normal
                )
                Row(Modifier.padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                    TimeTextField(
                        value = hours,
                        modifier = Modifier.focusRequester(focusRequester),
                        type = TimeTextFieldType.PRIMARY,
                        onValueChange = {
                            if (it.isNumericOrWhitespace()) {
                                onHoursChange(it)
                            }
                        }
                    )
                    Box(Modifier.width(24.dp)) {
                        Text(
                            ":",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    TimeTextField(
                        value = minutes,
                        type = TimeTextFieldType.SECONDARY,
                        onValueChange = {
                            if (it.isNumericOrWhitespace()) {
                                onMinutesChange(it)
                            }
                        }
                    )
                }
                Row {
                    Text(
                        stringResource(R.string.duration_picker_dialog_hours),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .weight(1f)
                            .paddingFromBaseline(top = 20.dp),
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(Modifier.width(24.dp))
                    Text(
                        stringResource(R.string.duration_picker_dialog_minutes),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .weight(1f)
                            .paddingFromBaseline(top = 20.dp),
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            Row(
                Modifier
                    .padding(end = 8.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancelClick) {
                    Text(stringResource(android.R.string.cancel).uppercase())
                }
                TextButton(
                    enabled = (hours.toIntOrNull() ?: 0) > 0 || (minutes.toIntOrNull() ?: 0) > 0,
                    onClick = {
                        onOkayClick(
                            (hours.toIntOrNull() ?: 0).hours + (minutes.toIntOrNull() ?: 0).minutes
                        )
                    }) {
                    Text(stringResource(android.R.string.ok).uppercase())
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DurationPickerPreview() {
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }

    PiHoleConnectTheme {
        DurationPicker(
            hours = hours,
            minutes = minutes,
            onHoursChange = { hours = it },
            onMinutesChange = { minutes = it },
            onOkayClick = {},
            onCancelClick = {})
    }
}
