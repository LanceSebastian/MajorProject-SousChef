package uk.ac.aber.dcs.souschefapp.screens

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.firebase.EditMode
import uk.ac.aber.dcs.souschefapp.firebase.Product
import uk.ac.aber.dcs.souschefapp.firebase.UploadState
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.AuthViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.LogViewModel
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.ProductViewModel
import uk.ac.aber.dcs.souschefapp.ui.components.BareSecondaryScreen
import uk.ac.aber.dcs.souschefapp.ui.components.CardRecipe
import uk.ac.aber.dcs.souschefapp.ui.components.ChoiceDialogue
import uk.ac.aber.dcs.souschefapp.ui.components.ConfirmDialogue
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme
import java.io.File

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
    val editMode by productViewModel.editMode.observeAsState(EditMode.View)
    val uploadState by productViewModel.uploadState.observeAsState(UploadState.Idle)

    val coroutineScope = rememberCoroutineScope()

    ProductScreen(
        context = context,
        navController = navController,
        uploadState = uploadState,
        product = product,
        editMode = editMode,
        setMode = { newMode ->
            productViewModel.setMode(newMode)
        },
        clearSelectProduct = {
            productViewModel.clearSelectProduct()
        },
        createProductToLog = { newProduct, imageUri ->
            coroutineScope.launch {
                val productId = productViewModel.createProductAndId(userId, newProduct, imageUri, context)
                if (productId != null){
                    logViewModel.addProductToCurrentLog(userId, productId, context)
                    navController.popBackStack()
                }
            }
        },
        updateProduct = { newProduct, imageUri ->
            productViewModel.updateProduct(userId, newProduct, imageUri, context)
        },
        archiveProduct = { newProduct ->
            productViewModel.archiveProduct(userId, newProduct.productId, context)
        },
        prepareCamera = {
            val photoFile = File.createTempFile("temp_image", ".jpg", context.cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
        }
    )
}

@Composable
fun ProductScreen(
    context: ComponentActivity,
    navController: NavHostController,
    uploadState: UploadState = UploadState.Idle,
    editMode: EditMode = EditMode.View,
    setMode: (EditMode) -> Unit,
    product: Product? = null,
    clearSelectProduct: () -> Unit,
    createProductToLog: (Product, Uri?) -> Unit,
    updateProduct: (Product, Uri?) -> Unit,
    archiveProduct: (Product) -> Unit,
    prepareCamera: () -> Uri,
){
    val isProductExist = product != null

    var isBackConfirm by remember { mutableStateOf(false) }

    var nameText by remember { mutableStateOf(product?.name ?: "") }
    var priceText by remember { mutableStateOf((product?.price ?: "").toString()) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isImageChanged by remember { mutableStateOf(false) }
    var isMediaChoiceDialog by remember { mutableStateOf(false) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) cameraUri?.let { selectedImageUri = it }
    }

    // Gallery launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
        isImageChanged = true
    }

    val isModified by remember {
        derivedStateOf {
            product?.let {
                it.name != nameText || it.price != priceText.toDoubleOrNull()
            } ?: (nameText.isNotBlank() || priceText.isNotBlank())
        }
    }

    BareSecondaryScreen(
        navController = navController,
        editMode = editMode,
        mainState = MainState.HOME,
        isBottomBar = false,
        editFunction = { setMode(EditMode.Edit) },

        // Check if there are unsaved changes
        backFunction = {
            if (editMode == EditMode.View || !isModified || !isImageChanged) {
                navController.popBackStack()
                clearSelectProduct()
                setMode(EditMode.View)
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

            if (editMode == EditMode.Edit) {
                updateProduct(newProduct, selectedImageUri)
            } else {
                createProductToLog(newProduct, selectedImageUri)
            }
            setMode(EditMode.View)
        },

        // Cancel Edit.
        crossFunction = {
            // Dialog Check
            nameText = product?.name ?: ""
            priceText = (product?.price ?: "").toString()
            setMode(EditMode.View)
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
                        imageUri = (selectedImageUri),
                        imageUrl = product?.imageUrl,
                        modifier = Modifier
                            .border(width = 1.dp, color = MaterialTheme.colorScheme.onSurface, shape = RoundedCornerShape(12.dp))
                            .blur(1.dp)
                            .fillMaxWidth(0.8f)
                    )

                    when (uploadState) {
                        is UploadState.Loading -> CircularProgressIndicator()
                        is UploadState.Success -> Text(
                            text = "Product created!",
                            color = MaterialTheme.colorScheme.onSurface)
                        is UploadState.Error -> Text(
                            text = "Error: ${uploadState.message}",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        else -> {
                            if (editMode != EditMode.View) {
                                Button(
                                    onClick = {
                                        isMediaChoiceDialog = true
                                    },
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
                    }
                }

                Spacer( modifier = Modifier.height(8.dp))

                /*      Name TextField      */
                TextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Name") },
                    enabled = editMode != EditMode.View,
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
                    enabled = editMode != EditMode.View,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            if (isMediaChoiceDialog){
                ChoiceDialogue(
                    onDismissRequest = { isMediaChoiceDialog = false },
                    mainAction = { imagePickerLauncher.launch("image/*") },
                    secondAction = {
                        cameraUri = prepareCamera()
                        cameraUri?.let { uri ->
                            cameraLauncher.launch(uri)
                        }
                    },
                    mainText = "Gallery",
                    secondText = "Camera"
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
                        if (isProductExist) updateProduct(newProduct, selectedImageUri) else createProductToLog(newProduct, selectedImageUri)
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
    val context = LocalContext.current
    val activity = context as ComponentActivity
    AppTheme {
        ProductScreen(
            context = activity,
            navController = navController,
            editMode = EditMode.Create,
            setMode = {},
            createProductToLog = {_,_ ->},
            updateProduct = {_,_ ->},
            archiveProduct = {},
            clearSelectProduct = {},
            prepareCamera = {Uri.parse("file://mock")}
        )
    }
}

@Preview
@Composable
fun ViewProductScreenPreview(){
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as ComponentActivity
    AppTheme {
        ProductScreen(
            context = activity,
            navController = navController,
            product = Product(
                productId = "",
                createdBy = "0",
                name = "Tesco Meal Deal",
                price = 3.40
            ),
            editMode = EditMode.View,
            setMode = {},
            createProductToLog = {_,_ ->},
            updateProduct = {_,_ ->},
            archiveProduct = {},
            clearSelectProduct = {},
            prepareCamera = {Uri.parse("file://mock")}
        )
    }
}

@Preview
@Composable
fun EditProductScreenPreview(){
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as ComponentActivity
    AppTheme {
        ProductScreen(
            context = activity,
            navController = navController,
            product = Product(
                productId = "",
                createdBy = "0",
                name = "Tesco Meal Deal",
                price = 3.40
            ),
            editMode = EditMode.Edit,
            setMode = {},
            createProductToLog = {_,_ ->},
            updateProduct = {_,_ ->},
            archiveProduct = {},
            clearSelectProduct = {},
            prepareCamera = {Uri.parse("file://mock")}
        )
    }
}