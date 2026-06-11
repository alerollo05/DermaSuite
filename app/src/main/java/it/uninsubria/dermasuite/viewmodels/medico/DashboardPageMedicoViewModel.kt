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


    init {
        loadUserDataAndPatient()
    }

    private fun loadUserDataAndPatient() {

        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { firebaseUser ->
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
                //Se quello scritto nella query (nella barra di ricerca) è presente nel nome, cognome o username del paziente lo metto nella lista filtrata
                //Creiamo delle stringhe complete per facilitare la ricerca
                val nomeCompleto = "${paziente.nome} ${paziente.cognome}"
                val cognomeNome = "${paziente.cognome} ${paziente.nome}"

                //Togliamo gli spazi se ci sono
                val queryPulita = newQuery.trim()

                //Facciamo il controllo sulle stringhe unite
                nomeCompleto.contains(queryPulita, ignoreCase = true) ||
                        cognomeNome.contains(queryPulita, ignoreCase = true) ||
                        paziente.username.contains(queryPulita, ignoreCase = true)
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        repository.signOut()
        onSuccess()
    }
}