package it.uninsubria.dermasuite.viewmodels.paziente

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.firestore
import it.uninsubria.dermasuite.firebase.AuthRepository
import it.uninsubria.dermasuite.firebase.DermaUser
import it.uninsubria.dermasuite.model.BmiRecord
import it.uninsubria.dermasuite.model.TimeFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

class HistoryBmiPageViewModel(
    private val repository: AuthRepository = AuthRepository()
): ViewModel() {

    var ListaCalcoli = listOf<BmiRecord>()

    private val _uiState = MutableStateFlow<List<BmiRecord>>(emptyList())
    val uiState: StateFlow<List<BmiRecord>> = _uiState.asStateFlow()

    private val _currentFilter = MutableStateFlow(TimeFilter.SIX_MONTHS)
    val currentFilter: StateFlow<TimeFilter> = _currentFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userData = MutableStateFlow<DermaUser?>(null)
    val userData: StateFlow<DermaUser?> = _userData.asStateFlow()

    private val _latestRecord = MutableStateFlow<BmiRecord?>(null)
    val latestRecord: StateFlow<BmiRecord?> = _latestRecord.asStateFlow()


    fun getHistoryBMIList(UserId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit){
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Nota: Il repository te li restituisce già ordinati in modo discendente
                ListaCalcoli = repository.getBmiRecords(UserId)

                _latestRecord.value = ListaCalcoli.firstOrNull()

                applyFilter(_currentFilter.value)

                _isLoading.value = false
                onSuccess()
            }catch (e: Exception){
                _isLoading.value = false
                onError(e)
            }
        }
    }

    fun applyFilter(filter: TimeFilter) {
        _currentFilter.value = filter
        val limitDate = when (filter) {
            TimeFilter.SIX_MONTHS -> Calendar.getInstance().apply { add(Calendar.MONTH, -6) }.time
            TimeFilter.ONE_YEAR -> Calendar.getInstance().apply { add(Calendar.YEAR, -1) }.time
            TimeFilter.TWO_YEARS -> Calendar.getInstance().apply { add(Calendar.YEAR, -2) }.time
            TimeFilter.ALL_TIME -> Date(0)
        }
        _uiState.value = ListaCalcoli.filter { it.CalculationDate >= limitDate }.sortedBy { it.CalculationDate }
    }
    
    fun deleteRecord(record: BmiRecord, UserId: String) {
        if (record.id.isEmpty() || UserId == "null") return
        viewModelScope.launch {
            // SOSTITUZIONE: Deleghiamo la cancellazione al repository
            val isDeleted = repository.deleteBmiRecord(UserId, record.id)

            if (isDeleted) {
                // Aggiorniamo la UI solo se la cancellazione su Firebase ha avuto successo
                ListaCalcoli = ListaCalcoli.filter { it.id != record.id }
                _latestRecord.value = ListaCalcoli.firstOrNull()
                applyFilter(_currentFilter.value)
            }
        }
    }

}