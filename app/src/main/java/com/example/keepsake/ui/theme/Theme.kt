package com.example.keepsake.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KeepsakeLightColors = lightColorScheme(
    primary = Terracotta,
    onPrimary = Color.White,
    secondary = FadedTerracotta,
    onSecondary = InkBrown,
    background = PaperCream,
    onBackground = InkBrown,
    surface = PaperCream,
    onSurface = InkBrown
)

// Same palette, just in case dark mode is ever supported — currently
// identical to light since the app doesn't have a dark variant yet.
private val KeepsakeDarkColors = darkColorScheme(
    primary = Terracotta,
    onPrimary = Color.White,
    secondary = FadedTerracotta,
    onSecondary = InkBrown,
    background = InkBrown,
    onBackground = PaperCream,
    surface = InkBrown,
    onSurface = PaperCream
)

@Composable
fun KeepsakeTheme(
    useDarkTheme: Boolean = false, // not wired to system setting yet — flip manually if wanted
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) KeepsakeDarkColors else KeepsakeLightColors
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}