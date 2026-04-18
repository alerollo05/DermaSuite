package it.uninsubria.dermasuite.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// Raggruppa tutti i dati in un unico oggetto (Single Source of Truth).
// È immutabile: per cambiare un valore, ne creiamo una copia. Questo evita bug di inconsistenza

data class RegisterUiState(
    // Dati dei campi di testo
    val nome: String = "",
    val cognome: String = "",
    val username: String = "",
    val dataNascita: String = "",
    val accountType: String = "Paziente", // Valore di default
    val password: String = "",
    val confirmPassword: String = "",

    // Stato della logica UI
    val isLoading: Boolean = false,       // Serve per mostrare una rotellina di caricamento
    val errorMessage: String? = null      // Per mostrare eventuali errori di validazione
)


class RegisterPageViewModel : ViewModel() {

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

    fun onNomeChanged(nuovoNome: String) {
        // .copy() crea un NUOVO oggetto stato cambiando solo il campo 'nome'
        uiState = uiState.copy(nome = nuovoNome)
    }

    fun onCognomeChanged(nuovoCognome: String) {
        uiState = uiState.copy(cognome = nuovoCognome)
    }

    fun onUsernameChanged(nuovoUsername: String) {
        uiState = uiState.copy(username = nuovoUsername)
    }

    fun onDataNascitaChanged(nuovaData: String) {
        uiState = uiState.copy(dataNascita = nuovaData)
    }

    fun onAccountTypeSelected(nuovoTipo: String) {
        uiState = uiState.copy(accountType = nuovoTipo)
    }

    fun onPasswordChanged(nuovaPass: String) {
        uiState = uiState.copy(password = nuovaPass)
    }

    fun onConfirmPasswordChanged(nuovaPass: String) {
        uiState = uiState.copy(confirmPassword = nuovaPass)
    }


    //Funzione chiamata quando l'utente preme il tasto "Create Account"

    fun onRegisterClick() {
        //Reset degli errori precedenti
        uiState = uiState.copy(errorMessage = null)

        //Validazione base
        if (uiState.password != uiState.confirmPassword) {
            uiState = uiState.copy(errorMessage = "Le password non coincidono!")
            return
        }
        //Altre validazioni da fare
        if (uiState.nome.isBlank() || uiState.username.isBlank()) {
            uiState = uiState.copy(errorMessage = "Compila tutti i campi obbligatori")
            return
        }

        // 3. Avvio caricamento
        uiState = uiState.copy(isLoading = true)

        /**
         * Logica Futura:
         * Qui inserirai la chiamata a Firebase o al tuo server.
         * Una volta finita la chiamata, imposterai isLoading = false.
         */
        println("Registrazione in corso per ${uiState.username}...")
    }
}