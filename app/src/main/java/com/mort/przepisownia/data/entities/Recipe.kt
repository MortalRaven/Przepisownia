package com.mort.przepisownia.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "recipe_name")
    val name: String = "",
    @ColumnInfo(name = "recipe_desc")
    val desc: String = "",
    @ColumnInfo(name = "recipe_is_fav")
    val isFavourite: Boolean = false,
    @ColumnInfo(name = "recipe_image_path")
    val imagePath: String = "",
    @ColumnInfo(name = "recipe_source")
    val link: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_viewed_at")
    val lastViewedAt: Long? = null
)


@Entity(tableName = "recipes_steps",
    foreignKeys = [ForeignKey(
        entity = Recipe::class,
        parentColumns = ["id"],
        childColumns = ["recipeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("recipeId")])
data class RecipeStep(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "recipeId")
    val recipeId: Long,
    @ColumnInfo(name = "step_number")
    val stepNumber: Int = 1,
    @ColumnInfo(name = "step_desc")
    val description: String = ""
)


data class RecipeWithDetails(
    @Embedded val recipe: Recipe,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<Ingredient>,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val steps: List<RecipeStep>
)

object DummyRecipes {
    val lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse lectus orci, elementum gravida lorem sed, blandit porta risus. Maecenas scelerisque tristique libero, a maximus odio sagittis eu. Nulla auctor purus et ante rutrum dapibus. In pellentesque ante laoreet mi iaculis tincidunt. Donec erat dolor, vehicula quis diam in, scelerisque pulvinar libero. Ut porttitor vestibulum leo et porttitor. Curabitur sit amet dictum metus. Suspendisse id hendrerit felis, nec porttitor sem. "
    val recipeList = listOf(
        Recipe(name = "Szakszuka", desc = lorem),
        Recipe(name = "Jajecznica", desc = lorem),
        Recipe(name = "Kotlety mielone", desc = lorem),
        Recipe(name = "Kremowa zupa z zieloną soczewicą", desc = lorem),
        Recipe(name = "Naleśniki", desc = lorem),
        Recipe(name = "Szakszuka", desc = lorem),
        Recipe(name = "Jajecznica", desc = lorem),
        Recipe(name = "Kotlety mielone", desc = lorem),
        Recipe(name = "Kremowa zupa z zieloną soczewicą", desc = lorem),
        Recipe(name = "Naleśniki", desc = lorem)
    )
}