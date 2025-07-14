package com.mort.przepisownia.ui.screens.recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mort.przepisownia.data.Graph
import com.mort.przepisownia.data.entities.IngredientInput
import com.mort.przepisownia.data.entities.Recipe
import com.mort.przepisownia.data.entities.RecipeWithDetails
import com.mort.przepisownia.data.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeViewModel(
    private val recipeRepository: RecipeRepository = Graph.recipeRepository,
) : ViewModel() {

    var recipeNameState by mutableStateOf("")
    var recipeDescState by mutableStateOf("")
    var recipeFavState by mutableStateOf(false)
    var recipeImageState by mutableStateOf("")
    var recipeLinkState by mutableStateOf("")

    var isLoading by mutableStateOf(true)
    var searchQuery by mutableStateOf("")

    private val _allRecipes = recipeRepository.getRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _pendingDeletedRecipe = MutableStateFlow<Recipe?>(null)
    val pendingDeletedRecipe: StateFlow<Recipe?> = _pendingDeletedRecipe

    val filteredRecipes: StateFlow<List<Recipe>> = combine(
        _allRecipes,
        snapshotFlow { searchQuery.trim() }
    ) { recipes, query ->
        if (query.isBlank()) {
            recipes
        } else {
            recipes.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.desc.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun getRecipeDetails(recipeId: Long): Flow<RecipeWithDetails> {
        return recipeRepository.getRecipeDetails(recipeId)
    }

    fun addFullRecipe(
        recipe: Recipe,
        ingredients: List<IngredientInput>,
        steps: List<String>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.addFullRecipe(recipe, ingredients, steps)
        }
    }

    fun getRecipeByID(id: Long): Flow<Recipe> {
        return recipeRepository.getRecipeByID(id)
    }

    fun updateRecipe(
        recipe: Recipe,
        ingredients: List<IngredientInput>,
        steps: List<String>
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

    fun setPendingDeletedRecipe(recipe: Recipe) {
        _pendingDeletedRecipe.value = recipe
    }

    fun clearPendingDeletedRecipe() {
        _pendingDeletedRecipe.value = null
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
}