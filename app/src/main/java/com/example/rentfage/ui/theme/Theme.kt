package com.example.rentfage.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta de colores para el tema claro, usando los rojos definidos.
private val LightColorScheme = lightColorScheme(
    primary = Red40,            // Rojo principal para botones, iconos, etc.
    onPrimary = Color.White,      // Texto sobre el color primario (rojo) en blanco.
    primaryContainer = Color(0xFFFFEBEE), // Un rojo muy claro para fondos de contenedores.
    onPrimaryContainer = Red40,   // Texto sobre el contenedor primario en rojo oscuro.
    secondary = LightRed40,       // Un rojo más claro para elementos secundarios.
    onSecondary = Color.White,    // Texto sobre el color secundario en blanco.
    tertiary = RedGrey40,         // Un tono de gris rojizo para acentos.
    background = Color.White,     // Fondo blanco.
    surface = Color.White,        // Superficies como tarjetas en blanco.
    onBackground = Color.Black,   // Texto sobre el fondo blanco en negro.
    onSurface = Color.Black         // Texto sobre superficies en negro.
)

// Paleta de colores para el tema oscuro.
private val DarkColorScheme = darkColorScheme(
    primary = Red80,            // Un rojo más brillante para el modo oscuro.
    secondary = LightRed80,       // Un rojo aún más claro.
    tertiary = RedGrey80,         // Un gris claro para acentos.
    // Los demás colores se invierten para el modo oscuro.
)

@Composable
fun RentfageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Se desactiva el color dinámico para forzar el uso de nuestra paleta de rojos.
    dynamicColor: Boolean = false, 
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
