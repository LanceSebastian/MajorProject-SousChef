package uk.ac.aber.dcs.souschefapp.screens

import android.window.SplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import uk.ac.aber.dcs.souschefapp.R
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@Composable
fun SplashScreen (
    onTimeout: () -> Unit
){
    val colorStops = arrayOf(
        0.14f to MaterialTheme.colorScheme.primary,
        0.91f to MaterialTheme.colorScheme.secondaryContainer
    )
    val brush =  Brush.verticalGradient(colorStops = colorStops)
    // Effect to trigger once when the Composable is first launched
    LaunchedEffect (true){
        delay(2000)
        onTimeout() // Navigate when done
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview(){
    AppTheme (){
        SplashScreen(onTimeout = {})
    }
}