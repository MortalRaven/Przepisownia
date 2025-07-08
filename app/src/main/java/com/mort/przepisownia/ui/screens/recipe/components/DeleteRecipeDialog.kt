package com.mort.przepisownia.ui.screens.recipe.components

import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import com.mort.przepisownia.data.entities.Recipe

@Composable
fun DeleteRecipeDialog(
    recipe: Recipe,
    onDismiss: () -> Unit,
    onConfirm: (Recipe) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Usuwanie przepisu") },
        text = { Text("Czy na pewno chcesz usunąć przepis: ${recipe.name}?") },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(recipe)
                }
            ) {
                Text("Tak")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Anuluj")
            }
        }
    )
}