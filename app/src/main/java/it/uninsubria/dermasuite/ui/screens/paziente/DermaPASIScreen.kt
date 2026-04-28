package it.uninsubria.dermasuite.ui.screens.paziente

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import it.uninsubria.dermasuite.viewmodels.paziente.PasiPageViewModel

@Composable
fun DermaPASIScreen(
    onBack: () -> Unit,
    onNavigateToChatP: () -> Unit,
    onNavigateToProfileP: () -> Unit,
    onNavigateToPasiHistory: () -> Unit,
    navController: NavController,
    viewModel: PasiPageViewModel = viewModel()
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
            "pasi_history_screen",
            {onNavigateToPasiHistory()}),
        BottomBarAction(
            "PROFILE", R.drawable.ic_profile,
            "profile_screen_paziente",
            {onNavigateToProfileP()})
    )
    Scaffold(
        topBar = {
            DermaTopBar(
                title = "Calcolo PASI",
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
        val currentData = viewModel.districtValues[viewModel.currentDistrict] ?: DistrictState()

        var pasiSuccess = false

        DermaColumnScreen(innerPadding = padding) {
            DermaHeading(
                titolo = "Calcolo PASI",
                sottotitolo = "Descrizione del pasi",
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
                onValueChange = { viewModel.updateDistrictParameters(eritema = it) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaSelectorParameterCard(
                title = "Indurimento",
                subtitle = "Seleziona il punteggio relativo all'indurimento",
                IconRes = R.drawable.ic_home,
                selectedValue = currentData.indurimento,
                onValueChange = { viewModel.updateDistrictParameters(indurimento = it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaSelectorParameterCard(
                title = "Desquamazione",
                subtitle = "Seleziona il punteggio relativo alla desquamazione",
                IconRes = R.drawable.ic_home,
                selectedValue = currentData.desquamazione,
                onValueChange = { viewModel.updateDistrictParameters(desquamazione = it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaSelectorParameterCard(
                title = "Area",
                subtitle = "Seleziona il punteggio relativo all'area",
                IconRes = R.drawable.ic_home,
                selectedValue = currentData.percentualeArea,
                onValueChange = { viewModel.updateDistrictParameters(percentualeArea = it) }
            )
            Spacer(modifier = Modifier.height(20.dp))
            DermaButton(
                text = "Calcola PASI",
                onClick = {
                    viewModel.calculateTotalPasiAndSave(
                        onSucces = {pasiSuccess = true},
                        onError = {pasiSuccess = false}
                    )
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            if(pasiSuccess){
               Text(
                   text = "PASI calcolati correttamente",
                   modifier = Modifier.padding(16.dp)
               )
            }
        }
    }
}