package com.example.diaryapp.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.diaryapp.R

@Composable
fun CustomOutlinedButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int = R.drawable.google,
    label: String = "Sign in with Google",
    labelLoadingState: String = "Please wait...",
    outlinedColor: Color = MaterialTheme.colorScheme.outline,
    labelColor: Color = MaterialTheme.colorScheme.primary,
    progressIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    cornerRadius: Dp = 100.dp,
    loadingState: Boolean = false,
    onClicked: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(
                shape = RoundedCornerShape(
                    size = cornerRadius
                )
            )
            .clickable(enabled = !loadingState) { onClicked() }
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(
                    size = cornerRadius
                ),
                color = outlinedColor
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .padding(start = 16.dp, end = 24.dp)
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = stringResource(R.string.outlined_button_icon),
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = if (loadingState) labelLoadingState else label,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp,
                fontSize = 14.sp,
                letterSpacing = 0.01.em,
                color = labelColor
            )
            if (loadingState){
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = progressIndicatorColor
                )
            }
        }
    }
}

@Preview
@Composable
fun OutlinedButtonPreview() {
    CustomOutlinedButton(
        onClicked = {}
    )
}

@Preview
@Composable
fun OutlinedButtonLoadingStatePreview() {
    CustomOutlinedButton(
        onClicked = {},
        loadingState = true
    )
}