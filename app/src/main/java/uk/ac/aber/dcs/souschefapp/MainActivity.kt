package uk.ac.aber.dcs.souschefapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import uk.ac.aber.dcs.souschefapp.room_viewmodel.AuthViewModel
import uk.ac.aber.dcs.souschefapp.room_viewmodel.HomeViewModel
import uk.ac.aber.dcs.souschefapp.room_viewmodel.IngredientViewModel
import uk.ac.aber.dcs.souschefapp.room_viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.room_viewmodel.NoteViewModel
import uk.ac.aber.dcs.souschefapp.room_viewmodel.ProductViewModel
import uk.ac.aber.dcs.souschefapp.room_viewmodel.ProfileViewModel
import uk.ac.aber.dcs.souschefapp.room_viewmodel.RecipeViewModel

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
                type = NavType.StringType
                nullable = false
            })
        ){ entry ->
            TopRecipePageScreen(
                navController = navController,
                recipeViewModel = recipeViewModel,
                ingredientViewModel = ingredientViewModel,
                recipeId = entry.arguments?.getString("recipeId")!!.toInt()
            )
        }

        /* Product */
        composable(
            route = Screen.Product.route + "/productId={productId}" + "/logDate={logDate}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("logDate"){
                    type = NavType.StringType
                    nullable = false
                }
                )
        ){ entry ->
            TopProductScreen(
                navController = navController,
                productViewModel = productViewModel,
                logViewModel = logViewModel,
                userPreferences = userPreferences,
                productId = entry.arguments?.getString("productId")!!.toInt(),
                logDate = entry.arguments?.getString("logDate")!!.toLong()

            )
        }
    }

}
