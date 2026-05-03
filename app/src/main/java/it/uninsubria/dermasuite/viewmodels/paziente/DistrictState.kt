package it.uninsubria.dermasuite.viewmodels.paziente

import com.google.firebase.firestore.PropertyName

//Utilizziamo questa classe per tenere memorizzati i valori dei parametri per ogni ditratto selezionato
//dal paziente

//Usiamo @get:Property per specificare il nome del campo nel DB, in modo tale che firebase non sbagli
data class DistrictState(
    @get:PropertyName("Erythema") val erythema : Int = -1,
    @get:PropertyName("Hardening") val hardening: Int = -1,
    @get:PropertyName("Desquamation") val desquamation: Int = -1,
    @get:PropertyName("PercentageArea") val percentageArea: Int = -1
)

//Non possiamo usare la classe enumerativa perchè firebase deve fare riferimento a un valore preciso singolo per il mapping dei dati tra firestore
//e una data class corretta
data class ParameterDistrictState(
    @get:PropertyName("ARMS") val arms: DistrictState = DistrictState(),
    @get:PropertyName("HEAD") val head: DistrictState = DistrictState(),
    @get:PropertyName("LEGS") val legs: DistrictState = DistrictState(),
    @get:PropertyName("TRUNK") val trunk: DistrictState = DistrictState()
)
