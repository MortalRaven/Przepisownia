package com.mort.przepisownia.data.repository

import com.mort.przepisownia.data.dao.StepDao
import com.mort.przepisownia.data.entities.RecipeStep
import kotlinx.coroutines.flow.Flow

class StepRepository(private val stepDao: StepDao) {

    suspend fun addStep(step: RecipeStep) {
        stepDao.addStep(step)
    }

    fun getRecipeSteps(recipeId: Long): Flow<List<RecipeStep>> {
        return stepDao.getRecipeSteps(recipeId)
    }

    suspend fun updateStep(step: RecipeStep) {
        stepDao.updateStep(step)
    }

    suspend fun deleteStep(step: RecipeStep) {
        stepDao.deleteStep(step)
    }
}