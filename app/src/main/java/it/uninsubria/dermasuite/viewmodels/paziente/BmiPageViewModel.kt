package it.uninsubria.dermasuite.viewmodels.paziente

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import it.uninsubria.dermasuite.model.BmiRecord
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat

class BmiPageViewModel : ViewModel() {

    data class BmiUiState(
            val height: String = "",
            val weight: String = "",
            val calculatedCategory: String = "",
            val calculatedBMI: Double = 0.0,
            val isSaving: Boolean = false,
            val saveSuccess: Boolean = false,

            )

    var uiState by mutableStateOf(BmiUiState())


    //La usiamo per abilitare il risultato della card
    var showResult by mutableStateOf(false)

    // Un semplice contatore che incrementiamo a ogni calcolo
    //Ci serve per andare a far scorrere ad ogni pressione del bottone verso il basso la pagina in modo da visualizzare la card di result
    var scrollTrigger by mutableStateOf(0)

    val auth = Firebase.auth
    val db = Firebase.firestore

    //Metodi per l'aggiornamento delle variabili in base alla UI
    fun onHeightChanged(newHeight: String) {
        uiState = uiState.copy(height = newHeight)
    }

    fun onWeightChanged(newWeight: String) {
        uiState = uiState.copy(weight = newWeight)
    }

    fun onCalculatedBMI(newResult: Double){
        uiState = uiState.copy(calculatedBMI = newResult)
    }
    fun onCalculatedCategoryChange(newCategory: String){
        uiState = uiState.copy(calculatedCategory = newCategory)
    }
    fun onIsSaving(newIsSaving: Boolean){
        uiState = uiState.copy(isSaving = newIsSaving)
    }
    fun onSaveSuccess(newSuccess: Boolean){
        uiState = uiState.copy(saveSuccess = newSuccess)
    }

    fun isCalcoloAbilitato(): Boolean {
        //Vediamo se i valori sono stati modificati
        return uiState.height != "" && uiState.weight != ""
    }


    fun calcolaBMIAndSave (context: Context, onSuccess: () -> Unit, onError: () -> Unit) {
        if(isCalcoloAbilitato()){
            //Convertiamo l'altezza da centimetri a metri
            val heightMeters = uiState.height.toDouble() / 100.0
            val weight = uiState.weight.toDouble()

            //Calcolo del BMI effettivo
            val bmi = weight / (heightMeters * heightMeters)
            //Usiamo la funzione getBMICategory per ricavare la categoria in base al BMI
            val category = BmiRecord.getBMICategory(bmi, context)

            //Arrotondiamo a una cifra decimale (es. da 22.8571... a 22.9)
            //Moltiplichiamo per 10, arrotondiamo all'intero più vicino, e dividiamo per 10
            val bmiFinale = Math.round(bmi * 10.0) / 10.0

            //Aggiorniamo i valori dell'UI con il risultato finale calcolato
            onCalculatedBMI(bmiFinale)
            onCalculatedCategoryChange(category)

            salvaBMI(bmiFinale, category, onSuccess = {
                showResult= true //Attiviamo la card quando il salvataggio è andato a buon fine
                scrollTrigger++ //Incrementiamo il contatore per far scorrere la pagina verso il basso (vedi nel BMIPageScreen)
                onSuccess()
            }, onError = onError)
        }
    }

    fun salvaBMI(
        bmi: Double,
        category: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isSaving = true)

                val user = auth.currentUser
                if (user == null) {
                    onError()
                    return@launch
                }

                val document = db.collection("users").document(user.uid).get().await()

                if(document != null && document.exists()){
                    val role = document.getString("role")
                    if(role == "Paziente"){ //Controlliamo che l'utente sia effettivamente un paziente per sicurezza
                        //Creiamo il pacchetto finito da spedire al DB
                        val payload = hashMapOf(
                            "CalculationDate" to FieldValue.serverTimestamp(),
                            "BmiTot" to bmi,
                            "Category" to category,
                            "Height" to uiState.height.toInt(),
                            "Weight" to uiState.weight.toInt(),
                        )

                        db.collection("users").document(user.uid).collection("BMI").add(payload).await()
                        onSuccess()
                        uiState = uiState.copy(isSaving = false)
                    }else{
                        uiState = uiState.copy(isSaving = false)
                        onError()
                    }
                }else{
                    uiState = uiState.copy(isSaving = false)
                    onError()
                }

            }catch(e: Exception){
                uiState = uiState.copy(isSaving = false)
                onError()
            }
        }
    }

}