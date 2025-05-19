package com.mort.przepisownia

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mort.przepisownia.data.Graph
import com.mort.przepisownia.data.entities.Ingredient
import com.mort.przepisownia.data.entities.Recipe
import com.mort.przepisownia.data.entities.RecipeStep
import com.mort.przepisownia.data.entities.RecipeWithDetails
import com.mort.przepisownia.data.repository.IngredientRepository
import com.mort.przepisownia.data.repository.RecipeRepository
import com.mort.przepisownia.data.repository.StepRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecipeViewModel(
    private val recipeRepository: RecipeRepository = Graph.recipeRepository,
    private val ingredientRepository: IngredientRepository = Graph.ingredientRepository,
    private val stepRepository: StepRepository = Graph.stepRepository
) : ViewModel() {

    var recipeNameState by mutableStateOf("")
    var recipeDescState by mutableStateOf("")
    var recipeImageState by mutableStateOf("")
    //var recipeIngredientsState by mutableStateListOf<Ingredient>()
    //var recipeStepsState by mutableStateListOf<RecipeStep>()

    var ingredientNameState by mutableStateOf("")
    var ingredientQuantityState by mutableStateOf("")
    var ingredientUnitState by mutableStateOf("")

    var stepDescState by mutableStateOf("")

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

    fun getRecipeByID(id:Long): Flow<Recipe> {
        return recipeRepository.getRecipeByID(id)
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.updateRecipe(recipe)
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

    fun onRecipeImageChanged(newString: String) {
        recipeImageState = newString
    }

    //Funkcje dot. składników
    fun onIngredientNameChanged(newString: String) {
        ingredientNameState = newString
    }

    fun onIngredientQuantityChanged(newString: String) {
        ingredientQuantityState = newString
    }

    fun onIngredientUnitChanged(newString: String) {
        ingredientUnitState = newString
    }

    //Funkcje dot. kroków
    fun onStepDescChanged(newString: String) {
        stepDescState = newString
    }
}