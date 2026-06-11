package it.uninsubria.dermasuite.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.model.BsaRecord
import it.uninsubria.dermasuite.viewmodels.paziente.HistoryBsaPageViewModel

@Composable
fun DermaBsaHistoryDialog(
    record: BsaRecord,
    onDismiss: () -> Unit, // Callback per chiudere il dialogo
    viewModel: HistoryBsaPageViewModel,
    userId: String?
) {
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Permette al dialogo di occupare più larghezza
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.85f).wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth().verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.bsa_dialog_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                // Visualizzazione dei risultati calcolati
                DetailRow(label = stringResource(R.string.bsa_dialog_date_time), value = record.dataOra)
                DetailRow(label = stringResource(R.string.bsa_dialog_result_bsa), value = stringResource(R.string.bsa_dialog_result_m2, record.bsa))
                DetailRow(label = stringResource(R.string.bsa_dialog_evaluation), value = record.valutazione)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // Linea di separazione

                Text(text = stringResource(R.string.bsa_dialog_input_data), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

                // Visualizzazione dei parametri di input originali
                DetailRow(label = stringResource(R.string.bsa_dialog_weight), value = stringResource(R.string.bsa_dialog_weight_kg, record.peso))
                DetailRow(label = stringResource(R.string.bsa_dialog_height), value = stringResource(R.string.bsa_dialog_height_cm, record.altezza))
                DetailRow(label = stringResource(R.string.bsa_dialog_gender), value = record.sesso)

                // Bottone per eliminare il record tramite il ViewModel
                Button(
                    onClick = {
                        viewModel.deleteRecord(record, userId)
                        onDismiss()
                    },
                    modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.bsa_dialog_delete_btn),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}