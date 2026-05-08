package it.uninsubria.dermasuite.ui.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.*
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
import it.uninsubria.dermasuite.model.EasiDistrictState
import it.uninsubria.dermasuite.model.EasiRecord
import it.uninsubria.dermasuite.model.mapSeverity
import it.uninsubria.dermasuite.viewmodels.paziente.HistoryEasiPageViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DermaEasiHistoryDialog (
    record: EasiRecord,
    onDismiss: () -> Unit,
    viewModel: HistoryEasiPageViewModel,
    context : Context
){
    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ){
        Card(
            modifier = Modifier.fillMaxWidth(0.85f).wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ){
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth().verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = stringResource(R.string.dialog_title), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))

                DetailRow(label = stringResource(R.string.data_pdf), value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(record.CalculationDate))
                DetailRow(label = "EASI Tot", value = String.format(Locale.getDefault(), "%.1f", record.EasiTot))
                DetailRow(label = stringResource(R.string.severity_pdf), value = record.mapSeverity(context))

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(text = stringResource(R.string.dialog_subtitle), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

                // Indentazione dei 5 parametri dell'EASI
                EasiDistrictDetailView(stringResource(id = DistrettoCorpo.HEAD.nameResId), record.ParameterDistrict.head)
                EasiDistrictDetailView(stringResource(id = DistrettoCorpo.ARMS.nameResId), record.ParameterDistrict.arms)
                EasiDistrictDetailView(stringResource(id = DistrettoCorpo.TRUNK.nameResId), record.ParameterDistrict.trunk)
                EasiDistrictDetailView(stringResource(id = DistrettoCorpo.LEGS.nameResId), record.ParameterDistrict.legs)

                Button(
                    onClick = {
                        viewModel.deleteRecord(record, currentUser?.uid.toString())
                        onDismiss()
                    },
                    modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.dialog_button), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

// Visualizzazione dei 5 parametri EASI
@Composable
fun EasiDistrictDetailView(districtName: String, district: EasiDistrictState){
    Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) {
        Text(text = "$districtName:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Column(modifier = Modifier.padding(start = 16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = "${stringResource(R.string.eritema_pdf)}: ${district.eritema}", style = MaterialTheme.typography.bodySmall)
            Text(text = "${stringResource(R.string.edema_pdf)}: ${district.edemaPapulizzazione}", style = MaterialTheme.typography.bodySmall)
            Text(text = "${stringResource(R.string.escoriazione_pdf)}: ${district.escoriazione}", style = MaterialTheme.typography.bodySmall)
            Text(text = "${stringResource(R.string.lichenificazione_pdf)}: ${district.lichenificazione}", style = MaterialTheme.typography.bodySmall)
            Text(text = "${stringResource(R.string.area_pdf)}: ${district.percentualeArea}", style = MaterialTheme.typography.bodySmall)
        }
    }
}