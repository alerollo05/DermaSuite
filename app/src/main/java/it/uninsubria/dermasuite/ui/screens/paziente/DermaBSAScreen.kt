package it.uninsubria.dermasuite.ui.screens.paziente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.*
import it.uninsubria.dermasuite.viewmodels.paziente.BsaPageViewModel

@Composable
fun DermaBSAScreen(
    onBack: () -> Unit,
    onNavigateToChatP: () -> Unit,
    onNavigateToProfileP: () -> Unit,
    onNavigateToBsaHistory: () -> Unit,
    navController: NavController,
    viewModel: BsaPageViewModel = viewModel()
) {
    // Osserviamo gli stati dal ViewModel
    val peso by viewModel.peso.collectAsState()
    val altezza by viewModel.altezza.collectAsState()
    val sesso by viewModel.sesso.collectAsState()
    val risultatoBsa by viewModel.risultatoBsa.collectAsState()
    val valutazione by viewModel.valutazione.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // --- LOGICA SNACKBAR ---
    val snackbarHostState = remember { SnackbarHostState() }

    // Ascoltiamo l'evento di successo dal ViewModel
    LaunchedEffect(Unit) {
        viewModel.saveSuccess.collect { success ->
            if (success) {
                snackbarHostState.showSnackbar(
                    message = "Calcolo salvato correttamente!",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    val listaIcone = listOf(
        BottomBarAction("HOME", R.drawable.ic_home, "dashboard_screen_paziente", { onBack() }),
        BottomBarAction("CHAT", R.drawable.ic_chat, "chat_screen_paziente", { onNavigateToChatP() }),
        BottomBarAction("HISTORY", R.drawable.ic_history, "bmi_history_screen", { onNavigateToBsaHistory() }),
        BottomBarAction("PROFILE", R.drawable.ic_profile, "profile_screen_paziente", { onNavigateToProfileP() })
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            DermaTopBar(
                title = "Calcolo BSA",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        bottomBar = {
            DermaBottomBar(
                navController = navController,
                actions = listaIcone
            )
        }
    ) { padding ->
        DermaColumnScreen(innerPadding = padding) {

            DermaHeading(
                titolo = "Calcola la tua Body Surface Area",
                sottotitolo = "Inserisci peso e altezza per calcolare la tua superficie corporea.",
                modifier = Modifier.padding(16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Input Altezza tramite componente personalizzato
                DermaTextField(
                    label = "Altezza (cm)",
                    placeholder = "Inserisci la tua altezza",
                    value = altezza,
                    onValueChange = { viewModel.onAltezzaChange(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input Peso tramite componente personalizzato
                DermaTextField(
                    label = "Peso (kg)",
                    placeholder = "Inserisci il tuo peso",
                    value = peso,
                    onValueChange = { viewModel.onPesoChange(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Selezione Sesso
                Text(
                    text = "Seleziona il Sesso",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = sesso == "Maschio",
                        onClick = { viewModel.onSessoChange("Maschio") }
                    )
                    Text("Maschio")

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = sesso == "Femmina",
                        onClick = { viewModel.onSessoChange("Femmina") }
                    )
                    Text("Femmina")
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Bottone personalizzato
                DermaButton(
                    text = if (isLoading) "SALVATAGGIO..." else "CALCOLA",
                    onClick = { viewModel.calcolaBsa() },
                    enabled = peso.isNotBlank() && altezza.isNotBlank() && !isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Visualizzazione Risultato (compare solo dopo il calcolo)
                if (risultatoBsa != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "La tua BSA:",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$risultatoBsa m²",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Valutazione: $valutazione",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}