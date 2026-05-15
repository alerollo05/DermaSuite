package it.uninsubria.dermasuite.ui.screens.medico

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaTopBar
import it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen

@Composable
fun DermaProfileMedicoScreen(
    onLogout: () -> Unit,
    navController: NavController,
    onNavigateToDashboardM: () -> Unit = {},
    onNavigateToChatM: () -> Unit = {}
) {

    // Definiamo le azioni per la BottomBar del medico
    val dashboardActions = listOf(
        BottomBarAction(stringResource(R.string.bottom_bar_paziente).uppercase(), R.drawable.ic_patients, "dashboard_screen_medico", onNavigateToDashboardM),
        BottomBarAction(stringResource(R.string.bottom_bar_chat).uppercase(), R.drawable.ic_chat, "chat_screen_medico", onNavigateToChatM),
        BottomBarAction(stringResource(R.string.bottom_bar_profilo).uppercase(), R.drawable.ic_profile, "profile_screen_medico", {})
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
        DermaColumnScreen(innerPadding = padding, verticalArrangement = Arrangement.Top) {

            Button(onClick = onLogout) {
                Text(text = "Logout")
            }
        }
    }
}