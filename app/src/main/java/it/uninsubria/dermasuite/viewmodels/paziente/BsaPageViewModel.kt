package it.uninsubria.dermasuite.viewmodels.paziente

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import it.uninsubria.dermasuite.R
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

class BsaPageViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _peso = MutableStateFlow("")
    val peso: StateFlow<String> = _peso.asStateFlow()

    private val _altezza = MutableStateFlow("")
    val altezza: StateFlow<String> = _altezza.asStateFlow()

    private val _sesso = MutableStateFlow("")
    val sesso: StateFlow<String> = _sesso.asStateFlow()

    private val _saveSuccess = MutableSharedFlow<Boolean>()
    val saveSuccess: SharedFlow<Boolean> = _saveSuccess.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    private val _risultatoBsa = MutableStateFlow<Double?>(null)
    val risultatoBsa: StateFlow<Double?> = _risultatoBsa.asStateFlow()

    private val _valutazione = MutableStateFlow("")
    val valutazione: StateFlow<String> = _valutazione.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


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
    fun onPesoChange(newPeso: String) {
        _peso.value = newPeso
        _risultatoBsa.value = null // Nasconde la card quando l'utente inizia a scrivere un nuovo peso
    }

    fun onAltezzaChange(newAltezza: String) {
        _altezza.value = newAltezza
        _risultatoBsa.value = null // Nasconde la card quando l'utente inizia a scrivere una nuova altezza
    }


    // Funzione principale attivata dal pulsante "Calcola".
    @RequiresApi(Build.VERSION_CODES.O)
    fun calcolaBsa(context: Context) {
        val pesoVal = _peso.value.toDoubleOrNull()
        val altezzaVal = _altezza.value.toDoubleOrNull()
        val sessoVal = _sesso.value

        // Usiamo una coroutine perché _errorMessage.emit() ha bisogno di essere sospeso
        viewModelScope.launch {
            // Controllo che i campi non siano vuoti o formattati male
            if (pesoVal == null || altezzaVal == null) {
                _errorMessage.emit(context.getString(R.string.bsa_error_missing_data))
                return@launch // Interrompe l'esecuzione della funzione
            }

            // Controllo Altezza
            if (altezzaVal < 50.0 || altezzaVal > 280.0) {
                _errorMessage.emit(context.getString(R.string.bsa_error_height_range))
                return@launch
            }

            // Controllo Peso
            if (pesoVal < 30.0 || pesoVal > 500.0) {
                _errorMessage.emit(context.getString(R.string.bsa_error_weight_range))
                return@launch
            }

            // Se arriviamo qui, i dati sono perfetti!
            _isLoading.value = true

            val bsaCalcolato = sqrt((pesoVal * altezzaVal) / 3600.0)
            val bsaArrotondato = String.format(java.util.Locale.US, "%.2f", bsaCalcolato).toDouble()

            _risultatoBsa.value = bsaArrotondato
            val valutazioneTesto = valutaBsa(bsaArrotondato, sessoVal, context)
            _valutazione.value = valutazioneTesto

            salvaSuFirestore(pesoVal, altezzaVal, sessoVal, bsaArrotondato, valutazioneTesto)
        }
    }

    // Determina se il BSA calcolato è nella norma rispetto alle medie adulte.
    private fun valutaBsa(bsa: Double, sesso: String, context: Context): String {
        // Valori medi di riferimento per adulti
        val media = if (sesso.lowercase() == "maschio") 1.9 else 1.6
        return when {
            bsa > media + 0.25 -> context.getString(R.string.bsa_eval_above_avg)
            bsa < media - 0.25 -> context.getString(R.string.bsa_eval_below_avg)
            else -> context.getString(R.string.bsa_eval_avg)
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
    fun isCalcoloAbilitato(): Boolean {
        // Aggiungiamo anche il controllo su isLoading per evitare calcoli multipli
        // Non stiamo già mostrando il risultato di questo esatto calcolo
        return peso.value.isNotBlank() &&
                altezza.value.isNotBlank() &&
                !isLoading.value &&
                risultatoBsa.value == null
    }
}