package it.uninsubria.dermasuite.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.model.BmiRecord
import it.uninsubria.dermasuite.viewmodels.paziente.HistoryBmiPageViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DermaBmiHistoryDialog (
    record: BmiRecord,
    onDismiss: () -> Unit,
    viewModel: HistoryBmiPageViewModel,
    context : Context
){
    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ){
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.dialog_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                DetailRow(
                    label = stringResource(R.string.data_pdf),
                    value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(record.CalculationDate)
                )

                DetailRow(
                    label = stringResource(R.string.label_altezza),
                    value = "${record.Height} cm"
                )

                DetailRow(
                    label = stringResource(R.string.label_peso),
                    value = "${record.Weight} kg"
                )

                DetailRow(
                    label = "BMI",
                    value = record.BmiTot.toString()
                )

                DetailRow(
                    label = stringResource(R.string.severity_pdf),
                    value = BmiRecord.getBMICategory(record.BmiTot, context)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Button(
                    onClick = {
                        viewModel.deleteRecord(record, currentUser?.uid.toString())
                        onDismiss()
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
