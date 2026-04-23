package it.uninsubria.dermasuite.ui.screens.paziente

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.uninsubria.dermasuite.ui.components.DermaButton
import it.uninsubria.dermasuite.viewmodels.paziente.DashboardPagePazienteViewModel

@Composable
fun DermaDashBoardPazienteScreen(
    onLogout: () -> Unit, //Questo è il logout che possiamo andare a richiamare dentro un bottone nella UI
    viewmodel: DashboardPagePazienteViewModel = viewModel(),
    onNavigateDashboardPASI: () -> Unit,
    onNavigateToStart: () -> Unit
){

    val nomeUtente = viewmodel.username //Andiamo a prendere il nome dell'utente dal viewModel


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Qui appare il nome recuperato da Firestore
            Text(text = "Ciao, $nomeUtente!", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            DermaButton("Log Out", onClick = {onLogout()})
            Spacer (modifier = Modifier.height(16.dp))
            DermaButton("Calcolo PASI", onClick = {onNavigateDashboardPASI()})

        }
    }
}