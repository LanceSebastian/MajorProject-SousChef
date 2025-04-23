package uk.ac.aber.dcs.souschefapp.ui.components

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toDrawable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.firebase.SelectMode
import uk.ac.aber.dcs.souschefapp.ui.navigation.Screen
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme


@Composable
fun BareMainScreen(
    navController: NavHostController,
    mainState: MainState = MainState.HOME,
    selectMode: SelectMode = SelectMode.View,
    onSearch: () -> Unit = {},
    onSecond: () -> Unit = {},
    onNavIcon: () -> Unit = {},
    floatButton: @Composable () -> Unit = {},
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Calculate drawer opening progress manually
    val isOpening = drawerState.targetValue == DrawerValue.Open
    val progress = when {
        drawerState.isAnimationRunning -> if (isOpening) drawerState.currentOffset else 1f - drawerState.currentOffset
        drawerState.currentValue == DrawerValue.Open -> 1f
        else -> 0f
    }
    val blurAmount = (progress * 5).dp

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navController = navController,
                onClose = {scope.launch { drawerState.close() }}
            )
        }
    ) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    mainState = mainState,
                    selectMode = selectMode,
                    navController = navController,
                    onSearch = onSearch,
                    onNavIcon = {
                        if (selectMode != SelectMode.Select) scope.launch { drawerState.open() }
                        else onNavIcon()
                    },
                    onSecond = onSecond,

                )
            },
            content = { innerPadding -> pageContent(innerPadding) },
            bottomBar = {
                if (selectMode == SelectMode.View) HomeNavigationBar(
                    mainState = mainState,
                    navController = navController
                )
            },
            floatingActionButton = floatButton,
            modifier = Modifier
                .blur(blurAmount)

        )
    }
}

@Composable
fun DrawerContent(
    navController: NavHostController,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        tonalElevation = 4.dp,
        color = Color.White,
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Menu", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            DrawerItem(
                title = "Sous Chef",
                onClick = {
                    onClose()
                    navController.navigate(Screen.Home.route)
                }
            )
            DrawerItem(
                title = "Scan Receipt*",
                onClick = {
                    onClose()
                    navController.navigate(Screen.Budget.route)
                },
                ready = false
            )
            DrawerItem(
                title = "Timer",
                onClick = {
                    onClose()
                },
                ready = false
            )
            DrawerItem(
                title = "Shopping List",
                onClick = {
                    onClose()
                    navController.navigate(Screen.ShoppingList.route)
                }
            )
        }
    }
}

@Composable
fun DrawerItem(title: String, onClick: () -> Unit, ready: Boolean = true) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .clickable{
                if(ready) onClick()
            }
            .padding(vertical = 8.dp),
        fontSize = 16.sp,
        color = if (!ready) Color.Gray else MaterialTheme.colorScheme.onSurface
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

@Preview(showBackground = true)
@Composable
fun DrawerContentView(){
    val navController = rememberNavController()
    AppTheme {
        DrawerContent(
            navController = navController,
            onClose = {}
        )
    }
}