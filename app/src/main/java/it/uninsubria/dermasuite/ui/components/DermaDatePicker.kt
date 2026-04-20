package it.uninsubria.dermasuite.ui.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.R
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DermaDatePicker(
   label: String,
   value: String,
   onDataSelected: (String) -> Unit,
   modifier: Modifier = Modifier //
) {
    //Siccome in compose possiamo far cambiare l'interfaccia in base allo stato delle variabili
    //creiamo una variabile che ci servirà a dire quando far comparire il date picker e quando non ci servirà
    var showDialog by remember { mutableStateOf(false) }
    //Creiamo una variabie che memorizza lo stato che assume il datepicker
    val datePickerState = rememberDatePickerState()

    //Creiamo il formato della data
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    //Definisco l'interfaccia del date picker
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("dd/mm/yyyy") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_date_picker_calendar),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                enabled = false, // Disabilitato per evitare focus/tastiera
                colors = TextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = Color(0xFFF1F4F8),
                    disabledLeadingIconColor = MaterialTheme.colorScheme.tertiary,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        }
    }
    if (showDialog) {
        DermaDatePickerDialog(
            state = datePickerState,
            onDismissRequest = { showDialog = false },
            onConfirm = {
                datePickerState.selectedDateMillis?.let { millis ->
                    onDataSelected(formatter.format(millis))
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun DermaDatePickerDialog(
    state: DatePickerState,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Annulla")
            }
        }
    ) {
        DatePicker(state = state)
    }
}