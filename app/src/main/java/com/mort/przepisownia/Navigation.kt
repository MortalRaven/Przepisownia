package com.mort.przepisownia

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun Navigation(
    viewModel: RecipeViewModel = viewModel(),
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
            RecipeListView(navController, viewModel)
        }

        composable(Screen.RecipeScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = 0L
                    nullable = false
                }
            )
        ){entry ->
            val id = entry.arguments!!.getLong("id")
            RecipeView(
                id = id,
                viewModel = viewModel,
                navController = navController
            )
        }

        composable(Screen.AddEditScreen.route + "/{id}",
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
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}