package it.uninsubria.dermasuite.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class LoginUiState(
    val username: String = "",
    val password: String = "",

    val isLoading: Boolean = false,       // Serve per mostrare una rotellina di caricamento
    val errorMessage: String? = null      // Per mostrare eventuali errori di validazione
)
class LoginPageViewModel : ViewModel() {
    /*
     Il "by mutableStateOf" rende questa variabile reattiva
     Quando il valore di 'uiState' cambia, Compose ridisegna automaticamente
     solo le parti dello schermo che leggono questi dati
     */
    var uiState by mutableStateOf(RegisterUiState())
        private set //Imposta il fatto che solo questo viewmodel può modificare questo valore,
                    // mentre tutti gli altri possono solo leggere i valori
    // Creiamo delle funzioni di aggiornamento per ogni campo di testo
    // Ogni volta che l'utente scrive un carattere, chiamiamo una di queste funzioni


    fun onUsernameChanged(nuovoUsername: String) {
        uiState = uiState.copy(username = nuovoUsername)
    }

    fun onPasswordChanged(nuovaPassword : String){
        uiState = uiState.copy(password = nuovaPassword)
    }

    //fun onLoginClick(){}

    // Logica di business, ad esempio analytics o controllo sessioni
    fun trackStartPageImpression() {
        // ...
    }
}