package com.mort.przepisownia.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "shopping_lists")
data class ShoppingList(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "list_name")
    val name: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_edited")
    val lastEdited: Long? = null
)

data class ListWithItems(
    @Embedded val shoppingList: ShoppingList,

    @Relation(
        parentColumn = "id",
        entityColumn = "listId"
    )
    val shoppingItems: List<ShoppingItem>
)