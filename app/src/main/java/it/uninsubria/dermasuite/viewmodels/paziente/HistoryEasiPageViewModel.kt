package it.uninsubria.dermasuite.viewmodels.paziente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import it.uninsubria.dermasuite.firebase.AuthRepository
import it.uninsubria.dermasuite.firebase.DermaUser
import it.uninsubria.dermasuite.model.EasiRecord // IMPORTANTE: Usa il tuo nuovo modello EASI
import it.uninsubria.dermasuite.model.TimeFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

class HistoryEasiPageViewModel(var repository: AuthRepository = AuthRepository()): ViewModel() {

    // creazione della variabile per accedere a firebase

    // Creazione della lista dei calcoli fatti da un utente scaricati da firebase
    private var listaCalcoli = listOf<EasiRecord>()

    // Per l'aggiornamento dei valori nella UI andiamo a creare un'altra lista che varia in base al
    // selettore che imposta l'utente
    private val _uiState = MutableStateFlow<List<EasiRecord>>(emptyList())
    val uiState: StateFlow<List<EasiRecord>> = _uiState.asStateFlow()

    private val _currentFilter = MutableStateFlow(TimeFilter.SIX_MONTHS)
    val currentFilter: StateFlow<TimeFilter> = _currentFilter.asStateFlow()

    // Stato per sapere se dobbiamo far vedere nella UI la rotella di caricamento
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Stato per i dati dell'utente
    private val _userData = MutableStateFlow<DermaUser?>(null)
    val userData: StateFlow<DermaUser?> = _userData.asStateFlow()

    fun getHistoryDB(UserId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                //Chiamata al repository
                listaCalcoli = repository.getEasiRecords(UserId)

                applyFilter(_currentFilter.value)

                _isLoading.value = false
            } catch(e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun applyFilter(filter: TimeFilter) {
        _currentFilter.value = filter
        val limitDate = when (filter) {
            TimeFilter.SIX_MONTHS -> Calendar.getInstance().apply { add(Calendar.MONTH, -6) }.time
            TimeFilter.ONE_YEAR -> Calendar.getInstance().apply { add(Calendar.YEAR, -1) }.time
            TimeFilter.TWO_YEARS -> Calendar.getInstance().apply { add(Calendar.YEAR, -2) }.time
            TimeFilter.ALL_TIME -> Date(0) // Prende tutto
        }
        // Per il grafico ordiniamo in ordine crescente
        _uiState.value = listaCalcoli.filter { it.CalculationDate >= limitDate }.sortedBy { it.CalculationDate }
    }

    fun deleteRecord(record: EasiRecord, UserId: String) {
        // Evitiamo di procedere se l'ID è vuoto o l'utente non è valido
        if (record.id.isEmpty() || UserId == "null") return

        viewModelScope.launch {
            try {
                // Rimuoviamo il record dalla lista di calcoli su Firebase (collection EASI)
                val isDeleted = repository.deleteEasiRecord(UserId, record.id)

                if (isDeleted) {
                    listaCalcoli = listaCalcoli.filter { it.id != record.id }
                    applyFilter(_currentFilter.value)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}