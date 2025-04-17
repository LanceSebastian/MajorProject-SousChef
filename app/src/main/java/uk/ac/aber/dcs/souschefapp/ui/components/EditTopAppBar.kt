package uk.ac.aber.dcs.souschefapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.souschefapp.firebase.EditMode
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTopAppBar(
    navController: NavHostController,
    title: String = "",
    editMode: EditMode = EditMode.View,
    editFunction: () -> Unit,
    backFunction: () -> Unit,
    saveFunction: () -> Unit,
    crossFunction: () -> Unit,
){
    var isMutableExpanded by remember { mutableStateOf(false) }
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .fillMaxWidth()
                ) },
            navigationIcon = {
                IconButton(onClick = {
                    backFunction()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            actions = {
                if (editMode == EditMode.Create){
                    IconButton(
                        onClick = { saveFunction() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (editMode == EditMode.Edit){
                    IconButton(onClick = {
                        saveFunction()
                    }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }


                    IconButton(
                        onClick = { crossFunction() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (editMode == EditMode.View) {
                    IconButton(
                        onClick = { editFunction() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }


                    IconButton(
                        onClick = { isMutableExpanded = !isMutableExpanded }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface
            )

        )
    }

}

@Preview(showBackground = true)
@Composable
fun ViewTopAppBarPreview(){
    val navController = rememberNavController()
    var isEdit by remember { mutableStateOf(false) }
    AppTheme {
        EditTopAppBar(
            navController = navController,
            editMode = EditMode.View,
            editFunction = { isEdit = true },
            backFunction = {},
            saveFunction = { isEdit = false },
            crossFunction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditTopAppBarPreview(){
    val navController = rememberNavController()
    var isEdit by remember { mutableStateOf(true)}
    AppTheme {
        EditTopAppBar(
            navController = navController,
            editMode = EditMode.Edit,
            editFunction = { isEdit = true },
            backFunction = {},
            saveFunction = { isEdit = false },
            crossFunction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateTopAppBarPreview(){
    val navController = rememberNavController()
    var isEdit by remember { mutableStateOf(true)}
    AppTheme {
        EditTopAppBar(
            navController = navController,
            editMode = EditMode.Create,
            editFunction = { isEdit = true },
            backFunction = {},
            saveFunction = { isEdit = false },
            crossFunction = {},
        )
    }
}