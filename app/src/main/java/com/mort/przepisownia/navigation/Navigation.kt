package com.mort.przepisownia.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mort.przepisownia.ui.common.EditMode
import com.mort.przepisownia.ui.screens.HomeView
import com.mort.przepisownia.ui.screens.recipe.AddEditRecipeView
import com.mort.przepisownia.ui.screens.recipe.RecipeDetailsView
import com.mort.przepisownia.ui.screens.recipe.RecipeListView
import com.mort.przepisownia.ui.screens.shopping.AddEditListScreen
import com.mort.przepisownia.ui.screens.shopping.ShoppingDetailsView
import com.mort.przepisownia.ui.screens.shopping.ShoppingListView
import com.mort.przepisownia.ui.screens.shopping.ShoppingViewModel

@Composable
fun Navigation(
    listViewModel: ShoppingViewModel = viewModel(),
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
                mode = if (id == 0L) EditMode.ADD else EditMode.EDIT,
                navController = navController
            )
        }

        composable(Screen.ShoppingScreen.route) {
            ShoppingListView(navController, listViewModel)
        }

        composable(
            Screen.AddEditListScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id"){
                    type = NavType.LongType
                    defaultValue = 0L
                    nullable = false
                }
            )
        ){entry ->
            val id = if (entry.arguments != null) entry.arguments!!.getLong("id") else 0L
            AddEditListScreen(
                id = id,
                mode = if (id == 0L) EditMode.ADD else EditMode.EDIT,
                navController = navController
            )
        }

        composable(
            Screen.ShoppingDetailsScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = 0L
                    nullable = false
                }
            )
        ){entry ->
            val id = entry.arguments!!.getLong("id")
            ShoppingDetailsView(
                id = id,
                navController = navController
            )
        }
    }
}