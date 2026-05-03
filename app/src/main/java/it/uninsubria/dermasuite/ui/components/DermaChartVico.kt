package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer



@Composable
fun DermaChartVico(
    yValues: List<Float>,
    xLabels: List<String>,
    lineColor: Color,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
) {
    //CREAZIONE DEL MODELLO DATI
    // Usiamo 'remember' con 'yValues' come chiave affinché il modello venga ricreato solo se i dati cambiano.
    // CartesianChartModel è il contenitore dei dati che il grafico deve rappresentare.
    val model = remember(yValues) {
        CartesianChartModel(
            // LineCartesianLayerModel.build permette di costruire la serie di dati per un grafico a linee.
            LineCartesianLayerModel.build {
                series(yValues)
            }
        )
    }

    //CONFIGURAZIONE E VISUALIZZAZIONE DEL GRAFICO
    // CartesianChartHost è il componente principale di Vico per integrare grafici in Jetpack Compose.
    CartesianChartHost(
        chart = rememberCartesianChart(
            // Definiamo il livello (layer) della linea.
            rememberLineCartesianLayer(
                // Personalizziamo l'aspetto della linea tramite un LineProvider.
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.Line(
                        // Definiamo il riempimento della linea con il colore passato come parametro.
                        fill = LineCartesianLayer.LineFill.single(fill(lineColor))
                    )
                )
            ),
            // Configurazione dell'asse verticale (Y) a sinistra (Start).
            startAxis = rememberStartAxis(),
            // Configurazione dell'asse orizzontale (X) in basso (Bottom).
            bottomAxis = rememberBottomAxis(
                // Il valueFormatter converte l'indice numerico (0.0, 1.0, 2.0...) nelle etichette testuali (xLabels).
                valueFormatter = { value, _, _ ->
                    // Recuperiamo l'etichetta corrispondente all'indice se presente nella lista.
                    xLabels.getOrNull(value.toInt()) ?: ""
                }
            ),
        ),
        model = model, // Colleghiamo il modello dati creato precedentemente.
        modifier = modifier // Applichiamo il modifier per dimensioni e stile.
    )
}
