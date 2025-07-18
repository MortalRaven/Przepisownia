package com.mort.przepisownia.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mort.przepisownia.data.entities.ListWithItems
import com.mort.przepisownia.data.entities.ShoppingList
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ShoppingListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addList(listEntity: ShoppingList): Long

    @Query("SELECT * FROM 'shopping_lists'")
    abstract fun getAllLists(): Flow<List<ShoppingList>>

    @Update
    abstract suspend fun updateList(listEntity: ShoppingList)

    @Delete
    abstract suspend fun deleteList(listEntity: ShoppingList)

    @Query("SELECT * FROM 'shopping_lists' WHERE id = :id")
    abstract fun getListByID(id: Long): Flow<ShoppingList>

    @Transaction
    @Query("SELECT * FROM 'shopping_lists' WHERE id = :listId")
    abstract fun getListItems(listId: Long): Flow<ListWithItems>
}