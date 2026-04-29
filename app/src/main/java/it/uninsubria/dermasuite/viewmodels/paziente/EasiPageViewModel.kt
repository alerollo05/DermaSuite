package it.uninsubria.dermasuite.viewmodels.paziente

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class EasiPageViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    //Creiamo una variabile per tenere traccia del distretto attualmente selezionato
    var currentDistrict by mutableStateOf(DistrettoCorpo.HEAD)

    var districtValues by mutableStateOf(
        DistrettoCorpo.values().associateWith {
            EasiDistrictState()
        }
    )

    //Creiamo la variabile per andare a salvare il risultato del calcolo del pasi
    var totalEasiResult by mutableStateOf(0.0)

    //Creiamo la variabile per andare a salvare la severità del risultato
    var serverityClass by mutableStateOf("")

    fun updateDistrictParameters(
        eritema: Int? = null,
        edemaPapulizzazione: Int? = null,
        escoriazione: Int? = null,
        lichenificazione: Int? = null,
        percentualeArea: Int? = null
    ) {
        val currentStateMap = districtValues.toMutableMap()
        val currentData = currentStateMap[currentDistrict] ?: EasiDistrictState()

        //Creiamo il nuovo stato aggiornato
        currentStateMap[currentDistrict] = currentData.copy(
            eritema = eritema ?: currentData.eritema,
            edemaPapulizzazione = edemaPapulizzazione ?: currentData.edemaPapulizzazione,
            escoriazione = escoriazione ?: currentData.escoriazione,
            lichenificazione = lichenificazione ?: currentData.lichenificazione,
            percentualeArea = percentualeArea ?: currentData.percentualeArea
        )
        districtValues = currentStateMap //Necessario per far aggiornare Compose
    }

    //Creiamo la funzione che calcola effettivamente l'EASI una volta che abbiamo i dati aggiornati
    fun calculateTotalEasiAndSave(onSucces: () -> Unit, onError: (String) -> Unit){
        // IMPLEMENTA
    }

    //Creiamo un metodo per andare a fare il salvataggio dei dati sul DB firestore
    private fun salvaPasi(onSuccess: () -> Unit, onError: (String) -> Unit, severityClass: String){
        // IMPLEMENTA
    }

    fun isDistrictComplete(distrettoCorpo: DistrettoCorpo) : Boolean {
        val state = districtValues[distrettoCorpo] ?: return false
        // Un distretto è completo solo se TUTTI i parametri sono stati toccati (diversi da -1)
        return state.eritema != -1 &&
                state.edemaPapulizzazione != -1 &&
                state.escoriazione != -1 &&
                state.lichenificazione != -1 &&
                state.percentualeArea != -1
    }
}