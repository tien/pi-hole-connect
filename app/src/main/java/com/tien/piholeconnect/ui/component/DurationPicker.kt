package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tien.piholeconnect.R
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme
import com.tien.piholeconnect.util.isNumericOrWhitespace
import java.util.*
import kotlin.time.Duration
import kotlin.time.hours
import kotlin.time.minutes

enum class TimeTextFieldType { PRIMARY, SECONDARY }

@Composable
fun TimeTextField(value: String, type: TimeTextFieldType, onValueChange: (value: String) -> Unit) {
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
        leadingIcon = { Spacer(Modifier.width(1.dp)) },
        placeholder = { Text("00", style = MaterialTheme.typography.h4) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.h4,
        onValueChange = {
            if (it.length <= 2) {
                onValueChange(it)
            }
        },
        visualTransformation = {
            TransformedText(
                if (it.isNotEmpty()) AnnotatedString(it.padStart(2, '0').toString()) else it,
                object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int =
                        if (it.isNotEmpty()) 2 else offset

                    override fun transformedToOriginal(offset: Int): Int =
                        if (it.isNotEmpty()) 2 else offset
                }
            )
        },
        colors = colors,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.width(96.dp),
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
            onCancelClick = onDismissRequest
        )
    }
}

@Composable
fun DurationPicker(
    hours: String,
    minutes: String,
    onHoursChange: (hours: String) -> Unit,
    onMinutesChange: (minutes: String) -> Unit,
    onOkayClick: (Duration) -> Unit,
    onCancelClick: () -> Unit
) {
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
                        "Enter time".toUpperCase(Locale.getDefault()),
                        style = MaterialTheme.typography.overline,
                    )
                }
                Row(Modifier.padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                    TimeTextField(
                        value = hours,
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
                            stringResource(R.string.duration_picker_dialog_hour),
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
                            stringResource(R.string.duration_picker_dialog_minute),
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
                    Text(stringResource(android.R.string.cancel).toUpperCase(Locale.getDefault()))
                }
                TextButton(
                    enabled = (hours.toIntOrNull() ?: 0) > 0 || (minutes.toIntOrNull() ?: 0) > 0,
                    onClick = {
                        onOkayClick(
                            (hours.toIntOrNull() ?: 0).hours + (minutes.toIntOrNull() ?: 0).minutes
                        )
                    }) {
                    Text(stringResource(android.R.string.ok).toUpperCase(Locale.getDefault()))
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