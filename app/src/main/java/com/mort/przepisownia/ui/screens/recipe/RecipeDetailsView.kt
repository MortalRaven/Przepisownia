package com.mort.przepisownia.ui.screens.recipe

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mort.przepisownia.R
import com.mort.przepisownia.navigation.Screen
import com.mort.przepisownia.ui.common.AppBarView
import com.mort.przepisownia.ui.common.LoadingOverlay
import com.mort.przepisownia.ui.common.MenuDropdownItem
import com.mort.przepisownia.ui.screens.recipe.components.DeleteRecipeDialog
import com.mort.przepisownia.utils.displayName
import com.mort.przepisownia.utils.formatDate
import com.mort.przepisownia.utils.inTextFormatter
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun RecipeDetailsView(
    id: Long,
    navController: NavController,
    viewModel: RecipeViewModel,
) {
    val context = LocalContext.current
    val recipe = viewModel.getRecipeDetails(id).collectAsState(initial = null).value
    if (recipe == null) {
        LoadingOverlay(true) { }
        return
    }

    val isFavourite = recipe.recipe.isFavourite
    val ingredientsCheckState = remember { mutableStateMapOf<Long, Boolean>() }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackMessage = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(recipe.ingredients) {
        ingredientsCheckState.clear()
        recipe.ingredients.forEach {
            ingredientsCheckState[it.id] = false
        }
    }

    val showDeleteRecipeDialog = remember { mutableStateOf(false) }

    if (showDeleteRecipeDialog.value) {
        DeleteRecipeDialog(
            recipe = recipe.recipe,
            onDismiss = { showDeleteRecipeDialog.value = false },
            onConfirm = {
                viewModel.setPendingDeletedRecipe(recipe.recipe)
                showDeleteRecipeDialog.value = false
                navController.navigate(Screen.RecipesScreen.route)
            }
        )
    }

    Scaffold(
        topBar = {
            AppBarView(
                title = recipe.recipe.name,
                onBackNavClick = { navController.navigateUp() },
                dropdownMenuItems = listOf(
                    MenuDropdownItem(
                        text = stringResource(R.string.edit),
                        action = { navController.navigate(Screen.AddEditScreen.route + "/$id") }
                    ),
                    MenuDropdownItem(
                        text = stringResource(R.string.delete),
                        action = {
                            showDeleteRecipeDialog.value = !showDeleteRecipeDialog.value
                        }
                    )
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
//Zdjęcie przepisu
            item {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    val imageFile = recipe.recipe.imagePath.takeIf { it.isNotBlank() }?.let {
                        File(context.filesDir, it)
                    }

                    if (imageFile != null && imageFile.exists()) {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            model = imageFile,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.vegetables_placeholder),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .background(MaterialTheme.colorScheme.surface),
                            contentScale = ContentScale.Crop,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
            }
//Nazwa przepisu
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = recipe.recipe.name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
//Link, Ulubione
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (recipe.recipe.link.isNotEmpty()) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5F),
                            shape = RoundedCornerShape(50),
                            onClick = {
                                val urlIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(recipe.recipe.link)
                                )
                                context.startActivity(urlIntent)
                            }
                        ) {
                            Text(stringResource(R.string.link))
                            Icon(
                                painter = painterResource(R.drawable.baseline_open_in_browser_24),
                                contentDescription = stringResource(R.string.link)
                            )
                        }
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5F),
                        shape = RoundedCornerShape(50),
                        onClick = {
                            viewModel.updateRecipeFav(recipe.recipe.id)
                            if (!isFavourite) {
                                snackMessage.value = context.resources.getString(R.string.recipe_added_favourite)
                            } else {
                                snackMessage.value = context.resources.getString(R.string.recipe_removed_favourite)
                            }
                            scope.launch {
                                snackbarHostState.showSnackbar(snackMessage.value)
                            }
                        }
                    ) {
                        Text(stringResource(R.string.favourite))
                        Icon(
                            painter = if (isFavourite) painterResource(R.drawable.baseline_favorite_24) else painterResource(
                                R.drawable.baseline_favorite_border_24
                            ),
                            contentDescription = stringResource(R.string.favourite)
                        )
                    }
                }
            }

//Opis przepisu
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = recipe.recipe.desc,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start
                )
            }

            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = stringResource(R.string.date_added, formatDate(recipe.recipe.createdAt)),
                    fontSize = 14.sp,
                    textAlign = TextAlign.End
                )
            }
//Tabela składników
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 4.dp),
                        text = stringResource(R.string.ingredients),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Column(
                        modifier = Modifier.border(width = 1.dp, color = Color.Gray),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        recipe.ingredients.forEachIndexed { _, ingredient ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Checkbox(
                                    checked = ingredientsCheckState[ingredient.id] ?: false,
                                    onCheckedChange = {
                                        ingredientsCheckState[ingredient.id] =
                                            !(ingredientsCheckState[ingredient.id] ?: false)
                                    }
                                )
                                //Nazwa składnika
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = ingredient.name,
                                    fontSize = 14.sp
                                )
                                //Ilość skłandika
                                Text(
                                    text = if (ingredient.quantity != null && ingredient.unit != null) {
                                        "${ingredient.quantity.inTextFormatter()} ${ingredient.unit.displayName(ingredient.quantity)}"
                                    } else {""},
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                }
            }
//Tabela kroków
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(R.string.recipe) + ":",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    recipe.steps.forEachIndexed { index, step ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = stringResource(R.string.recipe_step) + " ${index + 1}.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = step.description,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}