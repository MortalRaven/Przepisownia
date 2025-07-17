package com.mort.przepisownia.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mort.przepisownia.R
import com.mort.przepisownia.navigation.Screen

@Composable
fun HomeView(
    navController: NavController,
) {

    Scaffold {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item { Spacer(modifier = Modifier.height(24.dp)) }

            val itemsList = MenuList.menuList
            items(itemsList) { menuItem ->
                MenuItemCard(menuItem = menuItem) {
                    val screen = menuItem.screen
                    if (screen == Screen.AddEditScreen) {
                        navController.navigate(screen.route + "/0L")
                    } else {
                    navController.navigate(screen.route)
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp, max = 100.dp)
            .padding(start = 24.dp, end = 24.dp)
            .clickable { onClick() }
            .shadow(10.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(3f),
                text = menuItem.title,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Start
            )
            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .weight(1f),
                painter = painterResource(menuItem.icon),
                contentDescription = "",
            )
        }
    }
}

data class MenuItem(
    val title: String,
    val icon: Int,
    val screen: Screen,
)

object MenuList {
    val menuList = listOf(
        MenuItem("Dodaj przepis", R.drawable.baseline_add_24, Screen.AddEditScreen),
        MenuItem("Lista Przepisów", R.drawable.outline_menu_book_24, Screen.RecipesScreen),
        MenuItem("Lista Zakupów", R.drawable.outline_shopping_cart_24, Screen.RecipesScreen),
        MenuItem("Ustawienia", R.drawable.outline_settings_24, Screen.RecipesScreen)
    )
}