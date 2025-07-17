package com.mort.przepisownia.ui.screens.recipe.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Composable
fun StepDialog(
    step: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val description = remember { mutableStateOf(step) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edytuj krok") },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = description.value,
                    onValueChange = { description.value = it },
                    label = { Text("Krok") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(description.value)
            }) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}

enum class StepDialogMode {
    ADD, EDIT
}