package it.uninsubria.dermasuite.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.model.BmiRecord
import it.uninsubria.dermasuite.model.TimeFilter
import it.uninsubria.dermasuite.model.bmiPdfGenerator
import it.uninsubria.dermasuite.viewmodels.paziente.HistoryBmiPageViewModel
import kotlinx.coroutines.launch

@Composable
fun DermaBmiHistoryList(
    title: String,
    records: List<BmiRecord>,
    timeFilter: TimeFilter,
    username : String? = null,
    viewModel: HistoryBmiPageViewModel
){
    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedRecord by remember { mutableStateOf<BmiRecord?>(null) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            coroutineScope.launch {
                bmiPdfGenerator(title, context, records, timeFilter, username)
            }
        } else {
            Toast.makeText(context, R.string.stringa_errore_download, Toast.LENGTH_LONG).show()
        }
    }

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
                text = stringResource(R.string.title_list_history),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(
                onClick = {
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                            coroutineScope.launch {
                                bmiPdfGenerator(title, context, records, timeFilter, username)
                            }
                        }
                        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                            coroutineScope.launch {
                                bmiPdfGenerator(title, context, records, timeFilter, username)
                            }
                        }
                        else -> {
                            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
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

        records.reversed().forEach { record ->
            DermaBmiHistoryListItem(
                record = record,
                context = context,
                onItemClick = { selectedRecord = record}
            )
        }

        selectedRecord?.let{ record ->
            DermaBmiHistoryDialog(
                record = record,
                onDismiss = { selectedRecord = null },
                context = context,
                viewModel = viewModel
            )
        }
    }
}