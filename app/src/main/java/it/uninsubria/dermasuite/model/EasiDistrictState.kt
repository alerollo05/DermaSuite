package it.uninsubria.dermasuite.model

import com.google.firebase.firestore.PropertyName

data class EasiDistrictState(

    @get:PropertyName("Erythema") @set:PropertyName("Erythema") var eritema : Int = -1,
    @get:PropertyName("EdemaPapulation") @set:PropertyName("EdemaPapulation") var edemaPapulizzazione: Int = -1,
    @get:PropertyName("Excoriation") @set:PropertyName("Excoriation") var escoriazione: Int = -1,
    @get:PropertyName("Lichenification") @set:PropertyName("Lichenification") var lichenificazione: Int = -1,
    @get:PropertyName("PercentageArea") @set:PropertyName("PercentageArea") var percentualeArea: Int = -1
)

// Questa classe raggruppa i quattro distretti per un singolo calcolo EASI.
data class ParameterEasiDistrictState(
    @get:PropertyName("ARMS") @set:PropertyName("ARMS") var arms: EasiDistrictState = EasiDistrictState(),
    @get:PropertyName("HEAD") @set:PropertyName("HEAD") var head: EasiDistrictState = EasiDistrictState(),
    @get:PropertyName("LEGS") @set:PropertyName("LEGS") var legs: EasiDistrictState = EasiDistrictState(),
    @get:PropertyName("TRUNK") @set:PropertyName("TRUNK") var trunk: EasiDistrictState = EasiDistrictState()
)