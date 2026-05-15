package it.uninsubria.dermasuite.viewmodels.paziente

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import it.uninsubria.dermasuite.firebase.AuthRepository
import it.uninsubria.dermasuite.model.BsaRecord
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.sqrt

// ViewModel responsabile per la pagina di calcolo del BSA.
// Gestisce gli input dell'utente, esegue il calcolo matematico
// e salva il risultato su Firestore.
class BsaPageViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    // STATI DI INPUT DELLA UI
    // StateFlow per raccogliere i dati digitati dall'utente in tempo reale.
    private val _peso = MutableStateFlow("")
    val peso: StateFlow<String> = _peso.asStateFlow()

    private val _altezza = MutableStateFlow("")
    val altezza: StateFlow<String> = _altezza.asStateFlow()

    private val _sesso = MutableStateFlow("")
    val sesso: StateFlow<String> = _sesso.asStateFlow()

    // --- EVENTI E STATI DI OUTPUT ---
    // Usiamo SharedFlow invece di StateFlow per la Snackbar perché è un evento
    // "one-shot" (deve scattare una volta sola e non ripresentarsi se l'utente ruota lo schermo).
    private val _saveSuccess = MutableSharedFlow<Boolean>()
    val saveSuccess: SharedFlow<Boolean> = _saveSuccess.asSharedFlow()

    // SharedFlow per inviare messaggi di errore alla UI
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    // Stati per mostrare il risultato e il caricamento
    private val _risultatoBsa = MutableStateFlow<Double?>(null)
    val risultatoBsa: StateFlow<Double?> = _risultatoBsa.asStateFlow()

    private val _valutazione = MutableStateFlow("")
    val valutazione: StateFlow<String> = _valutazione.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Blocco init per recuperare automaticamente il sesso del paziente all'avvio
    init {
        recuperaSessoPaziente()
    }

    private fun recuperaSessoPaziente() {
        val userId = repository.getCurrentUserId()

        if (userId != null) {
            viewModelScope.launch {
                _sesso.value = repository.getSessoPaziente(userId)
            }
        }
    }

    // Funzioni per aggiornare gli stati quando l'utente digita nei TextField
    fun onPesoChange(newPeso: String) { _peso.value = newPeso }
    fun onAltezzaChange(newAltezza: String) { _altezza.value = newAltezza }
    fun onSessoChange(newSesso: String) { _sesso.value = newSesso }

    // Funzione principale attivata dal pulsante "Calcola".
    // L'annotazione @RequiresApi è necessaria perché usiamo LocalDateTime (introdotto in API 26).
    @RequiresApi(Build.VERSION_CODES.O)
    fun calcolaBsa() {
        val pesoVal = _peso.value.toDoubleOrNull()
        val altezzaVal = _altezza.value.toDoubleOrNull()
        val sessoVal = _sesso.value

        // Usiamo una coroutine perché _errorMessage.emit() ha bisogno di essere sospeso
        viewModelScope.launch {
            // Controllo che i campi non siano vuoti o formattati male
            if (pesoVal == null || altezzaVal == null) {
                _errorMessage.emit("Inserisci tutti i dati richiesti.")
                return@launch // Interrompe l'esecuzione della funzione
            }

            // Controllo Altezza
            if (altezzaVal < 50.0 || altezzaVal > 280.0) {
                _errorMessage.emit("L'altezza deve essere compresa tra 50 e 280 cm.")
                return@launch
            }

            // Controllo Peso
            if (pesoVal < 30.0 || pesoVal > 500.0) {
                _errorMessage.emit("Il peso deve essere compreso tra 30 e 500 kg.")
                return@launch
            }

            // Se arriviamo qui, i dati sono perfetti!
            _isLoading.value = true

            val bsaCalcolato = sqrt((pesoVal * altezzaVal) / 3600.0)
            val bsaArrotondato = String.format(java.util.Locale.US, "%.2f", bsaCalcolato).toDouble()

            _risultatoBsa.value = bsaArrotondato
            val valutazioneTesto = valutaBsa(bsaArrotondato, sessoVal)
            _valutazione.value = valutazioneTesto

            salvaSuFirestore(pesoVal, altezzaVal, sessoVal, bsaArrotondato, valutazioneTesto)
        }
    }

    // Determina se il BSA calcolato è nella norma rispetto alle medie adulte.
    private fun valutaBsa(bsa: Double, sesso: String): String {
        // Valori medi di riferimento per adulti
        val media = if (sesso.lowercase() == "maschio") 1.9 else 1.6
        return when {
            bsa > media + 0.25 -> "Sopra la media"
            bsa < media - 0.25 -> "Sotto la media"
            else -> "Nella media"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun salvaSuFirestore(peso: Double, altezza: Double, sesso: String, bsa: Double, valutazione: String) {
        val userId = repository.getCurrentUserId()

        if (userId == null) {
            _isLoading.value = false
            return
        }

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val dataOraAttuale = LocalDateTime.now().format(formatter)

        val record = BsaRecord(
            dataOra = dataOraAttuale,
            peso = peso,
            altezza = altezza,
            sesso = sesso,
            bsa = bsa,
            valutazione = valutazione
        )

        viewModelScope.launch {
            // CHIAMATA AL REPOSITORY E GESTIONE DELL'ESITO
            val isSuccess = repository.salvaBsaRecord(userId, record)

            if (isSuccess) {
                _isLoading.value = false
                _saveSuccess.emit(true)
            } else {
                _isLoading.value = false
                _errorMessage.emit("Errore durante il salvataggio dei dati.")
            }
        }
    }
}