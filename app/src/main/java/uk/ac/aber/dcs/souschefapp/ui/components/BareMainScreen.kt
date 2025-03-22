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
fun BareMainScreen(
    navController: NavHostController,
    mainState: MainState = MainState.HOME,
    onFloatClick: () -> Unit = {},
    onSearch: () -> Unit = {},
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
){
    Scaffold(
        topBar = { HomeTopAppBar(mainState = mainState, navController = navController, onSearch = onSearch) },
        content = { innerPadding -> pageContent(innerPadding) },
        bottomBar = { HomeNavigationBar(mainState = mainState, navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (mainState == MainState.RECIPES) { onFloatClick() }
                },
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
                modifier = Modifier
                    .alpha(if (mainState == MainState.RECIPES) 1f else 0f)
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BareMainScreenView(){
    val navController = rememberNavController()
    AppTheme {
        BareMainScreen(
            navController = navController,
            mainState = MainState.RECIPES
        )
    }
}