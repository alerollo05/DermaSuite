package it.uninsubria.dermasuite.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

//Visto che utilizziamo spesso blocchi stondati andiamo a definire uno shape unico in modo tale da rendere l'app modulare
val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp), // Per le card dei calcolatori
    large = RoundedCornerShape(24.dp)   // Per i bottoni principali e i contenitori grandi
)