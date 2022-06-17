package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import java.util.*
import kotlin.time.Duration

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
        TimeTextFieldType.PRIMARY -> TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = MaterialTheme.colors.primary.copy(TextFieldDefaults.BackgroundOpacity),
            textColor = MaterialTheme.colors.primary.copy(),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
        TimeTextFieldType.SECONDARY -> TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = MaterialTheme.colors.onSurface.copy(TextFieldDefaults.BackgroundOpacity),
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
                    style = MaterialTheme.typography.h4.copy(textAlign = TextAlign.Center)
                )
            }
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.h4.copy(textAlign = TextAlign.Center),
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
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.medium
                ) {
                    Text(
                        stringResource(R.string.duration_picker_enter_time).uppercase(),
                        style = MaterialTheme.typography.overline,
                    )
                }
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
                            style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold)
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
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.medium
                    ) {
                        Text(
                            stringResource(R.string.duration_picker_dialog_hours),
                            style = MaterialTheme.typography.overline,
                            modifier = Modifier
                                .weight(1f)
                                .paddingFromBaseline(top = 20.dp)
                        )
                    }
                    Spacer(Modifier.width(24.dp))
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.medium
                    ) {
                        Text(
                            stringResource(R.string.duration_picker_dialog_minutes),
                            style = MaterialTheme.typography.overline,
                            modifier = Modifier
                                .weight(1f)
                                .paddingFromBaseline(top = 20.dp)
                        )
                    }
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
                            Duration.hours(
                                hours.toIntOrNull() ?: 0
                            ) + Duration.minutes(minutes.toIntOrNull() ?: 0)
                        )
                    }) {
                    Text(stringResource(android.R.string.ok).uppercase())
                }
            }
        }
    }
}

@Preview
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