package uk.ac.aber.dcs.souschefapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.souschefapp.R
import uk.ac.aber.dcs.souschefapp.database.models.Ingredient
import uk.ac.aber.dcs.souschefapp.database.models.Recipe
import uk.ac.aber.dcs.souschefapp.ui.components.BareRecipePageScreen
import uk.ac.aber.dcs.souschefapp.ui.components.CardRecipe
import uk.ac.aber.dcs.souschefapp.ui.components.ConfirmDialogue
import uk.ac.aber.dcs.souschefapp.ui.components.IngredientDialogue
import uk.ac.aber.dcs.souschefapp.ui.components.InstructionDialogue
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import uk.ac.aber.dcs.souschefapp.room_viewmodel.IngredientViewModel
import uk.ac.aber.dcs.souschefapp.room_viewmodel.RecipeViewModel

@Composable
fun TopRecipePageScreen(
    recipeId: Int,
    navController: NavHostController,
    recipeViewModel: RecipeViewModel,
    ingredientViewModel: IngredientViewModel,
){
    val ingredients by ingredientViewModel.getIngredientsFromRecipe(recipeId).observeAsState(listOf())
    val recipe by recipeViewModel.getRecipeById(recipeId).observeAsState()

    RecipePageScreen(
        navController = navController,
        recipe = recipe,
        ingredients = ingredients,
        onRecipeAdd = { newRecipe ->
            recipeViewModel.insertRecipe(newRecipe)
        },
        onRecipeUpdate = { newRecipe ->
            recipeViewModel.updateRecipe(newRecipe)
        },
        onRecipeDelete = { newRecipe ->
            recipeViewModel.deactivateRecipe(newRecipe.recipeId)
        },
        onIngredientAdd = { ingredient ->
            ingredientViewModel.insertIngredient(ingredient)
        },
        onIngredientUpdate = { ingredient ->
            ingredientViewModel.updateIngredient(ingredient)
        },
        onIngredientDelete = { ingredient ->
            ingredientViewModel.deleteIngredient(ingredient)
        }

    )
}

