package com.example.diaryapp.presentation.write

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.diaryapp.models.Diary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteTopBar(
    selectedDiary: Diary?,
    uiCheck: Boolean,
    isEdit: Boolean,
    onBackClicked: () -> Unit,
    onEditClicked: () -> Unit,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    var title = remember {
        mutableStateOf("Revive memory")
    }

    if (selectedDiary != null){
        title.value = selectedDiary.title
    }

    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back arrow",
                    tint = color
                )
            }
        },
        title = {
            Text(
                text = title.value,
                fontWeight = FontWeight.Normal,
                lineHeight = 28.sp,
                fontSize = 22.sp,
                color = color,
            )
        },
        actions = {
            if (uiCheck){
                IconButton(onClick = {  }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date icon",
                        tint = color
                    )
                }
            }
            if (isEdit){
                IconButton(onClick = onEditClicked) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit icon",
                        tint = color
                    )
                }
            }
        },
    )
}