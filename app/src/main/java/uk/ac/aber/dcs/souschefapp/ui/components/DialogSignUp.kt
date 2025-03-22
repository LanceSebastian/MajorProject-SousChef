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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uk.ac.aber.dcs.souschefapp.database.models.Account
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@Composable
fun SignUpDialogue(
    onDismissRequest: () -> Unit,
    mainAction: (Account) -> Unit
) {

    var usernameText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var passwordRepeatText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }

    var differentPasswordsError by remember{ mutableStateOf(false) }
    var emptyFieldsError by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false)}
    var passwordError by remember { mutableStateOf(false)}
    var passwordRepeatError by remember { mutableStateOf(false)}
    var emailError by remember { mutableStateOf(false)} // have to go back to making this format friendly

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
                    text = "Sign Up",
                    style = MaterialTheme.typography.titleLarge
                )

                // Username
                TextField(
                    value = usernameText,
                    onValueChange = {
                        usernameText = it
                    },
                    label = { Text("Username") },
                    isError = usernameError,
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

                // Repeat Password
                TextField(
                    value = passwordRepeatText,
                    onValueChange = {
                        passwordRepeatText = it
                    },
                    label = { Text("Repeat Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordRepeatError,
                    singleLine = true
                )

                // Email
                TextField(
                    value = emailText,
                    onValueChange = {
                        emailText = it
                    },
                    label = { Text("Email") },
                    isError = emailError,
                    singleLine = true
                )
                if (emptyFieldsError) {
                    Text(
                        text = "Please fill in the fields.",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (differentPasswordsError) {
                    Text(
                        text = "Passwords do not match.",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        usernameText = ""
                        passwordText = ""
                        passwordRepeatText = ""
                        emailText = ""
                        onDismissRequest()
                    }) {
                        Text("Back")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {

                            differentPasswordsError = passwordRepeatText != passwordText
                            usernameError = usernameText.isEmpty()
                            passwordError = passwordText.isEmpty()
                            passwordRepeatError = passwordRepeatText.isEmpty()
                            emailError = emailText.isEmpty()
                            emptyFieldsError = usernameError || passwordError || passwordRepeatError || emailError

                            if (!differentPasswordsError && !emptyFieldsError) {
                                mainAction(
                                    Account(
                                        username = usernameText,
                                        email = emailText,
                                        password = passwordText
                                    )
                                )
                            }
                        }
                    ){
                        Text(
                            text = "Sign up"
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SignUpDialoguePreview(){
    AppTheme(dynamicColor = false){
        SignUpDialogue(
            onDismissRequest = {},
            mainAction = {}
        )
    }
}