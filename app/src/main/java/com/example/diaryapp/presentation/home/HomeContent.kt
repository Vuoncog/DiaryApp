package com.example.diaryapp.presentation.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaryapp.R
import com.example.diaryapp.models.Diary
import com.example.diaryapp.presentation.components.DateHeader
import com.example.diaryapp.presentation.components.DiaryCard
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    diaries: Map<LocalDate, List<Diary>>,
    paddingValues: PaddingValues
) {
    if (diaries.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                )
                .padding(horizontal = 16.dp)
        ) {
            diaries.forEach { localDate, listDiaries ->
                stickyHeader {
                    DateHeader(localDate = localDate)
                }

                items(
                    items = listDiaries,
                    key = { it._id }
                ) { diary ->
                    DiaryCard(
                        diary = diary,
                        onDiaryCardClicked = {}
                    )
                }
            }
        }
    } else {
        EmptyScreen()
    }
}

@Composable
fun EmptyScreen(
    title: String = "Your Diary is empty",
    description: String = "Let's start to create your first memory.",
    buttonLabel: String = "Create diary"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding()
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.empty_diary_cat),
            contentDescription = "Empty logo",
            modifier = Modifier.size(160.dp)
        )
        Spacer(modifier = Modifier.padding(bottom = 32.dp))
        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.outline,
                        fontStyle = MaterialTheme.typography.headlineSmall.fontStyle,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        letterSpacing = 0.1.sp
                    )
                ) {
                    append(title.substringBefore("empty"))
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = MaterialTheme.typography.headlineSmall.fontStyle,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        letterSpacing = 0.1.sp
                    )
                ) {
                    append("Empty")
                }
            },
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.padding(bottom = 32.dp))
        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = buttonLabel,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview
@Composable
fun HomeContentPreview() {
    HomeContent(
        diaries = mapOf(),
        paddingValues = PaddingValues()
    )
}