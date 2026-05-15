package it.uninsubria.dermasuite.ui.screens.paziente

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DermaBSAScreen(
    onBack: () -> Unit,
    onNavigateToChatP: () -> Unit,
    onNavigateToProfileP: () -> Unit,
    onNavigateToBsaHistory: () -> Unit,
    navController: NavController,
    viewModel: BsaPageViewModel = viewModel()
) {
    val scrollState = rememberScrollState() // Stato dello scroll


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

    // Ascoltiamo i messaggi di errore dal ViewModel
    LaunchedEffect(Unit) {
        viewModel.errorMessage.collect { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short // O SnackbarDuration.Long se vuoi che duri di più
            )
        }
    }

    // Effetto per lo scroll automatico quando compare il risultato
    LaunchedEffect(risultatoBsa) {
        if (risultatoBsa != null) {
            // Un piccolo delay permette a Compose di renderizzare la card
            // prima di calcolare il nuovo punto massimo di scroll
            kotlinx.coroutines.delay(150)
            scrollState.animateScrollTo(scrollState.maxValue)
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
        DermaColumnScreen(innerPadding = padding, scrollState = scrollState) {

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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input Peso tramite componente personalizzato
                DermaTextField(
                    label = "Peso (kg)",
                    placeholder = "Inserisci il tuo peso",
                    value = peso,
                    onValueChange = { viewModel.onPesoChange(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Bottone personalizzato
                DermaButton(
                    text = if (isLoading) "SALVATAGGIO..." else "CALCOLA",
                    onClick = { viewModel.calcolaBsa() },
                    enabled = peso.isNotBlank() && altezza.isNotBlank() && !isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Visualizzazione Risultato (compare solo dopo il calcolo)
                // Visualizzazione Risultato (compare solo dopo il calcolo)
                if (risultatoBsa != null) {
                    // Convertiamo il risultato in Double invece che in Float
                    val risultatoNumerico = risultatoBsa.toString().toDoubleOrNull() ?: 0.0

                    DermaResultCard(
                        title = "La tua BSA",
                        result = risultatoNumerico,
                        severity = valutazione,
                        max = 3 // Una BSA raramente supera i 3 m².
                    )
                }
            }
        }
    }
}