package uk.ac.aber.dcs.souschefapp

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uk.ac.aber.dcs.souschefapp.database.SousChefRepository
import uk.ac.aber.dcs.souschefapp.database.UserPreferences
import uk.ac.aber.dcs.souschefapp.screens.TopAuthScreen
import uk.ac.aber.dcs.souschefapp.screens.TopHistoryScreen
import uk.ac.aber.dcs.souschefapp.screens.TopHomeScreen
import uk.ac.aber.dcs.souschefapp.screens.TopProductScreen
import uk.ac.aber.dcs.souschefapp.screens.TopProfileScreen
import uk.ac.aber.dcs.souschefapp.screens.TopRecipePageScreen
import uk.ac.aber.dcs.souschefapp.screens.TopRecipesScreen
import uk.ac.aber.dcs.souschefapp.ui.navigation.Screen
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import uk.ac.aber.dcs.souschefapp.viewmodel.AuthViewModel
import uk.ac.aber.dcs.souschefapp.viewmodel.HomeViewModel
import uk.ac.aber.dcs.souschefapp.viewmodel.IngredientViewModel
import uk.ac.aber.dcs.souschefapp.viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.viewmodel.NoteViewModel
import uk.ac.aber.dcs.souschefapp.viewmodel.ProductViewModel
import uk.ac.aber.dcs.souschefapp.viewmodel.ProfileViewModel
import uk.ac.aber.dcs.souschefapp.viewmodel.RecipeViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val userPreferences = UserPreferences(applicationContext)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Navigation(userPreferences)
                }
            }
        }
    }
}

@Composable
fun Navigation(
    userPreferences: UserPreferences,
    authViewModel: AuthViewModel = viewModel()
    ) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()

    val logViewModel: LogViewModel = viewModel()
    val recipeViewModel: RecipeViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val noteViewModel: NoteViewModel = viewModel()
    val ingredientViewModel: IngredientViewModel = viewModel()



    NavHost(navController = navController, startDestination = Screen.Auth.route){

        /* Auth */
        composable(Screen.Auth.route){
            TopAuthScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        /* Home */
        composable(Screen.Home.route){
            TopHomeScreen(
                navController,
                logViewModel,
                recipeViewModel,
                noteViewModel,
                userPreferences
            )
        }

        /* History */
        composable(Screen.History.route){
            TopHistoryScreen(
                navController,
                logViewModel,
                recipeViewModel,
                userPreferences
            )
        }

        /* Recipes */
        composable(Screen.Recipes.route){
            TopRecipesScreen(
                navController,
                recipeViewModel
            )
        }

        /* Profile */
        composable(Screen.Profile.route){
            TopProfileScreen(
                navController,
                authViewModel
            )
        }

        /* RecipePage */
        composable(
            route = Screen.RecipePage.route + "/recipeId={recipeId}",
            arguments = listOf(navArgument("recipeId") {
                type = NavType.IntType
                nullable = false
            })
        ){ entry ->
            TopRecipePageScreen(
                navController,
                recipeViewModel,
                ingredientViewModel,
                recipeId = entry.arguments?.getInt("topicId")!!
            )
        }

        /* Product */
        composable(Screen.Product.route){
            TopProductScreen(
                navController,
                productViewModel
            )
        }
    }

}
