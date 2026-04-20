package it.uninsubria.dermasuite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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

    //Lo mettiamo in modo tale che se i campi sono troppi posso fare lo scrool per vederli tutti
    // e tenere in memoria gli stati
    val scrollState = rememberScrollState()

    Scaffold(
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
                titolo = stringResource(R.string.heading_register),
                sottotitolo = stringResource(R.string.subtitle_register)
            )
            Spacer(modifier = Modifier.height(40.dp))
            DermaAccountTypeSelector(
                selectedType = uiState.accountType, //Quindi di default nel file della classe abbiamo messo che viene selezionato paziente
                onTypeSelected = { viewModel.onAccountTypeSelected(it) }
            )
            Spacer(modifier = Modifier.height(40.dp))
            DermaTextField(
                label = "Nome",
                value = uiState.nome,
                onValueChange = { viewModel.onNomeChanged(it) },
                leadingIconRes = R.drawable.ic_button_paziente,
                placeholder = "Inserisci il tuo nome"
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaTextField(
                label = "Cognome",
                value = uiState.cognome,
                onValueChange = { viewModel.onCognomeChanged(it) },
                leadingIconRes = R.drawable.ic_button_paziente,
                placeholder = "Inserisci il tuo cognome"
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaDatePicker(
                label = "Data di Nascita",
                value = uiState.dataNascita,
                onDataSelected = { viewModel.onDataNascitaChanged(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaTextField(
                label = "Username",
                value = uiState.username,
                onValueChange = { viewModel.onUsernameChanged(it) },
                leadingIconRes = R.drawable.ic_chiocciola,
                placeholder = "Inserisci il tuo username"
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaTextField(
                label = "Password",
                value = uiState.password,
                isPassword = true,
                onValueChange = { viewModel.onPasswordChanged(it) },
                leadingIconRes = R.drawable.ic_scudo_password,
                placeholder = "Inserisci la password",
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaTextField(
                label = "Conferma Password",
                value = uiState.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                leadingIconRes = R.drawable.ic_scudo_password,
                placeholder = "Conferma la password",
                isPassword = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaPrivacyDisclaimerBox(
                text = stringResource(R.string.privacy_disclaimer)
            )
            Spacer(modifier = Modifier.height(24.dp))
            DermaButton(
                text = stringResource(R.string.btn_register_create_account),
                onClick = { viewModel.onRegisterClick() }
            )
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

