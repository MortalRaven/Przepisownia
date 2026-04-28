package com.mort.przepisownia.ui.screens.shopping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mort.przepisownia.navigation.Screen
import com.mort.przepisownia.ui.common.AppBarView
import com.mort.przepisownia.ui.common.EmptyScreen
import com.mort.przepisownia.ui.common.LoadingOverlay
import com.mort.przepisownia.ui.screens.shopping.components.ShoppingListCard

@Composable
fun ShoppingListView(
    navController: NavController,
    viewModel: ShoppingViewModel,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val sortType by viewModel.sortType.collectAsState()
    val listState = rememberLazyListState()

    val pendingList by viewModel.pendingDeleteList.collectAsState()
    val allLists by viewModel.filteredLists.collectAsState()
    val shoppingLists = remember(allLists, pendingList) { allLists.filter { it.id != pendingList?.id } }

    LaunchedEffect(pendingList) {
        pendingList?.let { list ->
            val result = snackbarHostState.showSnackbar(
                message = "Lista została usunięta.",
                actionLabel = "Cofnij",
                duration = SnackbarDuration.Short
            )

            when (result) {
                SnackbarResult.ActionPerformed -> {
                    viewModel.clearPendingDeleteList()
                }

                SnackbarResult.Dismissed -> {
                    viewModel.deleteList(list)
                    viewModel.clearPendingDeleteList()
                }

            }
        }
    }

    LaunchedEffect(shoppingLists) {
        if (pendingList == null) {
            listState.animateScrollToItem(0)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.pendingDeleteList.value?.let { list ->
                viewModel.deleteList(list)
                viewModel.clearPendingDeleteList()
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppBarView(
                title = "Listy Zakupów",
                onBackNavClick = { navController.navigate(Screen.HomeScreen.route) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(all = 20.dp),
                shape = CircleShape,
                onClick = {
                    navController.navigate(Screen.AddEditListScreen.route + "/0L")
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        }
    ) { paddingValues ->
        LoadingOverlay(isLoading = viewModel.isDbLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(8.dp)
            ) {
                if (shoppingLists.isEmpty()) {
                    EmptyScreen(text = "Nie masz jeszcze żadnych list zakupowych. \nRozpocznij planowanie dodając pierwszą!")
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            state = listState
                        ) {
                            items(shoppingLists, key = { shoppingList -> shoppingList.id }
                            ) { shoppingList ->
                                ShoppingListCard(shoppingList) {
                                    val id = shoppingList.id
                                    navController.navigate(Screen.ShoppingDetailsScreen.route + "/$id")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}