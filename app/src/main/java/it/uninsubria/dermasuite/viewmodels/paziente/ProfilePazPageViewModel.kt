package it.uninsubria.dermasuite.viewmodels.paziente

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.dermasuite.firebase.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.lang.reflect.Array.set
import java.text.SimpleDateFormat
import java.util.Locale


// Enum per distinguere cosa stiamo modificando nel Bottom Sheet
enum class EditType { NONE, GENERAL, EMAIL, PASSWORD }
class ProfilePazPageViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel(){


    // --- DATI PROFILO (STATO) ---
    var user by mutableStateOf<String?>(null); private set
    var nomeUtente by mutableStateOf<String?>(null); private set
    var cognomeUtente by mutableStateOf<String?>(null); private set
    var dataNascita by mutableStateOf<String?>(null); private set
    var sesso by mutableStateOf<String?>(null); private set
    var email by mutableStateOf<String?>(null); private set
    var password by mutableStateOf<String?>("********"); private set

    // --- GESTIONE ERRORI E FEEDBACK (UNICA) ---
    var snackbarMessage by mutableStateOf<String?>(null); private set
    var inputPopupError by mutableStateOf<String?>(null); private set // Unico errore per tutti i popup

    // --- STATI POPUP USERNAME ---
    var showUsernameDialog by mutableStateOf(false); private set
    var editUsernameText by mutableStateOf(""); private set

    // --- STATI POPUP EMAIL ---
    var showEmailDialog by mutableStateOf(false); private set
    var editEmailText by mutableStateOf(""); private set
    var currentPasswordForEmail by mutableStateOf(""); private set

