package uk.ac.aber.dcs.souschefapp.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import uk.ac.aber.dcs.souschefapp.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.ac.aber.dcs.souschefapp.ui.theme.AppTheme

@Composable
fun CardRecipe(
    modifier: Modifier = Modifier,
    text: String = "",
    imageUri: Uri? = null,
    overlay: Boolean = true,
    onClick: () -> Unit,
) {
    val defaultImage = painterResource(id = R.drawable.questionimage)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = modifier
            .size(width = 285.dp, height = 150.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            if (imageUri == null) {
                Image(
                    painter = defaultImage,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }

            // Overlay (Shadow)
            if (overlay) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )
            }

            // Content
            Box (
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart),
            ){
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                )
            }
        }
    }
}

@Preview
@Composable
fun CardRecipeView(){
    AppTheme{
        CardRecipe(
            text = "Banana Split",
            onClick = {}
        )
    }
}