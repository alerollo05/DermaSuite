package it.uninsubria.dermasuite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.DermaAccountTypeSelector
import it.uninsubria.dermasuite.ui.components.DermaButton
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaDatePicker
import it.uninsubria.dermasuite.ui.components.DermaHeading
import it.uninsubria.dermasuite.ui.components.DermaPrivacyDisclaimerBox
import it.uninsubria.dermasuite.ui.components.DermaTextField
import it.uninsubria.dermasuite.ui.components.DermaTopBar
import it.uninsubria.dermasuite.viewmodels.RegisterPageViewModel

@Composable
fun DermaRegisterPageScreen(
    viewModel: RegisterPageViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToStart: () -> Unit
){
    // Estraiamo lo stato attuale dal ViewModel.
    // Ogni volta che uiState cambia nel VM, questa riga lo rileva e aggiorna la UI.
    val uiState = viewModel.uiState

    // Effetto per navigare automaticamente se la registrazione ha successo
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            // Invece di onRegistrationSuccess (alla dashboard),
            // usa onNavigateToLogin per fargli fare il login manuale
            onNavigateToLogin()
        }
    }


    val snackbarHostState = remember { SnackbarHostState() } // Creazione stato della snackbar

    // Lanciamo la Snackbar quando c'è un errore
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            DermaTopBar(
                title = "DermaSuite",
                showBackButton = true,
                onBackClick = onNavigateToStart,
                actions = {
                    // Esempio di utilizzo dello slot 'actions' per l'icona profilo
                    /*
                    IconButton(onClick = { /* Azione opzionale */ }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow),
                            contentDescription = "Profilo",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }*/
                }
            )
        }
    ) { padding ->
        //Serve a calcolare lo spazio occupato dalla topBar e dalla BottomBar in modo tale che il contenuto
        //che mettiamo noi non sia coperto dalle barre
        DermaColumnScreen(innerPadding = padding){
            Spacer(modifier = Modifier.height(40.dp))

            //Mettiamo l'intestazione della pagina
            DermaHeading(
                titolo = stringResource(R.string.titolo_register),
                sottotitolo = stringResource(R.string.subtitle_register)
            )
            Spacer(modifier = Modifier.height(40.dp))
            DermaAccountTypeSelector(
                selectedType = uiState.accountType, //Quindi di default nel file della classe abbiamo messo che viene selezionato paziente
                onTypeSelected = { viewModel.onAccountTypeSelected(it) }
            )
            Spacer(modifier = Modifier.height(40.dp))
            DermaTextField(
                label = stringResource(R.string.textfield_name),
                value = uiState.nome,
                onValueChange = { viewModel.onNomeChanged(it) },
                leadingIconRes = R.drawable.ic_button_paziente,
                placeholder = stringResource(R.string.placeholder_name)
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaTextField(
                label = stringResource(R.string.textfield_surname),
                value = uiState.cognome,
                onValueChange = { viewModel.onCognomeChanged(it) },
                leadingIconRes = R.drawable.ic_button_paziente,
                placeholder = stringResource(R.string.placeholder_surname)
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaDatePicker(
                label = stringResource(R.string.textfield_birth),
                value = uiState.dataNascita,
                onDataSelected = { viewModel.onDataNascitaChanged(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaTextField(
                label = stringResource(R.string.textfield_user),
                value = uiState.username,
                onValueChange = { viewModel.onUsernameChanged(it) },
                leadingIconRes = R.drawable.ic_chiocciola,
                placeholder = stringResource(R.string.placeholder_user)
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaTextField(
                label = stringResource(R.string.textfield_email),
                value = uiState.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                leadingIconRes = R.drawable.ic_mail,
                placeholder = stringResource(R.string.placeholder_email)
            )

            DermaTextField(
                label = stringResource(R.string.textfield_password),
                value = uiState.password,
                isPassword = true,
                onValueChange = { viewModel.onPasswordChanged(it) },
                leadingIconRes = R.drawable.ic_scudo_password,
                placeholder = stringResource(R.string.placeholder_password),
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaTextField(
                label = stringResource(R.string.textfield_confirm_password_register),
                value = uiState.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                leadingIconRes = R.drawable.ic_scudo_password,
                placeholder = stringResource(R.string.placeholder_confirm_password_register),
                isPassword = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaPrivacyDisclaimerBox(
                text = stringResource(R.string.privacy_disclaimer)
            )
            Spacer(modifier = Modifier.height(24.dp))
            DermaButton(
                text = if (uiState.isLoading) stringResource(R.string.loading_button) else stringResource(R.string.btn_register_create_account),
                onClick = { viewModel.onRegisterClick() }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = stringResource(R.string.txt_signin_register),
                )
                TextButton(onNavigateToLogin) {
                    Text(
                        text = stringResource(R.string.txt_btn_signin_register),
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun DermaRegisterPageScreenPreview() {
    it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme() {
    DermaRegisterPageScreen(onNavigateToLogin = {}, onNavigateToStart = {})
    }
}

