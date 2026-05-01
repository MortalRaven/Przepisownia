package com.mort.przepisownia.ui.screens.shopping

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mort.przepisownia.R
import com.mort.przepisownia.data.entities.IngredientInput
import com.mort.przepisownia.data.entities.ListWithItems
import com.mort.przepisownia.data.entities.ShoppingList
import com.mort.przepisownia.navigation.Screen
import com.mort.przepisownia.ui.common.AppBarView
import com.mort.przepisownia.ui.common.EditMode
import com.mort.przepisownia.ui.screens.recipe.RecipeTextField
import com.mort.przepisownia.ui.screens.recipe.components.IngredientDialog
import com.mort.przepisownia.ui.screens.recipe.components.IngredientDialogMode
import kotlinx.coroutines.launch

@Composable
fun AddEditListScreen(
    id: Long,
    mode: EditMode,
    navController: NavController,
    viewModel: ShoppingViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val snackMessage = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current

    val ingredients = remember { mutableStateListOf<IngredientInput>() }

    val fullList = viewModel.getListItems(id).collectAsState(
        initial = ListWithItems(
            shoppingList = ShoppingList(0L, "", 0L, null),
            shoppingItems = listOf()
        )
    )

    val initialized = remember { mutableStateOf(false) }

    LaunchedEffect(mode, fullList.value) {
        if (!initialized.value) {
            when (mode) {
                EditMode.ADD -> {
                    viewModel.listNameState = ""
                }
                EditMode.EDIT -> {
                    if (fullList.value.shoppingList.id == 0L) {
                        return@LaunchedEffect
                    } else {
                        viewModel.listNameState = fullList.value.shoppingList.name
                        viewModel.listCreatedState = fullList.value.shoppingList.createdAt

                        ingredients.clear()
                        ingredients.addAll(fullList.value.shoppingItems.map {
                            IngredientInput(name = it.name, quantity = it.quantity, unit = it.unit)
                        })

                        viewModel.isListLoading = false
                    }
                }
            }
            initialized.value = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.isListLoading = true
        }
    }

    val showIngredientEditDialog = remember { mutableStateOf(false) }
    val ingredientDialogMode = remember { mutableStateOf(IngredientDialogMode.ADD) }
    val ingredientToEditIndex = remember { mutableStateOf(-1) }

    if (showIngredientEditDialog.value) {
        val ingredient = when (ingredientDialogMode.value) {
            IngredientDialogMode.ADD -> IngredientInput()
            IngredientDialogMode.EDIT -> ingredients[ingredientToEditIndex.value]
        }

        IngredientDialog(
            ingredient = ingredient,
            onDismiss = { showIngredientEditDialog.value = false },
            onConfirm = { updated ->
                if (ingredientDialogMode.value == IngredientDialogMode.ADD) {
                    ingredients.add(updated)
                } else {
                    ingredients[ingredientToEditIndex.value] = updated
                }
                showIngredientEditDialog.value = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppBarView(
                title = if (mode == EditMode.ADD) "Dodaj listę" else "Edytuj listę",
                onBackNavClick = { navController.navigateUp() },
                acceptable = true,
                onAcceptClick = {
                    if (ingredients.isNotEmpty()) {
                        if (mode == EditMode.EDIT) {
                            viewModel.updateList(
                                ShoppingList(
                                    id = id,
                                    name = viewModel.listNameState.trim(),
                                    createdAt = viewModel.listCreatedState,
                                    lastEdited = System.currentTimeMillis()
                                ),
                                itemsInput = ingredients.toList()
                            )
                            snackMessage.value = "Lista została zaktualizowana."
                        } else {
                            viewModel.addList(
                                list = ShoppingList(
                                    name = viewModel.listNameState.trim(),
                                    createdAt = System.currentTimeMillis()
                                ),
                                itemsInput = ingredients.toList()
                            )
                            snackMessage.value = "Lista została dodana."
                        }
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = snackMessage.value,
                                duration = SnackbarDuration.Short
                            )
                            navController.navigate(Screen.ShoppingScreen.route)
                        }
                    } else {
                        snackMessage.value = "Wypełnij pola."
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = snackMessage.value,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(all = 20.dp),
                shape = CircleShape,
                onClick = {
                    focusManager.clearFocus()
                    ingredientDialogMode.value = IngredientDialogMode.ADD
                    showIngredientEditDialog.value = !showIngredientEditDialog.value
                }
            ) {
                Icon(painter = painterResource(R.drawable.baseline_add_24), contentDescription = "")
            }
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
                RecipeTextField(
                    label = "Nazwa listy",
                    value = viewModel.listNameState,
                    onValueChanged = { viewModel.onListNameChange(it) }
                )
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    thickness = 1.dp
                )
            }

            if (ingredients.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Lista zakupów jest pusta.\nDodaj pierwszy przedmiot.",
                            fontSize = 20.sp
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    ingredients.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.padding(start = 16.dp, end = 8.dp).defaultMinSize(30.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.weight(1f).clickable {
                                    ingredientDialogMode.value = IngredientDialogMode.EDIT
                                    ingredientToEditIndex.value = index
                                    showIngredientEditDialog.value =
                                        !showIngredientEditDialog.value
                                },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = item.name,
                                    fontSize = 16.sp
                                )

                                Text(
                                    modifier = Modifier.weight(0.5f),
                                    text = "${item.quantity} ${item.unit}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End
                                )
                            }
                            Row(
                                modifier = Modifier.weight(0.2f)
                            ) {
                                IconButton(onClick = {
                                    ingredients.removeAt(index)
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_clear_24),
                                        contentDescription = "Usuń składnik",
                                        tint = Color.Red
                                    )
                                }
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