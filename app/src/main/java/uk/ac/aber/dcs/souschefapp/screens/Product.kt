package uk.ac.aber.dcs.souschefapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.souschefapp.database.UserPreferences
import uk.ac.aber.dcs.souschefapp.database.models.Log
import uk.ac.aber.dcs.souschefapp.database.models.Product
import uk.ac.aber.dcs.souschefapp.ui.components.BareRecipePageScreen
import uk.ac.aber.dcs.souschefapp.ui.components.CardRecipe
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import uk.ac.aber.dcs.souschefapp.room_viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.room_viewmodel.ProductViewModel

@Composable
fun TopProductScreen(
    navController: NavHostController,
    productViewModel: ProductViewModel,
    logViewModel: LogViewModel,
    userPreferences: UserPreferences,
    productId: Int,
    logDate: Long
){
    val accountId by userPreferences.accountId.observeAsState()
    android.util.Log.d("RoomDB", "Fetched data: " + accountId.toString())
    val product by productViewModel.getProductFromId(productId).observeAsState()
    val log by logViewModel.getLogFromDate(logDate).observeAsState()
    if (accountId != null) {
        ProductScreen(
            navController = navController,
            product = product,
            accountId = accountId!!,
            log = log,
            date = logDate,
            addLog = { newLog ->
                logViewModel.insertLog(newLog)
            },
            updateLog = { newLog ->
                logViewModel.updateLog(newLog)
            },
            addProduct = { newProduct ->
                productViewModel.insertProduct(newProduct)
            },
            updateProduct = { newProduct ->
                productViewModel.updateProduct(newProduct)
            },
            deleteProduct = { newProduct ->
                productViewModel.deleteProduct(newProduct)
            },
        )
    }
}

@Composable
fun ProductScreen(
    navController: NavHostController,
    product: Product? = null,
    accountId: Int,
    log: Log?,
    date: Long,
    addLog: (Log) -> Unit,
    updateLog: (Log) -> Unit,
    addProduct: (Product) -> Unit,
    updateProduct: (Product) -> Unit,
    deleteProduct: (Product) -> Unit
){
    var nameText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var isEdit by remember { mutableStateOf(false) }
    BareRecipePageScreen(
        navController = navController,
        isEdit = isEdit,
        isBottomBar = false,
        editFunction = { isEdit = !isEdit },
        backFunction = {
            if (nameText.isNotEmpty() || priceText.isNotEmpty()) {
                val newProduct = Product(
                    accountOwnerId = accountId,
                    name = nameText,
                    price = priceText.toBigDecimal()
                )
                val newLog = log?.let {
                    it.copy(productIdList = it.productIdList + newProduct.productId)
                } ?: Log(
                    accountOwnerId = accountId,
                    date = date,
                    productIdList = listOf(newProduct.productId)
                )

                if (log != null) updateLog(newLog) else addLog(newLog)
            }
        },
        saveFunction = {
            val newProduct = product?.copy(
                name = nameText,
                price = priceText.toBigDecimal()
            ) ?: Product(
                accountOwnerId = accountId,
                name = nameText,
                price = priceText.toBigDecimal()
            )

            if (product != null) updateProduct(newProduct) else addProduct(newProduct)
            isEdit = false
                       },
        deleteFunction = {
            if(product != null) deleteProduct(product)
            navController.popBackStack()
        },
    ){ innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 8.dp)
            ){
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
                    enabled = isEdit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer( modifier = Modifier.height(8.dp))

                /*      Price TextField      */
                TextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price") },
                    enabled = isEdit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ProductScreenPreview(){
    val navController = rememberNavController()
    AppTheme {
        ProductScreen(
            navController = navController,
            accountId = 0,
            log = null,
            date = 0,
            addProduct = {},
            updateProduct = {},
            deleteProduct = {},
            addLog = {},
            updateLog = {}
        )
    }
}