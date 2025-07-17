package com.mort.przepisownia.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mort.przepisownia.data.repository.RecipeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Graph {
    private lateinit var database: RecipeDatabase

    val recipeRepository by lazy {
        RecipeRepository(
            recipeDao = database.recipeDao(),
            ingredientsDao = database.ingredientDao(),
            stepsDao = database.stepDao()
        )
    }

    private val MIGRATION_1_2 = object: Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE recipes ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE recipes ADD COLUMN last_viewed_at INTEGER")
        }
    }

    fun provide(context: Context) {
        database =
            Room.databaseBuilder(context, RecipeDatabase::class.java, "recipeList.db")
                .addMigrations(MIGRATION_1_2)
                .build()

        CoroutineScope(Dispatchers.IO).launch {
            recipeRepository.updateMissingCreatedAt()
        }
    }
}