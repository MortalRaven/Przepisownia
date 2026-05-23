package com.mort.przepisownia.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mort.przepisownia.data.entities.Recipe
import com.mort.przepisownia.data.entities.RecipeWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecipeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addRecipe(recipeEntity: Recipe): Long

    @Query("SELECT * FROM 'recipes'")
    abstract fun getAllRecipes(): Flow<List<Recipe>>

    @Update
    abstract suspend fun updateRecipe(recipeEntity: Recipe)

    @Delete
    abstract suspend fun deleteRecipe(recipeEntity: Recipe)

    @Query("SELECT * FROM 'recipes' WHERE id = :id")
    abstract fun getRecipeByID(id: Long): Flow<Recipe>

    @Transaction
    @Query("SELECT * FROM 'recipes' WHERE id = :recipeId")
    abstract fun getRecipeDetails(recipeId: Long): Flow<RecipeWithDetails?>

    @Query("UPDATE 'recipes' SET created_at = :time WHERE created_at = 0")
    abstract suspend fun updateCreatedAtDefaults(time: Long)
}