package com.mort.przepisownia.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mort.przepisownia.ui.screens.HomeView
import com.mort.przepisownia.ui.screens.recipe.AddEditRecipeView
import com.mort.przepisownia.ui.screens.recipe.RecipeDetailsView
import com.mort.przepisownia.ui.screens.recipe.RecipeEditMode
import com.mort.przepisownia.ui.screens.recipe.RecipeListView

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(Screen.HomeScreen.route) {
            HomeView(navController)
        }

        composable(Screen.RecipesScreen.route) {
            RecipeListView(navController)
        }

        composable(
            Screen.RecipeScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = 0L
                    nullable = false
                }
            )
        ){entry ->
            val id = entry.arguments!!.getLong("id")
            RecipeDetailsView(
                id = id,
                navController = navController
            )
        }

        composable(
            Screen.AddEditScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id"){
                    type = NavType.LongType
                    defaultValue = 0L
                    nullable = false
                }
            )
        ){entry ->
            val id = if (entry.arguments != null) entry.arguments!!.getLong("id") else 0L
            AddEditRecipeView(
                id = id,
                mode = if (id == 0L) RecipeEditMode.ADD else RecipeEditMode.EDIT,
                navController = navController
            )
        }
    }
}