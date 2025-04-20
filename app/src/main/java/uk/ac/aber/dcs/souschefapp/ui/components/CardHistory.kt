package uk.ac.aber.dcs.souschefapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import uk.ac.aber.dcs.souschefapp.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Timestamp
import uk.ac.aber.dcs.souschefapp.firebase.Log
import uk.ac.aber.dcs.souschefapp.firebase.Product
import uk.ac.aber.dcs.souschefapp.firebase.Recipe
import uk.ac.aber.dcs.souschefapp.firebase.SelectMode
import uk.ac.aber.dcs.souschefapp.ui.navigation.Screen
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun CardHistory(
    navController: NavHostController,
    log: Log,
    recipes: List<Recipe> = emptyList(),
    products: List<Product> = emptyList(),
    rating: Int = 0,
    selectRecipe: (String) -> Unit,
    selectProduct: (String) -> Unit,
    selectMode: (SelectMode) -> Unit,
    setLog: (Long) -> Unit,
) {
    /*      Variables       */
    val satisfied = ImageVector.vectorResource(id = R.drawable.satisfied)
    val happy = ImageVector.vectorResource(id = R.drawable.happy)
    val neutral = ImageVector.vectorResource(id = R.drawable.neutral)
    val unhappy = ImageVector.vectorResource(id = R.drawable.unhappy)
    val disappointed = ImageVector.vectorResource(id = R.drawable.disappointed)
    val unknown = ImageVector.vectorResource(id = R.drawable.unknown)
    val arrowDropUp = ImageVector.vectorResource(id = R.drawable.arrow_drop_up)

    val menu: List<MenuItem> = recipes.map { MenuItem.RecipeItem(it) } +
            products.map { MenuItem.ProductItem(it) }

    val localDateTime = Instant
        .ofEpochSecond(log.createdAt.seconds,log.createdAt.nanoseconds.toLong())
        .atZone(ZoneId.systemDefault())

    val topFormat by remember {
        derivedStateOf{
            DateTimeFormatter
                .ofPattern("yyyy EEEE")
                .format(localDateTime)
        }
    }
    val mainFormat by remember {
        derivedStateOf{
            DateTimeFormatter
                .ofPattern("dd MMM")
                .format(localDateTime)
        }
    }

    var isExpanded by remember { mutableStateOf(false) }

    /*      UI       */
    Card(
        colors = CardDefaults.cardColors( containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        modifier = Modifier
            .width(360.dp)
            .height(if (!isExpanded) 120.dp else 350.dp)
            .clickable {  isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column (
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = topFormat,
                    style = MaterialTheme.typography.labelLarge
                )
                Icon(
                    imageVector = if (!isExpanded) Icons.Default.ArrowDropDown else arrowDropUp,
                    contentDescription = null
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = mainFormat,
                    style = MaterialTheme.typography.headlineLarge
                )
                Icon(
                    imageVector = when (rating) {
                        -2 -> disappointed
                        -1 -> unhappy
                        0 -> neutral
                        1 -> happy
                        2 -> satisfied
                        else -> unknown
                    },
                    contentDescription = null,
                    tint = Color(
                            when (rating) {
                                -2 -> 0xFFF33B3B
                                -1 -> 0xFFFFB001
                                0 -> 0xFFCACA04
                                1 -> 0xFF02C801
                                2 -> 0xFF0095FF
                                else -> 0xFFDAE9B6
                            }
                        ),
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = CircleShape
                        )
                )

            }

            /*      Recipes Content       */
            if (menu.isEmpty()){
                Text(
                    text = "Menu: n/a",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                val text = if (!isExpanded) {
                    menu.joinToString(", "){
                        item -> when (item) {
                            is MenuItem.RecipeItem -> item.recipe.name
                            is MenuItem.ProductItem -> item.product.name
                        }
                    }
                } else ""
                Text(
                    text = "Menu: $text",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            /*      Expanded Content       */
            if (isExpanded) {
                if (menu.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth()
                    ) {
                        items(menu) { item ->
                            when (item){
                                is MenuItem.RecipeItem -> {
                                    CardRecipe(
                                        text = item.recipe.name,
                                        onClick = {     // Navigate to Recipe
                                            selectRecipe(item.recipe.recipeId)
                                            navController.navigate( Screen.RecipePage.route )
                                        }
                                    )
                                }

                                is MenuItem.ProductItem -> {
                                    CardRecipe(
                                        text = item.product.name,
                                        onClick = {     // Navigate to Recipe
                                            selectProduct(item.product.productId)
                                            navController.navigate( Screen.RecipePage.route )
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth()
                    ){
                        Text("This is looking empty...")
                        Button(
                            onClick = {
                                selectMode(SelectMode.Select)
                                setLog(log.logId.toLong())
                                navController.navigate(Screen.Recipes.route)
                            }
                        ){
                            Text("Add recipe?")
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {     // Navigate to Log
                            setLog(localDateTime.toInstant().toEpochMilli())
                            navController.navigate(Screen.Home.route)
                        }
                    ) {
                        Text("View Log")
                    }
                }
            }
        }
    }
}

sealed class MenuItem {
    data class RecipeItem(val recipe: Recipe) : MenuItem()
    data class ProductItem(val product: Product) : MenuItem()
}

@Preview
@Composable
fun EmptyCardHistoryView(){
    val navController = rememberNavController()
    AppTheme {
        CardHistory(
            navController = navController,
            log = Log(),
            rating = 2,
            recipes = emptyList(),
            selectRecipe = {},
            setLog = {},
            selectMode = {},
            selectProduct = {}
        )
    }

}

@Preview
@Composable
fun CardHistoryView(){
    val navController = rememberNavController()
    val mockRecipes = mutableListOf(
        Recipe("1", "English Breakfast", ""),
        Recipe("2", "Chicken Sandwich", ""),
        Recipe("3", "Spaghetti Bolognese", ""),
        Recipe("4", "Vegetable Stir Fry", ""),
        Recipe("5", "Beef Tacos", ""),
        Recipe("6", "Margherita Pizza", "")
    )
    AppTheme {
        CardHistory(
            navController = navController,
            rating = 2,
            log = Log(),
            recipes = mockRecipes,
            selectRecipe = {},
            setLog = {},
            selectMode = {},
            selectProduct = {}
        )
    }

}