package it.uninsubria.dermasuite.viewmodels.paziente

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.dermasuite.firebase.AuthRepository
import kotlinx.coroutines.launch

class DashboardPagePazienteViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    var username by mutableStateOf<String?>(null)
        private set

    init {
        loadUserData()
    }

    private fun loadUserData() {
        // Recupera l'utente attualmente loggato nella sessione locale. Serve per ottenere l'UID,
        // che è la "chiave" per trovare il documento corretto su Firestore.
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { firebaseUser ->
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

}