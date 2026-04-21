package it.uninsubria.dermasuite.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.uninsubria.dermasuite.firebase.AuthRepository
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,       // Serve per mostrare una rotellina di caricamento
    val errorMessage: String? = null,      // Per mostrare eventuali errori di validazione
    val isSuccess: Boolean = false
)
class LoginPageViewModel (private val repository: AuthRepository = AuthRepository()) : ViewModel() {
    /*
     Il "by mutableStateOf" rende questa variabile reattiva
     Quando il valore di 'uiState' cambia, Compose ridisegna automaticamente
     solo le parti dello schermo che leggono questi dati
     */
    var uiState by mutableStateOf(LoginUiState())
        private set //Imposta il fatto che solo questo viewmodel può modificare questo valore,
                    // mentre tutti gli altri possono solo leggere i valori
    // Creiamo delle funzioni di aggiornamento per ogni campo di testo
    // Ogni volta che l'utente scrive un carattere, chiamiamo una di queste funzioni
    fun onEmailChanged(nuovaEmail: String) {
        uiState = uiState.copy(email = nuovaEmail)
    }

    fun onPasswordChanged(nuovaPassword : String){
        uiState = uiState.copy(password = nuovaPassword)
    }

    fun onLoginClick(){

        if (uiState.email.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Compila tutti i campi")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val result = repository.loginUser(uiState.email, uiState.password)

            uiState = if (result.isSuccess) {
                uiState.copy(isLoading = false, isSuccess = true)
            } else {
                uiState.copy(isLoading = false, errorMessage = "Email o Password errati")
            }
        }
    }

    // Logica di business, ad esempio analytics o controllo sessioni
    fun trackStartPageImpression() {
        // ...
    }
}