package uk.ac.aber.dcs.souschefapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.souschefapp.R
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.ui.navigation.Screen
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    mainState: MainState,
    navController: NavHostController,
    onSearch: () -> Unit
){
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text("Sous Chef", fontWeight = FontWeight.Bold)
            },
            navigationIcon = {
                IconButton(onClick = {}) {
                    Image(
                        painter = painterResource(id = R.drawable.chef),
                        contentDescription = null,
                        modifier = Modifier
                            .height(24.dp)
                    )
                }
            },

            actions = {
                IconButton(onClick = {
                    if (mainState != MainState.RECIPES) navController.navigate(Screen.Recipes.route)
                    onSearch()
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search"
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface
            )

        )
    }

}

@Preview(showBackground = true)
@Composable
fun HomeTopAppBarPreview(){
    val navController = rememberNavController()
    AppTheme {
        HomeTopAppBar(
            mainState = MainState.HOME,
            navController = navController,
            onSearch = {}
        )
    }
}