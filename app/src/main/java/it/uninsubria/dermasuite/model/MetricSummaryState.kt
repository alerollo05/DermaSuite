package it.uninsubria.dermasuite.model

// Questa classe rappresenta esattamente i dati che servono alla DermaAverageCard
data class MetricSummaryState(
    val title: String = "",
    val averageValue: String = "0.0",
    val severityLabel: String = "N/A",
    val trendPercentage: String = "0%",
    val isWorsening: Boolean = false, // Ci serve per capire se colorare il trend di rosso o verde
    val historicalData: List<Float> = emptyList()
)

