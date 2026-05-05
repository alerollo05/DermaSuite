package it.uninsubria.dermasuite.ui.screens.paziente

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    // Creiamo il "controllore" della Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // Ascoltiamo i cambiamenti del messaggio nel ViewModel
    LaunchedEffect(viewModel.snackbarMessage) {
        // Se il messaggio non è nullo, mostriamo la Snackbar
        viewModel.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message = message)
            // Una volta mostrata, puliamo lo stato nel ViewModel
            // per evitare che riappaia se si ruota lo schermo
            viewModel.clearSnackbarMessage()
        }
    }

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
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
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
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (nomeUtente == null || cognomeUtente == null) {
                        CircularProgressIndicator() // Mostra una rotellina di caricamento
                    } else {
                        DermaProfileField("Nome", nomeUtente)

                        DermaProfileField("Cognome", cognomeUtente)

                        DermaProfileField("Data di Nascita", dataNascita)

                        DermaProfileField(
                            label = "Username",
                            value = username ?: "",
                            modificaIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_modifica),
                                        contentDescription = "Modifica Username",
                                        modifier = Modifier
                                                // Rendiamo l'icona cliccabile
                                                .clickable { viewModel.openUsernameDialog() }
                                    )
                            }
                        )

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
        // --- POPUP (ALERT DIALOG) PER MODIFICARE USERNAME ---
        // Il popup viene "disegnato" solo se showUsernameDialog è vera.
        // Se nel ViewModel showUsernameDialog diventa false, Compose rimuove istantaneamente il popup dallo schermo.
        if (viewModel.showUsernameDialog) {
            // Componente Standard Material 3: AlertDialog è il contenitore predefinito per i messaggi di sistema.
            AlertDialog(
                //Gestione della chiusura "esterna": Questa lambda viene eseguita se l'utente clicca fuori dal popup
                // o preme il tasto "Indietro" del telefono. Chiamiamo la funzione che resetta lo stato a false.
                onDismissRequest = { viewModel.closeUsernameDialog() },
                title = {
                    Text(text = "Modifica Username")
                },
                // Qui inseriamo la logica di input.
                text = {
                    Column {
                        Text(
                            text = "Inserisci il tuo nuovo username qui sotto.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        OutlinedTextField(
                            value = viewModel.editUsernameText,
                            // Ogni volta che l'utente preme un tasto, inviamo il nuovo carattere
                            // al ViewModel che aggiorna la variabile. Senza questa riga, non riusciresti a scrivere nulla.
                            onValueChange = {
                                viewModel.updateEditUsernameText(it)
                                // Opzionale: cancella l'errore appena l'utente ricomincia a scrivere
                                if (viewModel.inputPopupError != null) {
                                    viewModel.clearInputPopupError()
                                }
                            },
                            placeholder = { Text("Inserisci Username") },
                            label = { Text("Nuovo Username") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        // --- MESSAGGIO DI ERRORE INLINE ---
                        if (viewModel.inputPopupError != null) {
                            Text(
                                text = viewModel.inputPopupError!!,
                                color = MaterialTheme.colorScheme.error, // Rosso standard
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                            )
                        }
                    }
                },
                // Pulsante di Conferma (posizionato solitamente in basso a destra) :
                // Quando cliccato, esegue la logica di salvataggio (confirmUsernameChange).
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmUsernameChange() }) {
                        Text("Conferma")
                    }
                },
                // Pulsante di Annullamento: Chiude semplicemente il popup senza salvare nulla.
                dismissButton = {
                    TextButton(onClick = { viewModel.closeUsernameDialog() }) {
                        Text("Annulla", color = Color.Gray)
                    }
                }
            )
        }
    }
}