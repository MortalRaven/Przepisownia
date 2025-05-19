package com.mort.przepisownia

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")
    object AddEditScreen: Screen("add_edit_recipe_screen")
    object RecipesScreen: Screen("recipes_screen")
    object ShoppingScreen: Screen("shopping_list_screen")
    object SettingsScreen: Screen("settings_screen")
}