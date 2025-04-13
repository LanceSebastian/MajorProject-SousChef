package uk.ac.aber.dcs.souschefapp.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Timestamp
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.firebase.Log
import uk.ac.aber.dcs.souschefapp.firebase.Recipe
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.AuthViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.RecipeViewModel
import uk.ac.aber.dcs.souschefapp.ui.components.BareMainScreen
import uk.ac.aber.dcs.souschefapp.ui.components.CardHistory
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

import java.time.Instant
import java.time.ZoneId
import java.util.Date

@Composable
fun TopHistoryScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    logViewModel: LogViewModel,
    recipeViewModel: RecipeViewModel
){

    val user by authViewModel.user.observeAsState()
    val userId = user?.uid

    val logs by logViewModel.logs.observeAsState(emptyList())
    val recipes by recipeViewModel.userRecipes.observeAsState(emptyList())

    // Listen for logs in real-time when the user exists
    DisposableEffect(userId) {
        if(userId != null){
            logViewModel.readLogs(userId)
        }

        onDispose {
            logViewModel.stopListening()
        }
    }

    HistoryScreen(
        navController = navController,
        logs = logs,
        recipes = recipes,
        selectRecipe = { recipeId ->
            recipeViewModel.selectRecipe(recipeId)
        },
        setLog = { dateMillis ->
            logViewModel.readLogFromDate(dateMillis)
        }
    )
}

@Composable
fun HistoryScreen(
    navController: NavHostController,
    logs: List<Log>,
    recipes: List<Recipe>,
    selectRecipe: (String) -> Unit,
    setLog: (Long) -> Unit,
){
    val sortedLogs = logs.sortedByDescending { it.createdAt }
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
                sortedLogs.forEach { log ->
                    val logRecipes = recipes.filter { log.recipeIdList.contains(it.recipeId) }
                    item {
                        CardHistory(
                            navController = navController,
                            recipes = logRecipes,
                            rating = log.rating,
                            date = log.createdAt,
                            selectRecipe = { recipeId ->
                                selectRecipe(recipeId)
                            },
                            setLog = { dateMillis ->
                                setLog(dateMillis)
                            }
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
            recipes = emptyList(),
            selectRecipe = {},
            setLog = {}
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
            logId = "1",
            createdAt = Timestamp(Date(System.currentTimeMillis())),
            createdBy = "0",
            rating = 2,
            recipeIdList = listOf("1", "2"),
            productIdList = listOf("101", "202"),
            note = "Tried a new recipe, turned out great!"
        ),
        Log(
            logId = "2",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 86_400_000)), // 1 day ago
            createdBy = "0",
            rating = -1,
            recipeIdList = listOf("3"),
            productIdList = listOf("303", "404"),
            note = "Didn't like the taste, will try adjusting ingredients."
        ),
        Log(
            logId = "3",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 172_800_000)), // 2 days ago
            createdBy = "0",
            rating = 1,
            recipeIdList = listOf("6", "5"),
            productIdList = listOf("505"),
            note = "A decent meal, but could use more seasoning."
        ),
        Log(
            logId = "5",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 345_600_000)), // 4 days ago
            createdBy = "0",
            rating = -2,
            recipeIdList = listOf("4"),
            productIdList = listOf("808", "909"),
            note = "Had a bad experience with this recipe."
        ),
        Log(
            logId = "4",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 259_200_000)), // 3 days ago
            createdBy = "0",
            rating = 0,
            recipeIdList = emptyList(),
            productIdList = listOf("606", "707"),
            note = "Tried a new product, unsure about it yet."
        ),
    )

    val sampleRecipes = mutableListOf(
        Recipe("1", "English Breakfast", ""),
        Recipe("2", "Chicken Sandwich", ""),
        Recipe("3", "Spaghetti Bolognese", ""),
        Recipe("4", "Vegetable Stir Fry", ""),
        Recipe("5", "Beef Tacos", ""),
        Recipe("6", "Margherita Pizza", "")
    )

    AppTheme {
        HistoryScreen(
            navController,
            logs = sampleLogs,
            recipes = sampleRecipes,
            selectRecipe = {},
            setLog = {}
        )
    }
}