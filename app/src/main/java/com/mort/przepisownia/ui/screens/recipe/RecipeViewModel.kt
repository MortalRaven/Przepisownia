package com.mort.przepisownia.ui.screens.recipe

import android.content.Context
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mort.przepisownia.R
import com.mort.przepisownia.data.Graph
import com.mort.przepisownia.data.entities.IngredientInput
import com.mort.przepisownia.data.entities.Recipe
import com.mort.przepisownia.data.entities.RecipeWithDetails
import com.mort.przepisownia.data.preferences.PreferencesManager
import com.mort.przepisownia.data.repository.RecipeRepository
import com.mort.przepisownia.ui.common.EditMode
import com.mort.przepisownia.ui.common.ViewType
import com.mort.przepisownia.ui.screens.recipe.components.SortType
import com.mort.przepisownia.utils.isValidUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeViewModel(
    private val recipeRepository: RecipeRepository = Graph.recipeRepository,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    private val _recipesLayout = MutableStateFlow(ViewType.GRID)
    val recipesLayout: StateFlow<ViewType> = _recipesLayout.asStateFlow()

    var recipeId by mutableLongStateOf(0L)
    var recipeNameState by mutableStateOf("")
    var recipeDescState by mutableStateOf("")
    var recipeFavState by mutableStateOf(false)
    var recipeImageState by mutableStateOf("")
    var recipeLinkState by mutableStateOf("")
    var recipeCreatedAt by mutableLongStateOf(0L)
    var recipeLastViewed: Long? by mutableStateOf(null)

    val nameIsEmpty by derivedStateOf {
        recipeNameState.isEmpty()
    }

    val linkHasErrors by derivedStateOf {
        if (recipeLinkState.isNotEmpty()) {
            !recipeLinkState.isValidUrl()
        } else {
            false
        }
    }

    private val _events = MutableSharedFlow<RecipeEvent>()
    val events = _events.asSharedFlow()

    var isDbLoading by mutableStateOf(true)
    var isRecipeLoading by mutableStateOf(true)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _sortType = MutableStateFlow(SortType.DATE_ADDED_DESC)
    val sortType: StateFlow<SortType> = _sortType

    private val _showFavorites = MutableStateFlow(false)
    val showFavorites: StateFlow<Boolean> = _showFavorites

    private val _pendingDeleteRecipe = MutableStateFlow<Recipe?>(null)
    val pendingDeleteRecipe: StateFlow<Recipe?> = _pendingDeleteRecipe

    private val _allRecipes = recipeRepository.getRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredRecipes: StateFlow<List<Recipe>> = combine(
        _allRecipes,
        _searchQuery,
        _sortType,
        _showFavorites
    ) { recipes, query, sortType, showFavs ->
        recipes.asSequence().filter {
            it.name.contains(query.trim(), ignoreCase = true) ||
                    it.desc.contains(query.trim(), ignoreCase = true)
        }
            .filter {
                !showFavs || it.isFavourite
            }
            .sortedWith(
                when (sortType) {
                    SortType.ALPHABET_ASC -> compareBy { it.name.lowercase() }
                    SortType.ALPHABET_DESC -> compareByDescending { it.name.lowercase() }
                    SortType.DATE_ADDED_ASC -> compareBy { it.createdAt }
                    SortType.DATE_ADDED_DESC -> compareByDescending { it.createdAt }
                    SortType.DATE_VIEWED_ASC -> compareBy<Recipe> { it.lastViewedAt }.thenBy { it.createdAt }
                    SortType.DATE_VIEWED_DESC -> compareByDescending<Recipe> { it.lastViewedAt }.thenByDescending { it.createdAt }
                }
            )
            .toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            filteredRecipes.collect { _ ->
                if (isDbLoading) {
                    isDbLoading = false
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            preferencesManager.recipesLayout.collect { layout ->
                val layoutEnum = try {
                    layout
                } catch (e: Exception) {
                    ViewType.GRID
                }
                _recipesLayout.value = layoutEnum
            }
        }
    }

    fun getRecipeByID(id: Long): Flow<Recipe> {
        return recipeRepository.getRecipeByID(id)
    }

    fun getRecipeDetails(recipeId: Long): Flow<RecipeWithDetails> {
        return recipeRepository.getRecipeDetails(recipeId)
    }

    fun saveRecipe(
        mode: EditMode,
        ingredients: List<IngredientInput>,
        steps: List<String>,
    ) {
        viewModelScope.launch {
            if (nameIsEmpty || linkHasErrors) {
                _events.emit(RecipeEvent.ShowSnackbar(R.string.warning_empty_fields))
                return@launch
            }

            if (mode == EditMode.EDIT) {
                updateRecipe(
                    recipe = Recipe(
                        id = recipeId,
                        name = recipeNameState.trim(),
                        desc = recipeDescState.trim(),
                        isFavourite = recipeFavState,
                        imagePath = recipeImageState,
                        link = recipeLinkState.trim(),
                        createdAt = recipeCreatedAt,
                        lastViewedAt = recipeLastViewed
                    ),
                    ingredients = ingredients,
                    steps = steps
                )
                _events.emit(RecipeEvent.ShowSnackbar(R.string.recipe_updated))
                _events.emit(RecipeEvent.NavigateBack)
            } else {
                addFullRecipe(
                    recipe = Recipe(
                        name = recipeNameState.trim(),
                        desc = recipeDescState.trim(),
                        isFavourite = recipeFavState,
                        imagePath = recipeImageState,
                        link = recipeLinkState.trim()
                    ),
                    ingredients = ingredients,
                    steps = steps
                )
                _events.emit(RecipeEvent.ShowSnackbar(R.string.recipe_added))
                _events.emit(RecipeEvent.NavigateBack)
            }
        }
    }

    private fun addFullRecipe(
        recipe: Recipe,
        ingredients: List<IngredientInput>,
        steps: List<String>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.addFullRecipe(recipe, ingredients, steps)
        }
    }

    private fun updateRecipe(
        recipe: Recipe,
        ingredients: List<IngredientInput>,
        steps: List<String>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.updateRecipe(recipe, ingredients, steps)
        }
    }

    fun updateRecipeFav(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.updateRecipeFav(id)
        }
    }

    fun updateRecipeLastViewed(id: Long, date: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.updateRecipeLastViewed(id, date)
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.deleteRecipe(recipe)
        }
    }

    fun onRecipeNameChanged(newString: String) {
        recipeNameState = newString
    }

    fun onRecipeDescChanged(newString: String) {
        recipeDescState = newString
    }

    fun onRecipeLinkChanged(newString: String) {
        recipeLinkState = newString
    }

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun updateSortType(newSortType: SortType) {
        _sortType.value = newSortType
    }

    fun toggleFavorites() {
        _showFavorites.value = !_showFavorites.value
    }

    fun resetFilters() {
        _sortType.value = SortType.ALPHABET_ASC
        _showFavorites.value = false
    }

    fun setRecipesLayout(viewType: ViewType) {
        _recipesLayout.value = viewType
        viewModelScope.launch {
            preferencesManager.setRecipesLayout(viewType)
        }
    }

    fun setPendingDeletedRecipe(recipe: Recipe) {
        _pendingDeleteRecipe.value = recipe
    }

    fun clearPendingDeleteList() {
        _pendingDeleteRecipe.value = null
    }
}

sealed class RecipeEvent {
    data class ShowSnackbar(val message: Int) : RecipeEvent()
    object NavigateBack : RecipeEvent()
}

class RecipeViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val recipeRepository = Graph.recipeRepository
        val preferencesManager = PreferencesManager(context)
        return RecipeViewModel(recipeRepository, preferencesManager) as T
    }
}