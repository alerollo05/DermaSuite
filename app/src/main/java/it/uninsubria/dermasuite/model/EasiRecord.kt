package it.uninsubria.dermasuite.model

import android.content.Context
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import it.uninsubria.dermasuite.R
import java.util.Date

// Questa classe modella lo stato di un singolo distretto per l'EASI.
data class EasiRecord(
    @DocumentId val id: String = "",
    @get:PropertyName("CalculationDate") @set:PropertyName("CalculationDate") var CalculationDate: Date = Date(),
    @get:PropertyName("ParameterDistrict") @set:PropertyName("ParameterDistrict") var ParameterDistrict: ParameterEasiDistrictState = ParameterEasiDistrictState(),
    @get:PropertyName("EasiTot") @set:PropertyName("EasiTot") var EasiTot: Float = 0f,
    @get:PropertyName("Severity") @set:PropertyName("Severity") var Severity: String = ""
)

// Mappatura della severità dal database.
fun EasiRecord.mapSeverity(context: Context): String {
    return when (this.Severity) {
        "LEVEL_SEVERE" -> context.getString(R.string.severity_severe)
        "LEVEL_MODERATE" -> context.getString(R.string.severity_moderate)
        else -> context.getString(R.string.severity_low)
    }
}