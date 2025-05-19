package com.mort.przepisownia

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(Screen.HomeScreen.route) {
            HomeView(navController)
        }

        composable(Screen.RecipesScreen.route) {
            RecipeView(navController)
        }

        composable(Screen.AddEditScreen.route){
            AddEditRecipeView(,, navController)
        }
    }
}