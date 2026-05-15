package com.mort.przepisownia.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mort.przepisownia.data.repository.RecipeRepository
import com.mort.przepisownia.data.repository.ShoppingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Graph {
    private lateinit var database: RecipeDatabase

    val recipeRepository by lazy {
        RecipeRepository(
            recipeDao = database.recipeDao(),
            ingredientsDao = database.ingredientDao(),
            stepsDao = database.stepDao(),
        )
    }

    val shoppingRepository by lazy {
        ShoppingRepository(
            shoppingListDao = database.shoppingListDao(),
            shoppingItemDao = database.shoppingItemDao()
        )
    }


    fun provide(context: Context) {
        database =
            Room.databaseBuilder(context, RecipeDatabase::class.java, "recipeList.db").build()

        CoroutineScope(Dispatchers.IO).launch {
            recipeRepository.updateMissingCreatedAt()
        }
    }
}