package uk.ac.aber.dcs.souschefapp.screens

import android.window.SplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import uk.ac.aber.dcs.souschefapp.R
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.AuthViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.ProductViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.RecipeViewModel
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@Composable
fun TopSplashScreen(
    authViewModel: AuthViewModel,
    logViewModel: LogViewModel,
    recipeViewModel: RecipeViewModel,
    productViewModel: ProductViewModel,
    onTimeoutMain: () -> Unit,
    onTimeoutSecond: () -> Unit,
){
    val user by authViewModel.user.observeAsState()
    val userId = user?.uid

    val isLoadingAutoLogin by authViewModel.isLoadingAutoLogin.observeAsState(true)
    val isLoadingLogs by logViewModel.isLoading.observeAsState(true)
    val isLoadingRecipes by recipeViewModel.isLoading.observeAsState(true)
    val isLoadingProducts by productViewModel.isLoading.observeAsState(true)
    val isReady = !(isLoadingLogs || isLoadingProducts || isLoadingRecipes || isLoadingAutoLogin)

    LaunchedEffect(userId){
        userId?.let{
            logViewModel.readLogs(it)
            productViewModel.readProducts(it)
            recipeViewModel.readRecipes(it)
        }
    }
    SplashScreen(
        userId = userId,
        isReady = isReady,
        onTimeoutMain = onTimeoutMain,
        onTimeoutSecond = onTimeoutSecond
    )
}

@Composable
fun SplashScreen (
    userId: String? = null,
    isReady: Boolean = false,
    onTimeoutMain: () -> Unit,
    onTimeoutSecond: () -> Unit
){
    val colorStops = arrayOf(
        0.14f to MaterialTheme.colorScheme.primary,
        0.91f to MaterialTheme.colorScheme.secondaryContainer
    )
    val brush =  Brush.verticalGradient(colorStops = colorStops)
    // Effect to trigger once when the Composable is first launched
    LaunchedEffect (isReady){
        delay(2000)
        if (isReady) {
            if (userId == null) onTimeoutMain() else onTimeoutSecond()
        } // Navigate when done
    }

    // Background Fade
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush,
                alpha = 0.75f
            )
    )

    // Splash UI
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ){
            Icon(
                painter = painterResource(R.drawable.chef),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sous Chef",
                style = MaterialTheme.typography.displaySmall
                )
            Spacer(modifier = Modifier.height(8.dp))
            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .size(100.dp)
            ){
                if (!isReady) CircularProgressIndicator() else Icon(imageVector = Icons.Default.Check, contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview(){
    AppTheme (){
        SplashScreen(
            onTimeoutSecond = {},
            onTimeoutMain = {}
        )
    }
}