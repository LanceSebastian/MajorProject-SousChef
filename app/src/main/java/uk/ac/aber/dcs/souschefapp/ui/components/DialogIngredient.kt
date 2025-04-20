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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uk.ac.aber.dcs.souschefapp.firebase.Ingredient
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@Composable
fun IngredientDialogue(
    onDismissRequest: () -> Unit,
    mainAction: (Ingredient) -> Unit,
    ingredient: Ingredient? = null,
){
    var nameText by remember { mutableStateOf(ingredient?.name ?: "") }
    val amount: String = if (ingredient?.quantity == null) "" else ingredient.quantity.toString()
    var amountText by remember { mutableStateOf(amount) }
    var unitText by remember{ mutableStateOf(ingredient?.unit ?: "") }
    var extraText by remember { mutableStateOf(ingredient?.description ?: "") }

    var emptyFieldsError by remember { mutableStateOf(false) }
    var emptyNameError by remember { mutableStateOf(false) }
    var emptyAmountError by remember { mutableStateOf(false) }
    var emptyUnitError by remember { mutableStateOf(false) }
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
                    text = "Add Ingredient",
                    style = MaterialTheme.typography.titleLarge
                )

                // Name
                TextField(
                    value = nameText,
                    onValueChange = {
                        if ( it.length <= 20) nameText = it
                    },
                    isError = emptyNameError,
                    shape = RoundedCornerShape(25.dp),
                    label = {Text("Name*")},
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
                        value = amountText,
                        onValueChange = { newText ->
                            if (newText.matches(Regex("^\\d*\\.?\\d{0,2}$")) && newText.length <= 8) {
                                amountText = newText
                            }
                        },
                        isError = emptyAmountError,
                        singleLine = true,
                        label = { Text("Amount*") },
                        shape = RoundedCornerShape(25.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .weight(0.6f)
                    )

                    TextField(
                        value = unitText,
                        onValueChange = { newText ->
                            if (newText.length <= 4) {
                                unitText = newText
                            }
                        },
                        label = { Text("Unit") },
                        isError = emptyUnitError,
                        shape = RoundedCornerShape(25.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .height(55.dp)
                            .weight(0.4f)
                    )
                }

                //Extra
                TextField(
                    value = extraText,
                    onValueChange = {
                        if ( it.length <= maxExtraChars ) extraText = it
                    },
                    shape = RoundedCornerShape(25.dp),
                    label = {Text("Extra")},
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
                            emptyNameError = nameText.isEmpty()
                            emptyUnitError = unitText.isEmpty()
                            emptyAmountError = amountText.isEmpty()
                            emptyFieldsError = emptyNameError  || emptyAmountError

                            if (!emptyFieldsError) {
                                mainAction(
                                    Ingredient(
                                        name = nameText,
                                        quantity = amountText,
                                        unit = unitText,
                                        description = extraText
                                    )
                                )
                                onDismissRequest()
                            }
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
            mainAction = {},

            )
    }
}