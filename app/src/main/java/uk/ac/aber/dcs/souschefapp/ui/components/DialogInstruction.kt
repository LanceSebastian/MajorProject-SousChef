package uk.ac.aber.dcs.souschefapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@Composable
fun InstructionDialogue(
    onDismissRequest: () -> Unit,
    mainAction: (String) -> Unit,
    instruction: String = "",
){

    var instructionText by remember { mutableStateOf(instruction) }
    var emptyFieldsError by remember { mutableStateOf(false) }
    val maxChars = 200

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
                    text = "Add Step",
                    style = MaterialTheme.typography.titleLarge
                )

                TextField(
                    value = instructionText,
                    onValueChange = {
                        if ( it.length <= maxChars ) instructionText = it
                                    },
                    isError = emptyFieldsError,
                    label = {Text("Instruction")},
                    shape = RoundedCornerShape(25.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .heightIn(min = 40.dp, max = 120.dp)
                        .fillMaxWidth()
                )

                if (emptyFieldsError) {
                    Text(
                        text = "Please fill in the fields",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        instructionText = ""
                        onDismissRequest()
                    }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {

                            emptyFieldsError = instructionText.isEmpty()

                            if (emptyFieldsError) mainAction(instructionText)
                        }
                    ){
                        Text(
                            text = "Save"
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun InstructionDialoguePreview(){
    AppTheme(dynamicColor = false){
        InstructionDialogue(
            onDismissRequest = {},
            mainAction = {},

            )
    }
}