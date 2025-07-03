package com.mort.przepisownia

import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mort.przepisownia.data.entities.Recipe
import java.io.File

@Composable
fun RecipeListView(
    navController: NavController,
    viewModel: RecipeViewModel
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppBarView(title = "Przepisy") {
                navController.navigateUp()
            }
        },
        //TODO Może lepiej jednak zrobić dodawanie na AppBarze
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(all = 20.dp),
                shape = CircleShape,
                onClick = {/*TODO*/}
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        }
    ) {
        //val recipeList = DummyRecipes.recipeList
        val recipeList = viewModel.getAllRecipes.collectAsState(initial = listOf())

        //Siatka zawierająca przepisy
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize().padding(it).padding(top = 16.dp, bottom = 16.dp),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(recipeList.value, key = {recipe -> recipe.id}
            ) {recipe ->
                RecipeItem(context = LocalContext.current, recipe = recipe) {
                    val id = recipe.id
                    navController.navigate(Screen.RecipeScreen.route + "/$id")
                }
            }
        }
    }
}

//Szablon karty z informacją o przepisie
@Composable
fun RecipeItem(context: Context, recipe: Recipe, onClick: () -> Unit) {
    // TODO Pobawić się jeszcze rozmiarami kart
    Card(
        modifier = Modifier.fillMaxWidth()
            .height(240.dp)
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .clickable { onClick() }
            .shadow(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                val imageFile = recipe.imagePath.takeIf { it.isNotBlank() }?.let {
                    File(context.filesDir, it)
                }

                if (imageFile != null && imageFile.exists()) {
                    AsyncImage(
                        model = imageFile,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.vegetables_placeholder),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )
                }
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