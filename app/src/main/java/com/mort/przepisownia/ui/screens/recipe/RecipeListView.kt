package com.mort.przepisownia.ui.screens.recipe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.mort.przepisownia.R
import com.mort.przepisownia.navigation.Screen
import com.mort.przepisownia.ui.common.AppBarView
import com.mort.przepisownia.ui.common.BlockOverlay
import com.mort.przepisownia.ui.common.EmptyScreen
import com.mort.przepisownia.ui.common.LoadingOverlay
import com.mort.przepisownia.model.ViewType
import com.mort.przepisownia.ui.screens.recipe.components.FilterDrawer
import com.mort.przepisownia.ui.screens.recipe.components.RecipeGridItem
import com.mort.przepisownia.ui.screens.recipe.components.RecipeListItem
import com.mort.przepisownia.ui.screens.recipe.components.SearchBarView

@Composable
fun RecipeListView(
    navController: NavController,
    viewModel: RecipeViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManger = LocalFocusManager.current
    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    var isSearching by rememberSaveable { mutableStateOf(false) }
    val isDrawerOpen = remember { mutableStateOf(false) }
    val shouldBlockInteractions = remember { mutableStateOf(false) }

    val sortType by viewModel.sortType.collectAsState()
    val showFavorites by viewModel.showFavorites.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val pendingRecipe by viewModel.pendingDeleteRecipe.collectAsState()
    val allRecipes by viewModel.filteredRecipes.collectAsState()
    val recipeList = remember(allRecipes, pendingRecipe) { allRecipes.filter { it.id != pendingRecipe?.id } }
    val recipesLayout by viewModel.recipesLayout.collectAsState()

    LaunchedEffect(pendingRecipe) {
        pendingRecipe?.let {recipe ->
            val result = snackbarHostState.showSnackbar(
                message = context.resources.getString(R.string.recipe_deleted),
                actionLabel = context.resources.getString(R.string.undo),
                duration = SnackbarDuration.Short
            )

            when (result) {
                SnackbarResult.ActionPerformed -> {
                    viewModel.clearPendingDeleteList()
                }

                SnackbarResult.Dismissed -> {
                    viewModel.deleteRecipe(recipe)
                    viewModel.clearPendingDeleteList()
                }
            }
        }
    }

    LaunchedEffect(sortType, showFavorites, searchQuery) {
        if (recipesLayout == ViewType.GRID) {
            gridState.scrollToItem(0)
        } else {
            listState.scrollToItem(0)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                isSearching = false
                viewModel.updateSearchQuery("")
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.pendingDeleteRecipe.value?.let { recipe ->
                viewModel.deleteRecipe(recipe)
                viewModel.clearPendingDeleteList()
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                AppBarView(
                    title = stringResource(R.string.recipes),
                    onBackNavClick = { navController.navigate(Screen.HomeScreen.route) },
                    searchable = true,
                    isSearching = isSearching,
                    onSearchClick = {
                        if (isSearching) {
                            viewModel.updateSearchQuery("")
                        }
                        isSearching = !isSearching
                    },
                    onFilterClick = {
                        focusManger.clearFocus()
                        isDrawerOpen.value = !isDrawerOpen.value
                    },
                    layoutEditable = true,
                    onLayoutClick = {
                        val newLayout =
                            if (recipesLayout == ViewType.GRID) ViewType.LIST else ViewType.GRID
                        viewModel.setRecipesLayout(newLayout)
                    },
                    layoutType = recipesLayout
                )
                if (isSearching) {
                    SearchBarView(
                        searchQuery = searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onFocusChanged = { hasFocus ->
                            shouldBlockInteractions.value = hasFocus
                            isDrawerOpen.value = false
                        }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(all = 20.dp),
                shape = CircleShape,
                onClick = { navController.navigate(Screen.AddEditScreen.route + "/0L") }
            ) {
                Icon(painter = painterResource(R.drawable.baseline_add_24), contentDescription = "")
            }
        }
    ) { paddingValues ->
        LoadingOverlay(isLoading = viewModel.isDbLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (recipeList.isEmpty()) {
                    EmptyScreen(text = stringResource(R.string.no_recipes))
                } else {
                    //Siatka zawierająca przepisy
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (recipesLayout == ViewType.GRID) {
                            LazyVerticalGrid(
                                modifier = Modifier.fillMaxSize(),
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                state = gridState
                            ) {
                                items(recipeList, key = { recipe -> recipe.id }
                                ) { recipe ->
                                    RecipeGridItem(
                                        context = LocalContext.current,
                                        recipe = recipe
                                    ) {
                                        val id = recipe.id
                                        viewModel.updateRecipeLastViewed(
                                            id,
                                            System.currentTimeMillis()
                                        )
                                        navController.navigate(Screen.RecipeScreen.route + "/$id")
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                state = listState
                            ) {
                                items(recipeList, key = { recipe -> recipe.id }
                                ) { recipe ->
                                    RecipeListItem(
                                        context = LocalContext.current,
                                        recipe = recipe
                                    ) {
                                        val id = recipe.id
                                        viewModel.updateRecipeLastViewed(
                                            id,
                                            System.currentTimeMillis()
                                        )
                                        navController.navigate(Screen.RecipeScreen.route + "/$id")
                                    }
                                }
                            }
                        }
                    }
                }

                if (shouldBlockInteractions.value) {
                    BlockOverlay {
                        focusManger.clearFocus()
                        shouldBlockInteractions.value = false
                    }
                }

                if (isDrawerOpen.value) {
                    BlockOverlay {
                        isDrawerOpen.value = false
                    }
                }

                AnimatedVisibility(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(300.dp)
                        .align(Alignment.CenterEnd),
                    visible = isDrawerOpen.value,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(300)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .width(300.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        FilterDrawer(
                            currentSort = sortType,
                            showOnlyFavorites = showFavorites,
                            onSortChange = { viewModel.updateSortType(it) },
                            onToggleFavorites = { viewModel.toggleFavorites() },
                            onApply = { isDrawerOpen.value = false },
                            onReset = {
                                viewModel.resetFilters()
                                isDrawerOpen.value = false
                            }
                        )
                    }
                }
            }
        }
    }
}
