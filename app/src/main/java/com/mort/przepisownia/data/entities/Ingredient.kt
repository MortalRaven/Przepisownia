package com.mort.przepisownia.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mort.przepisownia.utils.UnitType

@Entity(tableName = "ingredients",
    foreignKeys = [ForeignKey(
        entity = Recipe::class,
        parentColumns = ["id"],
        childColumns = ["recipeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("recipeId")])
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "recipeId")
    val recipeId: Long,
    @ColumnInfo(name = "ingredient_name")
    val name: String = "",
    @ColumnInfo(name = "ingredient_quantity")
    val quantity: Float? = null,
    @ColumnInfo(name = "ingredient_unit")
    val unit: UnitType? = null
)


data class IngredientInput(
    var name: String = "",
    var quantity: Float? = null,
    var unit: UnitType? = null
)