package it.uninsubria.dermasuite.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DermaProfileField(
    label: String,
    value: String?,
    modifier: Modifier = Modifier,
    // Questo parametro ci permette di passare qualsiasi icona (o anche più di una) a destra
    modificaIcon: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Spazio verticale tra un campo e l'altro
    ) {
        // La Label in alto (es. "Nome")
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
        )

        // Il Box grigio con testo e icone
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFF3F4F6), // Colore grigio chiaro per il Box
                    shape = RoundedCornerShape(12.dp) // Bordi arrotondati
                )
                .padding(horizontal = 16.dp, vertical = 14.dp), // Spazio interno al box
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Il Testo principale
            if (value != null) {
                Text(
                    text = value,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                    color = Color.Black, // Grigio molto scuro/nero
                    modifier = Modifier.weight(1f) // Occupa tutto lo spazio a sinistra spingendo le icone a destra
                )
            }

            // Se abbiamo passato un'icona, la disegna a destra
            if (modificaIcon != null) {
                modificaIcon()
            }
        }
    }
}