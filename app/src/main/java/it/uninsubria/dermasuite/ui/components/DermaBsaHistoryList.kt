package it.uninsubria.dermasuite.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.model.BsaRecord
import it.uninsubria.dermasuite.model.TimeFilter
import it.uninsubria.dermasuite.model.bsaPdfGenerator
import it.uninsubria.dermasuite.viewmodels.paziente.HistoryBsaPageViewModel
import kotlinx.coroutines.launch

@Composable
fun DermaBsaHistoryList(
    title: String,
    records: List<BsaRecord>,
    timeFilter: TimeFilter,
    username: String? = null,
    viewModel: HistoryBsaPageViewModel,
    userId: String?
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // Necessario per avviare la generazione PDF (suspend)
    var selectedRecord by remember { mutableStateOf<BsaRecord?>(null) } // Stato per gestire quale record mostrare nel dialogo

    // Gestore per richiedere il permesso di scrittura (solo per Android < 10)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            coroutineScope.launch { bsaPdfGenerator(title, context, records, timeFilter, username) }
        } else {
            Toast.makeText(context, "Permesso negato", Toast.LENGTH_LONG).show()
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Intestazione della sezione con titolo e icona di download
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Cronologia Calcoli", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            IconButton(
                onClick = {
                    // Controlla i permessi in base alla versione di Android
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> coroutineScope.launch { bsaPdfGenerator(title, context, records, timeFilter, username) }
                        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> coroutineScope.launch { bsaPdfGenerator(title, context, records, timeFilter, username) }
                        else -> requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
            ) { Icon(painter = painterResource(R.drawable.ic_download), contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
        }

        // Mostra ogni record nella lista (dal più recente)
        records.reversed().forEach { record ->
            DermaBsaHistoryListItem(record = record, onItemClick = { selectedRecord = record })
        }

        // Se un record è selezionato, mostra il dialogo di dettaglio
        selectedRecord?.let { record ->
            DermaBsaHistoryDialog(
                record = record,
                onDismiss = { selectedRecord = null },
                viewModel = viewModel,
                userId = userId
            )
        }
    }
}