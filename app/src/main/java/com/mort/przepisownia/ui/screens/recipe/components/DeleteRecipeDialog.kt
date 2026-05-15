package com.mort.przepisownia.ui.screens.recipe.components

import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mort.przepisownia.R
import com.mort.przepisownia.data.entities.Recipe

@Composable
fun DeleteRecipeDialog(
    recipe: Recipe,
    onDismiss: () -> Unit,
    onConfirm: (Recipe) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(R.string.deleting_recipe)) },
        text = { Text(stringResource(R.string.deleting_recipe_confirmation, recipe.name)) },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(recipe)
                }
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}