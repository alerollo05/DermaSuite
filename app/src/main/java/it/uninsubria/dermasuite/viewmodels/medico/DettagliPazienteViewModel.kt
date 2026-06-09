package it.uninsubria.dermasuite.viewmodels.medico

import androidx.compose.material.Snackbar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.uninsubria.dermasuite.firebase.AuthRepository
import it.uninsubria.dermasuite.firebase.DermaUser
import kotlinx.coroutines.launch
import it.uninsubria.dermasuite.model.MetricSummaryState
class DettagliPazienteViewModel (private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    //Per i caricamenti
    var isLoading by mutableStateOf(true)
        private set
    var nomePaziente by mutableStateOf("...")
        private set
    var usernamePaz by mutableStateOf("...")
        private set
    // Stati per le singole Card
    var pasiSummary by mutableStateOf(MetricSummaryState())
        private set
    var easiSummary by mutableStateOf(MetricSummaryState())
        private set
    var bmiSummary by mutableStateOf(MetricSummaryState())
        private set
    var bsaSummary by mutableStateOf(MetricSummaryState())
        private set

    //FUNZIONE PRINCIPALE: CARICAMENTO DATI
    fun loadPatientData(
        pazienteId: String,
        pasiTitle: String = "PASI Average",
        easiTitle: String = "EASI Average",
        bmiTitle: String = "BMI Average",
        bsaTitle: String = "BSA Average"
    ) {
        viewModelScope.launch {
            isLoading = true

            //Recupera i dati anagrafici del paziente
            val paziente = repository.getUserData(pazienteId)
            nomePaziente = if (paziente != null) "${paziente.nome.lowercase().replaceFirstChar { it.uppercase() }} ${paziente.cognome.lowercase().replaceFirstChar{it.uppercase()}}" else "Paziente non trovato"
            usernamePaz = if (paziente != null) "@${paziente.username}" else ""

            //Recupero record dal repository centralizzato
            val pasiRecords = repository.getPasiRecords(pazienteId)
            val easiRecords = repository.getEasiRecords(pazienteId)
            val bmiRecords = repository.getBmiRecords(pazienteId)
            val bsaRecords = repository.getBsaRecords(pazienteId)
            
            //Calcolo medie e trend per ogni metrica
            // Invertiamo le liste (.reversed()) perché Vico vuole i dati dal più vecchio al più nuovo
            pasiSummary = calculateMetric(
                title = pasiTitle,
                scores = pasiRecords.map { it.PasiTot.toFloat() }.reversed(),
                isLowerBetter = true
            )

            easiSummary = calculateMetric(
                title = easiTitle,
                scores = easiRecords.map { it.EasiTot }.reversed(),
                isLowerBetter = true
            )

            bmiSummary = calculateMetric(
                title = bmiTitle,
                scores = bmiRecords.map { it.BmiTot.toFloat() }.reversed(),
                isLowerBetter = true
            )

            bsaSummary = calculateMetric(
                title = bsaTitle,
                scores = bsaRecords.map { it.bsa.toFloat() }.reversed(),
                isLowerBetter = true // Per il BSA solitamente si cerca la stabilità
            )

            isLoading = false
        }
    }
    private fun calculateMetric(title: String, scores: List<Float>, isLowerBetter: Boolean): MetricSummaryState {
        if (scores.isEmpty()) return MetricSummaryState(title = title)

        val last = scores.last()
        val avg = scores.average().toFloat()

        // Calcolo Trend (confronto ultimo con penultimo)
        var trendStr = "Stable"
        var worsening = false
        if (scores.size >= 2) {
            val prev = scores[scores.size - 2]
            val diff = last - prev
            val percent = if (prev != 0f) (diff / prev) * 100 else 0f
            trendStr = "${if (percent > 0) "+" else ""}${String.format("%.1f", percent)}%"
            worsening = if (isLowerBetter) diff > 0 else diff < 0
        }

        // Definizione Severità (Esempio semplificato)
        val severity = when {
            last < 7 -> "Mild"
            last < 15 -> "Moderate"
            else -> "Severe"
        }

        return MetricSummaryState(
            title = title,
            averageValue = String.format("%.1f", last), // Mostriamo l'ultimo valore come principale
            severityLabel = severity,
            trendPercentage = trendStr,
            isWorsening = worsening,
            historicalData = scores.takeLast(10) // Ultime 10 misurazioni per la sparkline
        )
    }
    fun getUserData(){
        viewModelScope.launch {
            isLoading = true

        }
    }
}