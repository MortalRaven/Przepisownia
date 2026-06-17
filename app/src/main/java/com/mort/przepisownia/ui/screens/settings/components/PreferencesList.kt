package com.mort.przepisownia.ui.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.mort.przepisownia.R

data class Choice<V>(val value: V, val displayValue: Int)

@Composable
fun <V> PreferencesList(
    title: Int,
    choices: List<Choice<out V>>,
    selected: V,
    onSelect: (V) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (() -> Unit)
) {
    val (selectedOption, onOptionSelected) = remember(selected) { mutableStateOf(selected) }

    AlertDialog(
        title = { Text(stringResource(title)) },
        text = {
            Column(Modifier.selectableGroup()) {
                choices.forEach { choice ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (choice.value == selectedOption),
                                onClick = {
                                    onOptionSelected(choice.value)
                                    onSelect.invoke(choice.value)
                                },
                                role = Role.RadioButton
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (choice.value == selectedOption),
                            onClick = null
                        )
                        Text(
                            text = stringResource(choice.displayValue),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}