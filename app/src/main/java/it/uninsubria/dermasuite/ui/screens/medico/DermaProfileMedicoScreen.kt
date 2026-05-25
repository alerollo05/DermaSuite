package it.uninsubria.dermasuite.ui.screens.medico

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaAvatar
import it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaButton
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaHeading
import it.uninsubria.dermasuite.ui.components.DermaIsLoading
import it.uninsubria.dermasuite.ui.components.DermaOutlinedTextField
import it.uninsubria.dermasuite.ui.components.DermaProfileField
import it.uninsubria.dermasuite.ui.components.DermaTopBar
import it.uninsubria.dermasuite.viewmodels.medico.ProfileMedPageViewModel

@Composable
fun DermaProfileMedicoScreen(
    onLogout: () -> Unit,
    navController: NavController,
    onNavigateToDashboardM: () -> Unit,
    onNavigateToChatM: () -> Unit,
    viewModel: ProfileMedPageViewModel = viewModel()
){
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.updateAvatar(uri, context.contentResolver)
        }
    }

    LaunchedEffect(viewModel.snackbarMessage) {
        viewModel.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message = message)
            viewModel.clearSnackbarMessage()
        }
    }

    // Azioni della barra inferiore ottimizzate per i percorsi del medico
    val dashboardActions = listOf(
        BottomBarAction(stringResource(R.string.label_bottom_home), R.drawable.ic_home, "dashboard_screen_medico", onNavigateToDashboardM),
        BottomBarAction(stringResource(R.string.label_bottom_chat), R.drawable.ic_chat, "chat_screen_medico", onNavigateToChatM),
        BottomBarAction(stringResource(R.string.label_bottom_profile), R.drawable.ic_profile, "profile_screen_medico", { /* Sei già qui */ }),
    )

    val username = viewModel.user
    val nomeUtente = viewModel.nomeUtente
    val cognomeUtente = viewModel.cognomeUtente
    val email = viewModel.email
    val password = viewModel.password
    val dataNascita = viewModel.dataNascita
    val sesso = viewModel.sesso
    val specializzazione = viewModel.specializzazione
    val descrizione = viewModel.descrizione

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
                sottotitolo = "Visualizza e modifica le tue informazioni professionali",
                modifier = Modifier.padding(2.dp)
            )

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ){
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Box(
                        modifier = Modifier.clickable {
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    ) {
                        if (viewModel.isUploading) {
                            DermaIsLoading()
                        } else {
                            DermaAvatar(
                                avatarURL = viewModel.avatarUrl,
                                isComplex = "\u270F\uFE0F",
                                size = 100
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
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
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (nomeUtente == null || cognomeUtente == null) {
                        DermaIsLoading()
                    } else {
                        DermaProfileField(stringResource(R.string.label_nome), nomeUtente)
                        DermaProfileField(stringResource(R.string.label_cognome), cognomeUtente)
                        DermaProfileField(stringResource(R.string.label_data_nascita), dataNascita ?: stringResource(R.string.label_no_birthdate))
                        DermaProfileField(stringResource(R.string.label_sesso), sesso ?: "Non specificato")

                        DermaProfileField(
                            label = stringResource(R.string.label_username),
                            value = username ?: "",
                            modificaIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_modifica),
                                    contentDescription = stringResource(R.string.modifica_username_title),
                                    modifier = Modifier.clickable { viewModel.openUsernameDialog() }
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

                        // --- NUOVO CAMPO: SPECIALIZZAZIONE ---
                        DermaProfileField(
                            label = "Specializzazione",
                            value = specializzazione ?: "Non specificata",
                            modificaIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_modifica),
                                    contentDescription = "Modifica Specializzazione",
                                    modifier = Modifier.clickable { viewModel.openSpecializationDialog() }
                                )
                            }
                        )

                        // --- NUOVO CAMPO: DESCRIZIONE ---
                        DermaProfileField(
                            label = "Descrizione Professionale",
                            value = descrizione ?: "Nessuna descrizione inserita",
                            modificaIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_modifica),
                                    contentDescription = "Modifica Descrizione",
                                    modifier = Modifier.clickable { viewModel.openDescriptionDialog() }
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottone di Logout
            DermaButton(
                text = stringResource(R.string.btn_logout),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                onClick = { onLogout() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DermaButton(
                stringResource(R.string.btn_elimina_account),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                onClick = { viewModel.openDeleteDialog() }
            )
        }

        // --- POPUP DI MODIFICA (ALERT DIALOGS) ---

        // POPUP USERNAME
        if (viewModel.showUsernameDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeUsernameDialog() },
                title = { Text(text = stringResource(R.string.modifica_username_title), style = MaterialTheme.typography.headlineMedium) },
                text = {
                    Column {
                        Text(text = stringResource(R.string.modifica_username_desc), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
                        DermaOutlinedTextField(
                            value = viewModel.editUsernameText,
                            onValueChange = { viewModel.updateEditUsernameText(it) },
                            label = stringResource(R.string.label_nuovo_username),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (viewModel.inputPopupError != null) {
                            Text(text = viewModel.inputPopupError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp, start = 8.dp))
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { viewModel.confirmUsernameChange(context) }) { Text(stringResource(R.string.btn_conferma), style = MaterialTheme.typography.labelLarge) } },
                dismissButton = { TextButton(onClick = { viewModel.closeUsernameDialog() }) { Text(stringResource(R.string.btn_annulla), color = Color.Gray, style = MaterialTheme.typography.labelLarge) } }
            )
        }

        // POPUP EMAIL
        if (viewModel.showEmailDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeEmailDialog() },
                title = { Text(text = stringResource(R.string.modifica_email_title), style = MaterialTheme.typography.headlineMedium) },
                text = {
                    Column {
                        Text(text = stringResource(R.string.modifica_email_desc), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
                        DermaOutlinedTextField(
                            value = viewModel.editEmailText,
                            onValueChange = { viewModel.updateEditEmailText(it) },
                            label = stringResource(R.string.label_nuova_email),
                            modifier = Modifier.fillMaxWidth()
                        )
                        DermaOutlinedTextField(
                            value = viewModel.currentPasswordForEmail,
                            onValueChange = { viewModel.updateCurrentPasswordForEmail(it) },
                            label = stringResource(R.string.label_password_attuale),
                            isPassword = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (viewModel.inputPopupError != null) {
                            Text(text = viewModel.inputPopupError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { viewModel.confirmEmailChange(context) }) { Text(stringResource(R.string.btn_conferma), style = MaterialTheme.typography.labelLarge) } },
                dismissButton = { TextButton(onClick = { viewModel.closeEmailDialog() }) { Text(stringResource(R.string.btn_annulla), color = Color.Gray, style = MaterialTheme.typography.labelLarge) } }
            )
        }

        // POPUP PASSWORD
        if (viewModel.showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closePasswordDialog() },
                title = { Text(text = stringResource(R.string.modifica_password_title), style = MaterialTheme.typography.headlineMedium) },
                text = {
                    Column {
                        Text(text = stringResource(R.string.modifica_password_desc), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
                        DermaOutlinedTextField(value = viewModel.currentPasswordText, onValueChange = { viewModel.updateCurrentPasswordText(it) }, label = stringResource(R.string.label_password_attuale), isPassword = true, modifier = Modifier.fillMaxWidth())
                        DermaOutlinedTextField(value = viewModel.newPasswordText, onValueChange = { viewModel.updateNewPasswordText(it) }, label = stringResource(R.string.label_nuova_password), isPassword = true, modifier = Modifier.fillMaxWidth())
                        DermaOutlinedTextField(value = viewModel.confirmNewPasswordText, onValueChange = { viewModel.updateConfirmNewPasswordText(it) }, label = stringResource(R.string.label_conferma_password), isPassword = true, modifier = Modifier.fillMaxWidth())
                        if (viewModel.inputPopupError != null) {
                            Text(text = viewModel.inputPopupError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { viewModel.confirmPasswordChange(context) }) { Text(stringResource(R.string.btn_conferma), style = MaterialTheme.typography.labelLarge) } },
                dismissButton = { TextButton(onClick = { viewModel.closePasswordDialog() }) { Text(stringResource(R.string.btn_annulla), color = Color.Gray, style = MaterialTheme.typography.labelLarge) } }
            )
        }

        // POPUP SPECIALIZZAZIONE
        if (viewModel.showSpecializationDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeSpecializationDialog() },
                title = { Text(text = "Modifica Specializzazione", style = MaterialTheme.typography.headlineMedium) },
                text = {
                    Column {
                        Text(text = "Inserisci la tua specializzazione clinica principale da mostrare ai pazienti.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
                        DermaOutlinedTextField(
                            value = viewModel.editSpecializationText,
                            onValueChange = { viewModel.updateEditSpecializationText(it) },
                            label = "Nuova Specializzazione",
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (viewModel.inputPopupError != null) {
                            Text(text = viewModel.inputPopupError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { viewModel.confirmSpecializationChange(context) }) { Text(stringResource(R.string.btn_conferma), style = MaterialTheme.typography.labelLarge) } },
                dismissButton = { TextButton(onClick = { viewModel.closeSpecializationDialog() }) { Text(stringResource(R.string.btn_annulla), color = Color.Gray, style = MaterialTheme.typography.labelLarge) } }
            )
        }

        // POPUP DESCRIZIONE
        if (viewModel.showDescriptionDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeDescriptionDialog() },
                title = { Text(text = "Modifica Descrizione", style = MaterialTheme.typography.headlineMedium) },
                text = {
                    Column {
                        Text(text = "Aggiorna la tua biografia o descrizione professionale.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
                        DermaOutlinedTextField(
                            value = viewModel.editDescriptionText,
                            onValueChange = { viewModel.updateEditDescriptionText(it) },
                            label = "Nuova Descrizione",
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (viewModel.inputPopupError != null) {
                            Text(text = viewModel.inputPopupError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { viewModel.confirmDescriptionChange(context) }) { Text(stringResource(R.string.btn_conferma), style = MaterialTheme.typography.labelLarge) } },
                dismissButton = { TextButton(onClick = { viewModel.closeDescriptionDialog() }) { Text(stringResource(R.string.btn_annulla), color = Color.Gray, style = MaterialTheme.typography.labelLarge) } }
            )
        }
        // --- POPUP ELIMINA ACCOUNT ---
        if (viewModel.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeDeleteDialog() },
                title = {
                    Text(
                        text = "Elimina Account", // Se hai una string resource, usala qui
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.error // Lo facciamo rosso per attirare l'attenzione
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Attenzione: questa azione è irreversibile. Tutti i tuoi dati verranno cancellati. Inserisci la tua password per confermare.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        DermaOutlinedTextField(
                            value = viewModel.deletePasswordText,
                            onValueChange = { viewModel.updateDeletePasswordText(it) },
                            label = stringResource(R.string.label_password_attuale),
                            isPassword = true,
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
                    TextButton(
                        onClick = {
                            // Passiamo onLogout come callback di onSuccess!
                            viewModel.confirmDeleteAccount(context) { onLogout() }
                        }
                    ) {
                        Text(
                            text = "Elimina definitivamente",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.error // Bottone rosso
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.closeDeleteDialog() }) {
                        Text(
                            text = stringResource(R.string.btn_annulla),
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            )
        }
    }
}