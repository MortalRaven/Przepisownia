package com.mort.przepisownia.data.repository

import android.util.Log
import com.mort.przepisownia.data.dao.IngredientDao
import com.mort.przepisownia.data.dao.RecipeDao
import com.mort.przepisownia.data.dao.StepDao
import com.mort.przepisownia.data.entities.Ingredient
import com.mort.przepisownia.data.entities.IngredientInput
import com.mort.przepisownia.data.entities.Recipe
import com.mort.przepisownia.data.entities.RecipeStep
import com.mort.przepisownia.data.entities.RecipeWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val ingredientsDao: IngredientDao,
    private val stepsDao: StepDao,
) {
    suspend fun addRecipe(recipe: Recipe) {
        recipeDao.addRecipe(recipe)
    }

    suspend fun addFullRecipe(
        recipe: Recipe,
        ingredientsInput: List<IngredientInput>,
        stepsInput: List<String>,
    ) {
        //Log.d("DEBUG", "addFullRecipe wywołane z: $recipe, $ingredientsInput, $stepsInput")
        val recipeId = recipeDao.addRecipe(recipe)
        val ingredientsWithId = ingredientsInput.map {
            Ingredient(
                recipeId = recipeId,
                name = it.name,
                quantity = it.quantity,
                unit = it.unit
            )
        }
        val stepsWithId = stepsInput.mapIndexed {index, step ->
            RecipeStep(
                recipeId = recipeId,
                stepNumber = index + 1,
                description = step
            )
        }

        ingredientsDao.addIngredients(ingredientsWithId)
        stepsDao.addSteps(stepsWithId)
    }

    fun getRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()

    fun getRecipeByID(id: Long): Flow<Recipe> {
        return recipeDao.getRecipeByID(id)
    }

    fun getRecipeDetails(recipeId: Long): Flow<RecipeWithDetails> =
        recipeDao.getRecipeDetails(recipeId)

    suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateRecipe(recipe)
    }

    suspend fun updateRecipeFav(id: Long) {
        val recipe = recipeDao.getRecipeByID(id).first()
        val updatedRecipe = recipe.copy(isFavourite = !recipe.isFavourite)
        recipeDao.updateRecipe(updatedRecipe)
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe)
    }
}