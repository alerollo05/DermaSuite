package it.uninsubria.dermasuite.viewmodels


data class DermaUser(
    val uid: String = "",
    val nome: String = "",
    val cognome: String = "",
    val email: String = "",
    val username: String = "",
    val dataNascita: String = "",
    val role: String = ""
)
