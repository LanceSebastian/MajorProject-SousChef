package uk.ac.aber.dcs.souschefapp.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Timestamp
import uk.ac.aber.dcs.souschefapp.R
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.firebase.Log
import uk.ac.aber.dcs.souschefapp.firebase.Note
import uk.ac.aber.dcs.souschefapp.firebase.Recipe
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.AuthViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.ui.components.BareMainScreen
import uk.ac.aber.dcs.souschefapp.ui.components.CardRecipe
import uk.ac.aber.dcs.souschefapp.ui.components.DateNavigationBar
import uk.ac.aber.dcs.souschefapp.ui.components.HomeAddDialogue
import uk.ac.aber.dcs.souschefapp.ui.components.MyCalendar
import uk.ac.aber.dcs.souschefapp.ui.components.RecipeNote
import uk.ac.aber.dcs.souschefapp.ui.navigation.Screen
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

@Composable
fun TopHomeScreen(
    context: ComponentActivity,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    logViewModel: LogViewModel
){
    val user by authViewModel.user.observeAsState()
    val userId = user?.uid

    if (userId != null) logViewModel.readLogs(userId)
    val logs by logViewModel.logs.observeAsState(emptyList())

    val log by logViewModel.singleLog.observeAsState(null)

    // Listen for logs in real-time when the user exists
    DisposableEffect(userId) {
        if(userId != null){
            logViewModel.readLogs(userId)
        }

        onDispose {
            logViewModel.stopListening()
        }
    }

    HomeScreen(
        navController = navController,
        mainState = MainState.HOME,
        logs = logs,
        log = log,
        recipes = emptyList(),
        createLog = { dateMillis ->
            logViewModel.createLog(userId, dateMillis)
        },
        readLogFromDate = { dateMillis ->
            logViewModel.readLogFromDate(dateMillis)
        },
        updateRating = {millis, rating ->
            logViewModel.updateRating(userId, millis, rating)
        },
        updateNote = { millis, content ->
            logViewModel.updateNote(userId, millis, content, context)
        },
        deleteLog = { millis ->
            logViewModel.deleteLog(userId, millis)
        }

    )
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    mainState: MainState = MainState.HOME,
    logs: List<Log>, // This will be used for the calendar
    log: Log? = null,
    recipes: List<Recipe>,
    createLog: (Long) -> Unit,
    readLogFromDate: (Long) -> Unit,
    updateRating: (Long, Int) -> Unit,
    updateNote: (Long, String) -> Unit,
    deleteLog: (Long) -> Unit
){
    // Ratings
    // val ratingMap = ratings.associateBy { it.value }
    // val satisfiedRating = ratingMap[2]
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

    // Buttons
    var personalExpanded by remember { mutableStateOf(false) }
    var recipeExpanded by remember { mutableStateOf(false) }
    var addSelected by remember { mutableStateOf(false) }
    var editNoteSelected by remember { mutableStateOf(false) }

    // Dates and Calendars
    var dateDialogState by remember { mutableStateOf(false) }
    var datePicked by remember {
        mutableStateOf(LocalDate.now())
    }
    val datePickedEpoch: Long = datePicked.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    // toString
    val monthFormat by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("MMMM yyyy")
                .format(datePicked)
        }
    }
    val dayFormat by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("EEEE dd")
                .format(datePicked)
        }
    }

    // Get Log Data
    val isLogRecipes = log?.recipeIdList.isNullOrEmpty()
    var previousLog by remember { mutableStateOf<Log?>(null) }

    var logNote by remember { mutableStateOf("") }
    var logRating by remember { mutableStateOf(0) }

    val logRecipes = recipes.filter{ log?.recipeIdList?.contains(it.recipeId) == true }
    val notesList = remember { mutableStateListOf<Note>() }

    LaunchedEffect(log) {
        // Save previous log before switching
        previousLog?.let {
            updateRating(previousLog!!.logId.toLong(), logRating)
        }

        // Update state for new log
        logRating = log?.rating ?: 0
        previousLog = log
    }

    LaunchedEffect(datePickedEpoch) {
        readLogFromDate(datePickedEpoch)
    }

    DisposableEffect(Unit) {
        onDispose {
            // Save the current log when leaving the screen
            log?.let {
                updateRating(datePickedEpoch, logRating)
            }
        }
    }


    BareMainScreen(
        mainState = mainState,
        navController = navController
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DateNavigationBar(
                    backAction = { datePicked = datePicked.minusDays(1) },
                    forwardAction = { datePicked = datePicked.plusDays(1) },
                    expandAction = { dateDialogState = true },
                    topString = monthFormat,
                    mainString = dayFormat
                )

                // Show Calendar
                MyCalendar(
                    showDialog = dateDialogState,
                    onDismiss = { dateDialogState = false },
                    onDateSelected = { datePicked = it },
                    dateEpoch = datePickedEpoch
                )

                if (log == null) {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(R.drawable.chef),
                            contentDescription = null,
                            modifier = Modifier
                                .size(72.dp)
                                .alpha(0.75f)
                        )
                        Text(
                            text = "Looking Empty!",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = { createLog(datePickedEpoch) }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                            Text("Create a Log")
                        }
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = "Today's Menu", style = MaterialTheme.typography.titleLarge)
                        Button(
                            onClick = { addSelected = true },
                            contentPadding = PaddingValues(
                                top = 0.dp,
                                bottom = 0.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text(text = "Add")
                        }
                    }

                    if (isLogRecipes) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                                .height(150.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Looking a bit empty...",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }

                    } else {
                        LazyRow(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                                .height(150.dp)
                                .fillMaxWidth()
                        ) {
                            logRecipes.forEach { recipe ->
                                item {
                                    CardRecipe(
                                        text = recipe.name,
                                        onClick = {
                                            navController.navigate(
                                                Screen.RecipePage.route + "/recipeId=${recipe.recipeId}"
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ) {
                        ratings.forEach { (value, icon, color) ->
                            IconButton(onClick = { logRating = value }) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceContainer,
                                            shape = CircleShape
                                        )
                                        .size(if (logRating == value) 50.dp else 25.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    /*      Log Notes        */
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp)
                            .clickable { personalExpanded = true },
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Personal Note",
                                    style = MaterialTheme.typography.headlineSmall,

                                    )
                                if (!personalExpanded) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                } else {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (!editNoteSelected) Icons.Default.Edit else Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .clickable {
                                                    if (editNoteSelected) {
                                                        updateNote(datePickedEpoch, logNote)
                                                        editNoteSelected = false
                                                    } else {
                                                        editNoteSelected = true
                                                    }
                                                }
                                        )
                                        Icon(
                                            painter = painterResource(R.drawable.arrow_drop_up),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .clickable {
                                                    personalExpanded = false
                                                    editNoteSelected = false
                                                }
                                        )
                                    }
                                }
                            }
                            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                                if (personalExpanded) {
                                    if (logNote.isEmpty()) {
                                        Text(
                                            text = "What did you think of today?",
                                            modifier = Modifier.alpha(0.7f)
                                        )
                                    }
                                    BasicTextField(
                                        value = logNote,
                                        onValueChange = {
                                            logNote = it
                                        },
                                        enabled = editNoteSelected,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 40.dp, max = 120.dp)
                                    )
                                }
                            }
                        }
                    }

                    /*      Recipe Notes        */
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp)
                            .clickable(
                                enabled = !isLogRecipes,
                                onClick = { recipeExpanded = true }
                            )
                            .alpha(if (isLogRecipes) 0.5f else 1f),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Recipe Notes",
                                    style = MaterialTheme.typography.headlineSmall,

                                    )
                                if (!recipeExpanded) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_drop_up),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .clickable { recipeExpanded = false }
                                    )
                                }
                            }
                            if (recipeExpanded) {
                                if (notesList.isNotEmpty()) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        notesList.forEach() { note ->
                                            item {
                                                RecipeNote(
                                                    recipeName = TODO("return the name of the recipe"),
                                                    noteContent = note.content,
                                                    dateEpoch = note.createdAt.seconds
                                                )
                                                HorizontalDivider(
                                                    modifier = Modifier
                                                        .padding(vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (addSelected) {
                    HomeAddDialogue(
                        onDismissRequest = { addSelected = false },
                        mainAction = {
                            TODO()
                        },
                        secondAction = {
                            navController.navigate(Screen.Recipes.route)
                        }
                    )
                }
            }
        }
    }
}

// Grouping Rating data into a single class
data class Rating(
    val value: Int,
    val image: ImageVector,
    val color: Color
)

@Preview(showBackground = true)
@Composable
fun HomeScreenEmptyView(){
    val navController = rememberNavController()

    AppTheme {
        HomeScreen(
            navController = navController,
            mainState = MainState.HOME,
            logs = emptyList(),
            recipes = emptyList(),
            createLog = {},
            readLogFromDate = {},
            deleteLog = {},
            updateRating = {_,_ -> },
            updateNote = {_,_ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenView(){
    val navController = rememberNavController()

    val sampleLogs = listOf(
        Log(
            logId = "",
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis())),
            rating = 2,
            recipeIdList = listOf("1", "2"),
            productIdList = listOf("101", "202"),
            note = "Tried a new recipe, turned out great!"
        ),
        Log(
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 86_400_000)), // 1 day ago
            rating = -1,
            recipeIdList = listOf("3"),
            productIdList = listOf("303", "404"),
            note = "Didn't like the taste, will try adjusting ingredients."
        ),
        Log(
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 172_800_000)), // 2 days ago
            rating = 1,
            recipeIdList = listOf("6", "5"),
            productIdList = listOf("505"),
            note = "A decent meal, but could use more seasoning."
        ),
        Log(
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 259_200_000)), // 3 days ago
            rating = 0,
            recipeIdList = emptyList(),
            productIdList = listOf("606", "707"),
            note = "Tried a new product, unsure about it yet."
        ),
        Log(
            createdBy = "",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 345_600_000)), // 4 days ago
            rating = -2,
            recipeIdList = listOf("4"),
            productIdList = listOf("808", "909"),
            note = "Had a bad experience with this recipe."
        )
    )

    val mockNotes = mutableListOf(
        Note("Add more sausages!", Timestamp(Date(System.currentTimeMillis()))),
        Note("Cook eggs on lower heat.", Timestamp(Date(System.currentTimeMillis()))),
        Note("Add more breading", Timestamp(Date(System.currentTimeMillis()))),
        Note("Stir the sauce frequently for better consistency.", Timestamp(Date(System.currentTimeMillis() - 1_800_000))), // 30 minutes ago
        Note("Use fresh basil for better flavor.", Timestamp(Date(System.currentTimeMillis() - 3_600_000))), // 1 hour ago
        Note("Marinate the chicken overnight.", Timestamp(Date(System.currentTimeMillis() - 7_200_000))), // 2 hours ago
        Note("Don't overcook the shrimp.", Timestamp(Date(System.currentTimeMillis() - 12_000_000))), // 3 hours ago
        Note("Top with extra parmesan before serving.", Timestamp(Date(System.currentTimeMillis() - 21_600_000))), // 6 hours ago
        Note("Slice tomatoes thinly for the salad.", Timestamp(Date(System.currentTimeMillis() - 43_200_000))), // 12 hours ago
        Note("Make sure to use chilled dough for better texture.", Timestamp(Date(System.currentTimeMillis() - 86_400_000))), // 1 day ago
        Note("Use a non-stick pan to avoid sticking.", Timestamp(Date(System.currentTimeMillis() - 172_800_000))), // 2 days ago
        Note("Ensure even cooking by rotating the pan.", Timestamp(Date(System.currentTimeMillis() - 259_200_000))), // 3 days ago
        Note("Prepare ingredients ahead of time for a smoother process.", Timestamp(Date(System.currentTimeMillis() - 345_600_000))) // 4 days ago
    )

    val mockRecipes = mutableListOf(
        Recipe("1", "English Breakfast", ""),
        Recipe("2", "Chicken Sandwich", ""),
        Recipe("3", "Spaghetti Bolognese", ""),
        Recipe("4", "Vegetable Stir Fry", ""),
        Recipe("5", "Beef Tacos", ""),
        Recipe("6", "Margherita Pizza", "")
    )

    AppTheme {
        HomeScreen(
            navController = navController,
            mainState = MainState.HOME,
            logs = sampleLogs,
            log = sampleLogs[0],
            recipes = mockRecipes,
            createLog = {},
            readLogFromDate = {},
            deleteLog = {},
            updateRating = {_,_ -> },
            updateNote = {_,_ -> }
        )
    }
}