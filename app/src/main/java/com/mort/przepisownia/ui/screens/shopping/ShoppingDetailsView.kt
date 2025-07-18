package com.mort.przepisownia.ui.screens.shopping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mort.przepisownia.data.entities.ListWithItems
import com.mort.przepisownia.data.entities.ShoppingList
import com.mort.przepisownia.ui.common.AppBarView
import com.mort.przepisownia.utils.formatDate

@Composable
fun ShoppingDetailsView(
    id: Long,
    navController: NavController,
) {
    val viewModel: ShoppingViewModel = viewModel()

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

    Scaffold(
        topBar = {
            AppBarView(
                title = title,
                onBackNavClick = { navController.navigateUp() }
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
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    fullList.value.shoppingItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .wrapContentHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            androidx.compose.material3.Text(
                                modifier = Modifier.weight(1f),
                                text = item.name,
                                fontSize = 16.sp
                            )

                            androidx.compose.material3.Text(
                                modifier = Modifier.weight(0.5f),
                                text = "${item.quantity} ${item.unit}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End
                            )
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