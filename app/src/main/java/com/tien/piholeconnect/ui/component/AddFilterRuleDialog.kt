package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tien.piholeconnect.R
import com.tien.piholeconnect.model.WILDCARD_REGEX_PREFIX
import com.tien.piholeconnect.model.WILDCARD_REGEX_SUFFIX

@Composable
fun AddFilterRuleDialog(
    value: String,
    onValueChange: (String) -> Unit,
    isWildcardChecked: Boolean,
    onIsWildcardCheckedChanged: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(onDismissRequest = onDismissRequest) {
        AddFilterRuleCard(
            value = value,
            isWildcardChecked = isWildcardChecked,
            focusRequester = focusRequester,
            onValueChange = onValueChange,
            onIsWildcardCheckedChanged = onIsWildcardCheckedChanged,
            onConfirmClick = onConfirmClick,
            onCancelClick = onCancelClick
        )
    }
}

@Composable
fun AddFilterRuleCard(
    value: String,
    isWildcardChecked: Boolean,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onValueChange: (String) -> Unit,
    onIsWildcardCheckedChanged: (Boolean) -> Unit,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Card {
        Column {
            Column(
                Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(
                        stringResource(R.string.add_filter_rules_dialog_add_rule),
                        style = MaterialTheme.typography.subtitle1
                    )
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    label = { Text(stringResource(R.string.add_filter_rules_dialog_domain)) },
                    leadingIcon = if (isWildcardChecked) {
                        { Text(WILDCARD_REGEX_PREFIX) }
                    } else null,
                    value = value,
                    trailingIcon = if (isWildcardChecked) {
                        { Text(WILDCARD_REGEX_SUFFIX) }
                    } else null,
                    onValueChange = onValueChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                )
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.add_filter_rules_dialog_add_as_wildcard))
                    Switch(
                        checked = isWildcardChecked,
                        onCheckedChange = onIsWildcardCheckedChanged
                    )
                }
            }
            Divider()
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancelClick) { Text(stringResource(android.R.string.cancel).uppercase()) }
                TextButton(onClick = onConfirmClick) { Text(stringResource(R.string.add_filter_rules_dialog_add).uppercase()) }
            }
        }
    }
}

@Composable
@Preview
fun AddFilterRuleCardPreview() {
    var value by remember { mutableStateOf("") }
    var isWildcard by remember { mutableStateOf(false) }

    AddFilterRuleCard(
        value = value,
        onValueChange = { value = it },
        isWildcardChecked = isWildcard,
        onIsWildcardCheckedChanged = { isWildcard = it },
        onConfirmClick = {},
        onCancelClick = {})
}
