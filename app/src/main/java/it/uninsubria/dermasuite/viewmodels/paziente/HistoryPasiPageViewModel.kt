package it.uninsubria.dermasuite.viewmodels.paziente

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.Date

class HistoryPasiPageViewModel: ViewModel() {

    //creazione della variabile per accedere a firebase
    private val db = Firebase.firestore

    //Creazione della lista dei calcoli fatti da un utente scaricati da firebase
    private var ListaCalcoli = listOf<PasiRecord>()

    //Per l'aggiornamento dei valori nella UI andiamo a creare un'altra lista che varia in base al
    //selettore che imposta l'utente, dalla ListaCalcoli che contiene tutti i calcoli
    //seleziona solo quelli utili da stampare in quel momento nella UI
    //andremo a metterci dentro i record ordinati e puliti in base ai filtri
    private val _uiState = MutableStateFlow<List<PasiRecord>>(emptyList())
    val uiState: StateFlow<List<PasiRecord>> = _uiState.asStateFlow()

    //Creiamo una variabile per andare a vedere che filtro è ativo nella UI (6 mesi, 1 anno, tutto)
    private val _currentFilter = MutableStateFlow(TimeFilter.SIX_MONTHS)
    val currentFilter: StateFlow<TimeFilter> = _currentFilter.asStateFlow()

    // Stato per sapere se dobbiamo far vedere nella UI la rotella di caricamento
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getHistoryDB( UserId : String){
        _isLoading.value = true //Inizia il caricamento
        db.collection("users")
            .document(UserId)
            .collection("PASI")
            .get()
            .addOnSuccessListener { result ->
                //Mappiamo il documento FireStore nella data class creata prima
                ListaCalcoli = result.toObjects(PasiRecord::class.java)
                    .sortedByDescending{ it.CalculationDate} //Ordiniamo dal calcolo più recente in poi

                applyFilter(_currentFilter.value)

                _isLoading.value = false //Fine del caricamento
            }
            .addOnFailureListener {
                _isLoading.value = false //In caso di errore fine caricamento comunque per sicurezza
            }
    }

    fun applyFilter(filter: TimeFilter){
        _currentFilter.value = filter
        val limitDate = when (filter) {
            TimeFilter.SIX_MONTHS -> Calendar.getInstance().apply { add(Calendar.MONTH, -6) }.time
            TimeFilter.ONE_YEAR -> Calendar.getInstance().apply { add(Calendar.YEAR, -1) }.time
            TimeFilter.TWO_YEARS -> Calendar.getInstance().apply { add(Calendar.YEAR, -2) }.time
            TimeFilter.ALL_TIME -> Date(0) // Prende tutto
        }
        //Per il grafico ordiniamo in ordine crescente
        _uiState.value = ListaCalcoli.filter { it.CalculationDate >= limitDate}.sortedBy{it.CalculationDate}
    }
}
