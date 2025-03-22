package uk.ac.aber.dcs.souschefapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.database.UserPreferences
import uk.ac.aber.dcs.souschefapp.database.models.Log
import uk.ac.aber.dcs.souschefapp.database.models.Note
import uk.ac.aber.dcs.souschefapp.database.models.Recipe
import uk.ac.aber.dcs.souschefapp.ui.components.BareMainScreen
import uk.ac.aber.dcs.souschefapp.ui.components.CardHistory
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import uk.ac.aber.dcs.souschefapp.viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.viewmodel.RecipeViewModel
import java.time.Instant
import java.time.ZoneId

@Composable
fun TopHistoryScreen(
    navController: NavHostController,
    logViewModel: LogViewModel,
    recipeViewModel: RecipeViewModel,
    userPreferences: UserPreferences
){
    val accountId by userPreferences.accountId.observeAsState()
    val logs by logViewModel.getAllLogsFromAccount(accountId!!).observeAsState(listOf())
    val recipes by recipeViewModel.getAllRecipes().observeAsState(listOf())
    HistoryScreen(
        navController,
        logs,
        recipes
    )
}

@Composable
fun HistoryScreen(
    navController: NavHostController,
    logs: List<Log>,
    recipes: List<Recipe>
){
    BareMainScreen(
        navController = navController,
        mainState = MainState.HISTORY
    ){ innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                logs.forEach { log ->
                    val logRecipes = recipes.filter { log.recipeIdList.contains(it.recipeId) }
                    item {
                        CardHistory(
                            navController = navController,
                            recipes = logRecipes,
                            date = Instant.ofEpochMilli(log.date)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

            }
        }
    }
}

@Preview
@Composable
fun EmptyHistoryScreenPreview(){
    val navController = rememberNavController()
    AppTheme {
        HistoryScreen(
            navController,
            logs = emptyList(),
            recipes = emptyList()
        )
    }
}

@Preview
@Composable
fun HistoryScreenPreview(){
    val navController = rememberNavController()

    // Mock ViewModel or Data
    val sampleLogs = listOf(
        Log(
            logId = 1,
            accountOwnerId = 0,
            date = System.currentTimeMillis(),
            rating = 2,
            recipeIdList = listOf(1, 2),
            productIdList = listOf(101, 202),
            note = "Tried a new recipe, turned out great!"
        ),
        Log(
            logId = 2,
            accountOwnerId = 0,
            date = System.currentTimeMillis() - 86_400_000, // 1 day ago
            rating = -1,
            recipeIdList = listOf(3),
            productIdList = listOf(303, 404),
            note = "Didn't like the taste, will try adjusting ingredients."
        ),
        Log(
            logId = 3,
            accountOwnerId = 0,
            date = System.currentTimeMillis() - 172_800_000, // 2 days ago
            rating = 1,
            recipeIdList = listOf(6, 5),
            productIdList = listOf(505),
            note = "A decent meal, but could use more seasoning."
        ),
        Log(
            logId = 4,
            accountOwnerId = 0,
            date = System.currentTimeMillis() - 259_200_000, // 3 days ago
            rating = 0,
            recipeIdList = emptyList(),
            productIdList = listOf(606, 707),
            note = "Tried a new product, unsure about it yet."
        ),
        Log(
            logId = 5,
            accountOwnerId = 0,
            date = System.currentTimeMillis() - 345_600_000, // 4 days ago
            rating = -2,
            recipeIdList = listOf(4),
            productIdList = listOf(808, 909),
            note = "Had a bad experience with this recipe."
        )
    )

    val sampleRecipes = mutableListOf(
        Recipe(1, "English Breakfast", ""),
        Recipe(2, "Chicken Sandwich", ""),
        Recipe(3, "Spaghetti Bolognese", ""),
        Recipe(4, "Vegetable Stir Fry", ""),
        Recipe(5, "Beef Tacos", ""),
        Recipe(6, "Margherita Pizza", "")
    )

    AppTheme {
        HistoryScreen(
            navController,
            logs = sampleLogs,
            recipes = sampleRecipes
        )
    }
}