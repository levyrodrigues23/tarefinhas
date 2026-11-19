package br.com.levy.tarefinhas

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Claro = lightColorScheme(
    primary = Color(0xFF5B4BDB),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE4DFFF),
    onPrimaryContainer = Color(0xFF1A0E6B),
    secondary = Color(0xFF00A383),
    background = Color(0xFFF6F5FB),
    surface = Color.White,
    surfaceVariant = Color(0xFFECEAF4),
    onSurfaceVariant = Color(0xFF5E5C6E),
)

private val Escuro = darkColorScheme(
    primary = Color(0xFFC6BFFF),
    onPrimary = Color(0xFF2A1C9A),
    primaryContainer = Color(0xFF3F31B8),
    onPrimaryContainer = Color(0xFFE4DFFF),
    secondary = Color(0xFF4FDBB8),
    background = Color(0xFF131218),
    surface = Color(0xFF1D1C24),
    surfaceVariant = Color(0xFF2A2933),
    onSurfaceVariant = Color(0xFFC9C6D6),
)

@Composable
fun TemaTarefinhas(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Escuro else Claro, content = content)
}
