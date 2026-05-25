package it.uninsubria.dermasuite.viewmodels.medico

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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileMedPageViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    // --- DATI PROFILO (STATO) ---
    var user by mutableStateOf<String?>(null); private set
    var nomeUtente by mutableStateOf<String?>(null); private set
    var cognomeUtente by mutableStateOf<String?>(null); private set
    var dataNascita by mutableStateOf<String?>(null); private set
    var sesso by mutableStateOf<String?>(null); private set
    var email by mutableStateOf<String?>(null); private set
    var password by mutableStateOf<String?>("********"); private set

    // Nuovi campi per il medico
    var specializzazione by mutableStateOf<String?>(null); private set
    var descrizione by mutableStateOf<String?>(null); private set

    // --- GESTIONE ERRORI E FEEDBACK ---
    var snackbarMessage by mutableStateOf<String?>(null); private set
    var inputPopupError by mutableStateOf<String?>(null); private set

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

    // --- STATI POPUP SPECIALIZZAZIONE ---
    var showSpecializationDialog by mutableStateOf(false); private set
    var editSpecializationText by mutableStateOf(""); private set

    // --- STATI POPUP DESCRIZIONE ---
    var showDescriptionDialog by mutableStateOf(false); private set
    var editDescriptionText by mutableStateOf(""); private set

    // --- STATI AVATAR ---
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
                    avatarUrl = dermaUser.avatarUrl
                    specializzazione = dermaUser.specialization
                    descrizione = dermaUser.description

                    val timestamp = dermaUser.dataNascita
                    val date = timestamp?.toDate()
                    if (date != null) {
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dataNascita = formatter.format(date)
                    } else {
                        dataNascita = null
                    }
                    email = dermaUser.email
                    password = "********"
                }
            }
        }
    }

    // --- FUNZIONI DI UTILITY ---
    fun clearSnackbarMessage() { snackbarMessage = null }
    fun clearInputPopupError() { inputPopupError = null }

    // --- LOGICA USERNAME ---
    fun openUsernameDialog() { editUsernameText = ""; clearInputPopupError(); showUsernameDialog = true }
    fun closeUsernameDialog() { showUsernameDialog = false; clearInputPopupError() }
    fun updateEditUsernameText(newText: String) { editUsernameText = newText; clearInputPopupError() }
    fun confirmUsernameChange(context: android.content.Context) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (editUsernameText.isBlank()) {
            inputPopupError = context.getString(R.string.error_empty_username)
            return
        }
        viewModelScope.launch {
            val success = repository.updateUsername(uid, editUsernameText)
            if (success) {
                user = editUsernameText
                snackbarMessage = context.getString(R.string.msg_username_updated)
                closeUsernameDialog()
            } else {
                inputPopupError = context.getString(R.string.error_connection)
            }
        }
    }

    // --- LOGICA EMAIL ---
    fun openEmailDialog() { editEmailText = ""; currentPasswordForEmail = ""; clearInputPopupError(); showEmailDialog = true }
    fun closeEmailDialog() { showEmailDialog = false; clearInputPopupError() }
    fun updateEditEmailText(text: String) { editEmailText = text; clearInputPopupError() }
    fun updateCurrentPasswordForEmail(text: String) { currentPasswordForEmail = text; clearInputPopupError() }
    fun confirmEmailChange(context: android.content.Context) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (editEmailText.isBlank() || currentPasswordForEmail.isBlank()) {
            inputPopupError = context.getString(R.string.error_fill_all_fields)
            return
        }
        viewModelScope.launch {
            val passwordSuccess = repository.reauthenticate(currentPasswordForEmail)
            if (!passwordSuccess) {
                inputPopupError = context.getString(R.string.error_wrong_password)
                return@launch
            }
            val updateSuccess = repository.updateEmail(uid, editEmailText)
            if (updateSuccess) {
                email = editEmailText
                snackbarMessage = context.getString(R.string.msg_email_updated)
                closeEmailDialog()
            } else {
                inputPopupError = context.getString(R.string.error_update_failed)
            }
        }
    }

    // --- LOGICA PASSWORD ---
    fun openPasswordDialog() { currentPasswordText = ""; newPasswordText = ""; confirmNewPasswordText = ""; clearInputPopupError(); showPasswordDialog = true }
    fun closePasswordDialog() { showPasswordDialog = false; clearInputPopupError() }
    fun updateCurrentPasswordText(text: String) { currentPasswordText = text; clearInputPopupError() }
    fun updateNewPasswordText(text: String) { newPasswordText = text; clearInputPopupError() }
    fun updateConfirmNewPasswordText(text: String) { confirmNewPasswordText = text; clearInputPopupError() }
    fun confirmPasswordChange(context: android.content.Context) {
        if (currentPasswordText.isBlank() || newPasswordText.isBlank() || confirmNewPasswordText.isBlank()) {
            inputPopupError = context.getString(R.string.error_fill_all_fields)
            return
        }
        if (newPasswordText != confirmNewPasswordText) {
            inputPopupError = context.getString(R.string.error_passwords_not_match)
            return
        }
        if (newPasswordText.length < 6) {
            inputPopupError = context.getString(R.string.error_password_too_short)
            return
        }
        viewModelScope.launch {
            val reauthSuccess = repository.reauthenticate(currentPasswordText)
            if (!reauthSuccess) {
                inputPopupError = context.getString(R.string.error_current_password_wrong)
                return@launch
            }
            val updateSuccess = repository.updatePassword(newPasswordText)
            if (updateSuccess) {
                snackbarMessage = context.getString(R.string.msg_password_updated)
                closePasswordDialog()
            } else {
                inputPopupError = context.getString(R.string.error_update_failed)
            }
        }
    }

    // --- LOGICA SPECIALIZZAZIONE ---
    fun openSpecializationDialog() { editSpecializationText = specializzazione ?: ""; clearInputPopupError(); showSpecializationDialog = true }
    fun closeSpecializationDialog() { showSpecializationDialog = false; clearInputPopupError() }
    fun updateEditSpecializationText(text: String) { editSpecializationText = text; clearInputPopupError() }
    fun confirmSpecializationChange(context: android.content.Context) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (editSpecializationText.isBlank()) {
            inputPopupError = context.getString(R.string.error_fill_all_fields)
            return
        }
        viewModelScope.launch {
            val success = repository.updateSpecialization(uid, editSpecializationText)
            if (success) {
                specializzazione = editSpecializationText
                snackbarMessage = "Specializzazione aggiornata con successo."
                closeSpecializationDialog()
            } else {
                inputPopupError = context.getString(R.string.error_update_failed)
            }
        }
    }

    // --- LOGICA DESCRIZIONE ---
    fun openDescriptionDialog() { editDescriptionText = descrizione ?: ""; clearInputPopupError(); showDescriptionDialog = true }
    fun closeDescriptionDialog() { showDescriptionDialog = false; clearInputPopupError() }
    fun updateEditDescriptionText(text: String) { editDescriptionText = text; clearInputPopupError() }
    fun confirmDescriptionChange(context: android.content.Context) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (editDescriptionText.isBlank()) {
            inputPopupError = context.getString(R.string.error_fill_all_fields)
            return
        }
        viewModelScope.launch {
            val success = repository.updateDescription(uid, editDescriptionText)
            if (success) {
                descrizione = editDescriptionText
                snackbarMessage = "Descrizione aggiornata con successo."
                closeDescriptionDialog()
            } else {
                inputPopupError = context.getString(R.string.error_update_failed)
            }
        }
    }

    // --- LOGICA AVATAR ---
    fun updateAvatar(uri: Uri, contentResolver: ContentResolver) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isUploading = true
            val inputStream = try { contentResolver.openInputStream(uri) } catch (e: Exception) { null }
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