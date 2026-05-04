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
import java.text.SimpleDateFormat
import java.util.Locale


// Enum per distinguere cosa stiamo modificando nel Bottom Sheet
enum class EditType { NONE, GENERAL, EMAIL, PASSWORD }
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
                    val timestamp = dermaUser.dataNascita // Questo è l'oggetto Timestamp di Firebase
                    // Converti Timestamp in Date
                    val date = timestamp?.toDate()
                    // Crea un formattatore con il pattern che preferisci
                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    // Assegna la stringa formattata alla variabile dello stato
                    dataNascita = formatter.format(date)
                    email = dermaUser.email
                    password = "********"
                }
            }
        }
    }


}