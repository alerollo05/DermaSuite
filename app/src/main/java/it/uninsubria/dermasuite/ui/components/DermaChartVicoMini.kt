package it.uninsubria.dermasuite.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.common.fill

//Creiamo questa variante del grafico normale per poter visualizzare il grafico della media, senza però permettere all'utente di interagirci
@Composable
fun DermaChartVicoMini(
    yValues: List<Float>,
    lineColor: Color,
    modifier: Modifier = Modifier
){
    // Se non ci sono dati, non disegna nulla per evitare crash
    if (yValues.isEmpty()) return

    //Creazione del modello
    val model = remember(yValues) {
        CartesianChartModel(
            LineCartesianLayerModel.build {
                series(yValues)
            }
        )
    }
    //Host del grafico
    CartesianChartHost(
        chart = rememberCartesianChart(
            layers = arrayOf(
                rememberLineCartesianLayer(
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                        )
                    )
                )
            ),
            // Rimuoviamo completamente gli assi e il marker per il look da Card
            startAxis = null,
            bottomAxis = null,
            marker = null
        ),
        model = model,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp), // Altezza ridotta per stare nella card
        // Disabilitiamo scroll e zoom per non interferire con il tocco sulla Card
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        zoomState = rememberVicoZoomState(zoomEnabled = false)
    )
}