package it.uninsubria.dermasuite.firebase

//Definiamo una classe che utilizziamo per rappresentare l'utente all'interno della nostra app
data class User(
    val uid: String,
    val email: String,
    val name: String,
    val role: String = "patient"
)