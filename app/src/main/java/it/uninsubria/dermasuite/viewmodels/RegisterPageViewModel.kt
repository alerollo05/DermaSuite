package it.uninsubria.dermasuite.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import it.uninsubria.dermasuite.firebase.AuthRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

data class RegisterUiState(
    // Dati dei campi di testo
    val nome: String = "",
    val cognome: String = "",
    val username: String = "",
    val email: String = "",
    val dataNascita: String = "",
    val dataNascitaMillis: Long? = null,
    val sesso: String = "",
    val accountType: String = "Paziente",
    val password: String = "",
    val confirmPassword: String = "",

    // Stato della logica UI
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)


class RegisterPageViewModel : ViewModel() {

    private val repository = AuthRepository()

    var uiState by mutableStateOf(RegisterUiState())
        private set

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun onNomeChanged(nuovoNome: String) {
        uiState = uiState.copy(nome = nuovoNome)
    }

    fun onCognomeChanged(nuovoCognome: String) {
        uiState = uiState.copy(cognome = nuovoCognome)
    }

    fun onUsernameChanged(nuovoUsername: String) {
        uiState = uiState.copy(username = nuovoUsername)
    }

    fun onDataNascitaChanged(millis: Long) {
        uiState = uiState.copy(
            dataNascitaMillis = millis,
            dataNascita = dateFormatter.format(java.util.Date(millis))
        )
    }

    fun onSessoChanged(nuovoSesso: String) {
        uiState = uiState.copy(sesso = nuovoSesso)
    }

    fun onAccountTypeSelected(nuovoTipo: String) {
        uiState = uiState.copy(accountType = nuovoTipo)
    }
    fun onEmailChanged(nuovaEmail: String) {
        uiState = uiState.copy(email = nuovaEmail)
    }

    fun onPasswordChanged(nuovaPass: String) {
        uiState = uiState.copy(password = nuovaPass)
    }

    fun onConfirmPasswordChanged(nuovaPass: String) {
        uiState = uiState.copy(confirmPassword = nuovaPass)
    }


    //Funzione chiamata quando l'utente preme il tasto crea un account
    fun onRegisterClick(
        errorFillAll: String,
        errorInvalidName: String,
        errorInvalidSurname: String,
        errorNameTooShort: String,
        errorInvalidEmail: String,
        errorPassTooShort: String,
        errorPassNotMatch: String,
        errorRegFailed: String
    ) {
        //Reset degli errori precedenti
        uiState = uiState.copy(errorMessage = null)


        val nome = uiState.nome.trim()
        val cognome = uiState.cognome.trim()
        val username = uiState.username.trim()
        val email = uiState.email.trim()
        val sesso = uiState.sesso.trim()

        uiState = uiState.copy(
            nome = nome,
            cognome = cognome,
            username = username,
            email = email,
            sesso = sesso
        )


        if (nome.isBlank() || cognome.isBlank() || username.isBlank() || email.isBlank() || uiState.dataNascitaMillis == null || sesso.isBlank()) {
            uiState = uiState.copy(errorMessage = errorFillAll)
            return
        }

        val nameRegex = "^[\\p{L} .'-]+$".toRegex()

        if (!nameRegex.matches(nome)) {
            uiState = uiState.copy(errorMessage = errorInvalidName)
            return
        }

        if (!nameRegex.matches(cognome)) {
            uiState = uiState.copy(errorMessage = errorInvalidSurname)
            return
        }
        // Controllo lunghezza minima per nome e cognome
        if (nome.length < 2 || cognome.length < 2) {
            uiState = uiState.copy(errorMessage = errorNameTooShort)
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            uiState = uiState.copy(errorMessage = errorInvalidEmail)
            return
        }


        if (uiState.password.length < 6) {
            uiState = uiState.copy(errorMessage = errorPassTooShort)
            return
        }

        if (uiState.password != uiState.confirmPassword) {
            uiState = uiState.copy(errorMessage = errorPassNotMatch)
            return
        }

        // Connessione a Firebase tramite Repository
        viewModelScope.launch {//Couroutine per non far bloccare l'interfaccia in caso di errore
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val result = repository.registerUser(uiState)

            uiState = if (result.isSuccess) {
                uiState.copy(isLoading = false, isSuccess = true)
            } else {
                uiState.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: errorRegFailed
                )
            }
        }
    }
}
