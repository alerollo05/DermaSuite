package it.uninsubria.dermasuite.viewmodels.paziente

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PasiPageViewModel(): ViewModel() {
    //Creiamo una variabile per tenere traccia del distretto attualmente selezionato
    var currentDistrict by mutableStateOf(DistrettoCorpo.TESTA)

    //Creiamo una struttura per andare a memorizzare i parametri per ogni distretto selezionato
    var districtValues by mutableStateOf(
        DistrettoCorpo.values().associateWith {
            DistrictState()
        }
    )

}