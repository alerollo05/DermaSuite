package it.uninsubria.dermasuite.viewmodels.medico

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.firebase.AuthRepository
import it.uninsubria.dermasuite.firebase.DermaUser
import kotlinx.coroutines.launch


class DashboardPageMedicoViewModel (private val repository: AuthRepository = AuthRepository()) : ViewModel()  {

    // Stato per il nome dell'utente, se cambia il composable lo ridisegnerà e nel frattempo che carica scrive
    // la scritta caricamento, private set serve a dire che solo dentro la classe DashboardPageViewModel possiamo
    // modificare questo valore.
    var username by mutableStateOf("Caricamento...")
        private set

    //Altri stati
    //Per i caricamenti
    var isLoading by mutableStateOf(true)
        private set

    //Per la ricerca dei pazienti
    var searchQuery by mutableStateOf("")
    private set

    //Lista inizialmente vuota che conterrà i pazienti associati al medico loggato
    var allPatients by mutableStateOf<List<DermaUser>>(emptyList())
    private set

    //Lista dove andremo a memorizzare tutti i pazienti filtrati
    var filteredPatients by mutableStateOf<List<DermaUser>>(emptyList())


    // Il blocco init viene eseguito non appena il ViewModel viene creato. In questo modo, il recupero dei dati
    // da Firestore inizia istantaneamente quando l'utente entra nella Dashboard, senza bisogno di un input manuale.
    init {
        loadUserDataAndPatient()
    }

    private fun loadUserDataAndPatient() {
        // Recupera l'utente attualmente loggato nella sessione locale. Serve per ottenere l'UID,
        // che è la "chiave" per trovare il documento corretto su Firestore.
        val currentUser = FirebaseAuth.getInstance().currentUser
        // ?.let significa che se currentUser non è null allora esegui il blocco di codice tra le graffe
        currentUser?.let { firebaseUser ->
            // Poiché l'accesso al database è un'operazione che richiede tempo, non può essere fatta sul thread principale
            // (quello che gestisce i disegni sullo schermo). viewModelScope avvia una Coroutine,
            // ovvero un processo in background che non blocca l'app.
            viewModelScope.launch {
                isLoading = true
                //Carico i dati del medico
                val dermaUser = repository.getUserData(firebaseUser.uid)
                username = dermaUser?.username ?: "Dottore"
                //Carico la lista dei pazienti
                allPatients = repository.getMyPatients(firebaseUser.uid)
                //All'inizio le metto entrambe uguali con tutti i pazienti
                filteredPatients = allPatients

                isLoading = false;
            }
        }
    }
    // Funzione chiamata quando si scrive nella barra di ricerca
    fun onSearchingQuery(newQuery: String){
        searchQuery = newQuery
        //Andiamo a mettere nella lista dei pazienti filtrati solo quelli che corrispondono alla ricerca
        filteredPatients = if (newQuery.isBlank()){
            allPatients
        }else{
            allPatients.filter { paziente ->
                //Se quello scretto nella query è presente nel nome, cognome o username del paziente lo metto nella lista filtrata
                        paziente.nome.contains(newQuery, ignoreCase = true) ||
                        paziente.cognome.contains(newQuery, ignoreCase = true) ||
                        paziente.username.contains(newQuery, ignoreCase = true)
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        repository.signOut()
        onSuccess()
    }
}