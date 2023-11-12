package com.example.diaryapp.presentation.write

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteTopBar(
    title: String = "Revive memory",
    uiCheck: Boolean,
    isEdit: Boolean,
    date: LocalDate,
    time: LocalTime,
    onBackClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onDateChanged: (ZonedDateTime) -> Unit,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {

    var isDateChanged by remember {
        mutableStateOf(false)
    }
    val dateDialog = rememberSheetState()
    val timeDialog = rememberSheetState()

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
                text = title,
                fontWeight = FontWeight.Normal,
                lineHeight = 28.sp,
                fontSize = 22.sp,
                color = color,
            )
        },
        actions = {
            if (uiCheck) {
                if (isDateChanged) {
                    IconButton(onClick = {
                        onDateChanged(
                            ZonedDateTime.of(
                                date,
                                time,
                                ZoneId.systemDefault()
                            )
                        )
                        isDateChanged = false
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close icon",
                            tint = color
                        )
                    }
                } else {
                    IconButton(onClick = {
                        dateDialog.show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date icon",
                            tint = color
                        )
                    }
                }
            }
            if (isEdit) {
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

    val dateUpdated = remember {
        mutableStateOf(LocalDate.now())
    }
    CalendarDialog(
        state = dateDialog,
        config = CalendarConfig(monthSelection = true, yearSelection = true),
        selection = CalendarSelection.Date { localDate: LocalDate ->
            dateUpdated.value = localDate
            onDateChanged(
                ZonedDateTime.of(
                    localDate,
                    time,
                    ZoneId.systemDefault()
                )
            )
            isDateChanged = true
            timeDialog.show()
        }
    )

    ClockDialog(
        state = timeDialog,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            onDateChanged(
                ZonedDateTime.of(
                    dateUpdated.value,
                    LocalTime.of(hours, minutes),
                    ZoneId.systemDefault()
                )
            )
        }
    )
}