package com.mort.przepisownia.data.repository

import com.mort.przepisownia.data.dao.ShoppingItemDao
import com.mort.przepisownia.data.dao.ShoppingListDao
import com.mort.przepisownia.data.entities.IngredientInput
import com.mort.przepisownia.data.entities.ListWithItems
import com.mort.przepisownia.data.entities.ShoppingItem
import com.mort.przepisownia.data.entities.ShoppingList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ShoppingRepository(
    private val shoppingListDao: ShoppingListDao,
    private val shoppingItemDao: ShoppingItemDao
) {

    suspend fun addFullList(
        list: ShoppingList,
        itemsInput: List<IngredientInput>
    ) {
        val listId = shoppingListDao.addList(list)
        val itemsWithId = itemsInput.map {
            ShoppingItem(
                listId = listId,
                name = it.name,
                quantity = it.quantity,
                unit = it.unit
            )
        }

        shoppingItemDao.addItems(itemsWithId)
    }

    fun getListByID(id: Long): Flow<ShoppingList> {
        return shoppingListDao.getListByID(id)
    }

    fun getLists(): Flow<List<ShoppingList>> = shoppingListDao.getAllLists()

    fun getListItems(listId: Long): Flow<ListWithItems> =
        shoppingListDao.getListItems(listId)

    suspend fun deleteList(list: ShoppingList) {
        shoppingListDao.deleteList(list)
        shoppingItemDao.deleteItemsByListId(list.id)
    }

    suspend fun updateRecipe(
        list: ShoppingList,
        itemsInput: List<IngredientInput>
    ) {
        shoppingListDao.updateList(list)
        shoppingItemDao.deleteItemsByListId(list.id)
        shoppingItemDao.addItems(
            itemsInput.map {
                ShoppingItem(
                    listId = list.id,
                    name = it.name,
                    quantity = it.quantity,
                    unit = it.unit
                )
            }
        )
    }

    suspend fun updateItemChecked(id: Long) {
        val item = shoppingItemDao.getItemById(id).first()
        val updatedItem = item.copy(isChecked = !item.isChecked)
        shoppingItemDao.updateItem(updatedItem)
    }
}