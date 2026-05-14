package it.uninsubria.dermasuite.firebase

import com.google.firebase.Timestamp

data class DermaUser(
    val uid: String = "",
    val nome: String = "",
    val cognome: String = "",
    val email: String = "",
    val username: String = "",
    val dataNascita: Timestamp? = null, //Utilizziamo timeStamp per gestire le date più comodamente su firestore
    val role: String = "",
    val language: String = "", // Impostiamo "" di default
    // Per il PAZIENTE: conterrà l'UID del medico scelto (null se non l'ha ancora scelto)
    val doctorId: String? = null,

    // Per il MEDICO: informazioni da mostrare nella lista al paziente (opzionali in fase di registrazione)
    val specialization: String? = null,
    val description: String? = null,
    val ultimaValutazione: Timestamp? = null
)