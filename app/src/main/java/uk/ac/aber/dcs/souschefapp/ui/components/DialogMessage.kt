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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@Composable
fun ConfirmDialogue(
    onDismissRequest: () -> Unit,
    mainAction: () -> Unit,
    secondAction: () -> Unit = {},
    title: String = "Are you sure?",
    supportingText: String = "This will be a permanent decision.",
    mainButtonText: String = "Confirm",
    secondButtonText: String = ""
){
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
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    if (secondButtonText.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = secondAction) {
                            Text(secondButtonText)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = mainAction) {
                        Text(mainButtonText)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ConfirmDialoguePreview(){
    AppTheme(dynamicColor = false){
        ConfirmDialogue(
            onDismissRequest = {},
            mainAction = {},
        )
    }
}

@Preview
@Composable
fun TwoButtonsConfirmDialoguePreview(){
    AppTheme(dynamicColor = false){
        ConfirmDialogue(
            onDismissRequest = {},
            mainAction = {},
            title = "Leaving already?",
            supportingText = "Do you want to save your changes before you go?",
            mainButtonText = "Save",
            secondButtonText = "Don't Save"
        )
    }
}