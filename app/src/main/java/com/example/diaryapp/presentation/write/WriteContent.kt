@file:OptIn(
    ExperimentalFoundationApi::class,
)

package com.example.diaryapp.presentation.write

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.diaryapp.models.Diary
import com.example.diaryapp.models.GalleryImage
import com.example.diaryapp.models.GalleryState
import com.example.diaryapp.models.Mood
import com.example.diaryapp.presentation.components.GalleryUploader
import com.example.diaryapp.ui.theme.seed
import com.example.diaryapp.utility.toRealmInstant
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun WriteContent(
    paddingValues: PaddingValues,
    pagerState: PagerState,
    uiState: UiState,
    galleryState: GalleryState,
    date: LocalDate,
    time: LocalTime,
    isEditScreen: Boolean,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSaveClicked: (Diary) -> Unit,
    onDeleteClicked: (ObjectId) -> Unit,
    onAddClicked: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageClicked: (GalleryImage) -> Unit,
    descriptionPlaceholder: String = "Describe the moment",
    titlePlaceholder: String = "Memory about...",
    iconColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
    iconButtonSize: Dp = 32.dp,
) {
    val currentPage = remember {
        mutableIntStateOf(pagerState.currentPage)
    }
    val moodSize = Mood.values().size - 1
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = scrollState.maxValue) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .padding(
                top = paddingValues.calculateTopPadding(),
            )
            .padding(horizontal = 16.dp)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .imePadding()
                .padding(bottom = 16.dp)
                .verticalScroll(
                    state = scrollState
                )
        ) {
            MoodComponent(
                currentPage = currentPage,
                isEditScreen = isEditScreen,
                iconButtonSize = iconButtonSize,
                pagerState = pagerState,
                iconColor = iconColor,
                moodSize = moodSize
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                Text(
                    text = dateFormatter(date),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = timeFormatter(time),
                    style = MaterialTheme.typography.bodySmall,
                    color = seed
                )
            }

            Divider(
                modifier = Modifier
                    .width(160.dp)
                    .height(1.dp)
                    .align(CenterHorizontally),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                value = uiState.title,
                onValueChange = onTitleChanged,
                textStyle = MaterialTheme.typography.titleLarge,
                placeholder = {
                    Text(
                        text = titlePlaceholder,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        scope.launch {
                            scrollState.animateScrollTo(Int.MAX_VALUE)
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    }
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    unfocusedPlaceholderColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified,
                    disabledContainerColor = Color.Unspecified,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                ),
                enabled = isEditScreen
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.description,
                onValueChange = onDescriptionChanged,
                textStyle = MaterialTheme.typography.titleMedium,
                placeholder = {
                    Text(
                        text = descriptionPlaceholder,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified,
                    unfocusedPlaceholderColor = Color.Unspecified,
                    disabledContainerColor = Color.Unspecified,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.clearFocus()
                    }
                ),
                enabled = isEditScreen
            )
        }
        if (isEditScreen) {
            Button(
                modifier = Modifier
                    .padding(horizontal = 56.dp)
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                onClick = {
                    if (uiState.title.isNotBlank()
                        && uiState.description.isNotBlank()
//                        && uiState.selectedDiaryId == null
                    ) {
                        onSaveClicked(
                            Diary().apply {
                                if (uiState.selectedDiaryId != null) {
                                    this._id = ObjectId(uiState.selectedDiaryId)
                                }
                                this.title = uiState.title
                                this.description = uiState.description
                                this.date = uiState.date.toRealmInstant()
                                this.images =
                                    galleryState.images.map { it.remoteImagePath }.toRealmList()
                            }
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Title or description is not empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {
                Text(
                    text = if (uiState.title.isBlank()) "Revive" else "Save",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        } else {
            Button(
                modifier = Modifier
                    .padding(horizontal = 56.dp)
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                onClick = {
                    onDeleteClicked(ObjectId(uiState.selectedDiaryId!!))
                }) {
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = BottomStart,
    ) {
        GalleryUploader(
            modifier = Modifier
                .padding(bottom = 64.dp)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding(),
            galleryState = galleryState,
            isEditScreen = isEditScreen,
            onAddClicked = onAddClicked,
            onImageSelected = onImageSelected,
            onImageClicked = onImageClicked
        )
    }
}


@Composable
private fun MoodComponent(
    currentPage: MutableState<Int>,
    isEditScreen: Boolean,
    iconButtonSize: Dp,
    pagerState: PagerState,
    iconColor: Color,
    moodSize: Int,
) {
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if ((currentPage.value != 0).and(isEditScreen)) {
            IconButton(
                modifier = Modifier.size(
                    iconButtonSize
                ),
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(currentPage.value.minus(1))
                    }
                }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Left mood",
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = if (!(currentPage.value != 0).and(isEditScreen)) iconButtonSize else 0.dp,
                    end = if (!(currentPage.value != moodSize).and(isEditScreen)) iconButtonSize else 0.dp
                ),
            contentAlignment = Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.width(160.dp),
                userScrollEnabled = isEditScreen
            ) { page ->
                currentPage.value = page
                Column(
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(
                            context = LocalContext.current
                        )
                            .data(Mood.values()[page].icon)
                            .crossfade(true)
                            .build(),
                        contentDescription = "mood",
                        modifier = Modifier.size(160.dp)
                    )
                    Text(
                        text = Mood.values()[page].name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                            bottom = 4.dp
                        )
                    )

                }
            }
        }
        if ((currentPage.value != moodSize).and(isEditScreen)) {
            IconButton(
                modifier = Modifier.size(
                    iconButtonSize
                ), onClick = {
                    scope.launch {
                        pagerState.scrollToPage(currentPage.value.plus(1))
                    }
                }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Right mood",
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

fun dateFormatter(localDate: LocalDate): String =
    "${
        localDate.dayOfWeek.name.take(3).lowercase().replaceFirstChar {
            it.titlecase()
        }
    }, " +
            "${localDate.dayOfMonth} " +
            localDate.month.name.take(3).lowercase().replaceFirstChar {
                it.titlecase()
            } +
            " ${localDate.year}"

fun timeFormatter(localTime: LocalTime): String =
    DateTimeFormatter
        .ofPattern("hh:mm a")
        .format(localTime).uppercase()
