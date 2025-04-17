package uk.ac.aber.dcs.souschefapp.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.firebase.EditMode
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme


@Composable
fun BareRecipePageScreen(
    navController: NavHostController,
    editMode: EditMode = EditMode.View,
    editFunction: () -> Unit,
    backFunction: () -> Unit,
    saveFunction: () -> Unit,
    crossFunction: () -> Unit,
    isBottomBar: Boolean = true,
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
){
    Scaffold(
        topBar = {
            EditTopAppBar(
                navController = navController,
                editMode = editMode,
                editFunction = editFunction,
                backFunction = backFunction,
                saveFunction = saveFunction,
                crossFunction = crossFunction,
            ) },
        content = { innerPadding -> pageContent(innerPadding) },
        bottomBar = { if (isBottomBar) HomeNavigationBar(mainState = MainState.RECIPES, navController = navController) },
    )
}

@Preview(showBackground = true)
@Composable
fun BareRecipePageView(){
    val navController = rememberNavController()
    AppTheme {
        BareRecipePageScreen(
            navController = navController,
            editFunction = {},
            backFunction = {},
            saveFunction = {},
            crossFunction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditBareRecipePageView(){
    val navController = rememberNavController()
    AppTheme {
        BareRecipePageScreen(
            navController = navController,
            editFunction = {},
            backFunction = {},
            saveFunction = {},
            crossFunction = {},
        )
    }
}