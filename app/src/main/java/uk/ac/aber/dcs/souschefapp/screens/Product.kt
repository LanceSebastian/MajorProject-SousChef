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
import androidx.compose.runtime.DisposableEffect
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
import com.google.firebase.Timestamp
import uk.ac.aber.dcs.souschefapp.firebase.Product
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.AuthViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.ProductViewModel
import uk.ac.aber.dcs.souschefapp.ui.components.BareRecipePageScreen
import uk.ac.aber.dcs.souschefapp.ui.components.CardRecipe
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import java.util.Date

// Add Context
// if product == null, addProduct else updateProduct
// if product, fill in fields
@Composable
fun TopProductScreen(
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
            productViewModel.createProduct(userId, newProduct)
        },
        updateProduct = { newProduct ->
            productViewModel.updateProduct(userId, newProduct)
        },
        deleteProduct = { newProduct ->
            productViewModel.deleteProduct(userId, newProduct.productId)
        },
    )
}

@Composable
fun ProductScreen(
    navController: NavHostController,
    product: Product? = null,
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

        },
        saveFunction = {
            isEdit = !isEdit
//            val product = product?.copy(
//                productId = product.productId,
//                name = ,
//                createdBy = userId
//            ) ?: Log(
//                logId = standardDate(millis).toString(),
//                createdAt = Timestamp(Date(millis)),
//                createdBy = userId
//            )
//            updateProduct(
//
//                Product(
//                    productId = product?.productId,
//                    name = nameText,
//                    price = priceText.toDouble()
//                )
//            )
        },
        deleteFunction = {

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
                        }
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
            addProduct = {},
            updateProduct = {},
            deleteProduct = {},
        )
    }
}