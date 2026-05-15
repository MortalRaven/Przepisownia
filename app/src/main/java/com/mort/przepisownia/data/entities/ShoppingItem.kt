package com.mort.przepisownia.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mort.przepisownia.utils.UnitType

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
    val quantity: Float? = null,
    @ColumnInfo(name = "item_unit")
    val unit: UnitType? = null,
    @ColumnInfo(name = "item_is_checked")
    val isChecked: Boolean = false
)