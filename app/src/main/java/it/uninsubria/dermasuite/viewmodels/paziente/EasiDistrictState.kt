package it.uninsubria.dermasuite.viewmodels.paziente

data class EasiDistrictState (
    val eritema: Int = -1,           // Rossore
    val edemaPapulizzazione: Int = -1, // Gonfiore/Papule
    val escoriazione: Int = -1,      // Segni di grattamento
    val lichenificazione: Int = -1,   // Ispessimento cutaneo
    val percentualeArea: Int = -1    // Valore da 0 a 6
)