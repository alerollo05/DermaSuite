package it.uninsubria.dermasuite.ui.screens.paziente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaButton
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.viewmodels.paziente.DashboardPagePazienteViewModel

@Composable
fun DermaDashBoardPazienteScreen(
    navController: NavController,
    onNavigateToChatP: () -> Unit,
    onNavigateToProfileP: () -> Unit,
    onNavigateDashboardPASI: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardPagePazienteViewModel = viewModel() // Iniezione del ViewModel
){
    // Definiamo le azioni per questa specifica schermata
    val dashboardActions = listOf(
        BottomBarAction("HOME", R.drawable.ic_home, "dashboard_screen_paziente", { /* Sei già qui */ }),
        BottomBarAction("CHAT", R.drawable.ic_chat, "chat_screen_paziente", onNavigateToChatP),
        BottomBarAction("PROFILE", R.drawable.ic_profile, "profile_screen_paziente", onNavigateToProfileP)
    )
    val nomeUtente = viewModel.username // Recupera l'username dal ViewModel

    Scaffold(
        bottomBar = {
            DermaBottomBar(navController = navController, actions = dashboardActions)
        }
    ) { padding ->
        DermaColumnScreen(innerPadding = padding, verticalArrangement = Arrangement.Top) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Qui appare il nome recuperato da Firestore
                    Text(text = "Ciao, $nomeUtente!", fontSize = 24.sp)

                    Spacer(modifier = Modifier.height(20.dp))

                    DermaButton("Log Out", onClick = {onLogout()} )

                    Spacer(modifier = Modifier.height(20.dp))

                    DermaButton("PASI",onClick = {onNavigateDashboardPASI()})
                }

        }
    }

    }
}