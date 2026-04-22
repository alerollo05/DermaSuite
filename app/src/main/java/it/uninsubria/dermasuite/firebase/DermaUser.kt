package it.uninsubria.dermasuite.firebase

import com.google.firebase.Timestamp

data class DermaUser(
    val uid: String = "",
    val nome: String = "",
    val cognome: String = "",
    val email: String = "",
    val username: String = "",
    val dataNascita: Timestamp? = null, //Utilizziamo timeStamp per gestire le date più comodamente su firestore
    val role: String = ""
)