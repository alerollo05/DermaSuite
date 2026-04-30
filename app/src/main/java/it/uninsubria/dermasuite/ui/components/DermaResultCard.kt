package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.uninsubria.dermasuite.R

@Composable
fun DermaResultCard(
    title: String,
    result: Double,
    max: Int,
    severity: String,
){
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding( 16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = MaterialTheme.shapes.medium
    ){
        //Creiamo una variabile per il colore azzurro
        val azzurro = Color(0xFF40E0D0)
        Column(
            modifier = Modifier.padding(16.dp)
        ){
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ){
                Text(
                    text = result.toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 64.sp
                    )
                )
                Text(
                    text = "/$max",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            //Andiamo a creare una riga per la severità
            Surface(
                color = azzurro,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.padding(vertical = 12.dp)
            ){
                Text(
                    text = severity,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = stringResource(R.string.range_severità),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    )
                val punteggio = stringResource(R.string.punteggio)
                Text(
                    text = "$severity ($punteggio $result)",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            val progress = result/max.toDouble()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                        MaterialTheme.shapes.medium)
            ){
                Box(
                    modifier = Modifier.
                    fillMaxWidth(progress.toFloat())
                        .fillMaxHeight()
                        .background(azzurro, MaterialTheme.shapes.medium)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DermaResultCardPreview() {
    it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme() {
        DermaResultCard(
            title = "Risultato calcolo PASI",
            result = 5.0,
            max = 10,
            severity = "Severo"
        )
    }
}