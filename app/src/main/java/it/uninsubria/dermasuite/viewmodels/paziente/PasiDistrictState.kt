package it.uninsubria.dermasuite.viewmodels.paziente

import com.google.firebase.firestore.PropertyName

//Utilizziamo questa classe per tenere memorizzati i valori dei parametri per ogni ditratto selezionato
//dal paziente

//Usiamo @get:Property per specificare il nome del campo nel DB, in modo tale che firebase non sbagli
data class PasiDistrictState(
    @get:PropertyName("Erythema") @set:PropertyName("Erythema") var erythema : Int = -1,
    @get:PropertyName("Hardening") @set:PropertyName("Hardening") var hardening: Int = -1,
    @get:PropertyName("Desquamation") @set:PropertyName("Desquamation") var desquamation: Int = -1,
    @get:PropertyName("PercentageArea") @set:PropertyName("PercentageArea") var percentageArea: Int = -1
)

//Non possiamo usare la classe enumerativa perchè firebase deve fare riferimento a un valore preciso singolo per il mapping dei dati tra firestore
//e una data class corretta
data class ParameterDistrictState(
    @get:PropertyName("ARMS") @set:PropertyName("ARMS") var arms: PasiDistrictState = PasiDistrictState(),
    @get:PropertyName("HEAD") @set:PropertyName("HEAD") var head: PasiDistrictState = PasiDistrictState(),
    @get:PropertyName("LEGS") @set:PropertyName("LEGS") var legs: PasiDistrictState = PasiDistrictState(),
    @get:PropertyName("TRUNK") @set:PropertyName("TRUNK") var trunk: PasiDistrictState = PasiDistrictState()
)
