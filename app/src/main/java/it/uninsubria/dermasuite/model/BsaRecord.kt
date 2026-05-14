package it.uninsubria.dermasuite.model

import com.google.firebase.firestore.DocumentId

// Modello dati per il salvataggio dello storico del calcolo BSA su Firestore.
// Utilizza una data class Kotlin con nomi in camelCase (es. dataOra),
// il che permette a Firebase di mappare automaticamente i campi senza bisogno
// di usare le annotazioni @get:PropertyName.
data class BsaRecord(
    // @DocumentId è fondamentale: dice a Firebase di prendere l'ID alfanumerico
    // del documento appena scaricato (es. "7h8Jk2...") e inserirlo in questa variabile.
    // Senza questo, non sapremmo quale documento cancellare durante l'eliminazione.
    @DocumentId val id: String = "",

    // Salviamo la data come stringa già formattata (es. "08/05/2026 12:02")
    // per semplificare la lettura nel database e nella UI.
    val dataOra: String = "",

    val peso: Double = 0.0,
    val altezza: Double = 0.0,
    val sesso: String = "",
    val bsa: Double = 0.0,
    val valutazione: String = ""
)