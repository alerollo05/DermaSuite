package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shape.toVicoShape
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState

@Composable
fun DermaChartVico(
    yValues: List<Float>,
    xLabels: List<String>,
    fullDates: List<String>, // Lista per le date complete nel popup
    lineColor: Color,
    modifier: Modifier = Modifier
) {

    // 1. ALGORITMO DI CENTRAGGIO DELLE ETICHETTE
    // Creiamo una mappa che associa l'indice esatto alla stringa da mostrare
    val centeredLabelsMap = remember(xLabels) {
        val map = mutableMapOf<Int, String>()
        var i = 0
        while (i < xLabels.size) {
            val currentMonth = xLabels[i]
            val groupIndices = mutableListOf<Int>()

            // Troviamo tutti gli indici che appartengono allo stesso mese
            while (i < xLabels.size && xLabels[i] == currentMonth) {
                groupIndices.add(i)
                i++
            }

            // Applichiamo la logica di centraggio richiesta:
            // Se N è dispari (es. 3), size / 2 = 1 (seconda posizione).
            // Se N è pari (es. 4), size / 2 = 2 (terza posizione, inizio seconda metà).
            val middleRelativeIndex = groupIndices.size / 2
            val absoluteIndex = groupIndices[middleRelativeIndex]

            map[absoluteIndex] = currentMonth
        }
        map
    }

    // CREAZIONE DEL MODELLO DATI
    // Usiamo 'remember' con 'yValues' come chiave affinché il modello venga ricreato solo se i dati cambiano.
    val model = remember(yValues) {
        CartesianChartModel(
            // LineCartesianLayerModel.build permette di costruire la serie di dati per un grafico a linee.
            LineCartesianLayerModel.build {
                series(yValues)
            }
        )
    }

    //Sul punto andiamo a renderlo cliccabile con un pop up che ci dice la data precisa e il punteggio
    val marker = rememberDefaultCartesianMarker(
        label = rememberTextComponent(
            color = Color.White,
            background = rememberShapeComponent(
                color = lineColor,
                shape = MaterialTheme.shapes.medium.toVicoShape()
            ),
            padding = Dimensions.of(8.dp, 4.dp),
        ),
        // Formattiamo il testo del popup: Data precisa + Punteggio
        valueFormatter = { _, targets ->
            val lineTarget = targets.filterIsInstance<LineCartesianLayerMarkerTarget>().firstOrNull()
            // Accediamo ai punti e all'entry
            val point = lineTarget?.points?.firstOrNull()

            if (point != null) {
                val entry = point.entry
                val index = entry.x.toInt()
                val fullDate = fullDates.getOrNull(index) ?: ""
                "Data: $fullDate\nPASI: ${entry.y}"
            } else {
                ""
            }
        }
    )

    // CONFIGURAZIONE E VISUALIZZAZIONE DEL GRAFICO
    CartesianChartHost(
        chart = rememberCartesianChart(
            layers = arrayOf(
                // Definiamo il livello (layer) della linea.
                rememberLineCartesianLayer(
                    // Personalizziamo l'aspetto della linea tramite un LineProvider.
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        rememberLine(
                            // Definiamo il riempimento della linea con il colore passato come parametro.
                            fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                            pointProvider = LineCartesianLayer.PointProvider.single(
                                // Aggiungiamo i punti sulla linea
                                point = LineCartesianLayer.Point(
                                    component = rememberShapeComponent(lineColor, Shape.Pill),
                                    sizeDp = 6f
                                )
                            )
                        )
                    ),
                    axisValueOverrider = AxisValueOverrider.fixed(minY = 0.0, maxY = 72.0)
                )
            ),
            // Configurazione dell'asse verticale (Y) a sinistra (Start).
            startAxis = rememberStartAxis(),
            // Configurazione dell'asse orizzontale (X) in basso (Bottom).
            bottomAxis = rememberBottomAxis(
                valueFormatter = { value, _, _ ->
                    centeredLabelsMap[value.toInt()] ?: ""
                }
            ),
            marker = marker // Impostiamo il marker personalizzato
        ),
        model = model, // Colleghiamo il modello dati creato precedentemente.
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp), // Applichiamo dimensioni di default se non sovrascritte.
        // Abilitiamo lo scorrimento orizzontale del grafico
        scrollState = rememberVicoScrollState(scrollEnabled = true),
        // Lo zoom statico a 1.2f o superiore forza il grafico a essere più largo dello schermo,
        // rendendo effettivo lo scorrimento
        zoomState = rememberVicoZoomState(initialZoom = Zoom.static(1.5f))

    )
}
