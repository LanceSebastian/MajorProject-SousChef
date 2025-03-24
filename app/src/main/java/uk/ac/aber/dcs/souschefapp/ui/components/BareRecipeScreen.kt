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
    editFunction: () -> Unit,
    backFunction: () -> Unit,
    saveFunction: () -> Unit,
    deleteFunction: () -> Unit,
    isEdit: Boolean = false,
    isBottomBar: Boolean = true,
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
){
    Scaffold(
        topBar = {
            EditTopAppBar(
                navController = navController,
                isEdit = isEdit,
                editFunction = editFunction,
                backFunction = backFunction,
                saveFunction = saveFunction,
                deleteFunction = deleteFunction,
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
            deleteFunction = {},
        )
    }
}