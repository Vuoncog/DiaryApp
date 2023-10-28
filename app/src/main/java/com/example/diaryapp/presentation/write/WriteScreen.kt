@file:OptIn(ExperimentalFoundationApi::class)

package com.example.diaryapp.presentation.write

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.diaryapp.models.Diary
import com.example.diaryapp.utility.toInstant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WriteScreen(
    pagerState: PagerState,
    navigateToHome: () -> Unit,
    onEditClicked: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onButtonClicked: (Diary) -> Unit,
    uiState: UiState,
    editScreen: Boolean,
) {
    LaunchedEffect(key1 = uiState.mood) {
        pagerState.scrollToPage(uiState.mood.ordinal)
    }
    val date = LocalDate.ofInstant(
        uiState.selectedDiary.date.toInstant(),
        ZoneId.systemDefault()
    )

    val time =
        LocalTime.ofInstant(
            uiState.selectedDiary.date.toInstant(),
            ZoneId.systemDefault()
        )
    val uiCheck = uiState.selectedDiaryId == null || editScreen
    val isEditScreen = (uiState.selectedDiaryId != null).and(!uiCheck)

    Scaffold(
        topBar = {
            WriteTopBar(
                selectedDiary = null,
                uiCheck = uiCheck,
                isEdit = isEditScreen,
                onBackClicked = navigateToHome,
                onEditClicked = onEditClicked
            )
        },
        content = {
            WriteContent(
                paddingValues = it,
                pagerState = pagerState,
                isEditScreen = !isEditScreen,
                uiState = uiState,
                date = date,
                time = time,
                onTitleChanged = onTitleChanged,
                onDescriptionChanged = onDescriptionChanged,
                onButtonClicked = onButtonClicked
            )
        }
    )
}

