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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mort.przepisownia.R
import com.mort.przepisownia.data.entities.IngredientInput
import com.mort.przepisownia.navigation.Screen
import com.mort.przepisownia.ui.common.AppBarView
import com.mort.przepisownia.model.EditMode
import com.mort.przepisownia.ui.screens.recipe.components.IngredientDialog
import com.mort.przepisownia.ui.screens.recipe.components.RecipeTextField
import com.mort.przepisownia.utils.displayName
import com.mort.przepisownia.utils.inTextFormatter

@Composable
fun AddEditListScreen(
    id: Long,
    mode: EditMode,
    navController: NavController,
    viewModel: ShoppingViewModel
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    val fullList = viewModel.getListItems(id).collectAsState(initial = null).value

    //Wczytywanie listy
    LaunchedEffect(mode, fullList) {
        if (mode == EditMode.EDIT && fullList == null) {
            return@LaunchedEffect
        }
        viewModel.initializeList(id, mode, fullList)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect {event ->
            when (event) {
                is ShoppingListEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(event.message),
                        duration = SnackbarDuration.Short
                    )
                }
                is ShoppingListEvent.NavigateBack -> {
                    navController.navigate(Screen.ShoppingScreen.route)
                }
            }
        }
    }

    if (viewModel.ingredientDialogState.isVisible) {
        val ingredient = when (viewModel.ingredientDialogState.mode) {
            EditMode.ADD -> IngredientInput()
            EditMode.EDIT -> viewModel.ingredients[viewModel.ingredientDialogState.editIndex]
        }

        IngredientDialog(
            ingredient = ingredient,
            onDismiss = { viewModel.closeIngredientDialog() },
            onConfirm = { updated ->
                viewModel.saveIngredient(updated)
            }
        )
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppBarView(
                title = if (mode == EditMode.ADD) stringResource(R.string.add_list) else stringResource(R.string.edit_list),
                onBackNavClick = { navController.navigateUp() },
                acceptable = true,
                onAcceptClick = {
                    viewModel.saveList(
                        mode = mode
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(all = 20.dp),
                shape = CircleShape,
                onClick = {
                    focusManager.clearFocus()
                    viewModel.openIngredientDialog()
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
                    label = stringResource(R.string.list_name),
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

            if (viewModel.ingredients.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_ingredients_list),
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
                    viewModel.ingredients.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.padding(start = 16.dp, end = 8.dp).defaultMinSize(30.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.weight(1f).clickable {
                                    viewModel.openIngredientDialog(index)
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
                                    text = if (item.quantity != null && item.unit != null) {
                                        "${item.quantity.inTextFormatter()} ${item.unit?.displayName(item.quantity!!)}"
                                    } else {""},
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End
                                )
                            }
                            Row(
                                modifier = Modifier.weight(0.2f)
                            ) {
                                IconButton(onClick = {
                                    viewModel.ingredients.removeAt(index)
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_clear_24),
                                        contentDescription = stringResource(R.string.remove_ingredient),
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