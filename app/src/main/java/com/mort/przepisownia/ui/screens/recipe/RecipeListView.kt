package com.mort.przepisownia.ui.screens.recipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.mort.przepisownia.navigation.Screen
import com.mort.przepisownia.ui.common.AppBarView
import com.mort.przepisownia.ui.screens.recipe.components.RecipeItem

@Composable
fun RecipeListView(
    navController: NavController,
    viewModel: RecipeViewModel,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val focusManger = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var isSearching by rememberSaveable { mutableStateOf(false) }
    val shouldBlockInteractions = remember { mutableStateOf(false) }

    val pendingRecipe = viewModel.pendingDeletedRecipe.collectAsState()

    LaunchedEffect(pendingRecipe) {
        if (pendingRecipe.value != null) {
            val result = snackbarHostState.showSnackbar(
                message = "Przepis został usunięty.",
                actionLabel = "Cofnij",
                duration = SnackbarDuration.Short
            )

            when (result) {
                SnackbarResult.ActionPerformed -> {
                    viewModel.clearPendingDeletedRecipe()
                }

                SnackbarResult.Dismissed -> {
                    viewModel.deleteRecipe(pendingRecipe.value!!)
                    viewModel.clearPendingDeletedRecipe()
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                isSearching = false
                viewModel.searchQuery = ""
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val allRecipes = viewModel.filteredRecipes.collectAsState()
    val recipeList = allRecipes.value.filter { it.id != pendingRecipe.value?.id }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppBarView(
                title = "Przepisy",
                onBackNavClick = { navController.navigate(Screen.HomeScreen.route) },
                searchable = true,
                isSearching = isSearching,
                onSearchClick = {
                    if (isSearching) {
                        viewModel.searchQuery = ""
                    }
                    isSearching = !isSearching
                },
                searchQuery = viewModel.searchQuery,
                onQueryChange = { viewModel.searchQuery = it},
                onSearchFocusChanged = { hasFocus ->
                    shouldBlockInteractions.value = hasFocus
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(all = 20.dp),
                shape = CircleShape,
                onClick = { navController.navigate(Screen.AddEditScreen.route + "/0L") }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        }
    ) {
        if (recipeList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "\uD83C\uDF72 \nNie masz jeszcze żadnych przepisów.\nZacznij swoją przygodę kulinarną od dodania pierwszego!",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            //Siatka zawierająca przepisy
            Box(modifier = Modifier.fillMaxSize().padding(it)) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp, bottom = 16.dp),
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(recipeList, key = { recipe -> recipe.id }
                    ) { recipe ->
                        RecipeItem(context = LocalContext.current, recipe = recipe) {
                            val id = recipe.id
                            navController.navigate(Screen.RecipeScreen.route + "/$id")
                        }
                    }
                }

                if (shouldBlockInteractions.value) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Transparent)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        focusManger.clearFocus()
                                        shouldBlockInteractions.value = false
                                    }
                                )
                            }
                    )
                }
            }
        }
    }
}
