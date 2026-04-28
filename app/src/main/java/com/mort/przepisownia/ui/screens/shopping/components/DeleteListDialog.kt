package com.mort.przepisownia.ui.screens.shopping.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.mort.przepisownia.data.entities.ShoppingList

@Composable
fun DeleteListDialog(
    shoppingList: ShoppingList,
    onDismiss: () -> Unit,
    onConfirm: (ShoppingList) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Usuwanie listy") },
        text = { Text("Czy na pewno chcesz usunąć listę: ${shoppingList.name}?") },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(shoppingList)
                }
            ) {
                Text("Tak")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("Anuluj")
            }
        }
    )
}