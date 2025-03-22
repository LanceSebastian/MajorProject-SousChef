package uk.ac.aber.dcs.souschefapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.souschefapp.R
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.ui.navigation.Screen
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@Composable
fun HomeNavigationBar(
    mainState: MainState = MainState.HOME,
    navController: NavHostController,
    modifier: Modifier = Modifier
){
    val homeIcon = painterResource(R.drawable.home)
    val historyIcon = painterResource(R.drawable.history)
    val recipeIcon = painterResource(R.drawable.kitchen)
    val profileIcon = painterResource(R.drawable.person)

    NavigationBar (
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ){
        NavigationBarItem(
            selected = mainState.equals(MainState.HOME),
            onClick = {
                if (!mainState.equals(MainState.HOME)) navController.navigate(Screen.Home.route)
            },
            icon = {
                Image(
                    painter = homeIcon,
                    contentDescription = null,
                    alpha = if (mainState.equals(MainState.HOME)) 1.0f else 0.7f
                )
            },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.surfaceContainerLowest
            )
        )
        NavigationBarItem(
            selected = mainState.equals(MainState.HISTORY),
            onClick = {
                if (!mainState.equals(MainState.HISTORY)) navController.navigate(Screen.History.route)
            },
            icon = {
                Image(
                    painter =  historyIcon,
                    contentDescription = null,
                    alpha = if (mainState.equals(MainState.HISTORY)) 1.0f else 0.7f
                )
            },
            label = { Text("History") }
        )
        NavigationBarItem(
            selected = mainState.equals(MainState.RECIPES),
            onClick = {
                if (!mainState.equals(MainState.RECIPES)) navController.navigate(Screen.Recipes.route)
            },
            icon = {
                Image(
                    painter =  recipeIcon,
                    contentDescription = null,
                    alpha = if (mainState.equals(MainState.RECIPES)) 1.0f else 0.7f
                )
            },
            label = {Text("Recipes")}
        )
        NavigationBarItem(
            selected = mainState.equals(MainState.PROFILE),
            onClick = {
                if (!mainState.equals(MainState.PROFILE)) navController.navigate(Screen.Profile.route)
            },
            icon = {
                Image(
                    painter =  profileIcon,
                    contentDescription = null,
                    alpha = if (mainState.equals(MainState.PROFILE)) 1.0f else 0.7f
                )
            },
            label = {Text("Profile")}
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeNavigationBarPreview(){
    val navController = rememberNavController()
    AppTheme () {
        HomeNavigationBar(
            mainState = MainState.HOME,
            navController = navController,
            modifier = Modifier.fillMaxWidth())
    }
}