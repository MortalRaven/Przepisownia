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

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE recipes ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE recipes ADD COLUMN last_viewed_at INTEGER")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS shopping_lists")
            db.execSQL(
                """CREATE TABLE IF NOT EXISTS shopping_lists (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                list_name TEXT NOT NULL,
                created_at INTEGER NOT NULL DEFAULT 0,
                last_edited INTEGER
                )
                """.trimIndent()
            )
            db.execSQL("DROP TABLE IF EXISTS shopping_items")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS shopping_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                listId INTEGER NOT NULL,
                item_name TEXT NOT NULL,
                item_quantity TEXT NOT NULL,
                item_unit TEXT NOT NULL,
                item_is_checked INTEGER NOT NULL,
                FOREIGN KEY(listId) REFERENCES shopping_lists(id) ON DELETE CASCADE
                )
            """.trimIndent()
            )
            db.execSQL("CREATE INDEX index_shopping_items_listId ON shopping_items(listId)")
        }
    }

    fun provide(context: Context) {
        database =
            Room.databaseBuilder(context, RecipeDatabase::class.java, "recipeList.db")
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build()

        CoroutineScope(Dispatchers.IO).launch {
            recipeRepository.updateMissingCreatedAt()
        }
    }
}