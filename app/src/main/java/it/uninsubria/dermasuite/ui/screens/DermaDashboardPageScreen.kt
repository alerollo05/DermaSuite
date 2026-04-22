package it.uninsubria.dermasuite.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.uninsubria.dermasuite.ui.components.DermaButton
import it.uninsubria.dermasuite.viewmodels.DashboardPageViewModel

@Composable
fun DashboardPageScreen(
    onNavigateToStart: () -> Unit,
    viewModel: DashboardPageViewModel = viewModel() // Iniezione del ViewModel
){
    val nomeUtente = viewModel.userName

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Qui appare il nome recuperato da Firestore
            Text(text = "Ciao, $nomeUtente!", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(20.dp))

            DermaButton("Log Out", onClick = {
                // Esegue il logout reale e poi naviga
                viewModel.logout {
                    onNavigateToStart()
                }
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPagePreview() {
    it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme() {
        DashboardPagePreview()
    }
}