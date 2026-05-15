package com.mort.przepisownia.ui.screens.shopping.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mort.przepisownia.R
import com.mort.przepisownia.data.entities.ShoppingList

@Composable
fun DeleteListDialog(
    shoppingList: ShoppingList,
    onDismiss: () -> Unit,
    onConfirm: (ShoppingList) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(R.string.deleting_list)) },
        text = { Text(stringResource(R.string.deleting_list_confirmation, shoppingList.name)) },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(shoppingList)
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