@Composable
fun RecipePageScreen(
    navController: NavHostController,
    recipe: Recipe? = null,
    ingredients: List<Ingredient> = emptyList(),
    onRecipeAdd: (Recipe) -> Unit = {},
    onRecipeUpdate: (Recipe) -> Unit = {},
    onRecipeDelete: (Recipe) -> Unit = {},
    onIngredientAdd: (Ingredient) -> Unit = {},
    onIngredientUpdate: (Ingredient) -> Unit = {},
    onIngredientDelete: (Ingredient) -> Unit = {}

){
    var nameText by remember { mutableStateOf(recipe?.recipeName ?: "") }
    var isIngredientDialog by remember { mutableStateOf(false) }
    var isInstructionDialog by remember { mutableStateOf(false) }
    var isIngredientDelete by remember { mutableStateOf(false) }
    var isInstructionDelete by remember { mutableStateOf(false) }
    var isBackConfirm by remember { mutableStateOf(false) }
    var isEdit by remember { mutableStateOf(false) }

    //      Mutable Data
    val mutableIngredientList = ingredients.toMutableList()
    val mutableInstructionList = recipe?.instructionList?.toMutableList() ?: mutableListOf()
    var editIngredient: Ingredient? = null
    var editInstruction: String = ""

    BareRecipePageScreen(
        navController = navController,
        isEdit = isEdit,
        isBottomBar = false,
        editFunction = { isEdit = !isEdit },
        saveFunction = {
            if (recipe != null){
                val newRecipe = Recipe(
                    recipeId = recipe.recipeId,
                    recipeName = nameText,
                    instructionList = mutableInstructionList.toList()
                )
                onRecipeUpdate(newRecipe)
            } else {
                onRecipeAdd(Recipe(
                    recipeName = nameText,
                    instructionList = mutableInstructionList.toList()
                ))
            }
            isEdit = false
                       },
        deleteFunction = {
            if (recipe != null) onRecipeDelete(recipe)
            navController.popBackStack()
                         },
        backFunction = {
            isBackConfirm = true
        },


    ){ innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
                    .verticalScroll(rememberScrollState())

            ) {
                /*      Add Image       */
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    CardRecipe(
                        onClick = {},
                        modifier = Modifier
                            .border(width = 1.dp, color = MaterialTheme.colorScheme.onSurface, shape = RoundedCornerShape(12.dp))
                            .blur(1.dp)
                            .fillMaxWidth(0.8f)
                    )
                    Button(
                        onClick = { TODO("Implement Add image function") }
                    ){
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Text(
                            text = "Add Image"
                        )
                    }
                }

                Spacer( modifier = Modifier.height(8.dp))

                /*      Name TextField      */
                TextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer( modifier = Modifier.height(8.dp))

                /*          Ingredients         */
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(300.dp)

                ){
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ){
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ingredients")
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        isIngredientDialog = true
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn {
                            mutableIngredientList.forEach{ ingredient ->
                                item{
                                    var expanded by remember { mutableStateOf(false) }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Default.List,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .weight(0.1f)
                                        )
                                        Text(
                                            text = "${ingredient.quantity}${ingredient.unit} ${ingredient.name} ${ingredient.description}",
                                            modifier = Modifier
                                                .weight(1f)
                                        ) // I need a system that changes the units
                                        Box(
                                            modifier = Modifier
                                                .wrapContentSize(Alignment.TopStart)
                                                .weight(0.1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .clickable { expanded = true }
                                            )
                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false }
                                            ) {
                                                DropdownMenuItem(
                                                    text = { Text("Edit") },
                                                    onClick = {
                                                        editIngredient = ingredient
                                                        isIngredientDialog = true
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Delete") },
                                                    onClick = {
                                                        editIngredient = ingredient
                                                        isIngredientDelete = true
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }

                Spacer( modifier = Modifier.height(8.dp))

                /*          Instructions        */
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(300.dp)

                ){
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ){
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Instructions")
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable { isInstructionDialog = true }
                            )
                        }
                        LazyColumn {
                            mutableInstructionList.forEach{ instruction ->
                                item{
                                    var expanded by remember { mutableStateOf(false) }
                                    Row{
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.draggable),
                                            contentDescription = null
                                        )
                                        Text(text = instruction)
                                        Box(modifier = Modifier
                                            .wrapContentSize(Alignment.TopStart)) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .clickable { expanded = true }
                                            )
                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false }
                                            ) {
                                                DropdownMenuItem(
                                                    text = { Text("Edit") },
                                                    onClick = {
                                                        editInstruction = instruction
                                                        isInstructionDialog = true
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Delete") },
                                                    onClick = {
                                                        editInstruction = instruction
                                                        isInstructionDelete = true
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isIngredientDialog){
                IngredientDialogue(
                    onDismissRequest = { isInstructionDialog = false },
                    mainAction = { name, amount, unit, extra ->
                        val newIngredient = Ingredient(
                            recipeOwnerId = recipe!!.recipeId,
                            name = name,
                            description = extra,
                            quantity = amount.toInt(),
                            unit = unit
                        )
                        if (editIngredient == null) onIngredientAdd(newIngredient) else onIngredientUpdate(newIngredient)
                    },
                    ingredient = editIngredient
                )
                editIngredient = null
            }

            if (isIngredientDelete){
                ConfirmDialogue(
                    onDismissRequest = { isIngredientDelete = false },
                    mainAction = { onIngredientDelete(editIngredient!!) },
                    supportingText = "Deleting an ingredient is permanent.",
                    mainButtonText = "Delete"
                )
            }

            if (isInstructionDialog){
                InstructionDialogue(
                    onDismissRequest = { isInstructionDialog = false },
                    mainAction = { instruction ->
                        mutableInstructionList.add(instruction)
                    },
                    instruction = editInstruction
                )
                editInstruction = ""
            }

            if (isInstructionDelete){
                ConfirmDialogue(
                    onDismissRequest = { isIngredientDelete = false },
                    mainAction = { mutableInstructionList.remove(editInstruction) },
                    supportingText = "Deleting an instruction is permanent.",
                    mainButtonText = "Delete"
                )
            }

            if (isBackConfirm){
                ConfirmDialogue(
                    onDismissRequest = { isIngredientDelete = false },
                    mainAction = { navController.popBackStack() },
                    title = "Ready to go?",
                    supportingText = "Any unsaved data will be forgotten!",
                    mainButtonText = "Continue"
                )
            }

        }
    }
}

@Preview
@Composable
fun RecipePageScreenEmptyPreview(){
    val navController = rememberNavController()
    AppTheme {
        RecipePageScreen(
            navController = navController
        )
    }
}

@Preview
@Composable
fun RecipePageScreenPreview(){
    val navController = rememberNavController()
    val englishBreakfastRecipe = Recipe(
        recipeName = "Full English Breakfast",
        description = "A classic English breakfast with eggs, bacon, sausages, beans, and more.",
        instructionList = listOf(
            "1. Heat a pan over medium heat and cook the bacon until crispy.",
            "2. In the same pan, cook the sausages until browned and cooked through.",
            "3. Grill or fry the tomato halves until slightly charred.",
            "4. Sauté the mushrooms in butter until golden brown.",
            "5. Heat the baked beans in a small saucepan.",
            "6. Fry the black pudding slices until crispy.",
            "7. Cook the eggs to your preference (fried, scrambled, or poached).",
            "8. Toast the bread and butter it.",
            "9. Serve everything hot with a cup of English tea!"
        ),
        tags = listOf("Breakfast", "British", "Traditional"),
        isActive = true
    )
    val englishBreakfastIngredients = listOf(
        Ingredient(recipeOwnerId = 1, name = "Eggs", description = "Large eggs, preferably free-range", quantity = 2, unit = "pieces"),
        Ingredient(recipeOwnerId = 1, name = "Bacon", description = "Thick-cut smoked back bacon", quantity = 3, unit = "slices"),
        Ingredient(recipeOwnerId = 1, name = "Sausages", description = "Pork sausages, traditional British style", quantity = 2, unit = "pieces"),
        Ingredient(recipeOwnerId = 1, name = "Baked Beans", description = "Classic British-style baked beans in tomato sauce", quantity = 200, unit = "grams"),
        Ingredient(recipeOwnerId = 1, name = "Tomatoes", description = "Ripe tomatoes, halved and grilled", quantity = 1, unit = "piece"),
        Ingredient(recipeOwnerId = 1, name = "Mushrooms", description = "Button mushrooms, sautéed in butter", quantity = 100, unit = "grams"),
        Ingredient(recipeOwnerId = 1, name = "Toast", description = "Thick slices of white or brown bread, toasted", quantity = 2, unit = "slices"),
        Ingredient(recipeOwnerId = 1, name = "Black Pudding", description = "Traditional British black pudding (blood sausage)", quantity = 1, unit = "slice"),
        Ingredient(recipeOwnerId = 1, name = "Hash Browns", description = "Crispy golden hash browns", quantity = 2, unit = "pieces"),
        Ingredient(recipeOwnerId = 1, name = "Butter", description = "For spreading on toast", quantity = 10, unit = "grams"),
        Ingredient(recipeOwnerId = 1, name = "Tea", description = "English breakfast tea with milk", quantity = 1, unit = "cup")
    )
    AppTheme {
        RecipePageScreen(
            navController = navController,
            recipe = englishBreakfastRecipe,
            ingredients = englishBreakfastIngredients
        )
    }
}