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
import androidx.compose.ui.res.stringResource
import com.mort.przepisownia.R

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
        title = { Text(stringResource(R.string.edit_step)) },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = description.value,
                    onValueChange = { description.value = it },
                    label = { Text(stringResource(R.string.recipe_step)) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(description.value)
            }) {
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