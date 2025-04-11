package uk.ac.aber.dcs.souschefapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.FirebaseApp
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
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.AuthViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.NoteViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.RecipeViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val logViewModel: LogViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Navigation(
                    context = this,
                    authViewModel = authViewModel,
                    logViewModel = logViewModel,
                    recipeViewModel = recipeViewModel,
                    noteViewModel = noteViewModel
                )
            }
        }
    }
}

@Composable
fun Navigation(
    context: ComponentActivity,
    authViewModel: AuthViewModel,
    logViewModel: LogViewModel,
    recipeViewModel: RecipeViewModel,
    noteViewModel: NoteViewModel
) {
    val navController = rememberNavController()
    val user by authViewModel.user.observeAsState()

    LaunchedEffect(user){
        if (user == null){
            navController.navigate(Screen.Auth.route){
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }
    }


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
                context = context,
                navController = navController,
                authViewModel = authViewModel,
                logViewModel = logViewModel,
                recipeViewModel = recipeViewModel,
                noteViewModel = noteViewModel
            )
        }

        /* Recipes */
        composable(Screen.Recipes.route){
            TopRecipesScreen(
                context = context,
                navController = navController,
                authViewModel = authViewModel,
                recipeViewModel = recipeViewModel
            )
        }

        /* Profile */
        composable(Screen.Profile.route){
            TopProfileScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

    }

}
