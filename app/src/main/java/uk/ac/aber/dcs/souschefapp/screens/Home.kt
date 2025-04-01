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
import uk.ac.aber.dcs.souschefapp.R
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.database.models.Note
import uk.ac.aber.dcs.souschefapp.database.models.Product
import uk.ac.aber.dcs.souschefapp.database.models.Recipe
import uk.ac.aber.dcs.souschefapp.firebase.Log
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
        recipes = emptyList(),
        findLog = { millis ->
            logViewModel.findLog(millis)
        },
        createLog = { dateMillis ->
            logViewModel.createLog(userId, dateMillis)
        }
    )
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    mainState: MainState = MainState.HOME,
    logs: List<Log>, // This will be used for the calendar
    recipes: List<Recipe>,
    findLog: (Long) -> Log?,
    createLog: (Long) -> Unit,
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
    val log = findLog(datePickedEpoch)
    val isLogRecipes = log?.recipeIdList.isNullOrEmpty()
    var logRating by remember { mutableIntStateOf(log?.rating ?: 0) }
    var logNote by remember { mutableStateOf(log?.note ?: "") }

    LaunchedEffect (log){
        logRating = log?.rating ?: 0
        logNote = log?.note ?: ""
    }
    val logRecipes = recipes.filter{ log?.recipeIdList?.contains(it.recipeId) == true }
    val notesList = remember { mutableStateListOf<Note>() }


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
                                        text = recipe.recipeName,
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

                                                    } else {
                                                        editNoteSelected = !editNoteSelected
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
                                                    recipeName = logRecipes.find { it.recipeId == note.recipeOwnerId }?.recipeName
                                                        ?: "Unknown Recipe",
                                                    noteContent = note.content,
                                                    dateEpoch = note.date
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
            findLog = {_ -> null},
            createLog = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenView(){
    val navController = rememberNavController()

    // Mock ViewModel or Data
    val sampleLogs = listOf(
        Log(
            createdBy = "",
            date = System.currentTimeMillis(),
            rating = 2,
            recipeIdList = listOf(1, 2),
            productIdList = listOf(101, 202),
            note = "Tried a new recipe, turned out great!"
        ),
        Log(
            createdBy = "",
            date = System.currentTimeMillis() - 86_400_000, // 1 day ago
            rating = -1,
            recipeIdList = listOf(3),
            productIdList = listOf(303, 404),
            note = "Didn't like the taste, will try adjusting ingredients."
        ),
        Log(
            createdBy = "",
            date = System.currentTimeMillis() - 172_800_000, // 2 days ago
            rating = 1,
            recipeIdList = listOf(6, 5),
            productIdList = listOf(505),
            note = "A decent meal, but could use more seasoning."
        ),
        Log(
            createdBy = "",
            date = System.currentTimeMillis() - 259_200_000, // 3 days ago
            rating = 0,
            recipeIdList = emptyList(),
            productIdList = listOf(606, 707),
            note = "Tried a new product, unsure about it yet."
        ),
        Log(
            createdBy = "",
            date = System.currentTimeMillis() - 345_600_000, // 4 days ago
            rating = -2,
            recipeIdList = listOf(4),
            productIdList = listOf(808, 909),
            note = "Had a bad experience with this recipe."
        )
    )

    val mockNotes = mutableListOf(
        Note(1, 1, "Add more sausages!", System.currentTimeMillis()),
        Note(2, 1, "Cook eggs on lower heat.", System.currentTimeMillis()),
        Note(2, 2, "Add more breading", System.currentTimeMillis()),
        Note(3, 3, "Stir the sauce frequently for better consistency.", System.currentTimeMillis() - 1_800_000), // 30 minutes ago
        Note(4, 4, "Use fresh basil for better flavor.", System.currentTimeMillis() - 3_600_000), // 1 hour ago
        Note(5, 5, "Marinate the chicken overnight.", System.currentTimeMillis() - 7_200_000), // 2 hours ago
        Note(6, 6, "Don't overcook the shrimp.", System.currentTimeMillis() - 12_000_000), // 3 hours ago
        Note(7, 7, "Top with extra parmesan before serving.", System.currentTimeMillis() - 21_600_000), // 6 hours ago
        Note(8, 8, "Slice tomatoes thinly for the salad.", System.currentTimeMillis() - 43_200_000), // 12 hours ago
        Note(9, 9, "Make sure to use chilled dough for better texture.", System.currentTimeMillis() - 86_400_000), // 1 day ago
        Note(10, 10, "Use a non-stick pan to avoid sticking.", System.currentTimeMillis() - 172_800_000), // 2 days ago
        Note(11, 11, "Ensure even cooking by rotating the pan.", System.currentTimeMillis() - 259_200_000), // 3 days ago
        Note(12, 12, "Prepare ingredients ahead of time for a smoother process.", System.currentTimeMillis() - 345_600_000) // 4 days ago
    )

    val mockRecipes = mutableListOf(
        Recipe(1, "English Breakfast", ""),
        Recipe(2, "Chicken Sandwich", ""),
        Recipe(3, "Spaghetti Bolognese", ""),
        Recipe(4, "Vegetable Stir Fry", ""),
        Recipe(5, "Beef Tacos", ""),
        Recipe(6, "Margherita Pizza", "")
    )

    AppTheme {
        HomeScreen(
            navController = navController,
            mainState = MainState.HOME,
            logs = sampleLogs,
            recipes = mockRecipes,
            findLog = {_ -> null},
            createLog = {}
        )
    }
}