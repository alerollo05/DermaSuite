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
    // Se la lista è vuota, esci subito cosi l'app non crasha in caso di errore
    if (records.isEmpty()) return
    //estraiamo i dati in formati semplici da far comparire nel grafico
    val scores = records.map{it.PasiTot.toFloat()}
    val months = records.map{
        SimpleDateFormat("MMM",Locale.ITALIAN).format(it.CalculationDate).uppercase()
    }

    DermaChartCard(
        title = "PASI",
        subtitle = "Grafico PASI"
    ) {
        DermaChartVico(
            yValues = scores,
            xLabels = months,
            lineColor = MaterialTheme.colorScheme.primary
        )
    }
}