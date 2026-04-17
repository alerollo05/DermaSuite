package it.uninsubria.dermasuite.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

data class RegisterUiState(
    val nome: String = "",
    val cognome: String = "",
    val username: String = "",
    val dataNascita: String = "",
    val accountType: String = "Paziente",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false
)

class RegisterPageViewModel : ViewModel() {
    // Lo stato è pubblico in lettura ma privato in scrittura
    var uiState by mutableStateOf(RegisterUiState())
        private set

    // Funzioni per aggiornare lo stato (una per ogni campo)
    fun onNomeChanged(nuovoNome: String) {
        uiState = uiState.copy(nome = nuovoNome)
    }

    fun onCognomeChanged(nuovoCognome: String) {
        uiState = uiState.copy(cognome = nuovoCognome)
    }

    fun onAccountTypeSelected(nuovoTipo: String) {
        uiState = uiState.copy(accountType = nuovoTipo)
    }

    fun onRegisterClick() {
        // Qui chiamerai Gemini o il tuo database in futuro
        uiState = uiState.copy(isLoading = true)
    }
}