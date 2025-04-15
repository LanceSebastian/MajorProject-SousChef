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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.souschefapp.firebase.Mode
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
    val mode by productViewModel.mode.observeAsState(Mode.View)

    ProductScreen(
        navController = navController,
        product = product,
        mode = mode,
        setMode = { newMode ->
            productViewModel.setMode(newMode)
        },
        clearSelectProduct = {
            productViewModel.clearSelectProduct()
        },
        addProductToLog = { newProduct ->
            logViewModel.addProductToCurrentLog(userId, newProduct.productId, context)
        },
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
    mode: Mode = Mode.View,
    setMode: (Mode) -> Unit,
    product: Product? = null,
    clearSelectProduct: () -> Unit,
    addProductToLog: (Product) -> Unit,
    addProduct: (Product) -> Unit,
    updateProduct: (Product) -> Unit,
    archiveProduct: (Product) -> Unit
){
    val isProductExist = product != null

    var isBackConfirm by remember { mutableStateOf(false) }

    var nameText by remember { mutableStateOf(product?.name ?: "") }
    var priceText by remember { mutableStateOf((product?.price ?: "").toString()) }

    val isModified by remember {
        derivedStateOf {
            product?.let {
                it.name != nameText || it.price != priceText.toDoubleOrNull()
            } ?: (nameText.isNotBlank() || priceText.isNotBlank())
        }
    }

    LaunchedEffect(product?.productId) {
        if (mode != Mode.View && product != null && product.productId.isNotBlank()) {
            addProductToLog(product)
            navController.popBackStack()
            clearSelectProduct()
        }
    }


    BareRecipePageScreen(
        navController = navController,
        mode = mode,
        isBottomBar = false,
        editFunction = { setMode(Mode.Edit) },

        // Check if there are unsaved changes
        backFunction = {
            if (mode == Mode.View || !isModified) {
                navController.popBackStack()
            } else {
                isBackConfirm = true
            }
        },

        // Create or Update Product
        saveFunction = {
            val newProduct = product?.copy(
                name = nameText,
                price = priceText.toDouble()
            ) ?: Product(
                name = nameText,
                price = priceText.toDouble()
            )

            if (mode == Mode.Edit) updateProduct(newProduct) else addProduct(newProduct)
            setMode(Mode.View)
        },

        // Archive Product if it exists.
        crossFunction = {
            nameText = product?.name ?: ""
            priceText = (product?.price ?: "").toString()
            setMode(Mode.View)
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
                    if (mode != Mode.View) {
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
                }

                Spacer( modifier = Modifier.height(8.dp))

                /*      Name TextField      */
                TextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Name") },
                    enabled = mode != Mode.View,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer( modifier = Modifier.height(8.dp))

                /*      Price TextField      */
                TextField(
                    value = priceText,
                    onValueChange = { newText ->
                        if (newText.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            priceText = newText
                        }
                    },
                    label = { Text("Price") },
                    enabled = mode != Mode.View,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
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
fun CreateProductScreenPreview(){
    val navController = rememberNavController()
    AppTheme {
        ProductScreen(
            navController = navController,
            mode = Mode.Create,
            setMode = {},
            addProductToLog = {},
            addProduct = {},
            updateProduct = {},
            archiveProduct = {},
            clearSelectProduct = {},
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
            mode = Mode.View,
            setMode = {},
            addProductToLog = {},
            addProduct = {},
            updateProduct = {},
            archiveProduct = {},
            clearSelectProduct = {},
        )
    }
}

@Preview
@Composable
fun EditProductScreenPreview(){
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
            mode = Mode.Edit,
            setMode = {},
            addProductToLog = {},
            addProduct = {},
            updateProduct = {},
            archiveProduct = {},
            clearSelectProduct = {},
        )
    }
}