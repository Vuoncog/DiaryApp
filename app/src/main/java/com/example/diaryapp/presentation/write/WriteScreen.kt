@file:OptIn(ExperimentalFoundationApi::class)

package com.example.diaryapp.presentation.write

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.diaryapp.models.Diary
import com.example.diaryapp.models.GalleryImage
import com.example.diaryapp.models.GalleryState
import com.example.diaryapp.utility.toInstant
import org.mongodb.kbson.ObjectId
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WriteScreen(
    pagerState: PagerState,
    uiState: UiState,
    editScreen: Boolean,
    galleryState: GalleryState,
    navigateToHome: () -> Unit,
    onDateChanged: (ZonedDateTime) -> Unit,
    onEditClicked: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSaveClicked: (Diary) -> Unit,
    onDeleteClicked: (ObjectId) -> Unit,
    onAddClicked: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageDeleteClicked: (GalleryImage) -> Unit
) {
    var selectedGalleryImage by remember { mutableStateOf<GalleryImage?>(null) }
    LaunchedEffect(key1 = uiState.mood) {
        pagerState.scrollToPage(uiState.mood.ordinal)
    }
    val date = LocalDate.ofInstant(
        uiState.date,
        ZoneId.systemDefault()
    )

    val time =
        LocalTime.ofInstant(
            uiState.date,
            ZoneId.systemDefault()
        )
    val uiCheck = uiState.selectedDiaryId == null || editScreen
    val isEditScreen = (uiState.selectedDiaryId != null).and(!uiCheck)

    Scaffold(
        topBar = {
            WriteTopBar(
                title = uiState.title,
                date = LocalDate.ofInstant(
                    uiState.selectedDiary.date.toInstant(),
                    ZoneId.systemDefault()
                ),
                time = LocalTime.ofInstant(
                    uiState.selectedDiary.date.toInstant(),
                    ZoneId.systemDefault()
                ),
                uiCheck = uiCheck,
                isEdit = isEditScreen,
                onBackClicked = navigateToHome,
                onEditClicked = onEditClicked,
                onDateChanged = onDateChanged
            )
        },
        content = { paddingValues ->
            WriteContent(
                paddingValues = paddingValues,
                pagerState = pagerState,
                galleryState = galleryState,
                isEditScreen = !isEditScreen,
                uiState = uiState,
                date = date,
                time = time,
                onTitleChanged = onTitleChanged,
                onDescriptionChanged = onDescriptionChanged,
                onSaveClicked = onSaveClicked,
                onDeleteClicked = onDeleteClicked,
                onAddClicked = onAddClicked,
                onImageClicked = { selectedGalleryImage = it },
                onImageSelected = onImageSelected
            )
            AnimatedVisibility(visible = selectedGalleryImage != null) {
                Dialog(onDismissRequest = { selectedGalleryImage = null }) {
                    if (selectedGalleryImage != null) {
                        ZoomableImage(
                            selectedGalleryImage = selectedGalleryImage!!,
                            isEditScreen = isEditScreen,
                            onCloseClicked = { selectedGalleryImage = null },
                            onDeleteClicked = {
                                if (selectedGalleryImage != null) {
                                    onImageDeleteClicked(selectedGalleryImage!!)
                                    selectedGalleryImage = null
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ZoomableImage(
    selectedGalleryImage: GalleryImage,
    isEditScreen: Boolean,
    onCloseClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(1f) }
    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = maxOf(1f, minOf(scale * zoom, 5f))
                    val maxX = (size.width * (scale - 1)) / 2
                    val minX = -maxX
                    offsetX = maxOf(minX, minOf(maxX, offsetX + pan.x))
                    val maxY = (size.height * (scale - 1)) / 2
                    val minY = -maxY
                    offsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))
                }
            }
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = maxOf(.5f, minOf(3f, scale)),
                    scaleY = maxOf(.5f, minOf(3f, scale)),
                    translationX = offsetX,
                    translationY = offsetY
                ),
            model = ImageRequest.Builder(LocalContext.current)
                .data(selectedGalleryImage.images.toString())
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Fit,
            contentDescription = "Gallery Image"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onCloseClicked
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")
                    Text(text = "Close")
                }
            }
            if (!isEditScreen) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onDeleteClicked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Icon")
                        Text(text = "Delete")
                    }
                }
            }
        }
    }
}

