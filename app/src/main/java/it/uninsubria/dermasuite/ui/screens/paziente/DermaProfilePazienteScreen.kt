package it.uninsubria.dermasuite.ui.screens.paziente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaButton
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaHeading
import it.uninsubria.dermasuite.ui.components.DermaProfileField
import it.uninsubria.dermasuite.ui.components.DermaTopBar
import it.uninsubria.dermasuite.viewmodels.paziente.ProfilePazPageViewModel

@Composable
fun DermaProfilePazienteScreen(
    onLogout: () -> Unit,
    navController: NavController,
    onNavigateToDashboardP: () -> Unit,
    onNavigateToChatP: () -> Unit,
    viewModel: ProfilePazPageViewModel = viewModel()
){
    // Definiamo le azioni per questa specifica schermata
    val dashboardActions = listOf(
        BottomBarAction("HOME", R.drawable.ic_home, "dashboard_screen_paziente", onNavigateToDashboardP),
        BottomBarAction("CHAT", R.drawable.ic_chat, "chat_screen_paziente", onNavigateToChatP),
        BottomBarAction("PROFILE", R.drawable.ic_profile, "profile_screen_paziente", { /* Sei già qui */ }),
    )

    // Recupero dei campi dalla ViewModel
    val username = viewModel.user
    val nomeUtente = viewModel.nomeUtente
    val cognomeUtente = viewModel.cognomeUtente
    val email = viewModel.email
    val password = viewModel.password
    val dataNascita = viewModel.dataNascita


    Scaffold(
        topBar= {
            DermaTopBar(title = "DermaSuite", showBackButton = false, onBackClick = {})
        },
        bottomBar = {
            DermaBottomBar(navController = navController, actions = dashboardActions)
        }
    ) { padding ->
        DermaColumnScreen(innerPadding = padding, verticalArrangement = Arrangement.Top) {

            DermaHeading(
                titolo = "Gestione Profilo",
                sottotitolo = "Modifica le tue informazioni personali e gestisci la sicurezza del tuo account clinico.",
                modifier = Modifier.padding(2.dp)
            )

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(24.dp), // Arrotonda gli angoli della card
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White// Colore di sfondo della card
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp) // Ombra sotto la card
            ){
                Column(
                    modifier = Modifier
                        .padding(24.dp) // Padding interno per distanziare i campi dai bordi della card
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_profile),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Dati Anagrafici",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF0D214F),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (nomeUtente == null || cognomeUtente == null) {
                        CircularProgressIndicator() // Mostra una rotellina di caricamento
                    } else {
                        DermaProfileField("Nome", nomeUtente)

                        DermaProfileField("Cognome", cognomeUtente)

                        DermaProfileField("Data di Nascita", dataNascita)

                        DermaProfileField("Username", username, modificaIcon = {
                            Icon(painter = painterResource(id = R.drawable.ic_modifica), contentDescription = null)
                        })

                        DermaProfileField("Email", email, modificaIcon = {
                            Icon(painter = painterResource(id = R.drawable.ic_modifica), contentDescription = null)
                        })

                        DermaProfileField("Password", password, modificaIcon = {
                            Icon(painter = painterResource(id = R.drawable.ic_modifica), contentDescription = null)
                        })
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }


            }
            Spacer(modifier = Modifier.height(20.dp))

            DermaButton("Logout",onClick = {onLogout()})


        }
    }
}