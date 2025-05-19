package com.mort.przepisownia.data.repository

import com.mort.przepisownia.data.dao.RecipeDao
import com.mort.przepisownia.data.entities.Recipe
import com.mort.przepisownia.data.entities.RecipeWithDetails
import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val recipeDao: RecipeDao) {

    suspend fun addRecipe(recipe: Recipe) {
        recipeDao.addRecipe(recipe)
    }

    fun getRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()

    fun getRecipeByID(id: Long): Flow<Recipe> {
        return recipeDao.getRecipeByID(id)
    }

    fun getRecipeDetails(recipeId: Long): Flow<RecipeWithDetails> = recipeDao.getRecipeDetails(recipeId)

    suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateRecipe(recipe)
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe)
    }
}