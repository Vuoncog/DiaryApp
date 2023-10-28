@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class
)

package com.example.diaryapp.presentation.write

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.diaryapp.models.Diary
import com.example.diaryapp.models.Mood
import com.example.diaryapp.ui.theme.seed
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun WriteContent(
    paddingValues: PaddingValues,
    pagerState: PagerState,
    uiState: UiState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onButtonClicked: (Diary) -> Unit,
    date: LocalDate,
    time: LocalTime,
    isEditScreen: Boolean,
    descriptionPlaceholder: String = "Describe the moment",
    titlePlaceholder: String = "Memory about...",
    iconColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
    iconButtonSize: Dp = 32.dp,
) {
    var currentPage = remember {
        mutableIntStateOf(pagerState.currentPage)
    }
    val moodSize = Mood.values().size - 1
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
            )
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            MoodComponent(
                currentPage = currentPage,
                uiCheck = isEditScreen,
                iconButtonSize = iconButtonSize,
                pagerState = pagerState,
                iconColor = iconColor,
                moodSize = moodSize
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
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

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.title,
                onValueChange = onTitleChanged,
                textStyle = MaterialTheme.typography.titleMedium,
                placeholder = {
                    Text(
                        text = titlePlaceholder,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {}
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.outline
                )
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
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.outline
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {}
                )
            )
        }
        if (isEditScreen) {
            Button(
                modifier = Modifier
                    .padding(horizontal = 56.dp)
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                onClick = {
                    if (uiState.title.isNotBlank()
                        && uiState.description.isNotBlank()
                        && uiState.selectedDiaryId == null
                    ) {
                        onButtonClicked(
                            Diary().apply {
                                this.title = uiState.title
                                this.description = uiState.description
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
        }
    }
}


@Composable
private fun MoodComponent(
    currentPage: MutableState<Int>,
    uiCheck: Boolean,
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
        if ((currentPage.value != 0).and(uiCheck)) {
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
                    start = if (!(currentPage.value != 0).and(uiCheck)) iconButtonSize else 0.dp,
                    end = if (!(currentPage.value != moodSize).and(uiCheck)) iconButtonSize else 0.dp
                ),
            contentAlignment = Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.width(160.dp),
                userScrollEnabled = uiCheck
            ) { page ->
                currentPage.value = page
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
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
        if ((currentPage.value != moodSize).and(uiCheck)) {
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

private fun dateFormatter(localDate: LocalDate): String =
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

private fun timeFormatter(localTime: LocalTime): String =
    DateTimeFormatter
        .ofPattern("hh:mm a")
        .format(localTime).uppercase()
