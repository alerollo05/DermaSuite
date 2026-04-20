package it.uninsubria.dermasuite.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import it.uninsubria.dermasuite.R

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
//trasferimento da figma della tipografia già creata in precedenza

//andiamo a crea la famiglia di font dopo aver scaricato e aggiunto alla cartella res la cartell
//font per andare a impostare la famiglia di font che desideriamo
val PlusJakartaSans = FontFamily(
    Font(R.font.plusjakartasans_regular, FontWeight.Normal), //andiamo a richiamare da R che è la cartella res la cartella font e poi il tipo di stile
    Font(R.font.plusjakartasans_medium, FontWeight.Medium),//assegnamo a FontEeight.Medium il font che medio che abbiamo scaricato
    Font(R.font.plusjakartasans_semibold, FontWeight.SemiBold),
    Font(R.font.plusjakartasans_bold, FontWeight.Bold),
    Font(R.font.plusjakartasans_extrabold, FontWeight.ExtraBold)
)

val Typography = Typography(
    // Display - 32/120
    displayLarge = TextStyle(
        fontSize = 32.sp,
        fontFamily = PlusJakartaSans, //impostiamo la famiglia di font
        lineHeight = 38.4.sp, // 120% di 32
        fontWeight = FontWeight.Bold
    ),
    // Heading 1 - 24/130
    headlineLarge = TextStyle(
        fontSize = 24.sp,
        fontFamily = PlusJakartaSans, //impostiamo la famiglia di font
        lineHeight = 31.2.sp, // 130% di 24
        fontWeight = FontWeight.SemiBold
    ),
    // Heading 2 - 20/130
    headlineMedium = TextStyle(
        fontSize = 20.sp,
        fontFamily = PlusJakartaSans, //impostiamo la famiglia di font
        lineHeight = 26.sp,
        fontWeight = FontWeight.SemiBold
    ),

    // Body Large - 16/150
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontFamily = PlusJakartaSans, //impostiamo la famiglia di font
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal
    ),
    // Body Medium - 14/150
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontFamily = PlusJakartaSans, //impostiamo la famiglia di font
        lineHeight = 21.sp,
        fontWeight = FontWeight.Normal
    ),
    // Button / Label - 14/100
    // Style usato per le scritte nei bottoni come ad esempio nella login
    // Ho commentato quello che c'era prima nel TextStyle
    labelLarge = TextStyle(
        fontSize = 18.sp,
        fontFamily = PlusJakartaSans, //impostiamo la famiglia di font
        fontWeight = FontWeight.SemiBold
        //lineHeight = 24.sp,
        /*fontSize = 14.sp,
        fontFamily = PlusJakartaSans, //impostiamo la famiglia di font
        lineHeight = 14.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp*/
    ),
    // Caption - 12/140
    labelSmall = TextStyle(
        fontSize = 12.sp,
        fontFamily = PlusJakartaSans, //impostiamo la famiglia di font
        lineHeight = 16.8.sp,
        fontWeight = FontWeight.Normal
    )
)
