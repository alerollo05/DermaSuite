package it.uninsubria.dermasuite.ui.components

// Import per il layout e componenti Material Design 3
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import it.uninsubria.dermasuite.model.BsaRecord
import it.uninsubria.dermasuite.viewmodels.paziente.HistoryBsaPageViewModel

@Composable
fun DermaBsaHistoryDialog(
    record: BsaRecord,
    onDismiss: () -> Unit, // Callback per chiudere il dialogo
    viewModel: HistoryBsaPageViewModel,
    userId: String?
) {
    val scrollState = rememberScrollState() // Gestisce lo scorrimento se il contenuto è lungo

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
                    text = "Dettaglio Calcolo",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                // Visualizzazione dei risultati calcolati
                DetailRow(label = "Data e Ora", value = record.dataOra)
                DetailRow(label = "Risultato BSA", value = "${record.bsa} m²")
                DetailRow(label = "Valutazione", value = record.valutazione)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // Linea di separazione

                Text(text = "Dati inseriti:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

                // Visualizzazione dei parametri di input originali
                DetailRow(label = "Peso", value = "${record.peso} kg")
                DetailRow(label = "Altezza", value = "${record.altezza} cm")
                DetailRow(label = "Sesso", value = record.sesso)

                // Bottone per eliminare il record tramite il ViewModel
                Button(
                    onClick = {
                        viewModel.deleteRecord(record, userId)
                        onDismiss()
                    },
                    modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Elimina Calcolo",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}