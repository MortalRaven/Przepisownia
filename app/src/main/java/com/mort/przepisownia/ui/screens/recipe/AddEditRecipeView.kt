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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mort.przepisownia.data.entities.IngredientInput
import com.mort.przepisownia.data.entities.Recipe
import com.mort.przepisownia.data.entities.RecipeWithDetails
import com.mort.przepisownia.navigation.Screen
import com.mort.przepisownia.ui.common.AppBarView
import com.mort.przepisownia.ui.common.EditMode
import com.mort.przepisownia.ui.common.LoadingOverlay
import com.mort.przepisownia.ui.screens.recipe.components.IngredientDialog
import com.mort.przepisownia.ui.screens.recipe.components.IngredientDialogMode
import com.mort.przepisownia.ui.screens.recipe.components.StepDialog
import com.mort.przepisownia.ui.screens.recipe.components.StepDialogMode
import com.mort.przepisownia.utils.saveImageToInternalStorage
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AddEditRecipeView(
    id: Long,
    mode: EditMode,
    navController: NavController,
    viewModel: RecipeViewModel
) {
    val context = LocalContext.current
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
    //Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val snackMessage = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    //Obsługa focusu
    val focusManager = LocalFocusManager.current

    //Przepis, składniki, kroki
    val ingredients = remember { mutableStateListOf<IngredientInput>() }
    val steps = remember { mutableStateListOf<String>() }

    val recipeDetails = viewModel.getRecipeDetails(id).collectAsState(
        initial = RecipeWithDetails(
            recipe = Recipe(0L, "", "", false, "", ""),
            ingredients = listOf(),
            steps = listOf()
        )
    )

    val initialized = remember { mutableStateOf(false) }

    LaunchedEffect(mode, recipeDetails.value) {
        if (!initialized.value) {
            when (mode) {
                EditMode.ADD -> {
                    viewModel.recipeNameState = ""
                    viewModel.recipeDescState = ""
                    viewModel.recipeFavState = false
                    viewModel.recipeImageState = ""
                    viewModel.recipeLinkState = ""
                    viewModel.isRecipeLoading = false
                }

                EditMode.EDIT -> {
                    if (recipeDetails.value.recipe.id == 0L) {
                        return@LaunchedEffect
                    } else {
                        viewModel.recipeNameState = recipeDetails.value.recipe.name
                        viewModel.recipeDescState = recipeDetails.value.recipe.desc
                        viewModel.recipeFavState = recipeDetails.value.recipe.isFavourite
                        viewModel.recipeImageState = recipeDetails.value.recipe.imagePath
                        viewModel.recipeLinkState = recipeDetails.value.recipe.link

                        ingredients.clear()
                        ingredients.addAll(recipeDetails.value.ingredients.map {
                            IngredientInput(name = it.name, quantity = it.quantity, unit = it.unit)
                        })

                        steps.clear()
                        steps.addAll(recipeDetails.value.steps.map { it.description })
                        viewModel.isRecipeLoading = false
                    }
                }
            }
            initialized.value = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.isRecipeLoading = true
        }
    }

    //Okna edycji składnika i kroku
    val showIngredientEditDialog = remember { mutableStateOf(false) }
    val ingredientDialogMode = remember { mutableStateOf(IngredientDialogMode.ADD) }
    val ingredientToEditIndex = remember { mutableStateOf(-1) }

    val showStepEditDialog = remember { mutableStateOf(false) }
    val stepDialogMode = remember { mutableStateOf(StepDialogMode.ADD) }
    val stepToEditIndex = remember { mutableStateOf(-1) }

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

    if (showStepEditDialog.value) {
        val step = when (stepDialogMode.value) {
            StepDialogMode.ADD -> ""
            StepDialogMode.EDIT -> steps[stepToEditIndex.value]
        }

        StepDialog(
            step = step,
            onDismiss = { showStepEditDialog.value = false },
            onConfirm = { updated ->
                if (stepDialogMode.value == StepDialogMode.ADD) {
                    steps.add(updated)
                } else {
                    steps[stepToEditIndex.value] = updated
                }
                showStepEditDialog.value = false
            }
        )
    }

    Scaffold(
        //Chowanie klawiatury - usunięcie focusu przy kliknięciu na ekranie
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppBarView(
                title = "Edycja przepisu",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = "Dodaj zdjęcie"
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Dodaj zdjęcie"
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
                                label = "Nazwa przepisu",
                                value = viewModel.recipeNameState,
                                onValueChanged = { viewModel.onRecipeNameChanged(it) }
                            )
// Dodawanie opisu
                            RecipeTextField(
                                label = "Opis (opcjonalnie)",
                                value = viewModel.recipeDescState,
                                onValueChanged = { viewModel.onRecipeDescChanged(it) }
                            )
// Dodawanie linku
                            RecipeTextField(
                                label = "Link",
                                value = viewModel.recipeLinkState,
                                onValueChanged = { viewModel.onRecipeLinkChanged(it) }
                            )
                        }
                    }
                }

