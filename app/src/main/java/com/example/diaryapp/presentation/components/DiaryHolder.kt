package com.example.diaryapp.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.diaryapp.models.Diary
import com.example.diaryapp.models.Mood
import com.example.diaryapp.ui.theme.md_theme_light_inverseSurface
import com.example.diaryapp.ui.theme.md_theme_light_onSurface
import com.example.diaryapp.ui.theme.seed
import com.example.diaryapp.utility.toInstant
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@Composable
fun DiaryCard(
    diary: Diary,
    onDiaryCardClicked: (String) -> Unit
) {
    var isHiddenImage by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .padding(start = 12.dp)
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) {
                onDiaryCardClicked(diary._id.toHexString())
            },
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Divider(
            color = seed,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            thickness = 1.dp
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Mood.valueOf(diary.mood).containerColor)
                .border(
                    width = 2.dp,
                    color = md_theme_light_inverseSurface,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(bottom = 12.dp),
        ) {
            DiaryHeader(
                icon = Mood.valueOf(diary.mood).icon,
                backgroundColor = Mood.valueOf(diary.mood).primaryColor,
                title = diary.title,
                time = diary.date.toInstant()
            )
            Spacer(modifier = Modifier.padding(bottom = 8.dp))
            Text(
                text = diary.description,
                modifier = Modifier.padding(horizontal = 12.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 4,
                style = MaterialTheme.typography.bodyMedium
            )
            if (diary.image.isNotEmpty()) {
                ShowGalleryButton(
                    onClicked = {
                        isHiddenImage = !isHiddenImage
                    },
                    isHiddenImage = isHiddenImage
                )
            }
            AnimatedVisibility(
                visible = !isHiddenImage,
                enter = fadeIn() + expandVertically(
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                Gallery(
                    images = diary.image,
                    color = Mood.valueOf(diary.mood).primaryColor
                )
            }
        }
    }
}

@Composable
fun DiaryHeader(
    @DrawableRes icon: Int,
    backgroundColor: Color,
    title: String,
    time: Instant
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .background(backgroundColor)
            .bottomBorder(
                strokeWidth = 1.5.dp,
                color = md_theme_light_inverseSurface
            )
            .padding(horizontal = 12.dp)
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = "Mood icon"
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = md_theme_light_onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = SimpleDateFormat("hh:mm a", Locale.US)
                .format(Date.from(time)),
            style = MaterialTheme.typography.titleMedium,
            color = md_theme_light_onSurface
        )
    }
}

fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx / 2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width, y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

@Preview
@Composable
fun DiaryHeaderPreview() {
    DiaryCard(
        diary = Diary().apply {
            mood = Mood.Loved.name
            title = "My diary"
            description =
                "Lorem ipsum dolor sit amet consectetur. Sed duis aliquam tempor tortor. Enim phasellus tristique massa diam sed feugiat est enim sollicitudin. Amet suspendisse nec ac"
        },
        onDiaryCardClicked = {}
    )
}