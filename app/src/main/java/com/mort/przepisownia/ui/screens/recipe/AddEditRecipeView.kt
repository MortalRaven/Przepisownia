package com.mort.przepisownia.ui.screens.recipe

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mort.przepisownia.R
import com.mort.przepisownia.data.entities.IngredientInput
import com.mort.przepisownia.navigation.Screen
import com.mort.przepisownia.ui.common.AppBarView
import com.mort.przepisownia.model.EditMode
import com.mort.przepisownia.ui.common.LoadingOverlay
import com.mort.przepisownia.ui.screens.recipe.components.IngredientDialog
import com.mort.przepisownia.ui.screens.recipe.components.RecipeTextField
import com.mort.przepisownia.ui.screens.recipe.components.StepDialog
import com.mort.przepisownia.utils.displayName
import com.mort.przepisownia.utils.inTextFormatter
import com.mort.przepisownia.utils.saveImageToInternalStorage
import java.io.File

@Composable
fun AddEditRecipeView(
    id: Long,
    mode: EditMode,
    navController: NavController,
    viewModel: RecipeViewModel,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    //Obsługa zdjęć
    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                val fileName = saveImageToInternalStorage(context, it)
                if (fileName != null) {
                    viewModel.recipeImageState = fileName
                }
            }
        }

    val recipeDetails = viewModel.getRecipeDetails(id).collectAsState(initial = null).value

    //Wczytywanie przepisu
    LaunchedEffect(mode, recipeDetails) {
        if (mode == EditMode.EDIT && recipeDetails == null) {
            return@LaunchedEffect
        }
        viewModel.initializeRecipe(id, mode, recipeDetails)
    }

    //Eventy
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is RecipeEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(event.message),
                        duration = SnackbarDuration.Short
                    )
                }

                is RecipeEvent.NavigateBack -> {
                    navController.navigate(Screen.RecipesScreen.route)
                }
            }
        }
    }

    //Okno wprowadzania/edycji składnika
    if (viewModel.ingredientDialogState.isVisible) {
        val ingredient = when (viewModel.ingredientDialogState.mode) {
            EditMode.ADD -> IngredientInput()
            EditMode.EDIT -> viewModel.ingredients[viewModel.ingredientDialogState.editIndex]
        }

        IngredientDialog(
            ingredient = ingredient,
            onDismiss = {
                viewModel.closeIngredientDialog()
            },
            onConfirm = { updated ->
                viewModel.saveIngredient(updated)
            }
        )
    }

    //Okno wprowadzania/edycji kroku
    if (viewModel.stepDialogState.isVisible) {
        val step = when (viewModel.stepDialogState.mode) {
            EditMode.ADD -> ""
            EditMode.EDIT -> viewModel.steps[viewModel.stepDialogState.editIndex]
        }

        StepDialog(
            step = step,
            onDismiss = { viewModel.closeStepDialog() },
            onConfirm = { updated ->
                viewModel.saveStep(updated)
            }
        )
    }

    Scaffold(
        //Chowanie klawiatury - usunięcie focusu przy kliknięciu na ekranie
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppBarView(
                title = stringResource(R.string.title_edit_recipe),
                onBackNavClick = { navController.navigateUp() }
            )
        }
    ) { paddingValues ->
        LoadingOverlay(isLoading = viewModel.isRecipeLoading) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(10.dp)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
//Dodawanie zdjęcia
                item {
                    Card(
                        modifier = Modifier.clickable(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                        )
                    ) {
                        val imageFile = viewModel.recipeImageState.takeIf { it.isNotBlank() }?.let {
                            File(context.filesDir, it)
                        }
                        if (imageFile != null && imageFile.exists()) {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(180.dp),
                                model = imageFile,
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(180.dp)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_add_circle_24),
                                    contentDescription = stringResource(R.string.add_photo)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = stringResource(R.string.add_photo)
                                )
                            }
                        }
                    }
                }

                item {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
// Dodawanie nazwy
                            RecipeTextField(
                                label = stringResource(R.string.recipe_name),
                                value = viewModel.recipeNameState,
                                onValueChanged = { viewModel.onRecipeNameChanged(it) },
                                validatorHasErrors = viewModel.nameIsEmpty,
                                supportingText = stringResource(R.string.required_field)
                            )
// Dodawanie opisu
                            RecipeTextField(
                                label = stringResource(R.string.description),
                                value = viewModel.recipeDescState,
                                onValueChanged = { viewModel.onRecipeDescChanged(it) }
                            )
// Dodawanie linku
                            RecipeTextField(
                                label = stringResource(R.string.link),
                                value = viewModel.recipeLinkState,
                                onValueChanged = { viewModel.onRecipeLinkChanged(it) },
                                validatorHasErrors = viewModel.linkHasErrors,
                                supportingText = stringResource(R.string.invalid_link)
                            )
                        }
                    }
                }

