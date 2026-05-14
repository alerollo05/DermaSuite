package it.uninsubria.dermasuite.viewmodels.paziente

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
class BsaPageViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // --- STATI DI INPUT DELLA UI ---
    // StateFlow per raccogliere i dati digitati dall'utente in tempo reale.
    private val _peso = MutableStateFlow("")
    val peso: StateFlow<String> = _peso.asStateFlow()

    private val _altezza = MutableStateFlow("")
    val altezza: StateFlow<String> = _altezza.asStateFlow()

    private val _sesso = MutableStateFlow("Maschio") // Impostiamo un valore di default
    val sesso: StateFlow<String> = _sesso.asStateFlow()

    // --- EVENTI E STATI DI OUTPUT ---
    // Usiamo SharedFlow invece di StateFlow per la Snackbar perché è un evento
    // "one-shot" (deve scattare una volta sola e non ripresentarsi se l'utente ruota lo schermo).
    private val _saveSuccess = MutableSharedFlow<Boolean>()
    val saveSuccess: SharedFlow<Boolean> = _saveSuccess.asSharedFlow()

    // Stati per mostrare il risultato e il caricamento
    private val _risultatoBsa = MutableStateFlow<Double?>(null)
    val risultatoBsa: StateFlow<Double?> = _risultatoBsa.asStateFlow()

    private val _valutazione = MutableStateFlow("")
    val valutazione: StateFlow<String> = _valutazione.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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

        // Controlliamo che i valori inseriti siano validi (numeri maggiori di zero)
        if (pesoVal != null && altezzaVal != null && pesoVal > 0 && altezzaVal > 0) {
            _isLoading.value = true // Blocchiamo il pulsante per evitare doppi click

            // Calcolo matematico con la formula di Mosteller: sqrt((peso * altezza) / 3600)
            val bsaCalcolato = sqrt((pesoVal * altezzaVal) / 3600.0)

            // Arrotondiamo a 2 decimali e gestiamo il problema della virgola locale
            val bsaArrotondato = String.format("%.2f", bsaCalcolato).replace(",", ".").toDouble()

            // Aggiorniamo la UI con i risultati
            _risultatoBsa.value = bsaArrotondato
            val valutazioneTesto = valutaBsa(bsaArrotondato, sessoVal)
            _valutazione.value = valutazioneTesto

            // Procediamo al salvataggio nel database
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
        val userId = auth.currentUser?.uid

        if (userId == null) {
            _isLoading.value = false
            return
        }

        // Generiamo il timestamp testuale al momento del salvataggio
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val dataOraAttuale = LocalDateTime.now().format(formatter)

        // Prepariamo l'oggetto (record) da inviare a Firebase
        val record = BsaRecord(
            dataOra = dataOraAttuale,
            peso = peso,
            altezza = altezza,
            sesso = sesso,
            bsa = bsa,
            valutazione = valutazione
        )

        // Lanciamo una coroutine per l'operazione di rete (non blocca la UI)
        viewModelScope.launch {
            try {
                db.collection("users").document(userId)
                    .collection("BSA") // Salviamo nella sotto-collezione "BSA" del paziente
                    .add(record)
                    .await() // Attendiamo la conferma da parte di Firebase

                _isLoading.value = false // Sblocchiamo il pulsante
                _saveSuccess.emit(true)  // Diciamo alla View di mostrare la Snackbar di successo
            } catch (e: Exception) {
                Log.e("BsaViewModel", "Errore: ${e.message}")
                _isLoading.value = false
            }
        }
    }
}