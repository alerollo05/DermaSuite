package it.uninsubria.dermasuite.viewmodels.paziente

import androidx.annotation.StringRes
import com.google.firebase.firestore.PropertyName
import it.uninsubria.dermasuite.R

//Vado a creare una classe per mappare in modo identico i dati salvati sul Database Firestore

data class PasiRecord(
    @get:PropertyName("CalculationDate") val CalculationDate: java.util.Date = java.util.Date(),
    @get:PropertyName("ParameterDistrict") val ParameterDistrict: ParameterDistrictState = ParameterDistrictState(),
    @get:PropertyName("PasiTot") val PasiTot: Int = 0,
    @get:PropertyName("Severity") val Severity: String = ""
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

