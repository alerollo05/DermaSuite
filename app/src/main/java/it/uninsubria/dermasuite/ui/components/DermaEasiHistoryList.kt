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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.model.EasiRecord
import it.uninsubria.dermasuite.model.TimeFilter
import it.uninsubria.dermasuite.model.easiPdfGenerator
import it.uninsubria.dermasuite.viewmodels.paziente.HistoryEasiPageViewModel
import kotlinx.coroutines.launch

@Composable
fun DermaEasiHistoryList (
    title: String,
    records: List<EasiRecord>,
    timeFilter: TimeFilter,
    username : String? = null,
    viewModel: HistoryEasiPageViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedRecord by remember { mutableStateOf<EasiRecord?>(null) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            coroutineScope.launch { easiPdfGenerator(title, context, records, timeFilter, username) }
        } else {
            Toast.makeText(context, R.string.stringa_errore_download, Toast.LENGTH_LONG).show()
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)){
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
            Text(text = stringResource(R.string.heading_list_history_easi), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            IconButton(
                onClick = {
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> coroutineScope.launch { easiPdfGenerator(title, context, records, timeFilter, username) }
                        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> coroutineScope.launch { easiPdfGenerator(title, context, records, timeFilter, username) }
                        else -> requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
            ){ Icon(painter = painterResource(R.drawable.ic_download), contentDescription = "Download PDF", tint = MaterialTheme.colorScheme.primary) }
        }

        records.reversed().forEach { record ->
            DermaEasiHistoryListItem(record = record, onItemClick = { selectedRecord = record}, context = context)
        }

        selectedRecord?.let{ record ->
            DermaEasiHistoryDialog(record = record, onDismiss = { selectedRecord = null }, context = context, viewModel = viewModel)
        }
    }
}