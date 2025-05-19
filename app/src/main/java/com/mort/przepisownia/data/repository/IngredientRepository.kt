package com.mort.przepisownia.data.repository

import com.mort.przepisownia.data.dao.IngredientDao
import com.mort.przepisownia.data.entities.Ingredient
import kotlinx.coroutines.flow.Flow

class IngredientRepository (private val ingredientDao: IngredientDao) {

    suspend fun addIngredient(ingredient: Ingredient){
        ingredientDao.addIngredient(ingredient)
    }

    fun getIngredients(): Flow<List<Ingredient>> = ingredientDao.getAllIngredients()

    fun getRecipeIngredients(recipeId: Long): Flow<List<Ingredient>> {
        return ingredientDao.getRecipeIngredients(recipeId)
    }

    suspend fun updateIngredient(ingredient: Ingredient) {
        ingredientDao.updateIngredient(ingredient)
    }

    suspend fun deleteIngredient(ingredient: Ingredient) {
        ingredientDao.deleteIngredient(ingredient)
    }
}