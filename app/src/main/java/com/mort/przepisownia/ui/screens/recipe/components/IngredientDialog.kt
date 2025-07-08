package com.mort.przepisownia.ui.screens.recipe.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mort.przepisownia.data.entities.IngredientInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIngredientDialog(
    ingredient: IngredientInput,
    onDismiss: () -> Unit,
    onConfirm: (IngredientInput) -> Unit
) {
    val name = remember { mutableStateOf(ingredient.name) }
    val quantity = remember { mutableStateOf(ingredient.quantity) }
    val unit = remember { mutableStateOf(ingredient.unit) }
    val unitList = listOf("g", "dag", "kg", "ml", "l", "łyżeczka", "łyżka", "szklanka", "szt.")

    val expanded = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edytuj składnik") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Składnik") }
                )
                OutlinedTextField(
                    value = quantity.value,
                    onValueChange = { quantity.value = it },
                    label = { Text("Ilość") }
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