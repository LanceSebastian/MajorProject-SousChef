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
import uk.ac.aber.dcs.souschefapp.firebase.SelectMode
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme


@Composable
fun BareMainScreen(
    navController: NavHostController,
    mainState: MainState = MainState.HOME,
    selectMode: SelectMode = SelectMode.View,
    onSearch: () -> Unit = {},
    floatButton: @Composable () -> Unit = {},
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
){
    Scaffold(
        topBar = { HomeTopAppBar(mainState = mainState, selectMode = selectMode, navController = navController, onSearch = onSearch) },
        content = { innerPadding -> pageContent(innerPadding) },
        bottomBar = { if (selectMode == SelectMode.View) HomeNavigationBar(mainState = mainState, navController = navController) },
        floatingActionButton = floatButton
    )
}

@Preview(showBackground = true)
@Composable
fun BareMainScreenView(){
    val navController = rememberNavController()
    AppTheme {
        BareMainScreen(
            navController = navController,
            mainState = MainState.RECIPES,
            floatButton = {
                FloatingActionButton(
                    onClick = {},
                    content = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add")
                            Text(text = "Add Recipe")
                        }
                    },
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectModeMainScreenView(){
    val navController = rememberNavController()
    AppTheme {
        BareMainScreen(
            navController = navController,
            mainState = MainState.RECIPES,
            selectMode = SelectMode.Select
        )
    }
}