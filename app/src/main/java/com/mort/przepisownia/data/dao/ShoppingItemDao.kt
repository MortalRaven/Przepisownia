package com.mort.przepisownia.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mort.przepisownia.data.entities.Recipe
import com.mort.przepisownia.data.entities.ShoppingItem
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ShoppingItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addItem(itemEntity: ShoppingItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addItems(items: List<ShoppingItem>)

    @Query("SELECT * FROM 'shopping_items' WHERE id = :id")
    abstract fun getItemById(id: Long): Flow<ShoppingItem>

    @Query("SELECT * FROM 'shopping_items' WHERE listId = :listId")
    abstract fun getListItems(listId: Long): Flow<List<ShoppingItem>>

    @Update
    abstract suspend fun updateItem(itemEntity: ShoppingItem)

    @Delete
    abstract suspend fun deleteItem(itemEntity: ShoppingItem)

    @Query("DELETE FROM 'shopping_items' WHERE listId = :listId")
    abstract suspend fun deleteItemsByListId(listId: Long)
}