package it.uninsubria.dermasuite.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.dermasuite.firebase.AuthRepository
import kotlinx.coroutines.launch

class DashboardPagePazienteViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    // Stato per il nome dell'utente, se cambia il composable lo ridisegnerà e nel frattempo che carica scrive
    // la scritta caricamento, private set serve a dire che solo dentro la classe DashboardPageViewModel possiamo
    // modificare questo valore.
    var username by mutableStateOf("Caricamento...")
        private set

    // Il blocco init viene eseguito non appena il ViewModel viene creato. In questo modo, il recupero dei dati
    // da Firestore inizia istantaneamente quando l'utente entra nella Dashboard, senza bisogno di un input manuale.
    init {
        loadUserData()
    }

    private fun loadUserData() {
        // Recupera l'utente attualmente loggato nella sessione locale. Serve per ottenere l'UID,
        // che è la "chiave" per trovare il documento corretto su Firestore.
        val currentUser = FirebaseAuth.getInstance().currentUser
        // ?.let significa che se currentUser non è null allora esegui il blocco di codice tra le graffe
        currentUser?.let { firebaseUser ->
            // Poiché l'accesso al database è un'operazione che richiede tempo, non può essere fatta sul thread principale
            // (quello che gestisce i disegni sullo schermo). viewModelScope avvia una Coroutine,
            // ovvero un processo in background che non blocca l'app.
            viewModelScope.launch {
                val dermaUser = repository.getUserData(firebaseUser.uid)
                if (dermaUser != null) {
                    username = dermaUser.username
                } else {
                    username = "Utente"
                }
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        repository.signOut()
        onSuccess()
    }
}
