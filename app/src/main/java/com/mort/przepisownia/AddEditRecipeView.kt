package com.mort.przepisownia

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mort.przepisownia.data.entities.IngredientInput
import com.mort.przepisownia.data.entities.Recipe
import com.mort.przepisownia.data.entities.RecipeWithDetails
import com.mort.przepisownia.utils.saveImageToInternalStorage
import kotlinx.coroutines.launch
import java.io.File


//TODO NAPRAWIĆ MAJTANIE SIĘ EKRANU PRZY WPISYWANIU SKŁADNIKA BO LATA JAK POPIERDOLONY
@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun AddEditRecipeView(
    id: Long,
    viewModel: RecipeViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val photoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                val fileName = saveImageToInternalStorage(context, it)
                if(fileName != null) {
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

    //Scrollowanie do elementu z focusem
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    //Przepis, składniki, kroki
    val ingredients = remember { mutableStateListOf<IngredientInput>() }
    val newIngredient = remember { mutableStateOf(IngredientInput()) }
    val steps = remember { mutableStateListOf<String>() }
    val newStep = remember { mutableStateOf("") }

    val unitList = listOf("g", "dag", "kg", "ml", "l", "łyżeczka", "łyżka", "szklanka", "szt.")

    if (id != 0L) {
        val recipeDetails = viewModel.getRecipeDetails(id).collectAsState(
            initial = RecipeWithDetails(
                recipe = Recipe(0L, "", "", false, "", ""),
                ingredients = listOf(),
                steps = listOf()
            )
        )
        viewModel.recipeNameState = recipeDetails.value.recipe.name
        viewModel.recipeNameState = recipeDetails.value.recipe.name
        viewModel.recipeDescState = recipeDetails.value.recipe.desc
        viewModel.recipeFavState = recipeDetails.value.recipe.isFavourite
        viewModel.recipeImageState = recipeDetails.value.recipe.imagePath
        viewModel.recipeLinkState = recipeDetails.value.recipe.link
        viewModel.recipeIngredientsState = recipeDetails.value.ingredients
        viewModel.recipeStepsState = recipeDetails.value.steps

        ingredients.clear()
        ingredients.addAll(recipeDetails.value.ingredients.map {
            IngredientInput(name = it.name, quantity = it.quantity, unit = it.unit)
        })

        steps.clear()
        steps.addAll(recipeDetails.value.steps.map { it.description })
    } else {
        viewModel.recipeNameState = ""
        viewModel.recipeDescState = ""
        viewModel.recipeFavState = false
        viewModel.recipeImageState = ""
        viewModel.recipeLinkState = ""
        viewModel.recipeIngredientsState = listOf()
        viewModel.recipeStepsState = listOf()
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
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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

//Dodawanie zdjęcia
            item {
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val imageFile = viewModel.recipeImageState.takeIf { it.isNotBlank() }?.let {
                            File(context.filesDir, it)
                        }

                        if (imageFile != null && imageFile.exists()) {
                            AsyncImage(
                                modifier = Modifier.size(120.dp),
                                model = imageFile,
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                                    .size(120.dp, 120.dp)
                            ) {}
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(onClick = {
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }) {
                            Text(text = "+ Dodaj zdjęcie ")
                        }
                    }
                }
            }

//Dodawanie składników
//Tabela z dodanymi składnikami
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Lista Składników",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            ingredients.forEachIndexed { index, ingredient ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
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
//TODO Dodanie edycji składnika
                                    IconButton(onClick = {
                                        Log.i(
                                            "Infosek",
                                            "Edytowanie składnika"
                                        )
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edytuj składnik",
                                            tint = Color.Gray
                                        )
                                    }

                                    IconButton(onClick = {
                                        viewModel.removeIngredient(index)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Usuń składnik",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }

//Menu wprowadzania/edycji składnika
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            //Nazwa składnika
                            OutlinedTextField(
                                value = newIngredient.value.name,
                                onValueChange = {
                                    newIngredient.value = newIngredient.value.copy(name = it)
                                },
                                label = { Text("Składnik") },
                                modifier = Modifier
                                    .weight(0.4f)
                                    .onFocusEvent {
                                        if (it.isFocused) {
                                            coroutineScope.launch {
                                                bringIntoViewRequester.bringIntoView()
                                            }
                                        }
                                    },
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.weight(0.5f),
                                value = newIngredient.value.quantity,
                                onValueChange = {
                                    newIngredient.value = newIngredient.value.copy(quantity = it)
                                },
                                label = { Text("Ilość") },
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                            )

                            val expanded = remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                modifier = Modifier.weight(0.5f),
                                expanded = expanded.value,
                                onExpandedChange = { expanded.value = !expanded.value }
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.menuAnchor(),
                                    readOnly = true,
                                    value = newIngredient.value.unit,
                                    onValueChange = {},
                                    label = { Text("Jednostka") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expanded.value
                                        )
                                    }
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded.value,
                                    onDismissRequest = { expanded.value = false }
                                ) {
                                    unitList.forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit) },
                                            onClick = {
                                                newIngredient.value =
                                                    newIngredient.value.copy(unit = unit)
                                                expanded.value = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

//Przycisk zatwierdzenia składnika
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Button(
                                modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester),
                                shape = RoundedCornerShape(50),
                                onClick = {
                                    val input = newIngredient.value.copy()
                                    if (newIngredient.value.name.isNotBlank()) {
                                        ingredients.add(input)
                                        newIngredient.value = IngredientInput()
                                    }
                                }
                            ) {
                                Text("+ Dodaj składnik")
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(text = "Przepis", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                            steps.forEachIndexed { index, value ->
                                if (index != 0) {
                                    HorizontalDivider(thickness = 2.dp)
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        modifier = Modifier.padding(end = 10.dp),
                                        text = "${index + 1}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        modifier = Modifier.weight(1f),
                                        text = value,
                                        fontSize = 16.sp
                                    )

                                    IconButton(onClick = {}) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edytuj krok",
                                            tint = Color.Gray
                                        )
                                    }

                                    IconButton(onClick = {}) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Usuń krok",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = newStep.value,
                            onValueChange = { newStep.value = it },
                            label = { Text("Dodaj krok") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusEvent {
                                    if (it.isFocused) {
                                        coroutineScope.launch {
                                            bringIntoViewRequester.bringIntoView()
                                        }
                                    }
                                },
                            keyboardActions = KeyboardActions(onDone = {
                                if (newStep.value.isNotBlank()) {
                                    steps.add(newStep.value.trim())
                                    newStep.value = ""
                                }
                            }),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                        )

                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Button(modifier = Modifier
                                .bringIntoViewRequester(bringIntoViewRequester),
                                onClick = {
                                    if (newStep.value.isNotBlank()) {
                                        steps.add(newStep.value.trim())
                                        newStep.value = ""
                                    }
                                }) {
                                Text("+ Dodaj krok")
                            }
                        }
                    }
                }
            }

// Zapisywanie przepisu
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
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
                                        link = viewModel.recipeLinkState.trim()
                                    ),
                                    ingredients = ingredients.toList(),
                                    steps = steps.toList()
                                )
                                snackMessage.value = "Przepis został dodany."
                            }
                            scope.launch {
                                snackbarHostState.showSnackbar(snackMessage.value)
                                navController.navigateUp()
                            }
                        } else {
                            //Info o konieczności wypełnienia pól
                            snackMessage.value = "Wypełnij pola."
                            scope.launch {
                                snackbarHostState.showSnackbar(snackMessage.value)
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