    // --- STATI POPUP PASSWORD ---
    var showPasswordDialog by mutableStateOf(false); private set
    var currentPasswordText by mutableStateOf(""); private set
    var newPasswordText by mutableStateOf(""); private set
    var confirmNewPasswordText by mutableStateOf(""); private set


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
                    sesso = dermaUser.sesso
                    val timestamp = dermaUser.dataNascita // Questo è l'oggetto Timestamp di Firebase
                    // Converti Timestamp in Date
                    val date = timestamp?.toDate()
                    if (date != null) {
                        // Crea un formattatore con il pattern che preferisci
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        // Assegna la stringa formattata alla variabile dello stato
                        dataNascita = formatter.format(date)
                    } else {
                        dataNascita = "Data di nascita non inserita"
                    }
                    email = dermaUser.email
                    password = "********"
                }
            }
        }
    }

    // --- FUNZIONI DI UTILITY ---
    fun clearSnackbarMessage() { snackbarMessage = null }
    fun clearInputPopupError() { inputPopupError = null } // Resetta l'errore quando chiudi il popup

    // --- LOGICA USERNAME ---
    fun openUsernameDialog() {
        editUsernameText = ""
        clearInputPopupError()
        showUsernameDialog = true
    }
    fun closeUsernameDialog() {
        showUsernameDialog = false
        clearInputPopupError()
    }
    fun updateEditUsernameText(newText: String) {
        editUsernameText = newText
        clearInputPopupError()
    }
    fun confirmUsernameChange() { // Funzione che aggiorna l'username dell'utente
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) return

        if (editUsernameText.isBlank()) {
            // Mostriamo l'errore DENTRO il popup
            inputPopupError = "L'username non può essere vuoto!"
            return
        }

        viewModelScope.launch {// La .launch serve per far partire una coroutine(un thread secondario) asincrona, perchè se no bloccherebbe l'interfaccia e l'app crasherebbe
            // Chiamiamo il repository per aggiornare il dato nel database
            val success = repository.updateUsername(uid, editUsernameText)
            if (success) {
                // L'aggiornamento su Firestore è andato a buon fine!
                // Ora aggiorniamo lo stato locale per far cambiare il testo nella UI
                user = editUsernameText
                snackbarMessage = "Username aggiornato!" // Questa andrà bene nello Scaffold principale
                closeUsernameDialog() // Il popup si chiude, la patina sparisce e la Snackbar brilla!
            } else {
                // Se Firebase fallisce (es. no connessione)
                inputPopupError = "Errore di connessione."
            }
        }
    }

    // --- LOGICA EMAIL ---
    // Prepara e mostra il popup per la modifica dell'email.
    fun openEmailDialog() {
        editEmailText = ""
        currentPasswordForEmail = ""
        clearInputPopupError()
        showEmailDialog = true
    }
    fun closeEmailDialog() { // Chiude il popup della modifica email e resetta lo stato degli errori.
        showEmailDialog = false
        clearInputPopupError()
    }

    fun updateEditEmailText(text: String) { editEmailText = text; clearInputPopupError() } //Aggiorna il valore temporaneo della nuova email mentre l'utente scrive.
    fun updateCurrentPasswordForEmail(text: String) { currentPasswordForEmail = text; clearInputPopupError() } // Aggiorna il valore della password attuale necessaria per la ri-autenticazione.

    // Funzione per cambiare la mail
    fun confirmEmailChange() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        // Validazione Locale: Controlla che i campi non siano vuoti o composti solo da spazi.
        // Se mancano dati, imposta il messaggio di errore per il popup e si ferma.
        if (editEmailText.isBlank() || currentPasswordForEmail.isBlank()) {
            inputPopupError = "Compila tutti i campi."
            return
        }

        viewModelScope.launch {
            // FASE DI SICUREZZA (Ri-autenticazione):
            // Chiama il repository per verificare che la password attuale inserita sia corretta.
            // Questa riga "sospende" la funzione finché Firebase non risponde.
            val passwordSuccess = repository.reauthenticate(currentPasswordForEmail)

            // Se la password è sbagliata, mostra l'errore nel popup e interrompe la coroutine.
            if (!passwordSuccess) {
                inputPopupError = "La password inserita è errata."
                return@launch // Interrompe il processo asincrono, ma lascia il popup aperto
            }

            // AGGIORNAMENTO DATI ISTANTANEO:
            // Se la password che abbiamo inserito è corretta, tenta di aggiornare l'email in Auth e su Firestore all'istante.
            val updateSuccess = repository.updateEmail(uid, editEmailText)
            if (updateSuccess) {
                email = editEmailText // Cambiamo la variabile locale per vederla aggiornata a schermo
                snackbarMessage = "Email aggiornata con successo!"
                closeEmailDialog()
            } else {
                inputPopupError = "Errore durante l'aggiornamento."
            }
        }
    }

    // --- LOGICA PASSWORD ---
    // Prepara e mostra il popup per il cambio password.
    // Resetta tutti i campi di input (attuale, nuova e conferma) e pulisce gli errori.
    fun openPasswordDialog() {
        currentPasswordText = ""
        newPasswordText = ""
        confirmNewPasswordText = ""
        clearInputPopupError()
        showPasswordDialog = true
    }
    fun closePasswordDialog() { // Chiude il popup del cambio password e resetta lo stato degli errori.
        showPasswordDialog = false
        clearInputPopupError()
    }
    // Funzioni di aggiornamento dello stato per i campi di input della password
    fun updateCurrentPasswordText(text: String) { currentPasswordText = text; clearInputPopupError() }
    fun updateNewPasswordText(text: String) { newPasswordText = text; clearInputPopupError() }
    fun updateConfirmNewPasswordText(text: String) { confirmNewPasswordText = text; clearInputPopupError() }

    // Funzione per cambiare la password
    fun confirmPasswordChange() {
        // Primo controllo (Campi vuoti):
        // Verifica che l'utente abbia compilato tutti e tre i campi del popup.
        // Se anche solo uno è vuoto o contiene solo spazi, mostra l'errore e si ferma.
        if (currentPasswordText.isBlank() || newPasswordText.isBlank() || confirmNewPasswordText.isBlank()) {
            inputPopupError = "Compila tutti i campi."
            return
        }
        // Controllo se la nuova password coincide con la conferma
        if (newPasswordText != confirmNewPasswordText) {
            inputPopupError = "Le password non coincidono."
            return
        }
        // Controllo sulla lunghezza della password
        if (newPasswordText.length < 6) {
            inputPopupError = "La password deve avere almeno 6 caratteri."
            return
        }

        viewModelScope.launch {
            // FASE DI SICUREZZA (Ri-autenticazione):
            // Esattamente come per l'email, Firebase esige che l'utente dimostri
            // di essere il proprietario dell'account inviando la sua password attuale.
            val reauthSuccess = repository.reauthenticate(currentPasswordText)
            // Se la vecchia password inserita è sbagliata
            if (!reauthSuccess) {
                inputPopupError = "Password attuale errata."
                return@launch // Interrompe il processo asincrono, ma lascia il popup aperto
            }

            // Aggiorno password su Firestore
            val updateSuccess = repository.updatePassword(newPasswordText)
            if (updateSuccess) {
                snackbarMessage = "Password aggiornata con successo!"
                closePasswordDialog()
            } else {
                inputPopupError = "Errore durante l'aggiornamento."
            }
        }
    }


}