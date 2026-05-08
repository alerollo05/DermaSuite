package it.uninsubria.dermasuite.model

import android.content.Context
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import it.uninsubria.dermasuite.R
import java.util.Date

// Usiamo @get:Property e @set:Property per specificare il nome del campo nel DB
// in modo tale che Firebase non sbagli la mappatura (come hai fatto per il PASI).

// Questa classe modella lo stato di un singolo distretto per l'EASI.
// Deve corrispondere a EasiDistrictState, ma con le annotazioni per Firestore.

// La classe principale che rappresenta un calcolo EASI completo nel database.
data class EasiRecord(
    @DocumentId val id: String = "",
    @get:PropertyName("CalculationDate") @set:PropertyName("CalculationDate") var CalculationDate: Date = Date(),
    @get:PropertyName("ParameterDistrict") @set:PropertyName("ParameterDistrict") var ParameterDistrict: ParameterEasiDistrictState = ParameterEasiDistrictState(),
    @get:PropertyName("EasiTot") @set:PropertyName("EasiTot") var EasiTot: Float = 0f, // L'EASI spesso ha valori decimali
    @get:PropertyName("Severity") @set:PropertyName("Severity") var Severity: String = ""
)

// Mappatura della severità dal database.
// Se la scala di severità dell'EASI è diversa dal PASI, dovrai aggiornare la logica qui.
// Per ora, uso la stessa logica del PASI.
fun EasiRecord.mapSeverity(context: Context): String {
    return when (this.Severity) {
        "LEVEL_SEVERE" -> context.getString(R.string.severity_severe)
        "LEVEL_MODERATE" -> context.getString(R.string.severity_moderate)
        else -> context.getString(R.string.severity_low)
    }
}