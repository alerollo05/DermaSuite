package it.uninsubria.dermasuite.ui.screens.paziente

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
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaHeading
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

        DermaColumnScreen(innerPadding = padding) {
            DermaHeading(
                titolo = "Calcolo PASI",
                sottotitolo = "Descrizione del pasi",
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}