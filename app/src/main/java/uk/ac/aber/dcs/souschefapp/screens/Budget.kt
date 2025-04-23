package uk.ac.aber.dcs.souschefapp.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.souschefapp.database.MainState
import uk.ac.aber.dcs.souschefapp.firebase.viewmodel.BudgetViewModel
import uk.ac.aber.dcs.souschefapp.ui.components.BareMainScreen

@Composable
fun TopBudgetScreen (
    context: ComponentActivity,
    navController: NavHostController,
    budgetViewModel: BudgetViewModel
){
    val bitmap by budgetViewModel.receiptBitmap.observeAsState()
    val ocrText by budgetViewModel.ocrText.observeAsState()
    BudgetScreen(
        navController = navController,
        bitmap = bitmap,
        ocrText = ocrText,
        setBitmap = { result ->
            budgetViewModel.setBitmap(result)
        }
    )
}

@Composable
fun BudgetScreen (
    navController: NavHostController,
    bitmap: Bitmap? = null,
    ocrText: String? = null,
    setBitmap: (Bitmap) -> Unit,
){

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Camera
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { result ->
        result?.let { setBitmap(it) }
    }

    BareMainScreen(
        navController = navController,
        mainState = MainState.HOME,
    ){ innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item{
                    Button(onClick = { launcher.launch() }) {
                        Text("Scan Receipt")
                    }
                }
                item{
                    bitmap?.let {
                        Image(bitmap = it.asImageBitmap(), contentDescription = null)
                    }

                }
                item{
                    if (ocrText != null) {
                        if (ocrText.isNotBlank()) {
                            Text(text = ocrText)
                        }
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetScreenPreview() {
    val navController = rememberNavController()

    BudgetScreen(
        navController = navController,
        setBitmap = {}
    )
}