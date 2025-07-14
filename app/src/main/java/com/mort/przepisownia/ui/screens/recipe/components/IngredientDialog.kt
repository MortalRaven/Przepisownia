package com.mort.przepisownia.ui.screens.recipe.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mort.przepisownia.data.entities.IngredientInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientDialog(
    ingredient: IngredientInput,
    onDismiss: () -> Unit,
    onConfirm: (IngredientInput) -> Unit
) {
    val name = remember { mutableStateOf(ingredient.name) }
    val quantity = remember { mutableStateOf(ingredient.quantity) }
    val unit = remember { mutableStateOf(ingredient.unit) }
    val unitList = listOf("g", "dag", "kg", "ml", "l", "łyżeczka", "łyżka", "szklanka", "szt.")

    val expanded = remember { mutableStateOf(false) }

    val nameFocusRequester = remember { FocusRequester() }
    val qtyFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        nameFocusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edytuj składnik") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    modifier = Modifier.focusRequester(nameFocusRequester),
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Składnik") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { qtyFocusRequester.requestFocus() }
                    )
                )
                OutlinedTextField(
                    modifier = Modifier.focusRequester(qtyFocusRequester),
                    value = quantity.value,
                    onValueChange = { quantity.value = it },
                    label = { Text("Ilość") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.clearFocus()
                            expanded.value = true
                        }
                    )
                )
                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = !expanded.value }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
                        value = unit.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jednostka") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        unitList.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    unit.value = option
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(IngredientInput(name.value, quantity.value, unit.value))
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

enum class IngredientDialogMode {
    ADD, EDIT
}