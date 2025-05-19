package com.mort.przepisownia

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mort.przepisownia.data.entities.IngredientInput
import com.mort.przepisownia.data.entities.Recipe
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun AddEditRecipeView(
    id: Long,
    viewModel: RecipeViewModel,
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    if (id != 0L) {
        val recipe = viewModel.getRecipeByID(id).collectAsState(
            initial = Recipe(0L, "", "", false, "", "")
        )
        viewModel.recipeNameState = recipe.value.name
        viewModel.recipeDescState = recipe.value.desc
    } else {
        viewModel.recipeNameState = ""
        viewModel.recipeDescState = ""
    }

    val recipeName = remember { mutableStateOf("") }
    val recipeDesc = remember { mutableStateOf("") }
    val ingredients = remember { mutableStateListOf<IngredientInput>() }
    val newIngredient = remember { mutableStateOf(IngredientInput()) }
    val steps = remember { mutableStateListOf<String>() }
    val newStep = remember { mutableStateOf("") }

    val unitList = listOf("g", "dag", "kg", "ml", "l", "łyżeczka", "łyżka", "szklanka", "szt.")

    //Obsługa focusu
    //val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    //var isFocused = remember { mutableStateOf(false) }

    //Scrollowanie do elementu z focusem
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        //Chowanie klawiatury - usunięcie focusu przy kliknięciu na ekranie
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        scaffoldState = scaffoldState,
        topBar = {
            AppBarView(title = "Przepis") {
                navController.navigateUp()
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(start = 10.dp, end = 10.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

// Dodawanie nazwy
            item {
                RecipeTextField(
                    label = "Nazwa przepisu",
                    value = recipeName.value,
                    onValueChanged = { recipeName.value = it }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

// Dodawanie opisu
            item {
                RecipeTextField(
                    label = "Dodatkowy opis (opcjonalnie)",
                    value = recipeDesc.value,
                    onValueChanged = { recipeDesc.value = it }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

//Dodawanie zdjęcia
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .border(width = 1.dp, color = Color.Gray)
                            .size(120.dp, 120.dp)
                    ) {
                        //TODO Foto
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(onClick = {}) {
                        Text(text = "+ Dodaj zdjęcie ")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

//Dodawanie składników
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = Color.Gray)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "Lista Składników", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    ingredients.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier.weight(0.5f),
                                text = "${item.amount} ${item.unit}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                modifier = Modifier.weight(1f),
                                text = item.name,
                                fontSize = 16.sp
                            )

                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edytuj składnik",
                                    tint = Color.Gray
                                )
                            }

                            IconButton(onClick = {ingredients.removeAt(index)}) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Usuń składnik",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }

            item {
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
                        value = newIngredient.value.amount,
                        onValueChange = {
                            newIngredient.value = newIngredient.value.copy(amount = it)
                        },
                        label = { Text("Ilość") },
                        modifier = Modifier.weight(0.5f),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                    )

                    val expanded = remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        modifier = Modifier.weight(0.5f),
                        expanded = expanded.value,
                        onExpandedChange = {expanded.value = !expanded.value}
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = newIngredient.value.unit,
                            onValueChange = {},
                            label = { Text("")},
                            trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)}
                        )

                        ExposedDropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false}
                        ) {
                            unitList.forEach{ unit ->
                                DropdownMenuItem(
                                    text = { Text(unit) },
                                    onClick = {
                                        newIngredient.value = newIngredient.value.copy(unit = unit)
                                        expanded.value = false
                                    }
                                )
                            }
                        }
                    }
                }

                Button(modifier = Modifier
                    .padding(bottom = 12.dp)
                    .bringIntoViewRequester(bringIntoViewRequester),
                    onClick = {
                        val input = newIngredient.value.copy()
                        if (newIngredient.value.name.isNotBlank()) {
                            ingredients.add(input)
                            newIngredient.value = IngredientInput()
                        }
                    }) {
                    Text("+ Dodaj składnik")
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

// Dodawanie kroków
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = Color.Gray)
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

                            Text(modifier = Modifier.weight(1f), text = value, fontSize = 16.sp)

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
            }

            item {
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

                Button(modifier = Modifier
                    .padding(bottom = 12.dp)
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