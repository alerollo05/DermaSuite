package it.uninsubria.dermasuite.viewmodels.paziente

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class PasiPageViewModel(): ViewModel() {

    //Andiamo a creare le variabili per fare la connessione al DB
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    //Aggiungiamo una variabile per stampare la card del risultato, una volta fatto il calcolo il risultato finale
    var showResult by mutableStateOf(false)

    //Creiamo una variabile per tenere traccia del distretto attualmente selezionato
    var currentDistrict by mutableStateOf(DistrettoCorpo.HEAD)
    //by ci permette  di non dover sscrivere ogni volta setValue e getValue direttamente

    //Creiamo una struttura per andare a memorizzare i parametri per ogni distretto selezionato
    var districtValues by mutableStateOf(
        //mutableStateOf va a controllare costantemente i valori aggiornati della mappa in modo tale da farli
        //cambiare istantaneamente anche nell'interfaccia grafica se i valori cambiano nella mappa
        DistrettoCorpo.values().associateWith { //associteWith trasforma l'elenco dei distretti in una
            //mappa l chiaavi sono i distretti, mentre i valori sono nuove istanze di districtState cioè quindi
            //sono i valori dei parametri che dobbiamo andare a definire da 1 a 4
            DistrictState()
        }
    )

    //Creiamo la variabile per andare a salvare il risultato del calcolo del pasi
    var totalPasiResult by mutableStateOf(0.0)

    //Creiamo la variabile per andare a salvare la severità del risultato
    var serverityClass by mutableStateOf("")


    //Creiamo una funzione per andare ad aggiornare i parametri del distretto corrente
    fun updateDistrictParameters(
        eritema: Int? = null,
        indurimento: Int? = null,
        desquamazione: Int? = null,
        percentualeArea: Int? = null
    ) {
        val currentStateMap = districtValues.toMutableMap()
        val currentData = currentStateMap[currentDistrict] ?: DistrictState()

        //Creiamo il nuovo stato aggiornato
        currentStateMap[currentDistrict] = currentData.copy(
            eritema = eritema ?: currentData.eritema,
            indurimento = indurimento ?: currentData.indurimento,
            desquamazione = desquamazione ?: currentData.desquamazione,
            percentualeArea = percentualeArea ?: currentData.percentualeArea
        )
        districtValues = currentStateMap //Necessario per far aggiornare Compose
    }

    //Creiamo la funzione che calcola effettivamente il PASI una volta che abbiamo i dati aggiornati
    fun calculateTotalPasiAndSave(onSucces: () -> Unit, onError: (String) -> Unit){
        // 1. Calcolo del PASI
        var total = 0.0
        districtValues.forEach { district, data ->
            //cacloliamo per la specifica area la somma dei parametri
            val s = data.eritema + data.indurimento + data.desquamazione
            //Andiamo a prendere l'are interessata del distretto per poi fare il calcolo
            val a = data.percentualeArea.toDouble()
            //andiamo a prendere i pesi dei distretti dalla classe enum
            total += (s * a * district.weight)
        }
        totalPasiResult = Math.round(total * 10.0) /10.0
        //Calcoliamo il livello di severità in base al punteggio
        serverityClass = when {
            totalPasiResult < 5 -> "Lieve"
            totalPasiResult <= 10 -> "Moderato"
            else -> "Severa"
        }

        salvaPasi(onSuccess = {
            showResult= true //Attiviamo la card quando il salvataggio è andato a buon fine
            onSucces},
            onError = onError,serverityClass)

        }
    //Creiamo un metodo per andare a fare il salvataggio dei dati sul DB firestore
    private fun salvaPasi(onSuccess: () -> Unit, onError: (String) -> Unit, severityClass: String){
        val user = auth.currentUser
        if(user == null){
            onError("utente non autenticato")
            return
        }

        //Andiamo a verificare che l'utente sia un paziente (per sicurezza)
        db.collection("users").document(user.uid).get().addOnSuccessListener{
            document ->
                if(document.exists() && document.getString("role") == "Paziente"){

                    //Andiamo a preparare i dati dei distretti per il salvataggio dei dati su DB
                    //Prepariamo i dati dei distretti mappandoli in stringhe
                    //it.name.key va a prendere il nome del distretto che è un enum e lo trasforma in stringa
                    //cosi diventa salvabile in firesotre
                    //Mentre mapValues converte le istanze di districtState in stringhe
                    val dettagliMappa = districtValues.mapKeys { it.key.name }.mapValues { entry ->
                        mapOf(
                            "eritema" to entry.value.eritema,
                            "indurimento" to entry.value.indurimento,
                            "desquamazione" to entry.value.desquamazione,
                            "percentualeArea" to entry.value.percentualeArea
                        )
                    }
                    //Creiamo il pacchetto finito da spedire al DB
                    val payload = hashMapOf(
                        "dataCalcolo" to FieldValue.serverTimestamp(),
                        "pasiTotale" to totalPasiResult,
                        "severita" to severityClass,
                        "parametriDistretti" to dettagliMappa
                    )
                    // Salvataggio nella sottocollezione PASI
                    db.collection("users").document(user.uid)
                        .collection("PASI")
                        .add(payload)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onError(it.message ?: "Errore salvataggio") }
                } else {
                    onError("Solo i pazienti possono salvare i calcoli")
                }
            }
        }

        fun isDistrictComplete(distrettoCorpo: DistrettoCorpo) : Boolean {
            val state = districtValues[distrettoCorpo] ?: return false
            // Un distretto è completo solo se TUTTI i parametri sono stati toccati (diversi da -1)
            return state.eritema != -1 && 
                   state.indurimento != -1 && 
                   state.desquamazione != -1 && 
                   state.percentualeArea != -1
        }
    }

