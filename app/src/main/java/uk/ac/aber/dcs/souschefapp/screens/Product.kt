package uk.ac.aber.dcs.souschefapp.screens

import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import uk.ac.aber.dcs.souschefapp.firebase.Product
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.AuthViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.ProductViewModel
import uk.ac.aber.dcs.souschefapp.ui.components.BareRecipePageScreen
import uk.ac.aber.dcs.souschefapp.ui.components.CardRecipe
import uk.ac.aber.dcs.souschefapp.ui.components.ConfirmDialogue
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

// Add Context
// if product == null, addProduct else updateProduct
// if product, fill in fields
// Add save and exit dialog
@Composable
fun TopProductScreen(
    context: ComponentActivity,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    logViewModel: LogViewModel,
){
    val user by authViewModel.user.observeAsState(null)
    val userId = user?.uid

    val product by productViewModel.selectProduct.observeAsState(null)

    // Listen for logs in real-time when the user exists
    DisposableEffect(userId) {
        if(userId != null){
            productViewModel.readProducts(userId)
        }

        onDispose {
            productViewModel.stopListening()
        }
    }

    ProductScreen(
        navController = navController,
        product = product,
        addProduct = { newProduct ->
            productViewModel.createProduct(userId, newProduct, context)
        },
        updateProduct = { newProduct ->
            productViewModel.updateProduct(userId, newProduct, context)
        },
        archiveProduct = { newProduct ->
            productViewModel.archiveProduct(userId, newProduct.productId, context)
        },
    )
}

@Composable
fun ProductScreen(
    navController: NavHostController,
    product: Product? = null,
    addProduct: (Product) -> Unit,
    updateProduct: (Product) -> Unit,
    archiveProduct: (Product) -> Unit
){
    val isProductExist = product != null

    var isBackConfirm by remember { mutableStateOf(false) }

    var nameText by remember { mutableStateOf(product?.name ?: "") }
    var priceText by remember { mutableStateOf((product?.price ?: "").toString()) }
    var isEdit by remember { mutableStateOf( product == null) }

    var isModified = product?.let {
        it.name != nameText || it.price.toString() != priceText
    } ?: true


    BareRecipePageScreen(
        navController = navController,
        isEdit = isEdit,
        isBottomBar = false,
        editFunction = { isEdit = !isEdit },

        // Check if there are unsaved changes
        backFunction = {
            if (isModified) isBackConfirm = true else navController.popBackStack()
        },

        //  Create or Update Product
        saveFunction = {
            isEdit = !isEdit

            val updatedName = nameText
            val updatedPrice = priceText.toDouble()

            val newProduct = product?.copy(
                name = updatedName,
                price = updatedPrice
            ) ?: Product(
                name = updatedName,
                price = updatedPrice
            )

            if (isProductExist) updateProduct(newProduct) else addProduct(newProduct)

            isModified = false
        },

        // Archive Product if it exists.
        deleteFunction = {
            val updatedName = nameText
            val updatedPrice = priceText.toDouble()

            val newProduct = product?.copy(
                name = updatedName,
                price = updatedPrice
            ) ?: Product(
                name = updatedName,
                price = updatedPrice
            )

            if (isProductExist) archiveProduct(newProduct)
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
                        onClick = {
                            TODO("Implement Add image function")
                        },
                        enabled = false
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

            if (isBackConfirm){
                val updatedName = nameText
                val updatedPrice = priceText.toDouble()

                val newProduct = product?.copy(
                    name = updatedName,
                    price = updatedPrice
                ) ?: Product(
                    name = updatedName,
                    price = updatedPrice
                )

                ConfirmDialogue(
                    onDismissRequest = { isBackConfirm = false },
                    mainAction = {
                        if (isProductExist) updateProduct(newProduct) else addProduct(newProduct)
                        navController.popBackStack()
                                 },
                    secondAction = { navController.popBackStack() },
                    title = "Leaving already?",
                    supportingText = "Do you want to save your changes before you go?",
                    mainButtonText = "Save",
                    secondButtonText = "Don't Save"
                )
            }
        }
    }
}

@Preview
@Composable
fun NewProductScreenPreview(){
    val navController = rememberNavController()
    AppTheme {
        ProductScreen(
            navController = navController,
            addProduct = {},
            updateProduct = {},
            archiveProduct = {},
        )
    }
}

@Preview
@Composable
fun ViewProductScreenPreview(){
    val navController = rememberNavController()
    AppTheme {
        ProductScreen(
            navController = navController,
            product = Product(
                productId = "",
                createdBy = "0",
                name = "Tesco Meal Deal",
                price = 3.40
            ),
            addProduct = {},
            updateProduct = {},
            archiveProduct = {},
        )
    }
}