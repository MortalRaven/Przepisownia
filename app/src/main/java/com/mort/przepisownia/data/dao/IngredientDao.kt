package com.mort.przepisownia.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mort.przepisownia.data.entities.Ingredient
import kotlinx.coroutines.flow.Flow

@Dao
abstract class IngredientDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addIngredient(ingredientEntity: Ingredient)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addIngredients(ingredients: List<Ingredient>)

    @Query("SELECT * FROM 'ingredients'")
    abstract fun getAllIngredients(): Flow<List<Ingredient>>

    @Query("SELECT * FROM 'ingredients' WHERE recipeId = :recipeId")
    abstract fun getRecipeIngredients(recipeId: Long): Flow<List<Ingredient>>

    @Update
    abstract suspend fun updateIngredient(ingredientEntity: Ingredient)

    @Delete
    abstract suspend fun deleteIngredient(ingredientEntity: Ingredient)

    @Query("DELETE FROM 'ingredients' WHERE recipeId = :recipeId")
    abstract suspend fun deleteIngredientsByRecipeId(recipeId: Long)
}