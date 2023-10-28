package com.example.diaryapp.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onMenuClicked: () -> Unit,
    onDateClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onMenuClicked) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Hamburger menu",
                    tint = color
                )
            }
        },
        title = {
            Text(
                text = "Diary",
                fontWeight = FontWeight.Normal,
                lineHeight = 28.sp,
                fontSize = 22.sp,
                color = color
            )
        },
        actions = {
            IconButton(onClick = onDateClicked) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date icon",
                    tint = color
                )
            }
        },
    )
}