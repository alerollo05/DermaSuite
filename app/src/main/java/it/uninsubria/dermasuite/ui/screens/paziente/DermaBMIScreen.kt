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
import it.uninsubria.dermasuite.viewmodels.paziente.BmiPageViewModel
import it.uninsubria.dermasuite.viewmodels.paziente.EasiPageViewModel

@Composable
fun DermaBMIScreen(
    onBack: () -> Unit,
    onNavigateToChatP: () -> Unit,
    onNavigateToProfileP: () -> Unit,
    onNavigateToBmiHistory: () -> Unit,
    navController: NavController,
    viewModel: BmiPageViewModel = viewModel()

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
            "bmi_history_screen",
            {onNavigateToBmiHistory()}),
        BottomBarAction(
            "PROFILE", R.drawable.ic_profile,
            "profile_screen_paziente",
            {onNavigateToProfileP()})
    )
    Scaffold(
        topBar = {
            DermaTopBar(
                title = "Calcolo BMI",
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

        DermaColumnScreen(innerPadding = padding) {
            DermaHeading(
                titolo = "Calcolo BMI",
                sottotitolo = "Descrizione del bmi",
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}