package it.uninsubria.dermasuite.ui.screens.medico

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaBarraRicerca
import it.uninsubria.dermasuite.ui.components.DermaTopBar
import it.uninsubria.dermasuite.viewmodels.medico.DashboardPageMedicoViewModel
import  it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaHeading
import it.uninsubria.dermasuite.ui.components.DermaIsLoading
import it.uninsubria.dermasuite.ui.components.DermaListaPazienti

@Composable
fun DermaDashBoardMedicoScreen(
    navController: NavController,
    onNavigateToChatM: () -> Unit = {},
    onNavigateToProfileM: () -> Unit = {},
    viewModel: DashboardPageMedicoViewModel = viewModel()
){
    // Definiamo le azioni per la BottomBar del medico
    val dashboardActions = listOf(
        BottomBarAction(stringResource(R.string.bottom_bar_paziente).uppercase(), R.drawable.ic_patients, "dashboard_screen_medico", {}),
        BottomBarAction(stringResource(R.string.bottom_bar_chat).uppercase(), R.drawable.ic_chat, "chat_screen_medico", onNavigateToChatM),
        BottomBarAction(stringResource(R.string.bottom_bar_profilo).uppercase(), R.drawable.ic_profile, "profile_screen_medico", onNavigateToProfileM)
    )

    Scaffold(
        topBar = {
            DermaTopBar(title = "DermaSuite", showBackButton = false, onBackClick = {})
        },
        bottomBar = {
            DermaBottomBar(navController = navController, actions = dashboardActions)
        }
    ){
        padding ->
        DermaColumnScreen(innerPadding = padding, verticalArrangement = Arrangement.Top, scrollState = null) {
            Spacer(modifier = Modifier.height(24.dp))

            DermaHeading(
                titolo = stringResource(R.string.title_dashboard_medico),
                sottotitolo = stringResource(R.string.description_dashboard_medico)
            )

            Spacer(modifier = Modifier.height(16.dp))

            DermaBarraRicerca(viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            if(viewModel.isLoading){
                DermaIsLoading(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                )
            }else if(viewModel.allPatients.isEmpty()){
                   Text(
                       text = stringResource(R.string.no_patients_found),
                       style = MaterialTheme.typography.bodyMedium,
                       color = MaterialTheme.colorScheme.primary
                   )
            }else{
                DermaListaPazienti(viewModel, modifier = Modifier.weight(1f))
            }
        }
    }
}