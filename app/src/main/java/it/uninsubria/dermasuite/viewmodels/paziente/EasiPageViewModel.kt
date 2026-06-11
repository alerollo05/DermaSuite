package it.uninsubria.dermasuite.viewmodels.paziente

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import it.uninsubria.dermasuite.firebase.AuthRepository
import it.uninsubria.dermasuite.model.DistrettoCorpo
import it.uninsubria.dermasuite.model.EasiDistrictState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EasiPageViewModel(
    private val repository: AuthRepository = AuthRepository()
): ViewModel() {


    // Stato per decidere se mostrare o meno la card con il risultato finale
    var showResult by mutableStateOf(false)

    // Contatore utilizzato per triggerare lo scroll automatico verso il basso nella UI
    var scrollTrigger by mutableStateOf(0)

    // Tiene traccia di quale parte del corpo (Testa, Tronco, ecc.) l'utente sta valutando
    var currentDistrict by mutableStateOf(DistrettoCorpo.HEAD)

    // Mappa che associa ogni distretto del corpo al suo stato dei parametri (eritema, area, ecc.)
    var districtValues by mutableStateOf(
        DistrettoCorpo.values().associateWith {
            EasiDistrictState() // Inizializza ogni distretto con valori predefiniti (-1)
        }
    )

    // Variabili per memorizzare il valore numerico finale e la stringa della severità
    var totalEasiResult by mutableStateOf(0.0)
    var serverityClass by mutableStateOf("")

    // Funzione per aggiornare i singoli parametri del distretto attualmente selezionato
    fun updateDistrictParameters(
        eritema: Int? = null,
        edemaPapulizzazione: Int? = null,
        escoriazione: Int? = null,
        lichenificazione: Int? = null,
        percentualeArea: Int? = null
    ) {
        val currentStateMap = districtValues.toMutableMap() // Crea una copia modificabile della mappa
        val currentData = currentStateMap[currentDistrict] ?: EasiDistrictState()

        // Crea un nuovo stato copiando quello vecchio ma aggiornando solo i valori non nulli
        currentStateMap[currentDistrict] = currentData.copy(
            eritema = eritema ?: currentData.eritema,
            edemaPapulizzazione = edemaPapulizzazione ?: currentData.edemaPapulizzazione,
            escoriazione = escoriazione ?: currentData.escoriazione,
            lichenificazione = lichenificazione ?: currentData.lichenificazione,
            percentualeArea = percentualeArea ?: currentData.percentualeArea
        )
        districtValues = currentStateMap // Assegna la nuova mappa per scatenare la ricomposizione della UI

        // Quando un parametro cambia, nascondi il vecchio risultato.
        // Questo farà sì che !showResult in abilitaCalcolo() torni true
        showResult = false
    }

    // Funzione principale per il calcolo dell'indice EASI
    fun calculateTotalEasiAndSave(onSucces: () -> Unit, onError: (String) -> Unit){
        var total = 0.0

        // Itera su ogni distretto e applica la formula: (Somma Segni) * Area * Peso Distretto
        districtValues.forEach { (district, data) ->
            val sommaSegni = data.eritema + data.edemaPapulizzazione + data.escoriazione + data.lichenificazione
            val area = data.percentualeArea.toDouble()
            total += (sommaSegni * area * district.weight) // Aggiunge il parziale al totale
        }

        // Arrotonda il risultato a un decimale
        totalEasiResult = Math.round(total * 10.0) / 10.0

        // Assegna la classe di severità in base ai range standard dell'EASI
        serverityClass = when {
            totalEasiResult < 6.0 -> "LEVEL_LOW"
            totalEasiResult <= 22.9 -> "LEVEL_MODERATE"
            else -> "LEVEL_SEVERE"
        }

        // Tenta il salvataggio su Firestore
        salvaEasi(
            onSuccess = {
                showResult = true // Se salvato, mostra il risultato
                scrollTrigger++   // Attiva lo scroll
                onSucces()        // Callback di successo per la UI
            },
            onError = onError,
            severityClass = serverityClass
        )
    }

    // Funzione privata per gestire l'interazione con il database Firestore
    private fun salvaEasi(onSuccess: () -> Unit, onError: (String) -> Unit, severityClass: String){
        viewModelScope.launch {
            try {
                // Prendi l'UID tramite FirebaseAuth
                val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

                if (uid == null) {
                    onError("Utente non autenticato")
                    return@launch
                }

                // Creazione del Payload (questa parte rimane nel ViewModel perché è logica di business)
                val dettagliMappa = districtValues.mapKeys { it.key.technicalName }.mapValues { entry ->
                    mapOf(
                        "Erythema" to entry.value.eritema,
                        "EdemaPapulation" to entry.value.edemaPapulizzazione,
                        "Excoriation" to entry.value.escoriazione,
                        "Lichenification" to entry.value.lichenificazione,
                        "PercentageArea" to entry.value.percentualeArea
                    )
                }

                val payload = hashMapOf(
                    "CalculationDate" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                    "EasiTot" to totalEasiResult,
                    "Severity" to severityClass,
                    "ParameterDistrict" to dettagliMappa
                )

                // LA CHIAMATA PULITA AL REPOSITORY
                val isSuccess = repository.salvaEasiRecord(uid, payload)

                if (isSuccess) {
                    onSuccess()
                } else {
                    onError("Errore durante il salvataggio")
                }
            }catch (e: Exception){
                // Gestisce qualsiasi errore (Auth, Firestore, Rete) in un colpo solo
                onError(e.message ?: "Errore imprevisto")
            }
        }
    }


    // Verifica se tutti i parametri di un singolo distretto sono stati inseriti
    fun isDistrictComplete(distrettoCorpo: DistrettoCorpo) : Boolean {
        val state = districtValues[distrettoCorpo] ?: return false
        return state.eritema != -1 && state.edemaPapulizzazione != -1 &&
                state.escoriazione != -1 && state.lichenificazione != -1 &&
                state.percentualeArea != -1
    }

    // Controlla se l'intero modulo è pronto per il calcolo
    fun abilitaCalcolo(): Boolean {
        return DistrettoCorpo.values().all { isDistrictComplete(it) } && !showResult
    }
}