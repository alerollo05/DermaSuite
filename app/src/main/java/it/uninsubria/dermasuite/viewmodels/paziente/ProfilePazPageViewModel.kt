package it.uninsubria.dermasuite.viewmodels.paziente

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.dermasuite.firebase.AuthRepository
import kotlinx.coroutines.launch
import java.lang.reflect.Array.set

class ProfilePazPageViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel(){


    var user by mutableStateOf<String?>(null)
        private set
    var nomeUtente by mutableStateOf<String?>(null)
        private set
    var cognomeUtente by mutableStateOf<String?>(null)
         private set
    var dataNascita by mutableStateOf<String?>(null)
        private set
    var email by mutableStateOf<String?>(null)
        private set
    var password by mutableStateOf<String?>(null)
        private set
    var confermaPassword by mutableStateOf<String?>(null)
        private set


    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { firebaseUser ->
            viewModelScope.launch {
                val dermaUser = repository.getUserData(firebaseUser.uid)
                if (dermaUser != null) {
                    user = dermaUser.username
                    nomeUtente = dermaUser.nome
                    cognomeUtente = dermaUser.cognome
                    dataNascita = dermaUser.dataNascita.toString()
                    email = dermaUser.email
                    password = "********"
                }
            }
        }
    }


}