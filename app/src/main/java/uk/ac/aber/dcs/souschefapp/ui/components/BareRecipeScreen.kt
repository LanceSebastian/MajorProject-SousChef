package uk.ac.aber.dcs.souschefapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme


@Composable
fun BareRecipePageScreen(
    navController: NavHostController,
    saveFunction: () -> Unit = {},
    deleteFunction: () -> Unit = {},
    moreVertFunction: () -> Unit = {},
    isEdit: Boolean = false,
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
){
    Scaffold(
        topBar = { EditTopAppBar(navController = navController, isEdit = isEdit, saveFunction = saveFunction, deleteFunction = deleteFunction, moreVertFunction = moreVertFunction) },
        content = { innerPadding -> pageContent(innerPadding) },
        bottomBar = { HomeNavigationBar(mainState = MainState.RECIPES, navController = navController) },
    )
}

@Preview(showBackground = true)
@Composable
fun BareRecipePageView(){
    val navController = rememberNavController()
    AppTheme {
        BareRecipePageScreen(
            navController = navController,
        )
    }
}