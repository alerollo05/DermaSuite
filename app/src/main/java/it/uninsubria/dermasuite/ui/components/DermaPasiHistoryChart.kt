package it.uninsubria.dermasuite.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import it.uninsubria.dermasuite.viewmodels.paziente.PasiRecord
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DermaPasiHistoryChart (
        records: List<PasiRecord>
){
    //Recuperiamo la lingua corrente per poi andare a stampare i valori della data nel formato corretto
    val currentLocale = Locale.getDefault()

    // Se la lista è vuota, esci subito cosi l'app non crasha in caso di errore
    if (records.isEmpty()) return
    //estraiamo i dati in formati semplici da far comparire nel grafico
    val scores = records.map{it.PasiTot.toFloat()}
    val months = records.map{
        SimpleDateFormat("MMM",currentLocale).format(it.CalculationDate).uppercase()
    }
    // Date complete per il popup (es. 30 Aprile 2026)
    val fullDates = records.map {
        SimpleDateFormat("d MMMM yyyy", currentLocale).format(it.CalculationDate).uppercase()
    }

    DermaChartCard(
        title = "PASI",
        subtitle = "Grafico PASI"
    ) {
        DermaChartVico(
            yValues = scores,
            xLabels = months,
            lineColor = MaterialTheme.colorScheme.primary,
            fullDates = fullDates //Passiamo le date complete
        )
    }
}