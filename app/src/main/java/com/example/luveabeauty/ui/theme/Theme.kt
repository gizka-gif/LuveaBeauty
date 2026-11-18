package com.example.luveabeauty.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LuveaColorScheme = lightColorScheme(
    primary = Rose,
    onPrimary = Color.White,
    primaryContainer = Blush,
    onPrimaryContainer = RoseDeep,
    secondary = RoseDeep,
    onSecondary = Color.White,
    tertiary = Gold,
    background = Cream,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = BlushSoft,
    onSurfaceVariant = Muted,
    outline = FieldBorder,
    outlineVariant = Line,
    error = Danger
)

private val LuveaShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

// Tema terang saja agar warna brand tetap konsisten (tidak berubah mengikuti dark mode / wallpaper)
@Composable
fun LuvéaBeautyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LuveaColorScheme,
        typography = Typography,
        shapes = LuveaShapes,
        content = content
    )
}

// Tampilan layar penuh (konten sampai ke balik status bar & navigation bar)
fun ComponentActivity.enableLuveaEdgeToEdge() {
    val transparent = android.graphics.Color.TRANSPARENT
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.light(transparent, transparent),
        navigationBarStyle = SystemBarStyle.light(transparent, transparent)
    )
}
