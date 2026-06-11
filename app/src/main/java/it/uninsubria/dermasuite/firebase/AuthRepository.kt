package it.uninsubria.dermasuite.firebase

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import it.uninsubria.dermasuite.model.BmiRecord
import it.uninsubria.dermasuite.model.BsaRecord
import it.uninsubria.dermasuite.model.EasiRecord
import it.uninsubria.dermasuite.model.PasiRecord
import it.uninsubria.dermasuite.viewmodels.RegisterUiState
import kotlinx.coroutines.tasks.await


 //Classe AuthRepository per gestire le operazioni di autenticazione e database (Firestore).
 //Centralizza la logica di interazione con Firebase.

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    //Per caricare file pesanti come le immagini di avatar, serve utilizzare firebase storage e non firestore che serve solo per i file di testo
    private val storage = FirebaseStorage.getInstance()

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun getSessoPaziente(uid: String): String {
        return try {
            val dermaUser = getUserData(uid)
            // Se l'utente esiste e ha il sesso lo restituisce, altrimenti di default "Maschio"
            dermaUser?.sesso ?: "Maschio"
        } catch (e: Exception) {
            e.printStackTrace()
            "Maschio" // Fallback in caso di assenza di rete
        }
    }

    suspend fun registerUser(state: RegisterUiState): Result<Unit> {
        return try {
            //Crea l'account su Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(state.email, state.password).await()
            val uid = authResult.user?.uid ?: throw Exception("Errore durante il recupero dell'UID utente")

            //Convertiamo la data di nascita in formato TimeStamp
            val timestampNascita = state.dataNascitaMillis?.let {
                com.google.firebase.Timestamp(java.util.Date(it))}
            //Andiamo a prendere la lingua corrente con cui si sta registrando l'utente, in modo da poi avere la scelta dei medici della stessa lingua tra medico e paziente
            val deviceLanguage = java.util.Locale.getDefault().language

           //I nomi delle proprietà diventeranno direttamente le chiavi su fireStore
            val newUser = DermaUser(
                uid = uid,
                nome = state.nome,
                cognome = state.cognome,
                email = state.email,
                username = state.username,
                dataNascita = timestampNascita,
                sesso = state.sesso,
                role = state.accountType,
                deviceLanguage //salvo anche la lingua dell'utente in automatico senza che l'utente lo sappia
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
    suspend fun loginUser(email: String, pass: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun deleteAccount(currentPassword: String): Boolean {
        return try {
            val user = auth.currentUser ?: return false
            val uid = user.uid
            val userDocRef = db.collection("users").document(uid)

            // Ri-autenticazione (Obbligatoria prima di azioni distruttive)
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).await()

            // Eliminazione di tutte le sotto-collezioni associate all'utente
            val sottoCollezioni = listOf("BMI", "PASI", "EASI", "BSA")
            for (collezione in sottoCollezioni) {
                val snapshot = userDocRef.collection(collezione).get().await()
                for (documento in snapshot.documents) {
                    documento.reference.delete().await()
                }
            }

            // Eliminazione dell'avatar da Firebase Storage (se presente)
            try {
                storage.reference.child("avatars/$uid.jpg").delete().await()
            } catch (e: Exception) {
                // Se l'avatar non esiste o è già stato cancellato, ignoriamo l'errore
                // per evitare di bloccare l'intero processo di eliminazione account
                e.printStackTrace()
            }

            // Eliminazione del documento principale dell'utente da Firestore
            userDocRef.delete().await()

            // Eliminazione definitiva dell'account da Firebase Authentication
            user.delete().await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
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
    suspend fun updateUsername(uid: String, newUsername: String): Boolean {
        return try {
            // Aggiorna solo il campo "username" del documento corrispondente all'UID passatogli
            db.collection("users").document(uid).update("username", newUsername).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Funzione per la ri-autenticazione di sicurezza fatta nel pop-up per il cambio della password/email,
    // restituisce un Boolean (true se il login è confermato, false altrimenti).
    suspend fun reauthenticate(currentPassword: String): Boolean {
        return try {
            // Recuperiamo l'istanza dell'utente attualmente loggato.
            val user = auth.currentUser ?: return false
            // Creiamo un oggetto "Credential".
            // Firebase non accetta solo la password, ma vuole un pacchetto completo
            // che contenga sia l'email dell'utente loggato che la password appena inserita.
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            // Chiamiamo il metodo reauthenticate fornito dall'SDK di Firebase.
            // '.await()' sospende l'esecuzione finché Firebase non risponde (successo o errore).
            user.reauthenticate(credential).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Aggiorna l'email ISTANTANEAMENTE (richiede che la ri-autenticazione sia appena avvenuta)
    suspend fun updateEmail(uid: String, newEmail: String): Boolean {
        return try {
            val user = auth.currentUser ?: return false
            // AGGIORNAMENTO LATO AUTENTICAZIONE ISTANTANEO:
            user.updateEmail(newEmail).await()

            // AGGIORNAMENTO LATO DATABASE (FIRESTORE):
            db.collection("users").document(uid).update("email", newEmail).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    //Aggiorna la password (richiede che la ri-autenticazione sia appena avvenuta)
    suspend fun updatePassword(newPassword: String): Boolean {
        return try {
            val user = auth.currentUser ?: return false
            // Chiamiamo il metodo 'updatePassword' fornito dall'SDK di Firebase.
            // Questa riga comunica ai server di Google di invalidare la vecchia password
            // e impostare quella nuova per i futuri accessi.
            user.updatePassword(newPassword).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateSpecialization(uid: String, newSpecialization: String): Boolean {
        return try {
            db.collection("users").document(uid).update("specialization", newSpecialization).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateDescription(uid: String, newDescription: String): Boolean {
        return try {
            db.collection("users").document(uid).update("description", newDescription).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getAvailableDoctors(): List<DermaUser> {
        return try{
            //andiamo a prendere la lista di medici disponibili nel db
            val snapshot = db.collection("users")
                    .whereEqualTo("role", "Medico")
                    .get().await()

            snapshot.toObjects(DermaUser::class.java)
        }catch(e: Exception){
            e.printStackTrace()
            emptyList() // In caso di errore, restituisce una lista vuota
        }
    }

    suspend fun linkDoctorToPatient(patientUid: String, doctorUid: String) : Boolean{
        return try{
            //Andiamo ad aggiornare il campo medico del paziente interessato
            db.collection("users").document(patientUid)
                .update("doctorId", doctorUid).await()
            true
        }catch(e: Exception){
            e.printStackTrace()
            false
        }
    }

//Funzione per andare a prendere i pazienti associati al medico loggato in quel momento
    suspend fun getMyPatients(doctorUid: String) : List<DermaUser>{
        return try{
            val snapshot = db.collection("users")
                .whereEqualTo("role", "Paziente")
                .whereEqualTo("doctorId", doctorUid).get().await()
            //Andiamo a traformare il risultato della query in una lista di DermaUser
            snapshot.toObjects(DermaUser::class.java)
        }catch(e: Exception){
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun uploadAvatar(uid: String, inputStream: java.io.InputStream): String? {
        return try {
            // Crea un riferimento al file: "avatars/UID.jpg"
            val ref = storage.reference.child("avatars/$uid.jpg")

            // Carica lo stream di dati
            ref.putStream(inputStream).await()

            // Recupera l'URL pubblico
            val downloadUrl = ref.downloadUrl.await().toString()

            // Salva l'URL nel documento dell'utente su Firestore
            db.collection("users").document(uid).update("avatarUrl", downloadUrl).await()

            downloadUrl
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try { inputStream.close() } catch (_: Exception) {}
        }
    }

    //Getter dei dati specifici per ogni calcolo
    suspend fun getPasiRecords(uid: String): List<PasiRecord> {
        return try {
            val snapshot = db.collection("users")
                .document(uid)
                .collection("PASI") // Assicurati che il nome sia identico a quello usato nel salvataggio
                .orderBy("CalculationDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.toObjects(PasiRecord::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getEasiRecords(uid: String): List<EasiRecord> {
        return try {
            val snapshot = db.collection("users")
                .document(uid)
                .collection("EASI") // Nome della collezione usato nel salvataggio
                .orderBy("CalculationDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.toObjects(EasiRecord::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getBsaRecords(uid: String): List<BsaRecord> {
        return try {
            val snapshot = db.collection("users")
                .document(uid)
                .collection("BSA")
                .get()
                .await()

            snapshot.toObjects(BsaRecord::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getBmiRecords(uid: String): List<BmiRecord> {
        return try {
            val snapshot = db.collection("users")
                .document(uid)
                .collection("BMI") // Uso il nome che hai nel tuo codice
                .orderBy("CalculationDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.toObjects(BmiRecord::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    //CANCELLAZIONE CALCOLI

    suspend fun deleteEasiRecord(uid: String, recordId: String): Boolean {
        return try {
            db.collection("users").document(uid).collection("EASI").document(recordId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deletePasiRecord(uid: String, recordId: String): Boolean {
        return try {
            db.collection("users").document(uid).collection("PASI").document(recordId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteBsaRecord(uid: String, recordId: String): Boolean {
        return try {
            db.collection("users").document(uid).collection("BSA").document(recordId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    suspend fun deleteBmiRecord(uid: String, recordId: String): Boolean {
        return try {
            db.collection("users")
                .document(uid)
                .collection("BMI")
                .document(recordId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun aggiornaUltimaValutazione(uid: String) {
        try {
            db.collection("users").document(uid).update(
                "ultimaValutazione", com.google.firebase.firestore.FieldValue.serverTimestamp()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace() // Logghiamo ma non blocchiamo l'app se fallisce questo piccolo update
        }
    }

    //SALVATAGGIO DEI DATI
    suspend fun salvaEasiRecord(uid: String, payload: HashMap<String, Any>): Boolean {
        return try {
            db.collection("users").document(uid).collection("EASI").add(payload).await()
            aggiornaUltimaValutazione(uid)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun salvaBmiRecord(uid: String, payload: HashMap<String, Any>): Boolean {
        return try {
            db.collection("users").document(uid).collection("BMI").add(payload).await()
            aggiornaUltimaValutazione(uid)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun salvaPasiRecord(uid: String, payload: HashMap<String, Any>): Boolean {
        return try {
            db.collection("users").document(uid).collection("PASI").add(payload).await()
            aggiornaUltimaValutazione(uid)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun salvaBsaRecord(uid: String, record: it.uninsubria.dermasuite.model.BsaRecord): Boolean {
        return try {
            db.collection("users").document(uid).collection("BSA").add(record).await()
            aggiornaUltimaValutazione(uid)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