//Dodawanie składników
                item {
                    Card {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = stringResource(R.string.ingredients_list),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
//Tabela z dodanymi składnikami
                            viewModel.ingredients.forEachIndexed { index, ingredient ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    //Ilość składnika
                                    Text(
                                        modifier = Modifier.weight(0.5f),
                                        text = if (ingredient.quantity != null && ingredient.unit != null) {
                                            "${ingredient.quantity.inTextFormatter()} ${
                                                ingredient.unit?.displayName(
                                                    ingredient.quantity!!
                                                )
                                            }"
                                        } else {
                                            ""
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    //Składnik
                                    Text(
                                        modifier = Modifier.weight(1f),
                                        text = ingredient.name,
                                        fontSize = 16.sp
                                    )
                                    //Edycja składnika
                                    IconButton(onClick = {
                                        viewModel.openIngredientDialog(index)

                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_edit_24),
                                            contentDescription = stringResource(R.string.edit_ingredient),
                                            tint = Color.Gray
                                        )
                                    }
                                    //Usuwanie składnika
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

                            Spacer(modifier = Modifier.height(10.dp))
                            //Przycisk dodawania składnika
                            Row {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(50),
                                    onClick = {
                                        focusManager.clearFocus()
                                        viewModel.openIngredientDialog()
                                    }
                                ) {
                                    Text(stringResource(R.string.new_ingredient))
                                }
                            }
                        }
                    }
                }
// Dodawanie kroków
                item {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = stringResource(R.string.recipe_instructions),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
//Lista kroków
                                viewModel.steps.forEachIndexed { index, value ->
                                    if (index != 0) {
                                        HorizontalDivider(thickness = 2.dp)
                                    }

                                    Column {
                                        Text(
                                            modifier = Modifier.padding(end = 10.dp),
                                            text = stringResource(R.string.recipe_step) + " ${index + 1}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                modifier = Modifier.weight(1f),
                                                text = value,
                                                fontSize = 16.sp
                                            )

                                            IconButton(onClick = {
                                                viewModel.openStepDialog(index)
                                            }) {
                                                Icon(
                                                    painter = painterResource(R.drawable.baseline_edit_24),
                                                    contentDescription = stringResource(R.string.edit_step),
                                                    tint = Color.Gray
                                                )
                                            }

                                            IconButton(onClick = {
                                                viewModel.steps.removeAt(index)
                                            }) {
                                                Icon(
                                                    painter = painterResource(R.drawable.baseline_clear_24),
                                                    contentDescription = stringResource(R.string.remove_step),
                                                    tint = Color.Red
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                //Przycisk dodawania kroku
                                Row {
                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(50),
                                        onClick = {
                                            focusManager.clearFocus()
                                            viewModel.openStepDialog()
                                        }) {
                                        Text(stringResource(R.string.new_step))
                                    }
                                }
                            }
                        }
                    }
                }

// Zapisywanie przepisu
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RectangleShape,
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.isRecipeLoading = true
                            viewModel.saveRecipe(mode = mode)
                        }
                    ) {
                        Text(
                            text = if (id != 0L) stringResource(R.string.update_recipe) else stringResource(R.string.add_recipe),
                            style = TextStyle(fontSize = 18.sp),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}