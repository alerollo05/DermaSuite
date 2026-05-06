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
import it.uninsubria.dermasuite.model.DistrettoCorpo
import it.uninsubria.dermasuite.model.EasiDistrictState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EasiPageViewModel : ViewModel() {

    // Riferimenti alle istanze di Firestore e FirebaseAuth per i dati e l'utente
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    // Stato per decidere se mostrare o meno la card con il risultato finale
    var showResult by mutableStateOf(false)

    // Contatore utilizzato per triggerare lo scroll automatico verso il basso nella UI
    var scrollTrigger by mutableStateOf(0)

    // Tiene traccia di quale parte del corpo (Testa, Tronco, ecc.) l'utente sta valutando[cite: 11]
    var currentDistrict by mutableStateOf(DistrettoCorpo.HEAD)

    // Mappa che associa ogni distretto del corpo al suo stato dei parametri (eritema, area, ecc.)[cite: 11]
    var districtValues by mutableStateOf(
        DistrettoCorpo.values().associateWith {
            EasiDistrictState() // Inizializza ogni distretto con valori predefiniti (-1)[cite: 11]
        }
    )

    // Variabili per memorizzare il valore numerico finale e la stringa della severità[cite: 11]
    var totalEasiResult by mutableStateOf(0.0)
    var serverityClass by mutableStateOf("")

    // Funzione per aggiornare i singoli parametri del distretto attualmente selezionato[cite: 11]
    fun updateDistrictParameters(
        eritema: Int? = null,
        edemaPapulizzazione: Int? = null,
        escoriazione: Int? = null,
        lichenificazione: Int? = null,
        percentualeArea: Int? = null
    ) {
        val currentStateMap = districtValues.toMutableMap() // Crea una copia modificabile della mappa
        val currentData = currentStateMap[currentDistrict] ?: EasiDistrictState()

        // Crea un nuovo stato copiando quello vecchio ma aggiornando solo i valori non nulli[cite: 11]
        currentStateMap[currentDistrict] = currentData.copy(
            eritema = eritema ?: currentData.eritema,
            edemaPapulizzazione = edemaPapulizzazione ?: currentData.edemaPapulizzazione,
            escoriazione = escoriazione ?: currentData.escoriazione,
            lichenificazione = lichenificazione ?: currentData.lichenificazione,
            percentualeArea = percentualeArea ?: currentData.percentualeArea
        )
        districtValues = currentStateMap // Assegna la nuova mappa per scatenare la ricomposizione della UI[cite: 11]
    }

    // Funzione principale per il calcolo dell'indice EASI[cite: 11]
    fun calculateTotalEasiAndSave(onSucces: () -> Unit, onError: (String) -> Unit){
        var total = 0.0

        // Itera su ogni distretto e applica la formula: (Somma Segni) * Area * Peso Distretto[cite: 11]
        districtValues.forEach { (district, data) ->
            val sommaSegni = data.eritema + data.edemaPapulizzazione + data.escoriazione + data.lichenificazione
            val area = data.percentualeArea.toDouble()
            total += (sommaSegni * area * district.weight) // Aggiunge il parziale al totale[cite: 11]
        }

        // Arrotonda il risultato a un decimale[cite: 11]
        totalEasiResult = Math.round(total * 10.0) / 10.0

        // Assegna la classe di severità in base ai range standard dell'EASI[cite: 11]
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
                val user = auth.currentUser ?: return@launch onError("Utente non autenticato") // Controllo login

                // Verifica il ruolo dell'utente prima di procedere
                val document = db.collection("users").document(user.uid).get().await()
                    if(document.exists() && document.getString("role") == "Paziente"){

                        // Prepara i dettagli tecnici per ogni distretto da salvare
                        val dettagliMappa = districtValues.mapKeys { it.key.technicalName }.mapValues { entry ->
                            mapOf(
                                "Erythema" to entry.value.eritema,
                                "EdemaPapulation" to entry.value.edemaPapulizzazione,
                                "Excoriation" to entry.value.escoriazione,
                                "Lichenification" to entry.value.lichenificazione,
                                "PercentageArea" to entry.value.percentualeArea
                            )
                        }

                        // Crea il documento finale (payload)
                        val payload = hashMapOf(
                            "CalculationDate" to FieldValue.serverTimestamp(),
                            "EasiTot" to totalEasiResult,
                            "Severity" to severityClass,
                            "ParameterDistrict" to dettagliMappa
                        )

                        // Salva nella sottocollezione "EASI" del paziente
                        db.collection("users").document(user.uid)
                            .collection("EASI")
                            .add(payload)
                            .await()

                    } else {
                        onError("Solo i pazienti possono salvare i calcoli")
                    }
                }catch (e: Exception){
                // Gestisce qualsiasi errore (Auth, Firestore, Rete) in un colpo solo
                // Se il get().await() o l'add().await() falliscono (es. niente internet),
                // l'esecuzione salta direttamente qui dentro. Nessun crash, nessun blocco.
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
        return DistrettoCorpo.values().all { isDistrictComplete(it) }
    }
}