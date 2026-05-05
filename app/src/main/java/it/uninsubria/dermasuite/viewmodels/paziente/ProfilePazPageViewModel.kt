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

    // --- FUNZIONI PER GESTIRE IL POPUP ---

    fun openUsernameDialog() {
        // Quando apro il popup, il campo di testo si riempie con l'username attuale
        editUsernameText = user ?: ""
        showUsernameDialog = true
    }

    fun closeUsernameDialog() {
        showUsernameDialog = false
        inputPopupError = null // Resetta l'errore quando chiudi
    }

    fun updateEditUsernameText(newText: String) {
        editUsernameText = newText
    }

    fun clearSnackbarMessage() {
        snackbarMessage = null
    }

    fun clearInputPopupError(){
        inputPopupError = null
    }

    // Funzione che aggiorna l'username dell'utente
    fun confirmUsernameChange() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) return

        if (editUsernameText.isBlank()) {
            // Mostriamo l'errore DENTRO il popup
            inputPopupError = "L'username non può essere vuoto!"
            return
        }

        viewModelScope.launch {// La .launch serve per far partire una coroutine(thread) asincrona
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


}