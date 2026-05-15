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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaButton
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaHeading
import it.uninsubria.dermasuite.ui.components.DermaOutlinedTextField // IMPORT AGGIUNTO
import it.uninsubria.dermasuite.ui.components.DermaProfileField
import it.uninsubria.dermasuite.ui.components.DermaSpecialistCard
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
    val sesso = viewModel.sesso

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
                titolo = stringResource(R.string.gestione_profilo_title),
                sottotitolo = stringResource(R.string.gestione_profilo_subtitle),
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
                            text = stringResource(R.string.dati_anagrafici_title),
                            style = MaterialTheme.typography.headlineMedium, // Usato il nuovo stile da Type.kt
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (nomeUtente == null || cognomeUtente == null) {
                        CircularProgressIndicator() // Mostra una rotellina di caricamento
                    } else {
                        DermaProfileField(stringResource(R.string.label_nome), nomeUtente)

                        DermaProfileField(stringResource(R.string.label_cognome), cognomeUtente)

                        DermaProfileField(stringResource(R.string.label_data_nascita), dataNascita)

                        DermaProfileField(stringResource(R.string.label_sesso), sesso ?: "Non specificato")

                        DermaProfileField(
                            label = stringResource(R.string.label_username),
                            value = username ?: "",
                            modificaIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_modifica),
                                    contentDescription = stringResource(R.string.modifica_username_title),
                                    // Rendiamo l'icona cliccabile, dicendogli cosa fare una volta cliccata (apertura del popup)
                                    modifier = Modifier.clickable { viewModel.openUsernameDialog()}
                                )
                            }
                        )

                        DermaProfileField(stringResource(R.string.label_email), email, modificaIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_modifica),
                                contentDescription = stringResource(R.string.modifica_email_title),
                                modifier = Modifier.clickable { viewModel.openEmailDialog() }
                            )
                        })

                        DermaProfileField(stringResource(R.string.label_password), password, modificaIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_modifica),
                                contentDescription = stringResource(R.string.modifica_password_title),
                                modifier = Modifier.clickable { viewModel.openPasswordDialog() }
                            )
                        })
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }


            }



            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.specialista_riferimento_title),
                style = MaterialTheme.typography.headlineMedium, // Usato il nuovo stile da Type.kt
                color = MaterialTheme.colorScheme.primary, // O usa un blu scuro specifico se lo hai nei theme
                textAlign = TextAlign.Center, // Forza il testo al centro
                modifier = Modifier
                    .fillMaxWidth() // Occupa tutta la larghezza per permettere il centraggio
                    .padding(top = 24.dp, bottom = 8.dp)
            )

            // Richiamo il componente riutilizzabile appena creato
            DermaSpecialistCard(
                doctorName = stringResource(R.string.mock_doctor_name),
                doctorRole = stringResource(R.string.mock_doctor_role),
                doctorDescription = stringResource(R.string.mock_doctor_description),
                iconResId = R.drawable.ic_button_medico
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottone "Cambia Medico"
            Button(
                onClick = { /* TODO: Logica per cambiare medico */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp), // Altezza uguale al DermaButton
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Sfondo pieno (stesso del DermaButton)
                    contentColor = Color.White // Testo e icona bianchi
                ),
                shape = MaterialTheme.shapes.medium, // Usiamo lo stesso arrotondamento del tema
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp) // Piatto come da design
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile), // Icona omino
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.btn_cambia_medico),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            DermaButton(stringResource(R.string.btn_logout),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                onClick = {onLogout()}
            )
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
                    Text(
                        text = stringResource(R.string.modifica_username_title),
                        style = MaterialTheme.typography.headlineMedium // Stile per il titolo del popup
                    )
                },
                // Qui inseriamo la logica di input.
                text = {
                    Column {
                        Text(
                            text = stringResource(R.string.modifica_username_desc),
                            style = MaterialTheme.typography.bodyMedium, // Stile corretto dal tuo Type.kt
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        DermaOutlinedTextField(
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
                            label = stringResource(R.string.label_nuovo_username),
                            modifier = Modifier.fillMaxWidth()
                        )
                        // --- MESSAGGIO DI ERRORE INLINE ---
                        if (viewModel.inputPopupError != null) {
                            Text(
                                text = viewModel.inputPopupError!!,
                                color = MaterialTheme.colorScheme.error, // Rosso standard
                                style = MaterialTheme.typography.labelSmall, // Usato labelSmall (12sp) per l'errore
                                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                            )
                        }
                    }
                },
                // Pulsante di Conferma (posizionato solitamente in basso a destra) :
                // Quando cliccato, esegue la logica di salvataggio (confirmUsernameChange).
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmUsernameChange() }) {
                        Text(
                            text = stringResource(R.string.btn_conferma),
                            style = MaterialTheme.typography.labelLarge // Stile del bottone
                        )
                    }
                },
                // Pulsante di Annullamento: Chiude semplicemente il popup senza salvare nulla.
                dismissButton = {
                    TextButton(onClick = { viewModel.closeUsernameDialog() }) {
                        Text(
                            text = stringResource(R.string.btn_annulla),
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelLarge // Stile del bottone
                        )
                    }
                }
            )
        }
        // --- POPUP MODIFICA EMAIL ---
        if (viewModel.showEmailDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeEmailDialog() },
                title = {
                    Text(
                        text = stringResource(R.string.modifica_email_title),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                text = {
                    Column {
                        Text(
                            text = stringResource(R.string.modifica_email_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        DermaOutlinedTextField(
                            value = viewModel.editEmailText,
                            onValueChange = {
                                viewModel.updateEditEmailText(it)
                                if (viewModel.inputPopupError != null) {
                                    viewModel.clearInputPopupError()
                                }
                            },
                            label = stringResource(R.string.label_nuova_email),
                            modifier = Modifier.fillMaxWidth()
                        )
                        DermaOutlinedTextField(
                            value = viewModel.currentPasswordForEmail,
                            onValueChange = {
                                viewModel.updateCurrentPasswordForEmail(it)
                                if (viewModel.inputPopupError != null) {
                                    viewModel.clearInputPopupError()
                                }
                            },
                            label = stringResource(R.string.label_password_attuale),
                            isPassword = true, // Nasconde i caratteri della password e aggiunge l'occhio (Gestito dal componente)
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (viewModel.inputPopupError != null) {
                            Text(
                                text = viewModel.inputPopupError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmEmailChange() }) {
                        Text(stringResource(R.string.btn_conferma), style = MaterialTheme.typography.labelLarge)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.closeEmailDialog() }) {
                        Text(stringResource(R.string.btn_annulla), color = Color.Gray, style = MaterialTheme.typography.labelLarge)
                    }
                }
            )
        }

        // --- POPUP MODIFICA PASSWORD ---
        if (viewModel.showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closePasswordDialog() },
                title = {
                    Text(
                        text = stringResource(R.string.modifica_password_title),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                text = {
                    Column {
                        Text(
                            text = stringResource(R.string.modifica_password_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        DermaOutlinedTextField(
                            value = viewModel.currentPasswordText,
                            onValueChange = {
                                viewModel.updateCurrentPasswordText(it)
                                if (viewModel.inputPopupError != null) {
                                    viewModel.clearInputPopupError()
                                }
                            },
                            label = stringResource(R.string.label_password_attuale),
                            isPassword = true, // Nasconde i caratteri della password e aggiunge l'occhio (Gestito dal componente)
                            modifier = Modifier.fillMaxWidth()
                        )
                        DermaOutlinedTextField(
                            value = viewModel.newPasswordText,
                            onValueChange = {
                                viewModel.updateNewPasswordText(it)
                                if (viewModel.inputPopupError != null) {
                                    viewModel.clearInputPopupError()
                                }
                            },
                            label = stringResource(R.string.label_nuova_password),
                            isPassword = true, // Nasconde i caratteri della password e aggiunge l'occhio (Gestito dal componente)
                            modifier = Modifier.fillMaxWidth()
                        )
                        DermaOutlinedTextField(
                            value = viewModel.confirmNewPasswordText,
                            onValueChange = {
                                viewModel.updateConfirmNewPasswordText(it)
                                if (viewModel.inputPopupError != null) {
                                    viewModel.clearInputPopupError()
                                }
                            },
                            label = stringResource(R.string.label_conferma_password),
                            isPassword = true, // Nasconde i caratteri della password e aggiunge l'occhio (Gestito dal componente)
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (viewModel.inputPopupError != null) {
                            Text(
                                text = viewModel.inputPopupError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmPasswordChange() }) {
                        Text(stringResource(R.string.btn_conferma), style = MaterialTheme.typography.labelLarge)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.closePasswordDialog() }) {
                        Text(stringResource(R.string.btn_annulla), color = Color.Gray, style = MaterialTheme.typography.labelLarge)
                    }
                }
            )
        }
    }
}