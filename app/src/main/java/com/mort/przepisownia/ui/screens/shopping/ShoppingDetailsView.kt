package com.mort.przepisownia.ui.screens.shopping

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mort.przepisownia.data.entities.ListWithItems
import com.mort.przepisownia.data.entities.ShoppingItem
import com.mort.przepisownia.data.entities.ShoppingList
import com.mort.przepisownia.navigation.Screen
import com.mort.przepisownia.ui.common.AppBarView
import com.mort.przepisownia.ui.common.MenuDropdownItem
import com.mort.przepisownia.ui.screens.shopping.components.DeleteListDialog
import com.mort.przepisownia.utils.formatDate

@Composable
fun ShoppingDetailsView(
    id: Long,
    navController: NavController,
    viewModel: ShoppingViewModel
) {
    val fullList = viewModel.getListItems(id).collectAsState(
        initial = ListWithItems(
            shoppingList = ShoppingList(0L, "", 0L, null),
            shoppingItems = listOf()
        )
    )

    val title = if (fullList.value.shoppingList.name.isEmpty()) {
        formatDate(fullList.value.shoppingList.createdAt)
    } else {
        fullList.value.shoppingList.name
    }

    val showDeleteListDialog = remember { mutableStateOf(false) }

    if (showDeleteListDialog.value) {
        DeleteListDialog(
            shoppingList = fullList.value.shoppingList,
            onDismiss = { showDeleteListDialog.value = false },
            onConfirm = {
                viewModel.setPendingDeleteList(fullList.value.shoppingList)
                showDeleteListDialog.value = false
                navController.navigate(Screen.ShoppingScreen.route)
            }
        )
    }

    Scaffold(
        topBar = {
            AppBarView(
                title = title,
                onBackNavClick = { navController.navigateUp() },
                dropdownMenuItems = listOf(
                    MenuDropdownItem(
                        text = "Edytuj",
                        action = {
                            navController.navigate(Screen.AddEditListScreen.route + "/${fullList.value.shoppingList.id}")
                        }
                    ),
                    MenuDropdownItem(
                        text = "Usuń",
                        action = {
                            showDeleteListDialog.value = !showDeleteListDialog.value
                        }
                    )
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .imePadding(),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    fullList.value.shoppingItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .wrapContentHeight()
                                .clickable {
                                    viewModel.updateItemChecked(item.id)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = item.name,
                                fontSize = 18.sp,
                                textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                color = if (item.isChecked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onBackground
                            )
                            if (item.quantity.isNotBlank() && item.unit.isNotBlank()) {
                                Text(
                                    modifier = Modifier.weight(0.5f),
                                    text = "${item.quantity} ${item.unit}",
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.End,
                                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                    color = if (item.isChecked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ListItemRow(
    item: ShoppingItem
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .wrapContentHeight()
            .background(
                if (item.isChecked) Color.Black.copy(alpha = 0.5f) else Color.Transparent
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = item.name,
            fontSize = 16.sp,
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
        )

        Text(
            modifier = Modifier.weight(0.5f),
            text = "${item.quantity} ${item.unit}",
            fontSize = 16.sp,
            textAlign = TextAlign.End,
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}