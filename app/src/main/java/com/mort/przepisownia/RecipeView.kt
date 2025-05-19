package com.mort.przepisownia

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mort.przepisownia.data.entities.DummyRecipes
import com.mort.przepisownia.data.entities.Recipe

@Composable
fun RecipeView(
    navController: NavController
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppBarView(title = "Przepisy") {
                navController.navigateUp()
            }
        },
        //TODO Może lepiej jednak zrobić dodawanie na AppBarze
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(all = 20.dp),
                contentColor = Color.White,
                containerColor = colorResource(R.color.main_theme),
                shape = CircleShape,
                onClick = {/*TODO*/}
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        },
        backgroundColor = colorResource(R.color.background_main)
    ) {
        val recipeList = DummyRecipes.recipeList /*TODO*/

        //Siatka zawierająca przepisy
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize().padding(it).padding(top = 16.dp, bottom = 16.dp),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(recipeList) {
                    recipe -> RecipeItem(recipe = recipe) { }
            }
        }
    }
}

//Karta z informacją o przepisie
@Composable
fun RecipeItem(recipe: Recipe, onClick: () -> Unit) {
    // TODO Pobawić się jeszcze rozmiarami kart
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp).padding(top = 8.dp, start = 8.dp, end = 8.dp).clickable { onClick() },
        elevation = 10.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().background(Color.DarkGray)) {
                Icon(modifier = Modifier.fillMaxWidth().padding(16.dp),
                    imageVector = Icons.Default.Info,
                    contentDescription = "",
                    tint = Color.Gray)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = recipe.name, fontSize = 14.sp, lineHeight = 16.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = recipe.desc, fontSize = 12.sp, lineHeight = 14.sp,
                style = TextStyle.Default.copy(
                    lineBreak = LineBreak.Paragraph,
                    hyphens = Hyphens.Auto
                ),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}