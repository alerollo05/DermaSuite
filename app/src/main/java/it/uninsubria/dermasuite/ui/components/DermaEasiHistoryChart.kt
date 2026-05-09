package it.uninsubria.dermasuite.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.model.EasiRecord
import it.uninsubria.dermasuite.model.TimeFilter
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DermaEasiHistoryChart (
    records: List<EasiRecord>,
    timeFilter: TimeFilter,
){
    val currentLocale = Locale.getDefault()
    if (records.isEmpty()) return

    // EASI utilizza Float, gestiamo correttamente i dati per il grafico
    val scores = records.map{ it.EasiTot }
    val months = records.map{ SimpleDateFormat("MMM",currentLocale).format(it.CalculationDate).uppercase() }
    val fullDates = records.map { SimpleDateFormat("d MMMM yyyy", currentLocale).format(it.CalculationDate).uppercase() }

    val stringaSubGrafico = stringResource(R.string.string_sub_chart_card)
    DermaChartCard(
        title = "EASI",
        subtitle = "$stringaSubGrafico ${stringResource(timeFilter.displayName)}",
        indicatorColor = MaterialTheme.colorScheme.primary
    ) {
        DermaChartVico(
            yValues = scores,
            xLabels = months,
            lineColor = MaterialTheme.colorScheme.primary,
            fullDates = fullDates,
            valore = "EASI"
        )
    }
}

