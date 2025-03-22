package uk.ac.aber.dcs.souschefapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun RecipeNote(
    recipeName: String,
    noteContent: String,
    dateEpoch: Long
){
    val localDate: LocalDate = Instant.ofEpochMilli(dateEpoch)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    val dateFormat by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd.MM.yy")
                .format(localDate)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Text(
                text = recipeName,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = dateFormat,
                style = MaterialTheme.typography.titleSmall
            )
        }
        Text(
            text = noteContent,
            style = MaterialTheme.typography.bodyMedium
        )
    }

}

@Preview
@Composable
fun RecipeNoteView(){
    AppTheme {
        RecipeNote(
            "English Breakfast",
            "Cook eggs on lower heat",
            System.currentTimeMillis()
        )
    }
}