package uk.ac.aber.dcs.souschefapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import uk.ac.aber.dcs.souschefapp.R
import uk.ac.aber.dcs.souschefapp.firebase.Log
import uk.ac.aber.dcs.souschefapp.screens.Rating
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCalendar(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    dateEpoch: Long = System.currentTimeMillis()
) {
    if (showDialog) {
        val datePickerState = rememberDatePickerState(dateEpoch)

        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        val selectedDate = Instant.ofEpochMilli(selectedDateMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(selectedDate)
                    }
                    onDismiss()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@Composable
fun CustomCalendar(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit = {},
    onDatesSelected: (List<LocalDate>) -> Unit = {},
    date: LocalDate = LocalDate.now(),
    logs: List<Log> = emptyList(),
    isMany: Boolean = false
) {
    if (!showDialog) return

    var selectedMonth by remember(date) {
        mutableStateOf(date.withDayOfMonth(1).let { YearMonth.from(it) })
    }

    val today = LocalDate.now()
    var dateSelected by remember { mutableStateOf(date) }
    val selectedDates = remember { mutableStateListOf<LocalDate>() }

    val daysInMonth = selectedMonth.lengthOfMonth()
    val firstDayOfWeek = selectedMonth.atDay(1).dayOfWeek.value % 7 // Sunday = 0
    val colorScheme = MaterialTheme.colorScheme
    val dateColors = rememberLogColors(logs)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (isMany) {
                    onDatesSelected(selectedDates.sorted())
                } else {
                    onDateSelected(dateSelected)
                }
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { selectedMonth = selectedMonth.minusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Previous Month")
                }
                Text(
                    text = selectedMonth.month.name.lowercase().replaceFirstChar { it.uppercase() } +
                            " ${selectedMonth.year}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = { selectedMonth = selectedMonth.plusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Default.ArrowForward, contentDescription = "Next Month")
                }
            }
        },
        text = {
            Column {
                Row {
                    listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                        Text(
                            text = it,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                val totalCells = daysInMonth + firstDayOfWeek
                val rows = (totalCells + 6) / 7

                for (i in 0 until rows) {
                    Row {
                        for (j in 0..6) {
                            val dayIndex = i * 7 + j
                            val date = if (dayIndex >= firstDayOfWeek && dayIndex < totalCells) {
                                selectedMonth.atDay(dayIndex - firstDayOfWeek + 1)
                            } else null

                            val isSelected = if (isMany) selectedDates.contains(date) else dateSelected == date
                            val bgColor = when {
                                isSelected -> dateColors[date] ?: colorScheme.primary
                                else ->  Color.Transparent
                            }
                            val borderColor = when {
                                date == null -> Color.Transparent // For if the date is out of range
                                date > today -> Color.Transparent
                                dateColors[date] != null -> dateColors[date]!!
                                else -> Color.Transparent
                            }
                            val textColor = when {
                                isSelected -> colorScheme.onPrimary
                                today == date -> colorScheme.primary
                                else -> colorScheme.onSurface
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .background(bgColor, shape = CircleShape)
                                    .border(
                                        width = 1.dp,
                                        color = dateColors[date] ?: Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable(enabled = date != null) {
                                        date?.let {
                                            if (isMany) {
                                                if (selectedDates.contains(it)) {
                                                    selectedDates.remove(it)
                                                } else {
                                                    selectedDates.add(it)
                                                }
                                            } else {
                                                dateSelected = it
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = date?.dayOfMonth?.toString() ?: "", color = textColor)
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun rememberLogColors(logs: List<Log>): Map<LocalDate, Color> {
    val ratings = listOf(
        Rating(
            value = -2,
            image = ImageVector.vectorResource(id = R.drawable.disappointed),
            color = Color(0xFFF33B3B)
        ),
        Rating(
            value = -1,
            image = ImageVector.vectorResource(id = R.drawable.unhappy),
            color = Color(0xFFFFB001)
        ),
        Rating(
            value = 0,
            image = ImageVector.vectorResource(id = R.drawable.neutral),
            color = Color(0xFFCACA04)
        ),
        Rating(
            value = 1,
            image = ImageVector.vectorResource(id = R.drawable.happy),
            color = Color(0xFF02C801)
        ),
        Rating(
            value = 2,
            image = ImageVector.vectorResource(id = R.drawable.satisfied),
            color = Color(0xFF0095FF)
        )
    )

    return remember(logs) {
        logs.associate { log ->
            val localDate = log.createdAt
                .toDate() // Convert Firebase Timestamp → java.util.Date
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            val ratingColor = ratings.find { it.value == log.rating }?.color ?: Color.Gray

            localDate to ratingColor
        }
    }
}

@Preview
@Composable
fun MyCalendarView(){
    AppTheme{
        MyCalendar(true, {}, {LocalDate.now()})
    }
}

@Preview
@Composable
fun CustomCalendarView(){
    val sampleLogs = listOf(
        Log(
            logId = "",
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis())),
            rating = 2,
            recipeIdList = listOf("1", "2"),
            productIdList = listOf("0", "2"),
            note = "Tried a new recipe, turned out great!"
        ),
        Log(
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 86_400_000)), // 1 day ago
            rating = -1,
            recipeIdList = listOf("3"),
            productIdList = listOf("3", "4"),
            note = "Didn't like the taste, will try adjusting ingredients."
        ),
        Log(
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 172_800_000)), // 2 days ago
            rating = 1,
            recipeIdList = listOf("6", "5"),
            productIdList = listOf("5"),
            note = "A decent meal, but could use more seasoning."
        ),
        Log(
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 259_200_000)), // 3 days ago
            rating = 0,
            recipeIdList = emptyList(),
            productIdList = listOf("5", "4"),
            note = "Tried a new product, unsure about it yet."
        ),
        Log(
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 345_600_000)), // 4 days ago
            rating = -2,
            recipeIdList = listOf("4"),
            productIdList = listOf("2", "3"),
            note = "Had a bad experience with this recipe."
        )
    )
    AppTheme{
        CustomCalendar(
            showDialog = true,
            onDismiss = {},
            onDateSelected = {LocalDate.now()},
            logs = sampleLogs
        )
    }
}

@Preview
@Composable
fun RangeCustomCalendarView(){
    val sampleLogs = listOf(
        Log(
            logId = "",
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis())),
            rating = 2,
            recipeIdList = listOf("1", "2"),
            productIdList = listOf("0", "2"),
            note = "Tried a new recipe, turned out great!"
        ),
        Log(
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 86_400_000)), // 1 day ago
            rating = -1,
            recipeIdList = listOf("3"),
            productIdList = listOf("3", "4"),
            note = "Didn't like the taste, will try adjusting ingredients."
        ),
        Log(
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 172_800_000)), // 2 days ago
            rating = 1,
            recipeIdList = listOf("6", "5"),
            productIdList = listOf("5"),
            note = "A decent meal, but could use more seasoning."
        ),
        Log(
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 259_200_000)), // 3 days ago
            rating = 0,
            recipeIdList = emptyList(),
            productIdList = listOf("5", "4"),
            note = "Tried a new product, unsure about it yet."
        ),
        Log(
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 345_600_000)), // 4 days ago
            rating = -2,
            recipeIdList = listOf("4"),
            productIdList = listOf("2", "3"),
            note = "Had a bad experience with this recipe."
        )
    )
    AppTheme{
        CustomCalendar(
            showDialog = true,
            onDismiss = {},
            onDatesSelected = {LocalDate.now()},
            logs = sampleLogs,
            isMany = true
        )
    }
}