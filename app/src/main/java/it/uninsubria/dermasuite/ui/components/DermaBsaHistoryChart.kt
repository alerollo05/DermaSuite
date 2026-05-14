package it.uninsubria.dermasuite.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.model.BsaRecord
import it.uninsubria.dermasuite.model.TimeFilter
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DermaBsaHistoryChart(
    records: List<BsaRecord>,
    timeFilter: TimeFilter,
) {
    val currentLocale = Locale.getDefault()
    if (records.isEmpty()) return // Se non ci sono record, non renderizza nulla

    // Formattatore per trasformare la stringa salvata nel DB in un oggetto Date
    val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", currentLocale)

    // Converte i valori BSA in Float per il grafico
    val scores = records.map { it.bsa.toFloat() }

    // Crea le etichette per l'asse X (abbreviazione mese, es: GEN, FEB)
    val months = records.map {
        val date = inputFormat.parse(it.dataOra) ?: java.util.Date()
        SimpleDateFormat("MMM", currentLocale).format(date).uppercase()
    }

    // Crea le etichette estese per i tooltip o dettagli (es: 8 MAGGIO 2026)
    val fullDates = records.map {
        val date = inputFormat.parse(it.dataOra) ?: java.util.Date()
        SimpleDateFormat("d MMMM yyyy", currentLocale).format(date).uppercase()
    }

    val stringaSubGrafico = stringResource(R.string.string_sub_chart_card)

    // Contenitore grafico con titolo e sottotitolo dinamico
    DermaChartCard(
        title = "BSA (m²)",
        subtitle = "$stringaSubGrafico ${stringResource(timeFilter.displayName)}",
        indicatorColor = MaterialTheme.colorScheme.primary
    ) {
        // Componente core che disegna effettivamente le linee (basato sulla libreria Vico)
        DermaChartVico(
            yValues = scores,
            xLabels = months,
            lineColor = MaterialTheme.colorScheme.primary,
            fullDates = fullDates
        )
    }
}