package it.uninsubria.dermasuite.viewmodels.paziente

//Utilizziamo questa classe per tenere memorizzati i valori dei parametri per ogni ditratto selezionato
//dal paziente
data class DistrictState(
    val eritema : Int = -1,
    val indurimento: Int = -1,
    val desquamazione: Int = -1,
    val percentualeArea: Int = -1
)
