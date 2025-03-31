package uk.ac.aber.dcs.souschefapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@Composable
fun LoginDialogue(
    onDismissRequest: () -> Unit,
    mainAction: (String, String) -> Unit,
    authStatus: String? = null
){

    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    var emptyFieldsError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.titleLarge
                )

                // Username
                TextField(
                    value = emailText,
                    onValueChange = {
                        emailText = it
                    },
                    label = { Text("Email") },
                    isError = emailError,
                    singleLine = true
                )

                // Password
                TextField(
                    value = passwordText,
                    onValueChange = {
                        passwordText = it
                    },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordError,
                    singleLine = true
                )

                authStatus?.let {
                    Text(text = it)
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        emailText = ""
                        passwordText = ""
                        onDismissRequest()
                    }) {
                        Text("Back")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {

                            emailError = emailText.isEmpty()
                            passwordError = passwordText.isEmpty()
                            emptyFieldsError = emailError || passwordError

                            if (!emptyFieldsError) mainAction(emailText, passwordText)
                        }
                    ){
                        Text(
                            text = "Log in"
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginDialoguePreview(){
    AppTheme(dynamicColor = false){
        LoginDialogue(
            onDismissRequest = {},
            mainAction = {_, _ -> }
        )
    }
}