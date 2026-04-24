package it.uninsubria.dermasuite.viewmodels.paziente

//Utilizziamo questa classe per tenere memorizzati i valori dei parametri per ogni ditratto selezionato
//dal paziente
data class DistrictState(
    val eritema : Float = 0f,
    val indurimento: Float = 0f,
    val desquamazione: Float = 0f,
    val percentualeArea: Int = 0
)