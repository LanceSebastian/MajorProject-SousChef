package uk.ac.aber.dcs.souschefapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    saveFunction: () -> Unit,
    deleteFunction: () -> Unit,
    moreVertFunction: () -> Unit
){

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
                    saveFunction()
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    saveFunction()
                    navController.popBackStack()
                }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = {
                    if (isEdit) deleteFunction() else moreVertFunction()
                    navController.popBackStack()
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
fun EditTopAppBarPreview(){
    val navController = rememberNavController()
    AppTheme {
        EditTopAppBar(
            navController = navController,
            isEdit = false,
            saveFunction = {},
            deleteFunction = {},
            moreVertFunction = {}
        )
    }
}