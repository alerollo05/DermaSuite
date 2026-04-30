package it.uninsubria.dermasuite.ui.screens.paziente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaButton
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaTopBar

@Composable
fun DermaProfilePazienteScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
    navController: NavController,
    onNavigateToDashboardP: () -> Unit,
    onNavigateToChatP: () -> Unit
){
    // Definiamo le azioni per questa specifica schermata
    val dashboardActions = listOf(
        BottomBarAction("HOME", R.drawable.ic_home, "dashboard_screen_paziente", onNavigateToDashboardP),
        BottomBarAction("CHAT", R.drawable.ic_chat, "chat_screen_paziente", onNavigateToChatP),
        BottomBarAction("PROFILE", R.drawable.ic_profile, "profile_screen_paziente", { /* Sei già qui */ }),
    )

    Scaffold(
        topBar = {
            DermaTopBar(
                title = "Profile",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        bottomBar = {
            DermaBottomBar(navController = navController, actions = dashboardActions)
        }
    ) { padding ->
        DermaColumnScreen(innerPadding = padding, verticalArrangement = Arrangement.Top) {
            Column(
                modifier = Modifier.fillMaxWidth(), // 1. Prende tutta la larghezza
                horizontalAlignment = Alignment.CenterHorizontally // 2. Centra i componenti Text
            ) {
                Text(text = "PROFILE PAGE", fontSize = 24.sp)

                Spacer(modifier = Modifier.height(20.dp))

                DermaButton("Logout",onClick = {onLogout()})

            }

        }
    }
}