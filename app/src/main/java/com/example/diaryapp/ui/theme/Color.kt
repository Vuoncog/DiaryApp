package com.example.diaryapp.ui.theme

import androidx.compose.ui.graphics.Color

val md_theme_light_primary = Color(0xFF8C4F00)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFFFDCBF)
val md_theme_light_onPrimaryContainer = Color(0xFF2D1600)
val md_theme_light_secondary = Color(0xFF735943)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFFDCBF)
val md_theme_light_onSecondaryContainer = Color(0xFF291806)
val md_theme_light_tertiary = Color(0xFF596239)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFDDE8B3)
val md_theme_light_onTertiaryContainer = Color(0xFF171E00)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_outline = Color(0xFF837469)
val md_theme_light_background = Color(0xFFFFFBFF)
val md_theme_light_onBackground = Color(0xFF201B17)
val md_theme_light_surface = Color(0xFFFFF8F5)
val md_theme_light_onSurface = Color(0xFF201B17)
val md_theme_light_surfaceVariant = Color(0xFFF2DFD1)
val md_theme_light_onSurfaceVariant = Color(0xFF51443A)
val md_theme_light_inverseSurface = Color(0xFF352F2B)
val md_theme_light_inverseOnSurface = Color(0xFFFAEFE7)
val md_theme_light_inversePrimary = Color(0xFFFFB874)
val md_theme_light_surfaceTint = Color(0xFF8C4F00)
val md_theme_light_outlineVariant = Color(0xFFD5C3B6)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFFFFB874)
val md_theme_dark_onPrimary = Color(0xFF4B2800)
val md_theme_dark_primaryContainer = Color(0xFF6B3B00)
val md_theme_dark_onPrimaryContainer = Color(0xFFFFDCBF)
val md_theme_dark_secondary = Color(0xFFE2C0A4)
val md_theme_dark_onSecondary = Color(0xFF412C18)
val md_theme_dark_secondaryContainer = Color(0xFF59422D)
val md_theme_dark_onSecondaryContainer = Color(0xFFFFDCBF)
val md_theme_dark_tertiary = Color(0xFFC1CC99)
val md_theme_dark_onTertiary = Color(0xFF2C340F)
val md_theme_dark_tertiaryContainer = Color(0xFF424B23)
val md_theme_dark_onTertiaryContainer = Color(0xFFDDE8B3)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_outline = Color(0xFF9E8E81)
val md_theme_dark_background = Color(0xFF201B17)
val md_theme_dark_onBackground = Color(0xFFEBE0D9)
val md_theme_dark_surface = Color(0xFF17120F)
val md_theme_dark_onSurface = Color(0xFFCFC5BE)
val md_theme_dark_surfaceVariant = Color(0xFF51443A)
val md_theme_dark_onSurfaceVariant = Color(0xFFD5C3B6)
val md_theme_dark_inverseSurface = Color(0xFFEBE0D9)
val md_theme_dark_inverseOnSurface = Color(0xFF201B17)
val md_theme_dark_inversePrimary = Color(0xFF8C4F00)
val md_theme_dark_surfaceTint = Color(0xFFFFB874)
val md_theme_dark_outlineVariant = Color(0xFF51443A)
val md_theme_dark_scrim = Color(0xFF000000)


val seed = Color(0xFFA58C77)

sealed class EmotionColor(
    val primaryColor: Color,
    val containerColor: Color,
) {
    object Amazed : EmotionColor(
        primaryColor = Color(0xFFFFFAA0),
        containerColor = Color(0xFFFFFDDD)
    )

    object Angry : EmotionColor(
        primaryColor = Color(0xFFFF837B),
        containerColor = Color(0xFFFFD9D6)
    )

    object Satisfied : EmotionColor(
        primaryColor = Color(0xFFD6E9FF),
        containerColor = Color(0xFFEEF6FF)
    )

    object Confused : EmotionColor(
        primaryColor = Color(0xFFF7AE7F),
        containerColor = Color(0xFFFFE8D9)
    )

    object Sad : EmotionColor(
        primaryColor = Color(0xFF779ECB),
        containerColor = Color(0xFFBBCBDC)
    )

    object Loved : EmotionColor(
        primaryColor = Color(0xFFF8DADA),
        containerColor = Color(0xFFFFEBEB)
    )

    object Normal : EmotionColor(
        primaryColor = Color(0xFFDFDED4),
        containerColor = Color(0xFFEDEDEC)
    )

    object Sickness : EmotionColor(
        primaryColor = Color(0xFF8CA2B0),
        containerColor = Color(0xFFD1D4D6)
    )

    object Yummy : EmotionColor(
        primaryColor = Color(0xFFFF9845),
        containerColor = Color(0xFFFFDEC3)
    )

    object Doubt : EmotionColor(
        primaryColor = Color(0xFFCAA3DC),
        containerColor = Color(0xFFDEC8E8)
    )
}