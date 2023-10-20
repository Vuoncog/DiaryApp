package com.example.diaryapp.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.diaryapp.ui.theme.md_theme_light_onSurface
import io.realm.kotlin.types.RealmList
import kotlin.math.max

@Composable
fun Gallery(
    images: RealmList<String>,
    imageSize: Dp = 40.dp,
    imageShape: CornerBasedShape = RoundedCornerShape(8.dp),
    spaceBetween: Dp = 10.dp,
    color: Color
) {
    val configuration = LocalConfiguration.current
    val maxWidth = configuration.screenWidthDp.dp

    val numberOfDisplayImage = remember {
        derivedStateOf {
            max(
                a = 0,
                b = maxWidth.minus((32 + 28 + 12).dp).div(imageSize + spaceBetween).toInt().minus(1)

            )
        }
    }

    Log.d("Display Image:", numberOfDisplayImage.value.toString())

    val remainingImages = remember {
        derivedStateOf {
            images.size - numberOfDisplayImage.value
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(spaceBetween),
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        images.take(numberOfDisplayImage.value).forEach { image ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .crossfade(true)
                    .build(),
                contentDescription = "Gallery Image",
                modifier = Modifier
                    .clip(imageShape)
                    .size(imageSize)
            )
        }
        if (remainingImages.value > 0) {
            Box(
                modifier = Modifier
                    .clip(imageShape)
                    .background(color = color)
                    .size(imageSize)
            ) {
                Text(
                    text = "+${remainingImages.value}",
                    style = MaterialTheme.typography.titleSmall,
                    color = md_theme_light_onSurface,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

}

@Composable
fun ShowGalleryButton(
    onClicked: () -> Unit,
    isHiddenImage: Boolean
) {
    TextButton(onClick = onClicked) {
        Text(text = if (isHiddenImage) "Show gallery" else "Hide gallery")
    }
}