package uk.ac.aber.dcs.souschefapp.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.firebase.EditMode
import uk.ac.aber.dcs.souschefapp.firebase.Recipe
import uk.ac.aber.dcs.souschefapp.firebase.SelectMode
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.AuthViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.RecipeViewModel
import uk.ac.aber.dcs.souschefapp.ui.components.BareMainScreen
import uk.ac.aber.dcs.souschefapp.ui.components.CardRecipe
import uk.ac.aber.dcs.souschefapp.ui.navigation.Screen
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@Composable
fun TopRecipesScreen(
    context: ComponentActivity,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    logViewModel: LogViewModel,
    recipeViewModel: RecipeViewModel
){
    val user by authViewModel.user.observeAsState()
    val userId = user?.uid

    val recipes by recipeViewModel.userRecipes.observeAsState(emptyList())
    val selectMode by recipeViewModel.selectMode.observeAsState(SelectMode.View)

    // Listen for logs in real-time when the user exists
    DisposableEffect(userId) {
        if(userId != null){
            recipeViewModel.readRecipes(userId)
        }

        onDispose {
            recipeViewModel.stopListening()
        }
    }

    RecipesScreen(
        navController = navController,
        recipes = recipes,
        selectMode = selectMode,
        selectRecipe = { recipeId ->
            recipeViewModel.selectRecipe(recipeId)
        },
        setEditMode = { newMode ->
            recipeViewModel.setEditMode( newMode )
        },
        setSelectMode = { newMode ->
            recipeViewModel.setSelectMode(newMode)
        },
        addRecipesToLog = { recipeList ->
            logViewModel.addRecipesToLog(userId, recipeList, context)
        },

    )
}

@Composable
fun RecipesScreen(
    navController: NavHostController,
    recipes: List<Recipe> = emptyList(),
    selectMode: SelectMode = SelectMode.View,
    selectRecipe: (String) -> Unit,
    setEditMode: (EditMode) -> Unit,
    setSelectMode: (SelectMode) -> Unit,
    addRecipesToLog: (List<Recipe>) -> Unit,
){
    var isSearch by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val filteredRecipes = recipes.filter { it.name.contains(searchText, ignoreCase = true) }
    var isFloatClick by remember { mutableStateOf(false) }
    var recipeNameText by remember { mutableStateOf("") }
    val selectedRecipes = remember { mutableStateListOf<Recipe>() }

    BareMainScreen(
        navController = navController,
        mainState = MainState.RECIPES,
        selectMode = selectMode,
        onSearch = { isSearch = !isSearch },
        onNavIcon = {
            setSelectMode(SelectMode.View)
        },
        floatButton = {
             if (selectMode == SelectMode.View) AddRecipeFloat(
                onFloatClick = {
                    setEditMode(EditMode.Create)
                    navController.navigate(Screen.RecipePage.route)
                }
            )
            if (selectMode == SelectMode.Select) SubmitRecipesFloat(
                onFloatClick = {
                    addRecipesToLog(selectedRecipes)
                    navController.popBackStack()
                    setSelectMode(SelectMode.View)
                },
                recipes = selectedRecipes
            )
        }
    ){ innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column {
                if (isSearch){
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null
                            )
                        },
                        placeholder = { Text("Search Recipes.") },
                        shape = RoundedCornerShape(50.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {
                    items(filteredRecipes.chunked(2)) { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            pair.forEach { recipe ->
                                var isSelected by remember { mutableStateOf(false) }
                                Box(modifier = Modifier.weight(1f)) {
                                    CardRecipe(
                                        text = recipe.name,
                                        onClick = {
                                            if (selectMode == SelectMode.View) {
                                                selectRecipe(recipe.recipeId)
                                                navController.navigate(Screen.RecipePage.route)
                                            }
                                            if (selectMode == SelectMode.Select){
                                                if (selectedRecipes.contains(recipe)) {
                                                    selectedRecipes.remove(recipe)
                                                    isSelected = false
                                                }
                                                else {
                                                    selectedRecipes.add(recipe)
                                                    isSelected = true
                                                }
                                            }
                                        },
                                        imageUrl = recipe.imageUrl,
                                        modifier = if (isSelected) Modifier.border(8.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(12.dp)) else Modifier
                                    )
                                }
                            }

                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun AddRecipeFloat(
    onFloatClick: () -> Unit,
){
    FloatingActionButton(
        onClick = { onFloatClick() },
        content = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
                Text(text = "Add Recipe")
            }
        },
    )
}

@Composable
private fun SubmitRecipesFloat(
    onFloatClick: () -> Unit,
    recipes: List<Recipe> = emptyList()
){
    if (recipes.isEmpty()) return
    FloatingActionButton(
        onClick = { onFloatClick() },
        content = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                Icon(Icons.Filled.Check, contentDescription = "Confirm")
                Text(text = "Confirm")
            }
        },
    )
}

@Preview
@Composable
fun RecipesScreenPreview(){
    val navController = rememberNavController()
    val sampleRecipes = mutableListOf(
        Recipe("1", "English Breakfast", ""),
        Recipe("2", "Chicken Sandwich", ""),
        Recipe("3", "Spaghetti Bolognese", ""),
        Recipe("4", "Vegetable Stir Fry", ""),
        Recipe("5", "Beef Tacos", ""),
        Recipe("6", "Margherita Pizza", "")
    )

    AppTheme {
        RecipesScreen(
            navController = navController,
            recipes = sampleRecipes,
            setEditMode = {},
            selectRecipe = {},
            addRecipesToLog = {},
            setSelectMode = {}
        )
    }
}

@Preview
@Composable
fun SelectRecipesScreenPreview(){
    val navController = rememberNavController()
    val sampleRecipes = mutableListOf(
        Recipe("1", "English Breakfast", ""),
        Recipe("2", "Chicken Sandwich", ""),
        Recipe("3", "Spaghetti Bolognese", ""),
        Recipe("4", "Vegetable Stir Fry", ""),
        Recipe("5", "Beef Tacos", ""),
        Recipe("6", "Margherita Pizza", "")
    )

    AppTheme {
        RecipesScreen(
            navController = navController,
            recipes = sampleRecipes,
            selectMode = SelectMode.Select,
            setEditMode = {},
            selectRecipe = {},
            addRecipesToLog = {},
            setSelectMode = {}
        )
    }
}