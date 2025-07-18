package com.mort.przepisownia.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mort.przepisownia.data.dao.IngredientDao
import com.mort.przepisownia.data.dao.RecipeDao
import com.mort.przepisownia.data.dao.ShoppingItemDao
import com.mort.przepisownia.data.dao.ShoppingListDao
import com.mort.przepisownia.data.dao.StepDao
import com.mort.przepisownia.data.entities.Ingredient
import com.mort.przepisownia.data.entities.Recipe
import com.mort.przepisownia.data.entities.RecipeStep
import com.mort.przepisownia.data.entities.ShoppingItem
import com.mort.przepisownia.data.entities.ShoppingList

@Database(
    entities = [Recipe::class, Ingredient::class, RecipeStep::class, ShoppingList::class, ShoppingItem::class],
    version = 3,
    exportSchema = false
)
abstract class RecipeDatabase: RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun stepDao(): StepDao
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun shoppingItemDao(): ShoppingItemDao
}