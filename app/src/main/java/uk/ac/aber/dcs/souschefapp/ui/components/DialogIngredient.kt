package uk.ac.aber.dcs.souschefapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import uk.ac.aber.dcs.souschefapp.firebase.Ingredient
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@Composable
fun IngredientDialogue(
    onDismissRequest: () -> Unit,
    mainAction: (String, String, String, String,) -> Unit,
    ingredient: Ingredient? = null,
){
    var nameText by remember { mutableStateOf(ingredient?.name ?: "") }
    val amount: String = if (ingredient?.quantity == null) "" else ingredient.quantity.toString()
    var amountText by remember { mutableStateOf(amount) }
    var unitText by remember{ mutableStateOf(ingredient?.unit ?: "") }
    var extraText by remember { mutableStateOf(ingredient?.description ?: "") }

    var emptyFieldsError by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    val maxExtraChars = 200

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

                // Name
                TextField(
                    value = nameText,
                    onValueChange = {
                        if ( it.length <= 20) nameText = it
                    },
                    isError = emptyFieldsError,
                    shape = RoundedCornerShape(25.dp),
                    label = {Text("Name")},
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                // Amount
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextField(
                        value = extraText,
                        onValueChange = {
                            extraText = it
                        },
                        isError = emptyFieldsError,
                        singleLine = true,
                        label = { Text("Amount") },
                        shape = RoundedCornerShape(25.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(0.7f)
                    )

                    Button(
                        onClick = { isExpanded = !isExpanded},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .height(55.dp)
                            .weight(0.3f)
                    ) {
                        Text("Unit")
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable { TODO("Unit DropDownMenu") }
                        )
                    }
                }

                //TODO("Need a selection table here. I don't know of what design.")

                //Extra
                TextField(
                    value = extraText,
                    onValueChange = {
                        if ( it.length <= maxExtraChars ) extraText = it
                    },
                    isError = emptyFieldsError,
                    shape = RoundedCornerShape(25.dp),
                    label = {Text("Extra (optional)")},
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
                        nameText = ""
                        amountText = ""
                        extraText = ""
                        onDismissRequest()
                    }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {

                            emptyFieldsError = nameText.isEmpty() || amountText.isEmpty()

                            if (emptyFieldsError) mainAction(nameText, amountText, unitText, extraText)
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
fun IngredientDialoguePreview(){
    AppTheme(dynamicColor = false){
        IngredientDialogue(
            onDismissRequest = {},
            mainAction = {_,_,_,_ ->},

            )
    }
}