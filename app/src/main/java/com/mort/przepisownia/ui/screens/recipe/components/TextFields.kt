package com.mort.przepisownia.ui.screens.recipe.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun RecipeTextField(
    label: String,
    value: String,
    onValueChanged: (String) -> Unit,
    validatorHasErrors: Boolean = false,
    supportingText: String = ""
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChanged,
        label = { Text(text = label) },
        isError = validatorHasErrors,
        supportingText = {
            if (validatorHasErrors) {
                Text(text = supportingText)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )
}