package com.example.diaryapp.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onMenuClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onClearClicked: () -> Unit,
    onDateClicked: (LocalDate) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    isFiltered: Boolean,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val calendarDialog = rememberSheetState()

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
            if (isFiltered) {
                IconButton(onClick = onClearClicked) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear menu",
                        tint = color
                    )
                }
            }
            IconButton(onClick = { calendarDialog.show() }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date icon",
                    tint = color
                )
            }
            IconButton(onClick = onEditClicked) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit menu",
                    tint = color
                )
            }
        },
    )

    CalendarDialog(
        state = calendarDialog,
        selection = CalendarSelection.Date {
            onDateClicked(it)
        },
        config = CalendarConfig(monthSelection = true, yearSelection = true),
    )
}