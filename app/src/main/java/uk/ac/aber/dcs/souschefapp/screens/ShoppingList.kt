package uk.ac.aber.dcs.souschefapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.R
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.firebase.Ingredient
import uk.ac.aber.dcs.souschefapp.firebase.Log
import uk.ac.aber.dcs.souschefapp.firebase.Recipe
import uk.ac.aber.dcs.souschefapp.firebase.SelectMode
import uk.ac.aber.dcs.souschefapp.firebase.ShoppingItem
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.AuthViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.RecipeViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.ShoppingViewModel
import uk.ac.aber.dcs.souschefapp.ui.components.BareMainScreen
import uk.ac.aber.dcs.souschefapp.ui.components.CustomCalendar
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

@Composable
fun TopShoppingListScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    logViewModel: LogViewModel,
    shoppingViewModel: ShoppingViewModel
){
    val user by authViewModel.user.observeAsState()
    val userId = user?.uid

    val logs by logViewModel.logs.observeAsState(emptyList())
    val shoppingList by shoppingViewModel.shoppingItems.observeAsState(emptyList())

    // Listen for logs in real-time when the user exists
    DisposableEffect(Unit) {
        if (userId != null){
            logViewModel.readLogs(userId)
            shoppingViewModel.readItems(userId)
        }

        onDispose {
            logViewModel.stopListening()
            shoppingViewModel.stopListening()
        }
    }

    ShoppingListScreen(
        navController = navController,
        logs = logs,
        shoppingList = shoppingList,
        selectLogs = { list ->
            logViewModel.readLogsByDates(userId, list)
        },
        onSave = { newList ->
            shoppingViewModel.syncShoppingList(userId, newList)
        },
        fetchIngredients = { dates ->
            if (dates.isNotEmpty()) {
                shoppingViewModel.fetchCompiledIngredients(userId, logs, dates)
            }
        }
    )
}

@Composable
fun ShoppingListScreen(
    navController: NavHostController,
    logs: List<Log> = emptyList(),
    shoppingList: List<ShoppingItem> = emptyList(),
    selectLogs: (List<LocalDate>) -> Unit = {},
    onSave: (List<ShoppingItem>) -> Unit = {},
    fetchIngredients: (List<LocalDate>) -> Unit,
) {
    val localShoppingList = remember { mutableStateListOf<ShoppingItem>() }

    LaunchedEffect(shoppingList) {
        localShoppingList.clear()
        localShoppingList.addAll(shoppingList)
    }


    val checkedList by remember {
        derivedStateOf { localShoppingList.filter { it.checked } }
    }
    val uncheckedList by remember {
        derivedStateOf { localShoppingList.filterNot { it.checked } }
    }

    var isCalendar by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    var isEdit by remember { mutableStateOf(false) }

    BareMainScreen(
        navController = navController,
        mainState = MainState.HOME,
        selectMode = if (isEdit) SelectMode.Shopping_Edit else SelectMode.Shopping,
        onSearch = {
            isCalendar = true
        },
        onSecond = {
            if (isEdit) onSave(localShoppingList)
            isEdit = !isEdit
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            LazyColumn {
                items(uncheckedList) { item ->
                    val index = localShoppingList.indexOfFirst { it.itemId == item.itemId }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = item.checked,
                            onCheckedChange = {
                                if (index != -1) localShoppingList[index] = item.copy(checked = it)
                            },
                            modifier = Modifier
                                .weight(0.1f)
                        )
                        if (isEdit) {
                            BasicTextField(
                                value = item.content,
                                onValueChange = {
                                    if (index != -1) localShoppingList[index] =
                                        item.copy(content = it)
                                },
                                modifier = Modifier
                                    .weight(0.8f)
                            )
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        localShoppingList.remove(item)
                                    }
                                    .weight(0.1f)
                            )
                        } else {
                            Text(
                                text = item.content,
                                modifier = Modifier.weight(0.9f)
                            )
                        }
                    }
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = {
                                localShoppingList.add(
                                    ShoppingItem(
                                        content = "New Item"
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                            Text("List Item")
                        }
                    }
                }

                if (checkedList.isNotEmpty()) item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .clickable { isExpanded = !isExpanded }
                            .padding(horizontal = 8.dp)
                    ) {
                        if (isExpanded) Icon(
                            painter = painterResource(R.drawable.arrow_drop_up),
                            contentDescription = null
                        )
                        else Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                        Text(
                            text = "${checkedList.size} Checked Items",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                if (isExpanded) items(checkedList) { item ->
                    val index = localShoppingList.indexOfFirst { it.itemId == item.itemId }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = item.checked,
                            onCheckedChange = {
                                if (index != -1) localShoppingList[index] = item.copy(checked = it)
                            },
                            modifier = Modifier
                                .weight(0.1f)
                        )

                        if (isEdit) {
                            BasicTextField(
                                value = item.content,
                                onValueChange = {
                                    if (index != -1) localShoppingList[index] =
                                        item.copy(content = it)
                                },
                                modifier = Modifier
                                    .weight(0.8f)
                            )
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        localShoppingList.remove(item)
                                    }
                                    .weight(0.1f)
                            )
                        } else {
                            Text(
                                text = item.content,
                                modifier = Modifier
                                    .weight(0.8f)
                            )
                        }
                    }
                }
            }
        }

        if (isCalendar) CustomCalendar(
            showDialog = isCalendar,
            onDismiss = { isCalendar = false },
            onDatesSelected = { list ->
                fetchIngredients(list)
            },
            logs = logs,
            isMany = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListScreenPreview(){
    val navController = rememberNavController()
    val sampleShoppingItems = listOf(
        ShoppingItem(
            itemId = "1",
            checked = false,
            content = "2 cups Flour - All-purpose flour"
        ),
        ShoppingItem(
            itemId = "2",
            checked = false,
            content = "3 pieces Eggs - Large eggs"
        ),
        ShoppingItem(
            itemId = "3",
            checked = true,
            content = "1.5 cups Sugar - Granulated white sugar"
        ),
        ShoppingItem(
            itemId = "4",
            checked = false,
            content = "100 grams Butter - Unsalted, melted"
        ),
        ShoppingItem(
            itemId = "5",
            checked = true,
            content = "1 tbsp Baking Powder - Leavening agent"
        )
    )
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
    AppTheme {
        ShoppingListScreen(
            navController = navController,
            shoppingList = sampleShoppingItems,
            fetchIngredients = {},
            logs = sampleLogs
        )
    }
}