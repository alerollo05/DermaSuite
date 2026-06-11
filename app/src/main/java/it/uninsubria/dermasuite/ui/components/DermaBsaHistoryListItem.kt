package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import it.uninsubria.dermasuite.model.BsaRecord
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DermaBsaHistoryListItem(
    record: BsaRecord,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {}
) {
    // Formattazione della data per estrarre giorno e mese separatamente
    val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val date = inputFormat.parse(record.dataOra) ?: java.util.Date()

    val day = SimpleDateFormat("dd", Locale.getDefault()).format(date)
    val month = SimpleDateFormat("MMM", Locale.getDefault()).format(date).uppercase()

    Card(
        modifier = modifier.fillMaxWidth().clickable { onItemClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Barretta colorata decorativa a sinistra
            Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(MaterialTheme.colorScheme.primary))

            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                // Box stile "Calendario" (Giorno grande, Mese piccolo)
                Card(
                    modifier = Modifier.size(60.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = month, style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                        Text(text = day, style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.primary)
                    }
                }

                // Colonna centrale con il valore BSA e la valutazione sintetica
                Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(
                        text = stringResource(R.string.bsa_list_item_bsa, record.bsa),
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.bsa_list_item_evaluation, record.valutazione),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Gray
                    )
                }

                Icon(painter = painterResource(R.drawable.ic_mezza_freccia_destra), contentDescription = null, tint = Color.Gray)
            }
        }
    }
}