package uk.ac.aber.dcs.souschefapp.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.souschefapp.R
import uk.ac.aber.dcs.souschefapp.database.models.Account
import uk.ac.aber.dcs.souschefapp.ui.components.LoginDialogue
import uk.ac.aber.dcs.souschefapp.ui.components.SignUpDialogue
import uk.ac.aber.dcs.souschefapp.ui.navigation.Screen
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import uk.ac.aber.dcs.souschefapp.viewmodel.AuthViewModel
import uk.ac.aber.dcs.souschefapp.viewmodel.LoginState

@Composable
fun TopAuthScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
){
    val isAuthenticated by authViewModel.isAuthenticated.observeAsState(false)

    LaunchedEffect (isAuthenticated){
        if (isAuthenticated)
            navController.navigate(Screen.Home.route)
    }

    AuthScreen(
        navController = navController,

        onLogin = { username, password -> authViewModel.login(username, password)},
        onSignup = { account -> authViewModel.register(account) }
    )

}
@Composable
fun AuthScreen(
    navController: NavHostController,
    onLogin: (String, String) -> Unit,
    onSignup: (Account) -> Unit
){
    val colorStops = arrayOf(
        0.14f to MaterialTheme.colorScheme.primary,
        0.91f to MaterialTheme.colorScheme.secondaryContainer
    )

    val brush =  Brush.verticalGradient(colorStops = colorStops)
    var loginSelected by remember { mutableStateOf(false) }
    var signupSelected by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)) {

        // Background Fade
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush,
                    alpha = 0.75f
                )
        )

        // Background Image
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.chefbg),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.25f)
                    .padding(bottom = 100.dp)
            )
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp)
        ) {
            Button(
                onClick = { loginSelected = true },
                enabled = !(loginSelected || signupSelected),
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Transparent
                )
            ) {
                Text("Log In")
            }

            Button(
                onClick = { signupSelected = true },
                enabled = !(loginSelected || signupSelected),
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Transparent
                ),
                border = BorderStroke(1.dp, if ( !(loginSelected || signupSelected)) MaterialTheme.colorScheme.onPrimaryContainer else Color.Transparent)
            ) {
                Text("Sign Up")
            }
        }

        if (loginSelected) {
            LoginDialogue(
                onDismissRequest = { loginSelected = false },
                mainAction = {username, password ->
                    onLogin(username, password)
                }
            )
        }

        if (signupSelected) {
            SignUpDialogue(
                onDismissRequest = { signupSelected = false },
                mainAction = { account ->
                    onSignup(account)
                    navController.navigate(Screen.Home.route)
                }
            )
        }
    }

}

@Composable
@Preview
fun AuthScreenPreview(){
    val navController = rememberNavController()
    AppTheme(){
        AuthScreen(
            navController,
            onLogin = {_,_ -> {}},
            onSignup = {}
        )
    }
}
