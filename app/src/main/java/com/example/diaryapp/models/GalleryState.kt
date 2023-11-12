package com.example.diaryapp.models

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
class GalleryState {
    val images = mutableStateListOf<GalleryImage>()
    val imagesForDeleting = mutableStateListOf<GalleryImage>()

    fun addImage(galleryImage: GalleryImage) {
        images.add(galleryImage)
    }

    fun removeImage(galleryImage: GalleryImage) {
        images.remove(galleryImage)
        imagesForDeleting.add(galleryImage)
    }
}

@Composable
fun rememberGalleryState(): GalleryState {
    return remember { GalleryState() }
}

data class GalleryImage(
    val images: Uri,
    val remoteImagePath: String = "",
)