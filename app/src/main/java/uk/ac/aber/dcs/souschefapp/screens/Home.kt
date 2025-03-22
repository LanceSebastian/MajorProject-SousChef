package uk.ac.aber.dcs.souschefapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import uk.ac.aber.dcs.souschefapp.R
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.database.UserPreferences
import uk.ac.aber.dcs.souschefapp.database.models.Log
import uk.ac.aber.dcs.souschefapp.database.models.Note
import uk.ac.aber.dcs.souschefapp.database.models.Recipe
import uk.ac.aber.dcs.souschefapp.ui.components.BareMainScreen
import uk.ac.aber.dcs.souschefapp.ui.components.CardRecipe
import uk.ac.aber.dcs.souschefapp.ui.components.DateNavigationBar
import uk.ac.aber.dcs.souschefapp.ui.components.HomeAddDialogue
import uk.ac.aber.dcs.souschefapp.ui.components.MyCalendar
import uk.ac.aber.dcs.souschefapp.ui.components.RecipeNote
import uk.ac.aber.dcs.souschefapp.ui.navigation.Screen
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import uk.ac.aber.dcs.souschefapp.viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.viewmodel.NoteViewModel
import uk.ac.aber.dcs.souschefapp.viewmodel.RecipeViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TopHomeScreen(
    navController: NavHostController,
    logViewModel: LogViewModel,
    recipeViewModel: RecipeViewModel,
    noteViewModel: NoteViewModel,
    userPreferences: UserPreferences
){
    val accountId by userPreferences.getLoggedInAccountId(LocalContext.current).collectAsState(initial = null)
    val logs by logViewModel.getAllLogsFromAccount(accountId!!).observeAsState(listOf())
    val recipes by recipeViewModel.getAllRecipes().observeAsState(listOf())

    HomeScreen(
        navController = navController,
        mainState = MainState.HOME,
        accountId = accountId!!,
        logs = logs,
        recipes = recipes,
        getNotesFromRecipe = { recipeId ->
            noteViewModel.getNotesFromRecipe(recipeId)
        }
    )
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    mainState: MainState = MainState.HOME,
    accountId: Int,
    logs: List<Log>,
    recipes: List<Recipe>,
    getNotesFromRecipe: (Int) -> LiveData<List<Note>>
){
    // Ratings
    val satisfied = ImageVector.vectorResource(id = R.drawable.satisfied)
    val happy = ImageVector.vectorResource(id = R.drawable.happy)
    val neutral = ImageVector.vectorResource(id = R.drawable.neutral)
    val unhappy = ImageVector.vectorResource(id = R.drawable.unhappy)
    val disappointed = ImageVector.vectorResource(id = R.drawable.disappointed)

    val ratings = listOf(
        Triple(-2,disappointed, Color(0xFFF33B3B)),
        Triple(-1,unhappy,Color(0xFFFFB001)),
        Triple(0,neutral,Color(0xFFCACA04)),
        Triple(1,happy,Color(0xFF02C801)),
        Triple(2,satisfied,Color(0xFF0095FF))
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

    // Get any Log Data
    val log = logs.find {
        Instant.ofEpochMilli(it.date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate() == Instant.ofEpochMilli(datePickedEpoch)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
    val isLogRecipes = log?.recipeIdList.isNullOrEmpty()
    var logRating by remember { mutableIntStateOf(log?.rating ?: 0) }
    var logNote by remember { mutableStateOf(log?.note ?: "") }
    LaunchedEffect (log){
        logRating = log?.rating ?: 0
        logNote = log?.note ?: ""
    }
    val logRecipes = recipes.filter{ log?.recipeIdList?.contains(it.recipeId) == true }
    val notesList = remember { mutableStateListOf<Note>() }
    val observers = remember { mutableMapOf<Int, Observer<List<Note>>>() }

    LaunchedEffect(logRecipes) {

        notesList.clear()

        logRecipes.forEach { recipe ->
            val observer = Observer<List<Note>> { notes ->
                if (notes.isNotEmpty()) {
                    val uniqueNotes = notes.filterNot { it in notesList }
                    val logRecipeNotes = uniqueNotes.filter { it.recipeOwnerId == recipe.recipeId }
                    notesList.addAll(logRecipeNotes) // Add all notes directly
                }
            }

            getNotesFromRecipe(recipe.recipeId).observeForever(observer)
            observers[recipe.recipeId] = observer // Store observer reference
        }
    }

    // Cleanup observers when Composable is destroyed
    DisposableEffect(Unit) {
        onDispose {
            logRecipes.forEach { recipe ->
                observers[recipe.recipeId]?.let { observer ->
                    getNotesFromRecipe(recipe.recipeId).removeObserver(observer)
                }
            }
            observers.clear() // Clear stored observers
        }
    }


    BareMainScreen(
        mainState = mainState,
        navController = navController
    ){
        innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ){
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ){
                    Text(text = "Today's Menu", style = MaterialTheme.typography.titleLarge)
                    Button(
                        onClick = { addSelected = true },
                        contentPadding = PaddingValues(top = 0.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text(text = "Add")
                    }
                }
                if(isLogRecipes){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .height(150.dp)
                        .fillMaxWidth()
                    ){ Text(text = "Looking a bit empty...", style = MaterialTheme.typography.headlineSmall) }

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
                ){
                    ratings.forEach { (value, icon, color) ->
                        IconButton( onClick = { logRating = value }) {
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
                    Column (
                        modifier = Modifier
                            .padding(16.dp)
                    ){
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
                            if (!personalExpanded){
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null
                                )
                            } else {
                                Row (
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ){
                                    Icon(
                                        imageVector = if (!editNoteSelected) Icons.Default.Edit else Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .clickable { editNoteSelected = !editNoteSelected }
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
                            onClick = {recipeExpanded = true}
                        )
                        .alpha(if (isLogRecipes) 0.5f else 1f),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Column (
                        modifier = Modifier
                            .padding(16.dp)
                    ){
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
                            if (notesList.isNotEmpty()){
                                LazyColumn (
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                ){
                                    notesList.forEach(){ note ->
                                        item {
                                            RecipeNote(
                                                recipeName = logRecipes.find{ it.recipeId == note.recipeOwnerId }?.recipeName ?: "Unknown Recipe",
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

            if (addSelected){
                HomeAddDialogue(
                    onDismissRequest = { addSelected = false },
                    mainAction = { navController.navigate(Screen.Product.route) },
                    secondAction = { navController.navigate(Screen.Recipes.route)}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenEmptyView(){
    val navController = rememberNavController()

    // Mock ViewModel or Data
    val mockNotes = mutableListOf(
        Note(1, 1, "Hello", 0),
        Note(2, 1, "Hello", 0)
    )

    // Simulate the getNotesFromRecipe function
    val getNotesFromRecipe: (Int) -> LiveData<List<Note>> = {
        MutableLiveData(mockNotes)
    }

    AppTheme {
        HomeScreen(
            navController = navController,
            mainState = MainState.HOME,
            accountId = 0,
            logs = emptyList(),
            recipes = emptyList(),
            getNotesFromRecipe = getNotesFromRecipe
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
            logId = 1,
            accountOwnerId = 0,
            date = System.currentTimeMillis(),
            rating = 2,
            recipeIdList = listOf(1, 2),
            productIdList = listOf(101, 202),
            note = "Tried a new recipe, turned out great!"
        ),
        Log(
            logId = 2,
            accountOwnerId = 0,
            date = System.currentTimeMillis() - 86_400_000, // 1 day ago
            rating = -1,
            recipeIdList = listOf(3),
            productIdList = listOf(303, 404),
            note = "Didn't like the taste, will try adjusting ingredients."
        ),
        Log(
            logId = 3,
            accountOwnerId = 0,
            date = System.currentTimeMillis() - 172_800_000, // 2 days ago
            rating = 1,
            recipeIdList = listOf(6, 5),
            productIdList = listOf(505),
            note = "A decent meal, but could use more seasoning."
        ),
        Log(
            logId = 4,
            accountOwnerId = 0,
            date = System.currentTimeMillis() - 259_200_000, // 3 days ago
            rating = 0,
            recipeIdList = emptyList(),
            productIdList = listOf(606, 707),
            note = "Tried a new product, unsure about it yet."
        ),
        Log(
            logId = 5,
            accountOwnerId = 0,
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

    // Simulate the getNotesFromRecipe function
    val getNotesFromRecipe: (Int) -> LiveData<List<Note>> = {
        MutableLiveData(mockNotes)
    }

    AppTheme {
        HomeScreen(
            navController = navController,
            mainState = MainState.HOME,
            accountId = 0,
            logs = sampleLogs,
            recipes = mockRecipes,
            getNotesFromRecipe = getNotesFromRecipe
        )
    }
}