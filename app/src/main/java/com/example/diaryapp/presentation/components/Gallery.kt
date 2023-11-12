package com.example.diaryapp.presentation.components

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.diaryapp.models.GalleryImage
import com.example.diaryapp.models.GalleryState
import com.example.diaryapp.ui.theme.md_theme_light_onSurface
import io.realm.kotlin.types.RealmList
import kotlin.math.max

@Composable
fun Gallery(
    images: List<Uri>,
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
                contentScale = ContentScale.Crop,
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
fun GalleryUploader(
    modifier: Modifier,
    galleryState: GalleryState,
    isEditScreen: Boolean,
    spaceBetween: Dp = 10.dp,
    imageSize: Dp = 64.dp,
    imageShape: CornerBasedShape = RoundedCornerShape(6.dp),
    onAddClicked: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageClicked: (GalleryImage) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val maxWidth = configuration.screenWidthDp.dp
    val imageUpload = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(8),
        onResult = { images ->
            images.forEach {
                onImageSelected(it)
            }
        }
    )

    val numberOfDisplayImage = remember {
        derivedStateOf {
            max(
                a = 0,
                b = maxWidth.minus((32).dp).div(imageSize + spaceBetween).toInt()
                    .minus(1)
            )
        }
    }

    val displayImageQuantity =
        if (isEditScreen) numberOfDisplayImage.value.minus(1) else numberOfDisplayImage.value

    val remainingImages =
        galleryState.images.size - displayImageQuantity


    Log.d("Gallery", "Remain: ${remainingImages}")
    Log.d("Gallery", "DisplayImageQuantity: $displayImageQuantity")

    Row(
        horizontalArrangement = Arrangement.spacedBy(spaceBetween),
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .then(modifier)
    ) {
        if (isEditScreen) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(imageShape)
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = imageShape
                    )
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .size(imageSize)
                    .clickable {
                        onAddClicked()
                        imageUpload.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        galleryState.images.take(displayImageQuantity).forEach { galleryImage ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(galleryImage.images)
                    .crossfade(true)
                    .build(),
                contentDescription = "Gallery Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(imageShape)
                    .size(imageSize)
                    .clickable {
                        onImageClicked(galleryImage)
                    }
            )
        }
        if (remainingImages > 0) {
            Box(
                modifier = Modifier
                    .clip(imageShape)
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .size(imageSize)
            ) {
                Text(
                    text = "+${remainingImages}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun ShowGalleryButton(
    onClicked: () -> Unit,
    isGalleryOpened: Boolean,
    isLoading: Boolean
) {
    TextButton(
        modifier = Modifier.padding(top = 8.dp),
        onClick = onClicked
    ) {
        Text(
            text = if (isGalleryOpened) {
                if (isLoading) "Loading"
                else "Hide gallery"
            } else "Show gallery"
        )
    }
}