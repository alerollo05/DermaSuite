package it.uninsubria.dermasuite.ui.screens.paziente

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaAccountTypeSelector
import it.uninsubria.dermasuite.ui.components.DermaAvatar
import it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaButton
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaDoctorListDialog
import it.uninsubria.dermasuite.ui.components.DermaHeading
import it.uninsubria.dermasuite.ui.components.DermaIsLoading
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
    viewModel: ProfilePazPageViewModel = viewModel()
){

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    val currentDoctor = viewModel.currentDoctor

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

    val dashboardActions = listOf(
        BottomBarAction(stringResource(R.string.label_bottom_home), R.drawable.ic_home, "dashboard_screen_paziente", onNavigateToDashboardP),
        BottomBarAction(stringResource(R.string.label_bottom_profile), R.drawable.ic_profile, "profile_screen_paziente", { /* Sei già qui */ }),
    )

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
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ){
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Box(
                        modifier = Modifier
                            .clickable{
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))}
                    ) {
                        if (viewModel.isUploading) {
                            DermaIsLoading()
                        } else{
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
                        DermaIsLoading() // Mostra una rotellina di caricamento
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
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 8.dp)
            )

            if (currentDoctor == null) {
                DermaSpecialistCard(
                    doctorName = stringResource(R.string.label_no_doctor_assigned),
                    doctorRole = stringResource(R.string.label_dermatology),
                    doctorDescription = stringResource(R.string.desc_no_doctor_assigned),
                    iconResId = R.drawable.ic_button_medico,
                    doctorMail = "",
                    doctorLanguage = "",
                    doctorDataNascita = null
                )
            } else {
                val stringaNome = currentDoctor.nome.lowercase().replaceFirstChar { it.uppercase() }
                val stringaCognome = currentDoctor.cognome.lowercase().replaceFirstChar { it.uppercase() }
                DermaSpecialistCard(
                    doctorName = "${stringResource(R.string.label_doctor_prefix)} $stringaNome$stringaCognome",
                    doctorRole = currentDoctor.specialization ?: stringResource(R.string.label_default_specialization),
                    doctorDescription = currentDoctor.description ?: stringResource(R.string.desc_no_doctor_description),
                    doctorDataNascita = currentDoctor.dataNascita,
                    doctorMail = currentDoctor.email,
                    doctorLanguage = currentDoctor.language,
                    iconResId = R.drawable.ic_button_medico,
                    avatarURL = currentDoctor.avatarUrl
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.openDoctorDialog() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
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

            Spacer(modifier = Modifier.height(16.dp))

            DermaButton(stringResource(R.string.btn_logout),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                onClick = {onLogout()}
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


        if (viewModel.showUsernameDialog) {

            AlertDialog(
                onDismissRequest = { viewModel.closeUsernameDialog() },
                title = {
                    Text(
                        text = stringResource(R.string.modifica_username_title),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                text = {
                    Column {
                        Text(
                            text = stringResource(R.string.modifica_username_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        DermaOutlinedTextField(
                            value = viewModel.editUsernameText,
                            onValueChange = {
                                viewModel.updateEditUsernameText(it)
                                if (viewModel.inputPopupError != null) {
                                    viewModel.clearInputPopupError()
                                }
                            },
                            label = stringResource(R.string.label_nuovo_username),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (viewModel.inputPopupError != null) {
                            Text(
                                text = viewModel.inputPopupError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmUsernameChange(context) }) {
                        Text(
                            text = stringResource(R.string.btn_conferma),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                },

                dismissButton = {
                    TextButton(onClick = { viewModel.closeUsernameDialog() }) {
                        Text(
                            text = stringResource(R.string.btn_annulla),
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            )
        }

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
                    TextButton(onClick = { viewModel.confirmEmailChange(context) }) {
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
                            isPassword = true,
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
                            isPassword = true,
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
                    TextButton(onClick = { viewModel.confirmPasswordChange(context) }) {
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

        if(viewModel.showDoctorDialog){
            DermaDoctorListDialog(viewModel, context)
        }

        if (viewModel.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeDeleteDialog() },
                title = {
                    Text(
                        text = "Elimina Account",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.error
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
                            viewModel.confirmDeleteAccount(context) { onLogout() }
                        }
                    ) {
                        Text(
                            text = "Elimina definitivamente",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.error
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