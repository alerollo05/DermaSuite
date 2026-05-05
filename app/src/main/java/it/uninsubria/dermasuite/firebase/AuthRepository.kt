package it.uninsubria.dermasuite.firebase

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uninsubria.dermasuite.firebase.DermaUser
import it.uninsubria.dermasuite.viewmodels.RegisterUiState
import kotlinx.coroutines.tasks.await


 //Classe AuthRepository per gestire le operazioni di autenticazione e database (Firestore).
 //Centralizza la logica di interazione con Firebase.

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    //Registra un nuovo utente in Firebase Auth e salva i suoi dati aggiuntivi su Firestore.
    // Le funzioni suspend, vengono eseguite in modalità asincrona (al main thread), perchè sono operazioni
     // che se svolte nel mainthread farebbero crashare l'app
    suspend fun registerUser(state: RegisterUiState): Result<Unit> {
        return try {
            //Crea l'account su Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(state.email, state.password).await()
            val uid = authResult.user?.uid ?: throw Exception("Errore durante il recupero dell'UID utente")

            //Convertiamo la data di nascita in formato TimeStamp
            val timestampNascita = state.dataNascitaMillis?.let {
                com.google.firebase.Timestamp(java.util.Date(it))}

           //I nomi delle proprietà diventeranno direttamente le chiavi su fireStore
            val newUser = DermaUser(
                uid,
                state.nome,
                state.cognome,
                state.email,
                state.username,
                timestampNascita,
                state.accountType
            )

            //Salva i dati nella collezione "users" usando l'UID come ID documento
            db.collection("users").document(uid).set(newUser).await()

            // Forzo il logout dopo la registrazione e quindi chiudo la sessione, perchè se no dopo essersi registrati aprendo
            // l app mi passa direttamente alla dashboard, perchè l'SDK di Firebase ha salvato internamente un "token"
            // di autenticazione nella memoria protetta del dispositivo
            signOut()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Effettua il login dell'utente con email e password.
    suspend fun loginUser(email: String, pass: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Effettua il logout dell'utente.
    fun signOut() {
        auth.signOut()
    }

    //Funzione per andare a deserializzare i dati presi dal DB in un oggetto DermaUser
    suspend fun getUserData(uid: String): DermaUser? {
        return try {
            val snapshot = db.collection("users").document(uid).get().await()

            // Converte automaticamente il documento in un oggetto DermaUser
            snapshot.toObject(DermaUser::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Metodo per aggiornare il campo username
    suspend fun updateUsername(uid: String, newUsername: String): Boolean {
        return try {
            // Aggiorna solo il campo "username" del documento corrispondente all'UID passatogli
            db.collection("users").document(uid).update("username", newUsername).await()
            true // Restituisce true se l'operazione ha successo
        } catch (e: Exception) {
            e.printStackTrace()
            false // Restituisce false se c'è un errore (es. no internet)
        }
    }

    // Funzione per la ri-autenticazione di sicurezza fatta nel pop-up per il cambio della password/email,
    // restituisce un Boolean (true se il login è confermato, false altrimenti).
    suspend fun reauthenticate(currentPassword: String): Boolean {
        // Apriamo un blocco try-catch per gestire eventuali errori (es. password errata o assenza di rete).
        return try {
            // Recuperiamo l'istanza dell'utente attualmente loggato.
            // Se non c'è nessun utente in sessione (null), la funzione si ferma e restituisce false.
            val user = auth.currentUser ?: return false
            // Creiamo un oggetto "Credential".
            // Firebase non accetta solo la password, ma vuole un pacchetto completo
            // che contenga sia l'email dell'utente loggato che la password appena inserita.
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            // Chiamiamo il metodo reauthenticate fornito dall'SDK di Firebase.
            // '.await()' sospende l'esecuzione finché Firebase non risponde (successo o errore).
            user.reauthenticate(credential).await()
            // Se siamo arrivati qui senza errori, la ri-autenticazione è riuscita.
            true
        } catch (e: Exception) {
            // Se qualcosa va storto (es. la password è sbagliata), stampiamo l'errore in console
            // e restituiamo false per avvisare il ViewModel del fallimento.
            e.printStackTrace()
            false
        }
    }

    // Aggiorna l'email (richiede che la ri-autenticazione sia appena avvenuta)
    suspend fun updateEmail(uid: String, newEmail: String): Boolean {
        return try {
            val user = auth.currentUser ?: return false
            // AGGIORNAMENTO LATO AUTENTICAZIONE:
            // 'verifyBeforeUpdateEmail' invia un link di verifica alla nuova email.
            // L'email cambierà effettivamente solo dopo che l'utente avrà cliccato sul link.
            // '.await()' attende che la richiesta di invio della mail con il link venga completata.
            user.verifyBeforeUpdateEmail(newEmail).await()

            // AGGIORNAMENTO LATO DATABASE (FIRESTORE):
            // Accediamo alla collezione "users", cerchiamo il documento con l'UID dell'utente
            // e sovrascriviamo il campo "email" con il nuovo indirizzo.
            db.collection("users").document(uid).update("email", newEmail).await()
            true
        } catch (e: Exception) {
            // In caso di errore (es. email già in uso o sessione scaduta), restituiamo false.
            e.printStackTrace()
            false
        }
    }

    // 3. Aggiorna la password (richiede che la ri-autenticazione sia appena avvenuta)
    suspend fun updatePassword(newPassword: String): Boolean {
        return try {
            val user = auth.currentUser ?: return false
            // Chiamiamo il metodo 'updatePassword' fornito dall'SDK di Firebase.
            // Questa riga comunica ai server di Google di invalidare la vecchia password
            // e impostare quella nuova per i futuri accessi.
            // '.await()' aspetta la conferma della modifica dai server.
            user.updatePassword(newPassword).await()
            true
        } catch (e: Exception) {
            // Gestione errori (es. password troppo debole o necessità di ri-autenticazione).
            e.printStackTrace()
            false
        }
    }
}
