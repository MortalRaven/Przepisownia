package com.mort.przepisownia

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mort.przepisownia.data.Graph
import com.mort.przepisownia.data.entities.Ingredient
import com.mort.przepisownia.data.entities.IngredientInput
import com.mort.przepisownia.data.entities.Recipe
import com.mort.przepisownia.data.entities.RecipeStep
import com.mort.przepisownia.data.entities.RecipeWithDetails
import com.mort.przepisownia.data.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecipeViewModel(
    private val recipeRepository: RecipeRepository = Graph.recipeRepository,
) : ViewModel() {

    var recipeNameState by mutableStateOf("")
    var recipeDescState by mutableStateOf("")
    var recipeFavState by mutableStateOf(false)
    var recipeImageState by mutableStateOf("")
    var recipeLinkState by mutableStateOf("")
    var recipeIngredientsState by mutableStateOf(listOf<Ingredient>())
    var recipeStepsState by mutableStateOf(listOf<RecipeStep>())

    //Funkcje dot. przepisów
    lateinit var getAllRecipes: Flow<List<Recipe>>

    init {
        viewModelScope.launch {
            getAllRecipes = recipeRepository.getRecipes()
        }
    }

    fun getRecipeDetails(recipeId: Long): Flow<RecipeWithDetails> {
        return recipeRepository.getRecipeDetails(recipeId)
    }

    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.addRecipe(recipe)
        }
    }

    fun addFullRecipe(
        recipe: Recipe,
        ingredients: List<IngredientInput>,
        steps: List<String>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.addFullRecipe(recipe, ingredients, steps)
        }
    }

    fun getRecipeByID(id: Long): Flow<Recipe> {
        return recipeRepository.getRecipeByID(id)
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.updateRecipe(recipe)
        }
    }

    fun updateRecipeFav(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.updateRecipeFav(id)
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

    fun onRecipeFavChanged(isFav: Boolean) {
        recipeFavState = isFav
    }

    fun onRecipeImageChanged(newString: String) {
        recipeImageState = newString
    }

    fun onRecipeLinkChanged(newString: String) {
        recipeLinkState = newString
    }

//Funkcje dot. składników

    fun addIngredient(ingredient: Ingredient) {
        recipeIngredientsState = recipeIngredientsState + ingredient
    }

    fun removeIngredient(index: Int) {
        recipeIngredientsState = recipeIngredientsState.toMutableList().also { it.removeAt(index) }
    }


    //Funkcje dot. kroków
    fun addStep(step: RecipeStep) {
        recipeStepsState = recipeStepsState + step
    }

    fun removeStep(index: Int) {
        recipeStepsState = recipeStepsState.toMutableList().also { it.removeAt(index) }
    }
}