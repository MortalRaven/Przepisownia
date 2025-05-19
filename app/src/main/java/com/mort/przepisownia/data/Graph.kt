package com.mort.przepisownia.data

import android.content.Context
import androidx.room.Room
import com.mort.przepisownia.data.repository.IngredientRepository
import com.mort.przepisownia.data.repository.RecipeRepository
import com.mort.przepisownia.data.repository.StepRepository

object Graph {
    lateinit var database: RecipeDatabase

    val recipeRepository by lazy {
        RecipeRepository (recipeDao = database.recipeDao())
    }

    val ingredientRepository by lazy {
        IngredientRepository (ingredientDao = database.ingredientDao())
    }

    val stepRepository by lazy {
        StepRepository (stepDao = database.stepDao())
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, RecipeDatabase::class.java, "recipeList.db").build()
    }
}