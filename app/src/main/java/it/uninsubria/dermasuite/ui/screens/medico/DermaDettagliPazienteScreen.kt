package it.uninsubria.dermasuite.ui.screens.medico

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaAverageCard
import it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaHeading
import it.uninsubria.dermasuite.ui.components.DermaIsLoading
import it.uninsubria.dermasuite.ui.components.DermaTopBar
import it.uninsubria.dermasuite.viewmodels.medico.DettagliPazienteViewModel

@Composable
fun DermaDettagliPazienteScreen(
    pazienteId: String,
    navController: NavController,
    onNavigateToProfileM: () -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: DettagliPazienteViewModel = viewModel()
    ){

    val pasiTitle = stringResource(R.string.label_pasi_average)
    val easiTitle = stringResource(R.string.label_easi_average)
    val bmiTitle = stringResource(R.string.label_bmi_average)
    val bsaTitle = stringResource(R.string.label_bsa_average)
    val mildLabel = stringResource(R.string.label_mild)
    val moderateLabel = stringResource(R.string.label_moderate)
    val severeLabel = stringResource(R.string.label_severe)
    val stableLabel = stringResource(R.string.label_stable)
    val patientNotFoundMsg = stringResource(R.string.label_patient_not_found)

    LaunchedEffect(pazienteId, pasiTitle, easiTitle, bmiTitle, bsaTitle, mildLabel, moderateLabel, severeLabel, stableLabel, patientNotFoundMsg) {
        viewModel.loadPatientData(
            pazienteId,
            pasiTitle,
            easiTitle,
            bmiTitle,
            bsaTitle,
            mildLabel,
            moderateLabel,
            severeLabel,
            stableLabel,
            patientNotFoundMsg
        )
    }

    // Definiamo le azioni per la BottomBar del medico
    val dashboardActions = listOf(
        BottomBarAction(stringResource(R.string.bottom_bar_paziente).uppercase(), R.drawable.ic_patients, "dashboard_screen_medico", onBack),
        BottomBarAction(stringResource(R.string.bottom_bar_profilo).uppercase(), R.drawable.ic_profile, "profile_screen_medico", onNavigateToProfileM)
    )
    Scaffold(
        topBar = {
            DermaTopBar(title = "DermaSuite", showBackButton = true, onBackClick = onBack)
        },
        bottomBar = {
            DermaBottomBar(navController = navController, actions = dashboardActions)
        }
    ){
      padding ->
        if(viewModel.isLoading){
            DermaIsLoading()
        }else{
            DermaColumnScreen(innerPadding = padding) {

                Spacer(modifier = Modifier.height(16.dp))
                DermaHeading(
                    titolo = stringResource(R.string.titolo_dettaglio_paz),
                    sottotitolo = stringResource(R.string.sottotitolo_dettaglio_paz)
                )
                val s = stringResource(R.string.label_username_detail)
                DermaHeading(
                    titolo = viewModel.nomePaziente,
                    sottotitolo = "$s ${viewModel.usernamePaz}"
                )
                Spacer(modifier = Modifier.height(16.dp))

                //Creiamo una griglia di card con i vari calcoli
                val accentColor = MaterialTheme.colorScheme.primary

                // Card PASI
                DermaAverageCard(
                    title = viewModel.pasiSummary.title,
                    averageValue = viewModel.pasiSummary.averageValue,
                    severityLabel = viewModel.pasiSummary.severityLabel,
                    trendPercentage = viewModel.pasiSummary.trendPercentage,
                    trendColor = if (viewModel.pasiSummary.isWorsening) Color.Red else Color.Green,
                    accentColor = accentColor,
                    historicalData = viewModel.pasiSummary.historicalData,
                    onClick = { navController.navigate("pasi_history_screen/$pazienteId") }
                )

                // Card EASI
                DermaAverageCard(
                    title = viewModel.easiSummary.title,
                    averageValue = viewModel.easiSummary.averageValue,
                    severityLabel = viewModel.easiSummary.severityLabel,
                    trendPercentage = viewModel.easiSummary.trendPercentage,
                    trendColor = if (viewModel.easiSummary.isWorsening) Color.Red else Color.Green,
                    accentColor = accentColor,
                    historicalData = viewModel.easiSummary.historicalData,
                    onClick = { navController.navigate("easi_history_screen/$pazienteId") }
                )

                // Card BMI
                DermaAverageCard(
                    title = viewModel.bmiSummary.title,
                    averageValue = viewModel.bmiSummary.averageValue,
                    severityLabel = viewModel.bmiSummary.severityLabel,
                    trendPercentage = viewModel.bmiSummary.trendPercentage,
                    trendColor = if (viewModel.bmiSummary.isWorsening) Color.Red else Color.Green,
                    accentColor = Color(0xFF003366),
                    historicalData = viewModel.bmiSummary.historicalData,
                    onClick = { navController.navigate("bmi_history_screen/$pazienteId") }
                )
                //Card BSA
                DermaAverageCard(
                    title = viewModel.bsaSummary.title,
                    averageValue = viewModel.bsaSummary.averageValue,
                    severityLabel = viewModel.bsaSummary.severityLabel,
                    trendPercentage = viewModel.bsaSummary.trendPercentage,
                    trendColor = if (viewModel.bsaSummary.isWorsening) Color.Red else Color.Green,
                    accentColor = accentColor,
                    historicalData = viewModel.bsaSummary.historicalData,
                    onClick = { navController.navigate("bsa_history_screen/$pazienteId") }
                )
            }
        }
    }
}