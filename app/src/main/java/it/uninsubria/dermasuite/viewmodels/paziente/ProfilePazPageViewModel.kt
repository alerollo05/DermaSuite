package it.uninsubria.dermasuite.viewmodels.paziente

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.firebase.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import it.uninsubria.dermasuite.firebase.DermaUser


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

    //Variabili di stato per la ricerca dei medici per il paziente
    var currentDoctor by mutableStateOf<DermaUser?>(null); private set
    var showDoctorDialog by mutableStateOf(false); private set
    var doctorList by mutableStateOf<List<DermaUser>>(emptyList()); private set
    var isLoadingDoctors by mutableStateOf(false); private set

    //Variabili di stato per la gestione dell'avatar
    var avatarUrl by mutableStateOf<String?>(null); private set
    var isUploading by mutableStateOf(false); private set

    // --- STATI POPUP ELIMINAZIONE ACCOUNT ---
    var showDeleteDialog by mutableStateOf(false); private set
    var deletePasswordText by mutableStateOf(""); private set


    init {
        loadProfileData()
    }

    // --- LOGICA ELIMINAZIONE ACCOUNT ---
    fun openDeleteDialog() {
        deletePasswordText = ""
        clearInputPopupError()
        showDeleteDialog = true
    }

    fun closeDeleteDialog() {
        showDeleteDialog = false
        clearInputPopupError()
    }

    fun updateDeletePasswordText(text: String) {
        deletePasswordText = text
        clearInputPopupError()
    }

    fun confirmDeleteAccount(context: android.content.Context, onSuccess: () -> Unit) {
        if (deletePasswordText.isBlank()) {
            inputPopupError = context.getString(R.string.error_fill_all_fields) // "Compila tutti i campi"
            return
        }

        viewModelScope.launch {
            // Chiamiamo il repository passando la password inserita
            val success = repository.deleteAccount(deletePasswordText)

            if (success) {
                closeDeleteDialog()
                // Se l'eliminazione ha successo, chiamiamo la funzione che farà il logout/navigazione
                onSuccess()
            } else {
                // Se fallisce (es. password errata)
                inputPopupError = context.getString(R.string.error_wrong_password) // "Password errata"
            }
        }
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
                    avatarUrl = dermaUser.avatarUrl // Carica l'URL dell'avatar esistente
                    val timestamp = dermaUser.dataNascita // Questo è l'oggetto Timestamp di Firebase
                    // Converti Timestamp in Date
                    val date = timestamp?.toDate()
                    if (date != null) {
                        // Crea un formattatore con il pattern che preferisci
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        // Assegna la stringa formattata alla variabile dello stato
                        dataNascita = formatter.format(date)
                    } else {
                        dataNascita = null
                    }
                    email = dermaUser.email
                    password = "********"
                    if(dermaUser.doctorId != null){
                        currentDoctor = repository.getUserData(dermaUser.doctorId) //Metto i dati del dottore linkato al paziente nel viewModel
                    }
                }
            }
        }
    }

    //Metodi per la gestione del popup di ricerca dei medici
    fun openDoctorDialog(){
        showDoctorDialog = true
        isLoadingDoctors = true
        viewModelScope.launch {
            doctorList = repository.getAvailableDoctors() //Andiamo a richiamare il metodo nel auth repository per ottenere la lista dei medici
            isLoadingDoctors = false
        }
    }
    fun closeDoctorDialog(){
        showDoctorDialog = false
    }
    fun selectDoctor(doctorUid: String, context: android.content.Context){
        val stringaDialogSuccesso = context.getString(R.string.dialog_successo_ricerca_medico)
        val patientUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val success = repository.linkDoctorToPatient(patientUid,doctorUid)
            if(success){
                snackbarMessage = stringaDialogSuccesso
                currentDoctor = doctorList.find { it.uid == doctorUid } //Andiamo ad aggiornare la UI con il nuovo dottore
                closeDoctorDialog()
            }else{
                snackbarMessage = context.getString(R.string.dialog_errore_ricerca_medico)
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
    fun confirmUsernameChange(context: android.content.Context) { // Funzione che aggiorna l'username dell'utente
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) return

        if (editUsernameText.isBlank()) {
            // Mostriamo l'errore DENTRO il popup
            inputPopupError = context.getString(R.string.error_empty_username)
            return
        }

        viewModelScope.launch {// La .launch serve per far partire una coroutine(un thread secondario) asincrona, perchè se no bloccherebbe l'interfaccia e l'app crasherebbe
            // Chiamiamo il repository per aggiornare il dato nel database
            val success = repository.updateUsername(uid, editUsernameText)
            if (success) {
                // L'aggiornamento su Firestore è andato a buon fine!
                // Ora aggiorniamo lo stato locale per far cambiare il testo nella UI
                user = editUsernameText
                snackbarMessage = context.getString(R.string.msg_username_updated) // Questa andrà bene nello Scaffold principale
                closeUsernameDialog() // Il popup si chiude, la patina sparisce e la Snackbar brilla!
            } else {
                // Se Firebase fallisce (es. no connessione)
                inputPopupError = context.getString(R.string.error_connection)
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
    fun confirmEmailChange(context: android.content.Context) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        // Validazione Locale: Controlla che i campi non siano vuoti o composti solo da spazi.
        // Se mancano dati, imposta il messaggio di errore per il popup e si ferma.
        if (editEmailText.isBlank() || currentPasswordForEmail.isBlank()) {
            inputPopupError = context.getString(R.string.error_fill_all_fields)
            return
        }

        viewModelScope.launch {
            // FASE DI SICUREZZA (Ri-autenticazione):
            // Chiama il repository per verificare che la password attuale inserita sia corretta.
            // Questa riga "sospende" la funzione finché Firebase non risponde.
            val passwordSuccess = repository.reauthenticate(currentPasswordForEmail)

            // Se la password è sbagliata, mostra l'errore nel popup e interrompe la coroutine.
            if (!passwordSuccess) {
                inputPopupError = context.getString(R.string.error_wrong_password)
                return@launch // Interrompe il processo asincrono, ma lascia il popup aperto
            }

            // AGGIORNAMENTO DATI ISTANTANEO:
            // Se la password che abbiamo inserito è corretta, tenta di aggiornare l'email in Auth e su Firestore all'istante.
            val updateSuccess = repository.updateEmail(uid, editEmailText)
            if (updateSuccess) {
                email = editEmailText // Cambiamo la variabile locale per vederla aggiornata a schermo
                snackbarMessage = context.getString(R.string.msg_email_updated)
                closeEmailDialog()
            } else {
                inputPopupError = context.getString(R.string.error_update_failed)
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
    fun confirmPasswordChange(context: android.content.Context) {
        // Primo controllo (Campi vuoti):
        // Verifica che l'utente abbia compilato tutti e tre i campi del popup.
        // Se anche solo uno è vuoto o contiene solo spazi, mostra l'errore e si ferma.
        if (currentPasswordText.isBlank() || newPasswordText.isBlank() || confirmNewPasswordText.isBlank()) {
            inputPopupError = context.getString(R.string.error_fill_all_fields)
            return
        }
        // Controllo se la nuova password coincide con la conferma
        if (newPasswordText != confirmNewPasswordText) {
            inputPopupError = context.getString(R.string.error_passwords_not_match)
            return
        }
        // Controllo sulla lunghezza della password
        if (newPasswordText.length < 6) {
            inputPopupError = context.getString(R.string.error_password_too_short)
            return
        }

        viewModelScope.launch {
            // FASE DI SICUREZZA (Ri-autenticazione):
            // Esattamente come per l'email, Firebase esige che l'utente dimostri
            // di essere il proprietario dell'account inviando la sua password attuale.
            val reauthSuccess = repository.reauthenticate(currentPasswordText)
            // Se la vecchia password inserita è sbagliata
            if (!reauthSuccess) {
                inputPopupError = context.getString(R.string.error_current_password_wrong)
                return@launch // Interrompe il processo asincrono, ma lascia il popup aperto
            }

            // Aggiorno password su Firestore
            val updateSuccess = repository.updatePassword(newPasswordText)
            if (updateSuccess) {
                snackbarMessage = context.getString(R.string.msg_password_updated)
                closePasswordDialog()
            } else {
                inputPopupError = context.getString(R.string.error_update_failed)
            }
        }
    }

    fun updateAvatar(uri: Uri, contentResolver: ContentResolver) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isUploading = true
            val inputStream = try {
                contentResolver.openInputStream(uri)
            } catch (e: Exception) {
                null
            }

            if (inputStream != null) {
                val url = repository.uploadAvatar(uid, inputStream)
                if (url != null) {
                    avatarUrl = url
                    snackbarMessage = "Immagine caricata con successo."
                } else {
                    snackbarMessage = "Errore durante il caricamento."
                }
            } else {
                snackbarMessage = "Immagine non trovata."
            }
            isUploading = false
        }
    }


}