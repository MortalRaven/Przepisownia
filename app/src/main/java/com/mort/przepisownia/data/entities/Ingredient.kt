package com.mort.przepisownia.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val quantity: String = "",
    @ColumnInfo(name = "ingredient_unit")
    val unit: String = ""
)

data class MeasurementUnits(
    val tablespoons: List<String> = listOf("łyżeczka", "łyżeczki",),
    val spoons: List<String> = listOf("łyżka", "łyżki"),
    val pieces: String = "szt.",
    val weight: List<String> = listOf("g", "dag", "kg"),
    val liquids: List<String> = listOf("ml", "l")
)

data class IngredientInput(
    var name: String = "",
    var quantity: String = "",
    var unit: String = ""
)