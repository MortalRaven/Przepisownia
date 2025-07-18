package com.mort.przepisownia.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items",
    foreignKeys = [ForeignKey(
        entity = ShoppingList::class,
        parentColumns = ["id"],
        childColumns = ["listId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("listId")])
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "listId")
    val listId: Long,
    @ColumnInfo(name = "item_name")
    val name: String = "",
    @ColumnInfo(name = "item_quantity")
    val quantity: String = "",
    @ColumnInfo(name = "item_unit")
    val unit: String = "",
    @ColumnInfo(name = "item_is_checked")
    val isChecked: Boolean = false
)