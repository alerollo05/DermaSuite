package it.uninsubria.dermasuite.viewmodels.paziente

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PasiPageViewModel(): ViewModel() {
    //Creiamo una variabile per tenere traccia del distretto attualmente selezionato
    var currentDistrict by mutableStateOf(DistrettoCorpo.HEAD)
    //by ci permette  di non dover sscrivere ogni volta setValue e getValue direttamente

    //Creiamo una struttura per andare a memorizzare i parametri per ogni distretto selezionato
    var districtValues by mutableStateOf(
        //mutableStateOf va a controllare costantemente i valori aggiornati della mappa in modo tale da farli
        //cambiare istantaneamente anche nell'interfaccia grafica se i valori cambiano nella mappa
        DistrettoCorpo.values().associateWith { //associteWith trasforma l'elenco dei distretti in una
            //mappa l chiaavi sono i distretti, mentre i valori sono nuove istanze di districtState cioè quindi
            //sono i valori dei parametri che dobbiamo andare a definire da 1 a 4
            DistrictState()
        }
    )

    //Creiamo una funzione per andare ad aggiornare i dati del distretto corrente in base a cosa fa l'utente
    private fun updateCurrentDistrictData(update: (DistrictState) -> DistrictState){
        val currentStateMap = districtValues.toMutableMap()
        val currentData = currentStateMap[currentDistrict] ?: DistrictState()
        currentStateMap[currentDistrict] = currentData
        districtValues = currentStateMap
    }

    //Creiamo la funzione che calcola effettivamente il PASI una volta che abbiamo i dati aggiornati
    fun calculateTotalPasi(): Double {
        var total = 0.0
        districtValues.forEach { district, data ->
            val s = data.eritema + data.indurimento + data.desquamazione
            val a = mapPercentageToAreaScore(data.percentualeArea)
            total += (s * a * district.ordinal)
        }
        return Math.round(total * 10.0) /10.0
    }

}