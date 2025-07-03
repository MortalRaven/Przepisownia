package com.mort.przepisownia

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mort.przepisownia.data.entities.Recipe
import com.mort.przepisownia.data.entities.RecipeWithDetails
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun RecipeView(
    id: Long,
    viewModel: RecipeViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val recipe = viewModel.getRecipeDetails(id).collectAsState(
        initial = RecipeWithDetails(
            recipe = Recipe(0L, "", "", false, "", ""),
            ingredients = listOf(),
            steps = listOf()
        )
    )
    viewModel.recipeFavState = recipe.value.recipe.isFavourite

    val ingredientsCheckState = remember { mutableStateMapOf<Long, Boolean>() }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackMessage = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(recipe.value.ingredients) {
        ingredientsCheckState.clear()
        recipe.value.ingredients.forEach {
            ingredientsCheckState[it.id] = false
        }
    }

    Scaffold(
        topBar = {
            AppBarView(title = recipe.value.recipe.name) {
                navController.navigateUp()
            }
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
            //TODO Fotka przepisu/placeholder
            item {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    val imageFile = recipe.value.recipe.imagePath.takeIf { it.isNotBlank() }?.let {
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
                    Button(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(48.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = CircleShape,
                        onClick = {
                            viewModel.updateRecipeFav(recipe.value.recipe.id)
                            if (!viewModel.recipeFavState) {
                                snackMessage.value = "Dodano przepis do ulubionych."
                            } else {
                                snackMessage.value = "Usunięto przepis do ulubionych."
                            }
                            scope.launch {
                                snackbarHostState.showSnackbar(snackMessage.value)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (viewModel.recipeFavState) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Ulubione"
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
                    text = recipe.value.recipe.name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
//Link do przepisu
            if (recipe.value.recipe.link.isNotEmpty()) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        text = "Link: ${recipe.value.recipe.link}",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }

//Opis przepisu
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = recipe.value.recipe.desc,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start
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
                        text = "Składniki",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Column(
                        modifier = Modifier.border(width = 1.dp, color = Color.Gray),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        recipe.value.ingredients.forEachIndexed { _, ingredient ->
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

                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = ingredient.name,
                                    fontSize = 14.sp
                                )

                                Text(
                                    text = "${ingredient.quantity} ${ingredient.unit}",
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
                        text = "Przepis:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    recipe.value.steps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = "${index + 1}. ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = step.description,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}