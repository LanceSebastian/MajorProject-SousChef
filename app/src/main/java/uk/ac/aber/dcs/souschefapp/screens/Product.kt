package uk.ac.aber.dcs.souschefapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.database.UserPreferences
import uk.ac.aber.dcs.souschefapp.database.models.Product
import uk.ac.aber.dcs.souschefapp.ui.components.BareMainScreen
import uk.ac.aber.dcs.souschefapp.ui.components.BareRecipePageScreen
import uk.ac.aber.dcs.souschefapp.ui.components.CardRecipe
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import uk.ac.aber.dcs.souschefapp.viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.viewmodel.ProductViewModel

@Composable
fun TopProductScreen(
    navController: NavHostController,
    productViewModel: ProductViewModel,
    userPreferences: UserPreferences,
    productId: Int
){
    val accountId by userPreferences.accountId.observeAsState()
    val product by productViewModel.getProductFromId(productId).observeAsState()
    if (accountId != null) {
        ProductScreen(
            navController = navController,
            product = product,
            accountId = accountId!!,
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
    addProduct: (Product) -> Unit,
    updateProduct: (Product) -> Unit,
    deleteProduct: (Product) -> Unit
){
    var nameText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var isEdit by remember { mutableStateOf(false) }
    BareRecipePageScreen(
        navController = navController,
        saveFunction = {
            if (product != null) {
                updateProduct(
                    Product(
                        productId = product.productId,
                        accountOwnerId = product.accountOwnerId,
                        name = nameText,
                        price = priceText.toBigDecimal()
                    )
                )
            } else {
                addProduct(
                    Product(
                        accountOwnerId = accountId,
                        name = nameText,
                        price = priceText.toBigDecimal()
                    )
                )
            }
            isEdit = false
                       },
        deleteFunction = {
            if(product != null) deleteProduct(product)
            navController.popBackStack()
        },
        isEdit = isEdit
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
            addProduct = {},
            updateProduct = {},
            deleteProduct = {}
        )
    }
}