package it.uninsubria.dermasuite.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.model.PasiRecord
import it.uninsubria.dermasuite.model.mapSeverity
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DermaPasiHistoryListItem(
    record : PasiRecord,
    context: Context,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {}
    ){
    val day = SimpleDateFormat("dd", Locale.getDefault()).format(record.CalculationDate)
    val month = SimpleDateFormat("MMM", Locale.getDefault()).format(record.CalculationDate).uppercase()

    Card(
        modifier = Modifier.fillMaxWidth().clickable{ onItemClick()},
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 5f),
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ){
        Row(
            //Gli diamo questa altezza atuoregolat in base all'altezza della card
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight() // Ora "riempie" l'altezza minima della Row
                    .width(6.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Creiamo il blocco della data
                Card(
                    modifier = Modifier.size(height = 60.dp, width = 60.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = month,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = day,
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = "${stringResource(R.string.stringa_ris_pasi)} ${record.PasiTot}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${stringResource(R.string.severity_pdf)}: ${
                            record.mapSeverity(
                                context
                            )
                        }",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.Gray
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.ic_mezza_freccia_destra),
                    contentDescription = "arrow_right",
                    tint = Color.Gray
                )
            }
        }
    }
}