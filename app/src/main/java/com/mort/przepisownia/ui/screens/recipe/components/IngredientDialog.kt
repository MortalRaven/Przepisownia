package com.mort.przepisownia.ui.screens.recipe.components

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mort.przepisownia.R
import com.mort.przepisownia.data.entities.IngredientInput
import com.mort.przepisownia.ui.common.EditMode
import com.mort.przepisownia.utils.UnitType
import com.mort.przepisownia.utils.displayName

data class IngredientDialogUiState(
    val isVisible: Boolean = false,
    val mode: EditMode = EditMode.ADD,
    val editIndex: Int = -1
)

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientDialog(
    ingredient: IngredientInput,
    onDismiss: () -> Unit,
    onConfirm: (IngredientInput) -> Unit,
) {
    val context = LocalContext.current
    val name = remember { mutableStateOf(ingredient.name) }
    val quantity = remember { mutableStateOf(ingredient.quantity?.toString() ?: "") }
    val unit = remember { mutableStateOf(ingredient.unit) }
    val unitList = UnitType.entries

    val expanded = remember { mutableStateOf(false) }

    val nameFocusRequester = remember { FocusRequester() }
    val qtyFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        nameFocusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(R.string.edit_ingredient)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                //Pole do wprowadzania nazwy składnika
                OutlinedTextField(
                    modifier = Modifier.focusRequester(nameFocusRequester),
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text(stringResource(R.string.ingredient)) },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { qtyFocusRequester.requestFocus() }
                    )
                )
                //Pole do wprowadzania ilości składnika
                OutlinedTextField(
                    modifier = Modifier.focusRequester(qtyFocusRequester),
                    value = quantity.value,
                    onValueChange = {
                        if (it.isEmpty()) {
                            quantity.value = it
                        } else {
                            quantity.value = when (it.toDoubleOrNull()) {
                                null -> quantity.value
                                else -> it
                            }
                        }
                    },
                    label = { Text(stringResource(R.string.quantity)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.clearFocus()
                            expanded.value = true
                        }
                    )
                )
                //Rozwijana lista z jednostkami miary
                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = !expanded.value }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
                        value = unit.value?.displayName(1F) ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { context.resources.getString(R.string.unit) },
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
                                text = { Text(option.displayName(1F)) },
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
                onConfirm(
                    IngredientInput(
                        name.value,
                        quantity.value.toFloatOrNull(),
                        unit.value
                    )
                )
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