//Dodawanie składników
                item {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "Lista Składników",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
//Tabela z dodanymi składnikami
                                ingredients.forEachIndexed { index, ingredient ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            modifier = Modifier.weight(0.5f),
                                            text = "${ingredient.quantity} ${ingredient.unit}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            modifier = Modifier.weight(1f),
                                            text = ingredient.name,
                                            fontSize = 16.sp
                                        )
                                        //Edycja składnika
                                        IconButton(onClick = {
                                            ingredientDialogMode.value = IngredientDialogMode.EDIT
                                            ingredientToEditIndex.value = index
                                            showIngredientEditDialog.value =
                                                !showIngredientEditDialog.value
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edytuj składnik",
                                                tint = Color.Gray
                                            )
                                        }
                                        //Usuwanie składnika
                                        IconButton(onClick = {
                                            ingredients.removeAt(index)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Usuń składnik",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row {
                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(50),
                                        onClick = {
                                            ingredientDialogMode.value = IngredientDialogMode.ADD
                                            showIngredientEditDialog.value =
                                                !showIngredientEditDialog.value
                                        }
                                    ) {
                                        Text("+ Nowy składnik")
                                    }
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
                                    text = "Przygotowanie",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
//Lista kroków
                                steps.forEachIndexed { index, value ->
                                    if (index != 0) {
                                        HorizontalDivider(thickness = 2.dp)
                                    }

                                    Column {
                                        Text(
                                            modifier = Modifier.padding(end = 10.dp),
                                            text = "Krok ${index + 1}",
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
                                                stepDialogMode.value = StepDialogMode.EDIT
                                                stepToEditIndex.value = index
                                                showStepEditDialog.value = !showStepEditDialog.value
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Edytuj krok",
                                                    tint = Color.Gray
                                                )
                                            }

                                            IconButton(onClick = {
                                                steps.removeAt(index)
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Usuń krok",
                                                    tint = Color.Red
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row {
                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RectangleShape,
                                        onClick = {
                                            stepDialogMode.value = StepDialogMode.ADD
                                            showStepEditDialog.value = !showStepEditDialog.value
                                        }) {
                                        Text("+ Nowy krok")
                                    }
                                }
                            }
                        }
                    }
                }

// Zapisywanie przepisu
                item {
                    Button(
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RectangleShape,
                        onClick = {
                            if (viewModel.recipeNameState.isNotEmpty()) {
                                if (id != 0L) {
                                    //Aktualizacja przepisu
                                    viewModel.updateRecipe(
                                        Recipe(
                                            id = id,
                                            name = viewModel.recipeNameState.trim(),
                                            desc = viewModel.recipeDescState.trim(),
                                            isFavourite = viewModel.recipeFavState,
                                            imagePath = viewModel.recipeImageState,
                                            link = viewModel.recipeLinkState.trim()
                                        ),
                                        ingredients = ingredients.toList(),
                                        steps = steps.toList()
                                    )
                                    snackMessage.value = "Przepis został zaktualizowany."
                                } else {
                                    //Zapisanie nowego przepisu
                                    viewModel.addFullRecipe(
                                        recipe = Recipe(
                                            name = viewModel.recipeNameState.trim(),
                                            desc = viewModel.recipeDescState.trim(),
                                            isFavourite = viewModel.recipeFavState,
                                            imagePath = viewModel.recipeImageState,
                                            link = viewModel.recipeLinkState.trim(),
                                            createdAt = System.currentTimeMillis()
                                        ),
                                        ingredients = ingredients.toList(),
                                        steps = steps.toList()
                                    )
                                    snackMessage.value = "Przepis został dodany."
                                }
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = snackMessage.value,
                                        duration = SnackbarDuration.Short
                                    )
                                    navController.navigate(Screen.RecipesScreen.route)
                                }
                            } else {
                                //Info o konieczności wypełnienia pól
                                snackMessage.value = "Wypełnij pola."
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = snackMessage.value,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    ) {
                        Text(
                            text = if (id != 0L) "Zaktualizuj przepis" else "Dodaj przepis",
                            style = TextStyle(fontSize = 18.sp),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeTextField(
    label: String,
    value: String,
    onValueChanged: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        label = { Text(text = label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )
}