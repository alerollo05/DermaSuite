package it.uninsubria.dermasuite.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

            //Prepara l'oggetto con i dati dell'utente per Firestore
            val userMap = hashMapOf(
                "uid" to uid,
                "nome" to state.nome,
                "cognome" to state.cognome,
                "email" to state.email,
                "username" to state.username,
                "dataNascita" to state.dataNascita,
                "role" to state.accountType
            )

            //Salva i dati nella collezione "users" usando l'UID come ID documento
            db.collection("users").document(uid).set(userMap).await()

            // Forzo il logout dopo la registrazione e quindi chiudo la sessione, perchè se no dopo essersi registrati aprendo
            // l app mi passa direttamente alla dashboard, perchè l'SDK di Firebase ha salvato internamente un "token"
            // di autenticazione nella memoria protetta del dispositivo
            auth.signOut()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, pass: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Effettua il login dell'utente.

    suspend fun signIn(email: String, pass: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


     // Effettua il logout dell'utente.

    fun signOut() {
        auth.signOut()
    }
}
