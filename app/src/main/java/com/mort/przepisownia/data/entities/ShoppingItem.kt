package com.mort.przepisownia.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "item_name")
    val name: String = "",
    @ColumnInfo(name = "item_is_checked")
    val isChecked: Boolean = false
)