package com.mort.przepisownia.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mort.przepisownia.data.entities.RecipeStep
import kotlinx.coroutines.flow.Flow

@Dao
abstract class StepDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addStep(step: RecipeStep)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addSteps(steps: List<RecipeStep>)

    @Query("SELECT * FROM 'recipes_steps' WHERE recipeId = :recipeId")
    abstract fun getRecipeSteps(recipeId: Long): Flow<List<RecipeStep>>

    @Update
    abstract suspend fun updateStep(stepEntity: RecipeStep)

    @Delete
    abstract suspend fun deleteStep(stepEntity: RecipeStep)

    @Query("DELETE FROM 'recipes_steps' WHERE recipeId = :recipeId")
    abstract suspend fun deleteStepsByRecipeId(recipeId: Long)
}