package uk.ac.aber.dcs.souschefapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.ui.components.BareMainScreen
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import uk.ac.aber.dcs.souschefapp.viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.viewmodel.ProductViewModel

@Composable
fun TopProductScreen(
    navController: NavHostController,
    productViewModel: ProductViewModel
){
    ProductScreen(navController)
}

@Composable
fun ProductScreen(navController: NavHostController){
    BareMainScreen(
        navController = navController,
        mainState = MainState.RECIPES
    ){ innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .fillMaxSize()
            ) {
                item {
                    Text(text = "Recipes Go Here.")
                }

            }
        }
    }
}

@Preview
@Composable
fun ProductScreenPreview(){
    val navController = rememberNavController()
    AppTheme { ProductScreen(navController) }
}