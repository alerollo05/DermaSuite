package it.uninsubria.dermasuite.ui.theme

import android.app.Activity
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



private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = WaterGreen,
    tertiary = WaterGreenHover,
    background = Background,  // Sfondo scuro per il dark mode
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

// questa funzione ha dei colori predefiniti non si possono aggiungere colori,
// posso solo usare le variabili già prefissate dalla funzione
private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = WaterGreen,
    tertiary = WaterGreenHover,
    background = Background, // il grigio chiarissimo di sfondo
    onPrimary = White,
    onBackground = Black,
    onSurface = Black
)

@Composable
fun DermaSuiteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Cambiamo il default di dynamicColor a false per mantenere il tuo stile Figma
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes, //andiamon ad aggiungere al tema le impostazioni che abbiamo dato nel file shape per gli arrotondamenti
        content = content
    )
}