package it.uninsubria.dermasuite.ui.screens.paziente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaButton
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaDistrictSelector
import it.uninsubria.dermasuite.ui.components.DermaHeading
import it.uninsubria.dermasuite.ui.components.DermaSelectorParameterCard
import it.uninsubria.dermasuite.ui.components.DermaTopBar
import it.uninsubria.dermasuite.viewmodels.paziente.DistrictState
import it.uninsubria.dermasuite.viewmodels.paziente.EasiDistrictState
import it.uninsubria.dermasuite.viewmodels.paziente.EasiPageViewModel
import it.uninsubria.dermasuite.viewmodels.paziente.PasiPageViewModel

@Composable
fun DermaEASIScreen(
    onBack: () -> Unit,
    onNavigateToChatP: () -> Unit,
    onNavigateToProfileP: () -> Unit,
    onNavigateToEasiHistory: () -> Unit,
    navController: NavController,
    viewModel: EasiPageViewModel = viewModel()

){
    val listaIcone = listOf(
        BottomBarAction(
            "HOME", R.drawable.ic_home,
            "dashboard_screen_paziente",
            {onBack()}),
        BottomBarAction(
            "CHAT", R.drawable.ic_chat,
            "chat_screen_paziente",
            {onNavigateToChatP()}),
        BottomBarAction(
            "HISTORY", R.drawable.ic_history,
            "easi_history_screen",
            {onNavigateToEasiHistory()}),
        BottomBarAction(
            "PROFILE", R.drawable.ic_profile,
            "profile_screen_paziente",
            {onNavigateToProfileP()})
    )
    Scaffold(
        topBar = {
            DermaTopBar(
                title = "Calcolo EASI",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        bottomBar = {
            DermaBottomBar(
                navController = navController,
                actions = listaIcone
            )
        }
    ) { padding ->

        //Andiamo a recuperare i dati relativi al distretto selezionato al momento
        val currentData = viewModel.districtValues[viewModel.currentDistrict] ?: EasiDistrictState()

        var easiSuccess = false

        DermaColumnScreen(innerPadding = padding, verticalArrangement = Arrangement.Top) {
            DermaHeading(
                titolo = "Calcolo EASI",
                sottotitolo = "Descrizione del easi",
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            DermaDistrictSelector(
                "Distretto",
                selectedDistrict = viewModel.currentDistrict,
                onDistrictSelected = { viewModel.currentDistrict = it },
                chkComplete = { distretto -> viewModel.isDistrictComplete(distretto) }
            )

            DermaSelectorParameterCard(
                title = "Eritema",
                subtitle = "Seleziona il punteggio relativo all'eritema",
                IconRes = R.drawable.ic_home,
                selectedValue = currentData.eritema,
                maxValue = 3,
                onValueChange = { viewModel.updateDistrictParameters(eritema = it) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            DermaSelectorParameterCard(
                title = "Edema Papulizzazione",
                subtitle = "Seleziona il punteggio relativo all'edema papulizzazione",
                IconRes = R.drawable.ic_home,
                maxValue = 3,
                selectedValue = currentData.edemaPapulizzazione,
                onValueChange = { viewModel.updateDistrictParameters(edemaPapulizzazione = it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DermaSelectorParameterCard(
                title = "Escoriazione",
                subtitle = "Seleziona il punteggio relativo alla escoriazione",
                IconRes = R.drawable.ic_home,
                maxValue = 3,
                selectedValue = currentData.escoriazione,
                onValueChange = { viewModel.updateDistrictParameters(escoriazione = it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DermaSelectorParameterCard(
                title = "Lichenificazione",
                subtitle = "Seleziona il punteggio relativo alla lichenificazione",
                IconRes = R.drawable.ic_home,
                maxValue = 3,
                selectedValue = currentData.lichenificazione,
                onValueChange = { viewModel.updateDistrictParameters(lichenificazione = it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DermaSelectorParameterCard(
                title = "Area",
                subtitle = "Seleziona il punteggio relativo all'area",
                IconRes = R.drawable.ic_home,
                maxValue = 6,
                selectedValue = currentData.percentualeArea,
                onValueChange = { viewModel.updateDistrictParameters(percentualeArea = it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            /*DermaButton(
                text = "Calcola EASI",
                onClick = {
                    viewModel.calculateTotalEasiAndSave(
                        onSucces = {easiSuccess = true},
                        onError = {easiSuccess = false}
                    )
                }
            )
            Spacer(modifier = Modifier.height(20.dp))*/


        }
    }
}