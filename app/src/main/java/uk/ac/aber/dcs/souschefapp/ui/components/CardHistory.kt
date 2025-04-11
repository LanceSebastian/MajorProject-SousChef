package uk.ac.aber.dcs.souschefapp.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import uk.ac.aber.dcs.souschefapp.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Timestamp
import uk.ac.aber.dcs.souschefapp.firebase.Recipe
import uk.ac.aber.dcs.souschefapp.ui.navigation.Screen
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CardHistory(
    navController: NavHostController,
    date: Timestamp = Timestamp.now(),
    recipes: List<Recipe> = emptyList(),
    rating: Int = 0,
) {
    val satisfied = ImageVector.vectorResource(id = R.drawable.satisfied)
    val happy = ImageVector.vectorResource(id = R.drawable.happy)
    val neutral = ImageVector.vectorResource(id = R.drawable.neutral)
    val unhappy = ImageVector.vectorResource(id = R.drawable.unhappy)
    val disappointed = ImageVector.vectorResource(id = R.drawable.disappointed)
    val unknown = ImageVector.vectorResource(id = R.drawable.unknown)

    val localDateTime = Instant
        .ofEpochSecond(date.seconds, date.nanoseconds.toLong())
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

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        modifier = Modifier
            .width(360.dp)
            .height(if (!isExpanded) 120.dp else 300.dp)
            .clickable { if (recipes.isNotEmpty()) isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column (
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
                Column {

                    Text(
                        text = topFormat,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = mainFormat,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
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

            // Recipe Names Here...
            if (recipes.isEmpty()){
                Text(
                    text = "Recipes: n/a",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text(
                    text = "Recipes: ${recipes.joinToString(", "){it.name}}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            // Expansion Content Here...
            if (isExpanded) {
                LazyRow(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .height(150.dp)
                        .fillMaxWidth()
                ) {
                    recipes.forEach { recipe ->
                        item {
                            CardRecipe(
                                text = recipe.name,
                                onClick = {
                                    navController.navigate(
                                        Screen.RecipePage.route + "/recipeId=${recipe.recipeId}"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
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
            recipes = mockRecipes
        )
    }

}