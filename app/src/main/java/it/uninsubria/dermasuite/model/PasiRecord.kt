package it.uninsubria.dermasuite.model

import android.content.Context
import androidx.annotation.StringRes
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import it.uninsubria.dermasuite.R
import java.util.Date

//Vado a creare una classe per mappare in modo identico i dati salvati sul Database Firestore

data class PasiRecord(
    @DocumentId val id: String = "",
    @get:PropertyName("CalculationDate") @set:PropertyName("CalculationDate") var CalculationDate: Date = Date(),
    @get:PropertyName("ParameterDistrict") @set:PropertyName("ParameterDistrict") var ParameterDistrict: ParameterDistrictState = ParameterDistrictState(),
    @get:PropertyName("PasiTot") @set:PropertyName("PasiTot") var PasiTot: Int = 0,
    @get:PropertyName("Severity") @set:PropertyName("Severity") var Severity: String = ""
)


//Creiamo una classe enumerativa per i filtri di tempo nel grafico
//Creiamo una displayName per poter anadare a renderlo modulare per la lingua
//Creiamo un technicalName per poterlo utilizzare per andare a fare la logica nel viewModel con il Database
enum class TimeFilter(@StringRes val displayName : Int, technicalName: String) {
    SIX_MONTHS(R.string.time_filter_six_months,"six_months"),
    ONE_YEAR(R.string.time_filter_one_year,"one_year"),
    TWO_YEARS(R.string.time_filter_two_years,"two_years"),
    ALL_TIME(R.string.time_filter_all_time,"all_time")
}

//Mappatura della serverità dal database
fun PasiRecord.mapSeverity(context: Context): String {
    return when (this.Severity) {
        "LEVEL_SEVERE" -> context.getString(R.string.severity_severe)
        "LEVEL_MODERATE" -> context.getString(R.string.severity_moderate)
        else -> context.getString(R.string.severity_low)
    }
}
