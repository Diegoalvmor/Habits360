package com.example.habits360.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),       // purple_500
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC5),     // teal_200
    onSecondary = Color.Black,
    tertiary = Color(0xFFBB86FC),      // purple_200
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    primaryContainer = Color(0xFF018786),  // teal_700
    onPrimaryContainer = Color.White
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),       // purple_200
    onPrimary = Color.Black,
    secondary = Color(0xFF03DAC5),     // teal_200
    onSecondary = Color.Black,
    tertiary = Color(0xFF6200EE),      // purple_500
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    primaryContainer = Color(0xFF03DAC5),
    onPrimaryContainer = Color.Black
)


@Composable
fun Habits360Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
