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

// Raggruppa tutti i dati in un unico oggetto (Single Source of Truth).
// È immutabile: per cambiare un valore, ne creiamo una copia. Questo evita bug di inconsistenza

data class RegisterUiState(
    // Dati dei campi di testo
    val nome: String = "",
    val cognome: String = "",
    val username: String = "",
    val email: String = "",
    //usiamo il tipo stringa per mostrare la data nell UI
    val dataNascita: String = "",
    //andiamo a creare un tipo long per andare a memorizzarlo su firestore
    val dataNascitaMillis: Long? = null,
    val accountType: String = "Paziente", // Valore di default
    val password: String = "",
    val confirmPassword: String = "",

    // Stato della logica UI
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,       // Serve per mostrare una rotellina di caricamento
    val errorMessage: String? = null      // Per mostrare eventuali errori di validazione
)


class RegisterPageViewModel : ViewModel() {

    private val repository = AuthRepository()

    /*
     Il "by mutableStateOf" rende questa variabile reattiva
     Quando il valore di 'uiState' cambia, Compose ridisegna automaticamente
     solo le parti dello schermo che leggono questi dati
     */
    var uiState by mutableStateOf(RegisterUiState())
        private set //Imposta il fatto che solo questo viewmodel può modificare questo valore,
                    // mentre tutti gli altri possono solo leggere i valori

    // Formattatore per trasformare i millisecondi in testo leggibile (dd/MM/yyyy)
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


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

    //In questo modo traduco i millisecondi inviati dal DermaDatePicker in un formato leggibile
    fun onDataNascitaChanged(millis: Long) {
        uiState = uiState.copy(
            dataNascitaMillis = millis,
            dataNascita = dateFormatter.format(java.util.Date(millis))
        )
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
        if (uiState.nome.isBlank() || uiState.username.isBlank() || uiState.email.isBlank()|| uiState.dataNascitaMillis == null) {
            uiState = uiState.copy(errorMessage = "Compila tutti i campi obbligatori")
            return
        }

        // Connessione a Firebase tramite Repository
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val result = repository.registerUser(uiState)

            uiState = if (result.isSuccess) {
                uiState.copy(isLoading = false, isSuccess = true)
            } else {
                uiState.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Errore durante la registrazione"
                )
            }
        }
    }
}
