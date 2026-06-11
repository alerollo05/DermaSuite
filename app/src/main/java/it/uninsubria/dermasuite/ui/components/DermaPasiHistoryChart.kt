package it.uninsubria.dermasuite.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.model.PasiRecord
import it.uninsubria.dermasuite.model.TimeFilter
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DermaPasiHistoryChart (
    records: List<PasiRecord>,
    timeFilter: TimeFilter,
){
    // Prende la lingua corrente per formattare la data
    val currentLocale = Locale.getDefault()

    // Se la lista è vuota esce cosi non crasha in caso di errore
    if (records.isEmpty()) return
    //estrazione dei dati
    val scores = records.map{it.PasiTot.toFloat()}
    val months = records.map{
        SimpleDateFormat("MMM",currentLocale).format(it.CalculationDate).uppercase()
    }
    // Date complete per il popup
    val fullDates = records.map {
        SimpleDateFormat("d MMMM yyyy", currentLocale).format(it.CalculationDate).uppercase()
    }

    var stringaSubGrafico = stringResource(R.string.string_sub_chart_card)
    DermaChartCard(
        title = "PASI",
        subtitle = "$stringaSubGrafico ${stringResource(timeFilter.displayName)}",
        indicatorColor = MaterialTheme.colorScheme.primary
    ) {
        DermaChartVico(
            tipoCalcolo = "PASI",
            maxScala = 72,
            yValues = scores,
            xLabels = months,
            lineColor = MaterialTheme.colorScheme.primary,
            fullDates = fullDates, //Passiamo le date complete
            valore = "PASI"
        )
    }
}