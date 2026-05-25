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
import it.uninsubria.dermasuite.model.PasiDistrictState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PasiPageViewModel(private val repository: AuthRepository = AuthRepository()): ViewModel() {

    //Aggiungiamo una variabile per stampare la card del risultato, una volta fatto il calcolo il risultato finale
    var showResult by mutableStateOf(false)

    // Un semplice contatore che incrementiamo a ogni calcolo
    //Ci serve per andare a far scorrere ad ogni pressione del bottone verso il basso la pagina in modo da visualizzare la card di result
    var scrollTrigger by mutableStateOf(0)

    //Creiamo una variabile per tenere traccia del distretto attualmente selezionato
    var currentDistrict by mutableStateOf(DistrettoCorpo.HEAD)
    //by ci permette  di non dover sscrivere ogni volta setValue e getValue direttamente

    //Creiamo una struttura per andare a memorizzare i parametri per ogni distretto selezionato
    var districtValues by mutableStateOf(
        //mutableStateOf va a controllare costantemente i valori aggiornati della mappa in modo tale da farli
        //cambiare istantaneamente anche nell'interfaccia grafica se i valori cambiano nella mappa
        DistrettoCorpo.values().associateWith { //associateWith trasforma l'elenco dei distretti in una
            //mappa l chiavi sono i distretti, mentre i valori sono nuove istanze di districtState cioè quindi
            //sono i valori dei parametri che dobbiamo andare a definire da 1 a 4
            PasiDistrictState()
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
        val currentData = currentStateMap[currentDistrict] ?: PasiDistrictState()

        //Creiamo il nuovo stato aggiornato
        currentStateMap[currentDistrict] = currentData.copy(
            erythema = eritema ?: currentData.erythema,
            hardening = indurimento ?: currentData.hardening,
            desquamation = desquamazione ?: currentData.desquamation,
            percentageArea = percentualeArea ?: currentData.percentageArea
        )
        districtValues = currentStateMap //Necessario per far aggiornare Compose

        // Quando un parametro cambia, nascondi il vecchio risultato.
        // Questo farà sì che !showResult in abilitaCalcolo() torni true!
        showResult = false
    }

    //Creiamo la funzione che calcola effettivamente il PASI una volta che abbiamo i dati aggiornati
    fun calculateTotalPasiAndSave(onSucces: () -> Unit, onError: (String) -> Unit){
        // Calcolo del PASI
        var total = 0.0
        districtValues.forEach { district, data ->
            //cacloliamo per la specifica area la somma dei parametri
            val s = data.erythema + data.hardening + data.desquamation
            //Andiamo a prendere l'are interessata del distretto per poi fare il calcolo
            val a = data.percentageArea.toDouble()
            //andiamo a prendere i pesi dei distretti dalla classe enum
            total += (s * a * district.weight)
        }
        totalPasiResult = Math.round(total * 10.0) /10.0
        //Calcoliamo il livello di severità in base al punteggio
        serverityClass = when {
            totalPasiResult < 5 -> "LEVEL_LOW"
            totalPasiResult <= 10 -> "LEVEL_MODERATE"
            else -> "LEVEL_SEVERE"
        }

        salvaPasi(onSuccess = {
            showResult= true //Attiviamo la card quando il salvataggio è andato a buon fine
            scrollTrigger++ //Incrementiamo il contatore per far scorrere la pagina verso il basso (vedi nel PASIPageScreen
            onSucces()},
            onError = onError,serverityClass)
        }
    //Creiamo un metodo per andare a fare il salvataggio dei dati sul DB firestore
    private fun salvaPasi(onSuccess: () -> Unit, onError: (String) -> Unit, severityClass: String){
       viewModelScope.launch{
           try {
               //Prendiamo l'ID utente dal repository invece che da FirebaseAuth
               val uid = repository.getCurrentUserId()

               if (uid == null) {
                   onError("Utente non autenticato")
                   return@launch
               }

               //Prepariamo i dati (questo è compito del ViewModel)
               val dettagliMappa = districtValues.mapKeys { it.key.technicalName }.mapValues { entry ->
                   mapOf(
                       "Erythema" to entry.value.erythema,
                       "Hardening" to entry.value.hardening,
                       "Desquamation" to entry.value.desquamation,
                       "PercentageArea" to entry.value.percentageArea
                   )
               }

               // Creiamo il pacchetto finito
               val payload = hashMapOf(
                   // Usiamo il percorso completo per FieldValue così non serve l'import in alto
                   "CalculationDate" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                   "PasiTot" to totalPasiResult,
                   "Severity" to severityClass,
                   "ParameterDistrict" to dettagliMappa
               )

               //Deleghiamo il salvataggio al Repository
               val isSuccess = repository.salvaPasiRecord(uid, payload)

               if (isSuccess) {
                   onSuccess()
               } else {
                   onError("Errore durante il salvataggio dei dati su Firebase")
               }
           }catch (e: Exception){
               // Gestisce qualsiasi errore (Auth, Firestore, Rete) in un colpo solo
               // Se il get().await() o l'add().await() falliscono (es. niente internet),
               // l'esecuzione salta direttamente qui dentro. Nessun crash, nessun blocco.
               onError(e.message ?: "Errore imprevisto")
           }
       }

        }

        fun isDistrictComplete(distrettoCorpo: DistrettoCorpo) : Boolean {
            val state = districtValues[distrettoCorpo] ?: return false
            // Un distretto è completo solo se TUTTI i parametri sono stati toccati (diversi da -1)
            return state.erythema != -1 &&
                   state.hardening != -1 &&
                   state.desquamation != -1 &&
                   state.percentageArea != -1
        }

        fun abilitaCalcolo(): Boolean{
            // Verifichiamo che per ogni distretto la funzione isDistrictComplete restituisca true
             return DistrettoCorpo.values().all { isDistrictComplete(it) } && !showResult
        }
}

