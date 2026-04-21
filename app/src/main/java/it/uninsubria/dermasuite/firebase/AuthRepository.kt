package it.uninsubria.dermasuite.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uninsubria.dermasuite.viewmodels.DermaUser
import it.uninsubria.dermasuite.viewmodels.RegisterUiState
import kotlinx.coroutines.tasks.await


 //Classe AuthRepository per gestire le operazioni di autenticazione e database (Firestore).
 //Centralizza la logica di interazione con Firebase.

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


     //Registra un nuovo utente in Firebase Auth e salva i suoi dati aggiuntivi su Firestore.

    suspend fun registerUser(state: RegisterUiState): Result<Unit> {
        return try {
            //Crea l'account su Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(state.email, state.password).await()
            val uid = authResult.user?.uid ?: throw Exception("Errore durante il recupero dell'UID utente")

           //I nomi delle proprietà diventeranno direttamente le chiavi su fireStore
            val newUser = DermaUser(
                uid,
                state.nome,
                state.cognome,
                state.email,
                state.username,
                state.dataNascita,
                state.accountType
            )

            //Salva i dati nella collezione "users" usando l'UID come ID documento
            db.collection("users").document(uid).set(newUser).await()

            // Forzo il logout dopo la registrazione e quindi chiudo la sessione, perchè se no dopo essersi registrati aprendo
            // l app mi passa direttamente alla dashboard, perchè l'SDK di Firebase ha salvato internamente un "token"
            // di autenticazione nella memoria protetta del dispositivo
            auth.signOut()

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

    //Cambio password
    suspend fun updateUserPassword(newPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                // Firebase si occupa di tutto: convalida, hashing e aggiornamento sicuro
                user.updatePassword(newPassword).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Utente non loggato"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Metodo per il reset via Email per password dimenticata
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
