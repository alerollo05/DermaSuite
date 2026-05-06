package it.uninsubria.dermasuite.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.model.DistrettoCorpo
import it.uninsubria.dermasuite.model.PasiDistrictState
import it.uninsubria.dermasuite.model.PasiRecord
import it.uninsubria.dermasuite.model.mapSeverity
import it.uninsubria.dermasuite.viewmodels.paziente.HistoryPasiPageViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DermaPasiHistoryDialog (
    record: PasiRecord,
    onDismiss: () -> Unit,
    viewModel: HistoryPasiPageViewModel,
    context : Context
){
    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser

    Dialog(
        onDismissRequest = onDismiss, //Chiude il dialogo alla chiusura del popup, quando clicco fuori dallo schermo
        properties = DialogProperties(
            dismissOnBackPress = true, // Si chiude col tasto indietro del telefono
            dismissOnClickOutside = true, // Si chiude cliccando nello spazio vuoto
            usePlatformDefaultWidth = false // Disabilita la larghezza forzata di sistema
        )
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)// Occupa solo l'85% della larghezza, lasciando i bordi liberi
                .wrapContentHeight(),// L'altezza si adatta solo al contenuto
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ){
            Column(
                modifier = Modifier
                    .padding(24.dp) // Padding interno spazioso
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Titolo del popup
                Text(
                    text = stringResource(R.string.dialog_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Info Generali per il calcolo
                DetailRow(
                    label = stringResource(R.string.data_pdf),
                    value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(record.CalculationDate)
                )
                DetailRow(label = stringResource(R.string.pasi_tot_pdf), value = record.PasiTot.toString())
                DetailRow(label = stringResource(R.string.severity_pdf), value = record.mapSeverity(context))

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = stringResource(R.string.dialog_subtitle),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                //Stampiamo con il formato indentato tutti i parametri per ogni distretto
                DistrictDetailView(stringResource(id = DistrettoCorpo.HEAD.nameResId), record.ParameterDistrict.head)
                DistrictDetailView(stringResource(id = DistrettoCorpo.ARMS.nameResId), record.ParameterDistrict.arms)
                DistrictDetailView(stringResource(id = DistrettoCorpo.TRUNK.nameResId), record.ParameterDistrict.trunk)
                DistrictDetailView(stringResource(id = DistrettoCorpo.LEGS.nameResId), record.ParameterDistrict.legs)

                Button(
                    onClick = {
                        viewModel.deleteRecord(record,currentUser?.uid.toString());
                        onDismiss()//cosi chiudiamo il popup una volta eliminato
                              },
                    modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally)

                ) {
                    Text(
                        text = stringResource(R.string.dialog_button),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

        }
    }
}
@Composable
fun DetailRow(label: String, value: String){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "$label:", fontWeight = FontWeight.Bold)
        Text(text = value)
    }
}

@Composable
fun DistrictDetailView(districtName: String, district: PasiDistrictState){
    Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) {
        Text(
            text = "$districtName:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        // Colonna innestata con padding a sinistra (questo crea l'indentazione!)
        Column(
            modifier = Modifier.padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = "${stringResource(R.string.eritema_pdf)}: ${district.erythema}", style = MaterialTheme.typography.bodySmall)
            Text(text = "${stringResource(R.string.indurimento_pdf)}: ${district.hardening}", style = MaterialTheme.typography.bodySmall)
            Text(text = "${stringResource(R.string.desquamazione_pdf)}: ${district.desquamation}", style = MaterialTheme.typography.bodySmall)
            Text(text = "${stringResource(R.string.area_pdf)}: ${district.percentageArea}", style = MaterialTheme.typography.bodySmall)
        }
    }
}