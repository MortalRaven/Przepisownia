package com.mort.przepisownia.navigation

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")
    object RecipeScreen: Screen("recipe_screen")
    object AddEditScreen: Screen("add_edit_recipe_screen")
    object RecipesScreen: Screen("recipes_screen")
    object AddEditListScreen: Screen("add_edit_list_screen")
    object ShoppingScreen: Screen("shopping_list_screen")
    object ShoppingDetailsScreen: Screen("shopping_details_screen")
    object SettingsScreen: Screen("settings_screen")
}