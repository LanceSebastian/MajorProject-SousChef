package uk.ac.aber.dcs.souschefapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
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
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTopAppBar(
    navController: NavHostController,
    title: String = "",
    isEdit: Boolean = false,
    editFunction: () -> Unit,
    backFunction: () -> Unit,
    saveFunction: () -> Unit,
    deleteFunction: () -> Unit,
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
                IconButton(onClick = {
                    if (isEdit) saveFunction() else editFunction()
                }
                ) {
                    Icon(
                        imageVector = if (isEdit) Icons.Outlined.Check else Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = {
                    if (isEdit) {
                        deleteFunction()
                    } else {
                        isMutableExpanded = !isMutableExpanded
                    }
                }
                ) {
                    Icon(
                        imageVector = if (isEdit) Icons.Outlined.Delete else Icons.Outlined.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
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
fun FalseEditTopAppBarPreview(){
    val navController = rememberNavController()
    var isEdit by remember { mutableStateOf(false) }
    AppTheme {
        EditTopAppBar(
            navController = navController,
            isEdit = isEdit,
            editFunction = { isEdit = true },
            backFunction = {},
            saveFunction = { isEdit = false },
            deleteFunction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrueEditTopAppBarPreview(){
    val navController = rememberNavController()
    var isEdit by remember { mutableStateOf(true)}
    AppTheme {
        EditTopAppBar(
            navController = navController,
            isEdit = isEdit,
            editFunction = { isEdit = true },
            backFunction = {},
            saveFunction = { isEdit = false },
            deleteFunction = {},
        )
    }
}