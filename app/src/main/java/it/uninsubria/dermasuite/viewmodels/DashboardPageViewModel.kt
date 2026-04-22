package it.uninsubria.dermasuite.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.dermasuite.firebase.AuthRepository
import kotlinx.coroutines.launch

class DashboardPageViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    // Stato per il nome dell'utente
    var userName by mutableStateOf("Caricamento...")
        private set

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { firebaseUser ->
            viewModelScope.launch {
                val dermaUser = repository.getUserData(firebaseUser.uid)
                if (dermaUser != null) {
                    userName = dermaUser.nome
                } else {
                    userName = "Utente"
                }
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        repository.signOut()
        onSuccess()
    }
}
