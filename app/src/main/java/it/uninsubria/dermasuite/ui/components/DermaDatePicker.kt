package it.uninsubria.dermasuite.ui.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.screens.DermaRegisterPageScreen
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DermaDatePicker(
   label: String,
   value: String,
   onDataSelected: (String) -> Unit,
   modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    //Creiamo una variabie che memorizza lo stato che assume il datepicker


    // Calcoliamo i due estremi temporali
    val dateLimits = remember {
        val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))

        // 1. Limite superiore: 18 anni fa (non puoi essere più giovane di così)
        calendar.add(java.util.Calendar.YEAR, -18)
        val maxDate = calendar.timeInMillis

        // 2. Limite inferiore: 120 anni fa (non puoi essere più anziano di così)
        // Reset del calendar a oggi prima di sottrarre 120
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(java.util.Calendar.YEAR, -120)
        val minDate = calendar.timeInMillis

        Pair(minDate, maxDate)
    }

    val (minDateLimit, maxDateLimit) = dateLimits

    val datePickerState = rememberDatePickerState(
        initialDisplayedMonthMillis = maxDateLimit, // Parte sempre dal 2008 (18 anni fa)
        selectableDates = object : androidx.compose.material3.SelectableDates {

            // ABILITA SOLO LE DATE COMPRESE NEL RANGE
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in minDateLimit..maxDateLimit
            }

            // Blocca anche la selezione degli anni troppo lontani nel menu a tendina
            override fun isSelectableYear(year: Int): Boolean {
                val calendar = java.util.Calendar.getInstance()
                val currentYear = calendar.get(java.util.Calendar.YEAR)
                return year in (currentYear - 120)..(currentYear - 18)
            }
        }
    )

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
                placeholder = { Text("dd/mm/yyyy" , color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_date_picker_calendar),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                enabled = false, // Disabilitato per evitare focus/tastiera
                colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.Gray,
                    unfocusedTextColor = Color.Gray,
                    focusedTextColor = Color.Gray,
                    disabledContainerColor = Color(0xFFF1F4F8),
                    disabledLeadingIconColor = MaterialTheme.colorScheme.tertiary,
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
    val bluScuroDerma = Color(0xFF001A3F)
    val grigioTesto = Color(0xFF49454F)   // Grigio scuro per i numeri dei giorni
    val biancoPuro = Color.White

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK", color = bluScuroDerma)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Annulla", color = grigioTesto)
            }
        },
        colors = DatePickerDefaults.colors(
        containerColor = Color.White
        )
    ) {
        DatePicker(state = state,
            showModeToggle = false,
            colors = DatePickerDefaults.colors(
                containerColor = biancoPuro,

                titleContentColor = bluScuroDerma,
                headlineContentColor = bluScuroDerma,

                // Mese corrente e freccette
                navigationContentColor = bluScuroDerma,

                // Giorni della settimana (M, T, W...)
                weekdayContentColor = Color.Gray,

                // --- I NUMERI DEI GIORNI ---
                dayContentColor = grigioTesto,            // Numeri normali
                selectedDayContainerColor = bluScuroDerma, // Cerchio giorno selezionato
                selectedDayContentColor = biancoPuro,     // Numero dentro il cerchio blu

                // Giorno di oggi
                todayContentColor = bluScuroDerma,
                todayDateBorderColor = bluScuroDerma
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DermaDatePickerPreview() {
    it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme() {
    DermaDatePicker(label = "Data di Nascita", value = "12/12/2000", onDataSelected = {})
    }
}



