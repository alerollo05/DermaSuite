package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.viewmodels.paziente.PasiRecord
import it.uninsubria.dermasuite.viewmodels.paziente.TimeFilter
import it.uninsubria.dermasuite.viewmodels.pdfGenerator
import kotlinx.coroutines.launch

@Composable
fun DermaPasiHistoryList (
    records: List<PasiRecord>,
    timeFilter: TimeFilter,
    username : String? = null
) {
    val context = LocalContext.current
    var selectedRecord by remember { mutableStateOf<PasiRecord?>(null) }
    //Creiamo lo scope per lanciare la coroutine, lo usiamo per il thread in background
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = stringResource(R.string.heading_list_history),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            var title = stringResource(R.string.title_PDF_PASI)

            IconButton(
                //Chiamiamo la funzione passando i record già filtrati per il periodo di tempo
                onClick = {
                    coroutineScope.launch {
                        pdfGenerator(title,context, records,timeFilter, username)
                        }
                    }
            ){
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = "Download PDF",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}