package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.uninsubria.dermasuite.R


@Composable
fun DermaBMIScaleBar(
    bmiValue: Double
){
    // Colori della scala BMI in base alla severità
    val colorUnderweight = Color(0xFF9ECEFF) // Azzurro chiaro
    val colorHealthy = Color(0xFF4EE2C0)     // Verde acqua/Menta
    val colorOverweight = Color(0xFFFFD54F)  // Giallo
    val colorObese = Color(0xFFD32F2F)       // Rosso
    val colorDot = Color(0xFF001F3F)         // Blu scuro per il pallino

    // Range visivo del BMI per calcolare la posizione (da 10 a 40 secondo internet copre quasi tutti i casi di calcolo possibile)
    val minVisBmi = 10f
    val maxVisBmi = 40f
    val clampedBmi = bmiValue.toFloat().coerceIn(minVisBmi, maxVisBmi) //Serve a dirci se è nel range
    val progressPercentage = (clampedBmi - minVisBmi) / (maxVisBmi - minVisBmi)

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ){
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val totalWidth = maxWidth

            // Barra segmentata proporzionale ai range medici reali
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(7.dp))
            ) {
                // Barra segmentata proporzionale ai range medici reali
                // Sottopeso (10 - 18.5) -> Peso: 8.5
                Box(modifier = Modifier.weight(8.5f).fillMaxHeight().background(colorUnderweight))
                // Normo peso (18.5 - 25) -> Peso: 6.5
                Box(modifier = Modifier.weight(6.5f).fillMaxHeight().background(colorHealthy))
                // Sovrappeso (25 - 30) -> Peso: 5.0
                Box(modifier = Modifier.weight(5.0f).fillMaxHeight().background(colorOverweight))
                // Obeso (30 - 40) -> Peso: 10.0
                Box(modifier = Modifier.weight(10.0f).fillMaxHeight().background(colorObese))
            }
            // Indicatore (Pallino)
            val dotXPosition = totalWidth * progressPercentage
            val dotRadius = 7.dp

            Box(
                modifier = Modifier
                    .offset(x = dotXPosition - dotRadius) // Centra il pallino rispetto al valore
                    .size(14.dp)
                    .background(Color.White, CircleShape) // Bordo bianco
                    .padding(2.dp) // Spessore del bordo
                    .background(colorDot, CircleShape) // Interno blu scuro
            )

        }

        Spacer(modifier = Modifier.height(8.dp))

        // Etichette di testo sotto la barra
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.bmi_severity_low).uppercase(), fontSize = 9.sp, color = colorUnderweight, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.bmi_severity_moderate).uppercase(), fontSize = 9.sp, color = colorHealthy, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.bmi_severity_severe).uppercase(), fontSize = 9.sp, color = colorOverweight, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.bmi_severity_obese).uppercase(), fontSize = 9.sp, color = colorObese, fontWeight = FontWeight.Bold)
        }
    }

}

@Composable
fun DermaResultCard(
    title: String,
    result: Double,
    max: Int,
    severity: String,
    isBMI: Boolean = false //Per modificare la barra del risultato in base alla pagina
){
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding( 16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        //Creiamo una variabile per il colore azzurro
        val azzurro = Color(0xFF40E0D0)
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = result.toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 64.sp
                    )
                )
                if (!isBMI) {
                    Text(
                        text = "/$max",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            //Andiamo a creare una riga per la severità
            Surface(
                color = azzurro,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text(
                    text = severity.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
            //Barra di divisione
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(16.dp))
            // Logica condizionale per la barra in fondo
            if (isBMI) {
                //Barra BMI
                DermaBMIScaleBar(bmiValue = result)
            } else {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.range_severità), // "Range di severità"
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    )
                    val punteggio = stringResource(R.string.punteggio) // "Punteggio"
                    Text(
                        text = "$severity ($punteggio $result)",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                val progress = if (max > 0) result / max.toDouble() else 0.0
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                            MaterialTheme.shapes.medium
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.toFloat())
                            .fillMaxHeight()
                            .background(azzurro, MaterialTheme.shapes.medium)
                    )
                }
            }
        }
    }
}