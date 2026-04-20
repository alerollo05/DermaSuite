package it.uninsubria.dermasuite.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaHeading
import it.uninsubria.dermasuite.ui.components.DermaTextField
import it.uninsubria.dermasuite.ui.components.DermaTopBar
import it.uninsubria.dermasuite.ui.theme.Placeholder
import it.uninsubria.dermasuite.viewmodels.LoginPageViewModel


@Composable
fun LoginPageScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToStart: () -> Unit,
    //onLoginSuccess: () -> Unit, // Questo ti porta alla dashboard una volta verificato l' accesso
    viewModel: LoginPageViewModel = viewModel()
){
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            DermaTopBar(
                title = "DermaSuite",
                showBackButton = false,
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
    ){ padding ->
        DermaColumnScreen(innerPadding = padding, verticalArrangement = Arrangement.Top) {
            Icon(
                painter = painterResource(id = R.drawable.ic_lucchetto),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.fillMaxWidth(), // 1. Prende tutta la larghezza
                horizontalAlignment = Alignment.CenterHorizontally // 2. Centra i componenti Text
            ) {
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center // 3. Centra il testo internamente
                )
                Text(
                    text = "Access your clinical dashboard and diagnostic suite with secure credential",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center, // 3. Centra il testo internamente
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            DermaTextField(
                label = "Username",
                value = uiState.username,
                onValueChange = { viewModel.onUsernameChanged(it)},
                placeholder = "Inserisci username"
           )
            Spacer(modifier = Modifier.height(40.dp))

            DermaTextField(
                label = "Password",
                value = uiState.password,
                onValueChange = {viewModel.onPasswordChanged(it)},
                placeholder = "Inserisci password"
            )


        }


    }




}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme() {
        LoginPageScreen({},{})
    }
}