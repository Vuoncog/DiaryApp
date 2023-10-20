package com.example.diaryapp.models

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.diaryapp.R
import com.example.diaryapp.ui.theme.EmotionColor

enum class Mood(
    @DrawableRes val icon: Int,
    val primaryColor: Color,
    val containerColor: Color,
) {
    Amazed(
        icon = R.drawable.amazed,
        primaryColor = EmotionColor.Amazed.primaryColor,
        containerColor = EmotionColor.Amazed.containerColor
    ),
    Angry(
        icon = R.drawable.angry,
        primaryColor = EmotionColor.Angry.primaryColor,
        containerColor = EmotionColor.Angry.containerColor
    ),
    Confused(
        icon = R.drawable.confused,
        primaryColor = EmotionColor.Confused.primaryColor,
        containerColor = EmotionColor.Confused.containerColor
    ),
    Doubt(
        icon = R.drawable.doubt,
        primaryColor = EmotionColor.Doubt.primaryColor,
        containerColor = EmotionColor.Doubt.containerColor
    ),
    Sickness(
        icon = R.drawable.face_mask,
        primaryColor = EmotionColor.Sickness.primaryColor,
        containerColor = EmotionColor.Sickness.containerColor
    ),
    Loved(
        icon = R.drawable.love,
        primaryColor = EmotionColor.Loved.primaryColor,
        containerColor = EmotionColor.Loved.containerColor
    ),
    Sad(
        icon = R.drawable.sad,
        primaryColor = EmotionColor.Sad.primaryColor,
        containerColor = EmotionColor.Sad.containerColor
    ),
    Satisfied(
        icon = R.drawable.smile,
        primaryColor = EmotionColor.Satisfied.primaryColor,
        containerColor = EmotionColor.Satisfied.containerColor
    ),
    Normal(
        icon = R.drawable.smile1,
        primaryColor = EmotionColor.Normal.primaryColor,
        containerColor = EmotionColor.Normal.containerColor
    ),
    Yummy(
        icon = R.drawable.yummy,
        primaryColor = EmotionColor.Yummy.primaryColor,
        containerColor = EmotionColor.Yummy.containerColor
    ),

}
