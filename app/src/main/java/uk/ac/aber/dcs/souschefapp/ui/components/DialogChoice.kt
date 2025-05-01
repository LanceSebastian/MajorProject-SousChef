package uk.ac.aber.dcs.souschefapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoiceDialogue(
    onDismissRequest: () -> Unit,
    mainAction: () -> Unit,
    secondAction: () -> Unit,
    mainText: String = "main",
    secondText: String = "second",
    enableSecond: Boolean = true,
){
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf(mainText, secondText)

    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .height(220.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "What to Add?",
                    style = MaterialTheme.typography.displaySmall
                )

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    options.forEachIndexed { index, label ->
                        val isSecond = secondText == label
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            ),
                            onClick = { selectedIndex = index },
                            selected = index == selectedIndex,
                            label = { Text(label) },
                            enabled = if (isSecond) enableSecond else true,
                            modifier = Modifier
                                .alpha(if(isSecond && !enableSecond) 0.2f else 1f)
                        )
                    }
                }
                if (!enableSecond) Text(
                    "There are no ${secondText}s.",
                    color = MaterialTheme.colorScheme.error
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        onDismissRequest()
                    }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (selectedIndex == 0) mainAction() else secondAction()
                            onDismissRequest()
                        }
                    ){
                        Text(
                            text = "Continue"
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeAddDialoguePreview(){
    AppTheme(dynamicColor = false){
        ChoiceDialogue(
            onDismissRequest = {},
            mainAction = {},
            secondAction = {},
            enableSecond = false

            )
    }
}