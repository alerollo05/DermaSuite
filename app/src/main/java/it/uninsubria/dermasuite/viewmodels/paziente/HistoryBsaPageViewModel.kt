package it.uninsubria.dermasuite.viewmodels.paziente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import it.uninsubria.dermasuite.firebase.AuthRepository
import it.uninsubria.dermasuite.firebase.DermaUser
import it.uninsubria.dermasuite.model.BsaRecord
import it.uninsubria.dermasuite.model.TimeFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ViewModel responsabile di scaricare, filtrare e cancellare i dati dello storico BSA.
class HistoryBsaPageViewModel(
    private val repository: AuthRepository = AuthRepository()
): ViewModel() {


    // Questa variabile mantiene tutti i calcoli scaricati da Firebase (non filtrati).
    // È utile per non dover rifare chiamate al server ogni volta che si cambia il filtro.
    private var listaCalcoli = listOf<BsaRecord>()

    // uiState è la lista che viene effettivamente mostrata all'utente (dopo il filtro)
    private val _uiState = MutableStateFlow<List<BsaRecord>>(emptyList())
    val uiState: StateFlow<List<BsaRecord>> = _uiState.asStateFlow()

    private val _currentFilter = MutableStateFlow(TimeFilter.SIX_MONTHS)
    val currentFilter: StateFlow<TimeFilter> = _currentFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userData = MutableStateFlow<DermaUser?>(null)
    val userData: StateFlow<DermaUser?> = _userData.asStateFlow()

    // Poiché nel BsaRecord abbiamo salvato "dataOra" come String, usiamo questo format
    // per convertirla momentaneamente in un oggetto Date quando dobbiamo fare calcoli (come i filtri).
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    fun getHistoryDB(UserId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                //Recuperiamo i dati dell'utente dal repository
                _userData.value = repository.getUserData(UserId)

                //Recuperiamo i record BSA dal repository
                listaCalcoli = repository.getBsaRecords(UserId)
                    .sortedByDescending { parseDate(it.dataOra) }

                applyFilter(_currentFilter.value)
                _isLoading.value = false
            } catch(e: Exception) {
                _isLoading.value = false
            }
        }
    }

    // Applica un filtro temporale ai dati scaricati.
    fun applyFilter(filter: TimeFilter) {
        _currentFilter.value = filter

        // Calcoliamo la data limite (es. se filtro è 6 mesi, limitDate è "oggi meno 6 mesi")
        val limitDate = when (filter) {
            TimeFilter.SIX_MONTHS -> Calendar.getInstance().apply { add(Calendar.MONTH, -6) }.time
            TimeFilter.ONE_YEAR -> Calendar.getInstance().apply { add(Calendar.YEAR, -1) }.time
            TimeFilter.TWO_YEARS -> Calendar.getInstance().apply { add(Calendar.YEAR, -2) }.time
            TimeFilter.ALL_TIME -> Date(0) // Data 0 prende tutto lo storico
        }

        // Filtriamo la lista locale scartando i record più vecchi di limitDate.
        // NOTA: Manteniamo l'ordinamento decrescente (i più nuovi prima) per comodità.
        // Sarà la singola View a girare la lista se le serve in ordine cronologico per i grafici.
        _uiState.value = listaCalcoli.filter {
            (parseDate(it.dataOra) ?: Date(0)) >= limitDate
        }
    }

    fun deleteRecord(record: BsaRecord, UserId: String?) {
        if (record.id.isEmpty() || UserId == "null") return

        viewModelScope.launch {
            try {
                //CANCELLAZIONE TRAMITE REPOSITORY
                val isDeleted = repository.deleteBsaRecord(UserId!!, record.id)

                if (isDeleted) {
                    listaCalcoli = listaCalcoli.filter { it.id != record.id }
                    applyFilter(_currentFilter.value)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Funzione di supporto per trasformare la stringa "dataOra" in un oggetto Date gestibile.
    private fun parseDate(dateStr: String): Date? {
        return try {
            dateFormat.parse(dateStr)
        } catch (e: Exception) {
            Date(0) // Fallback di sicurezza in caso la stringa sia malformata
        }
    }
}