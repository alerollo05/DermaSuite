package it.uninsubria.dermasuite.model

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import it.uninsubria.dermasuite.R
import java.util.Date

data class BmiRecord(
    @DocumentId val id: String = "",
    @get:PropertyName("CalculationDate") @set:PropertyName("CalculationDate") var CalculationDate: Date = Date(),
    @get:PropertyName("Category") @set:PropertyName("Category") var Category: String = "",
    @get:PropertyName("BmiTot") @set:PropertyName("BmiTot") var BmiTot: Double = 0.0,
    @get:PropertyName("Height") @set:PropertyName("Height") var Height: Double = 0.0,
    @get:PropertyName("Weight") @set:PropertyName("Weight") var Weight: Double = 0.0,
    ) {

    //Equivale a una classe static in Java mettere il companion object qui dentro
    companion object {
        fun getBMICategory(bmi: Double, context: Context): String {

            return when {
                bmi < 18.5 -> context.getString(R.string.bmi_severity_low)
                bmi in 18.5..24.9 -> context.getString(R.string.bmi_severity_moderate)
                bmi in 25.0..29.9 -> context.getString(R.string.bmi_severity_severe)
                else -> context.getString(R.string.bmi_severity_obese)
            }
        }
    }
}