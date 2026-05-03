package it.uninsubria.dermasuite.ui.screens.paziente

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaHeading
import it.uninsubria.dermasuite.ui.components.DermaPasiHistoryChart
import it.uninsubria.dermasuite.ui.components.DermaTopBar
import it.uninsubria.dermasuite.viewmodels.paziente.HistoryPasiPageViewModel

@Composable
fun DermaPASIHistoryScreen(
    onBack: () -> Unit,
    navController: NavController,
    onNavigateToChatP: () -> Unit,
    onNavigateToDashBoardPaziente: () -> Unit,
    onNavigateToProfileP: () -> Unit,
    viewModel: HistoryPasiPageViewModel = viewModel()
){
    //Andiamo a prendere l'istanza dell'utente corrente
    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser

    val isLoading by viewModel.isLoading.collectAsState() // Osserva il caricamento dei dati4

    // Avvia il caricamento quando la pagina si apre
    //Senza la gestione del caricamento compose è troppo veloce e firebase non riesce a restituire in tempo
    //i dati e quindi Vico se legge una lista di valori nulli fa crashare l'app
    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            viewModel.getHistoryDB(uid)
        }
    }

    val records by viewModel.uiState.collectAsState()

    val listaIcone = listOf(
        BottomBarAction(
            stringResource(R.string.menu_home), R.drawable.ic_home,
            "dashboard_screen_paziente",
            {onNavigateToDashBoardPaziente()}),
        BottomBarAction(
            stringResource(R.string.menu_chat), R.drawable.ic_chat,
            "chat_screen_paziente",
            {onNavigateToChatP()}),
        BottomBarAction(
            stringResource(R.string.menu_history), R.drawable.ic_history,
            "pasi_history_screen",
            {}),
        BottomBarAction(
            stringResource(R.string.menu_profile), R.drawable.ic_profile,
            "profile_screen_paziente",
            {onNavigateToProfileP()})
    )

    Scaffold(
        topBar = {
            DermaTopBar(
                title = "DermaSuite",
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
    ){
        padding ->
        DermaColumnScreen(innerPadding = padding){
            DermaHeading(
                titolo = stringResource(R.string.title_HistoryPASI),
                sottotitolo = stringResource(R.string.description_Hist_PASI),
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            if(isLoading){
                Box(
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF003366))
                }
            }else if(records.isEmpty()){
                // Se ha finito ma non ci sono dati, mostriamo un messaggio
                Text("Nessun calcolo PASI trovato.")
            }else{
                // Solo se ci sono dati carichiamo il grafico (Evita il crash!)
                DermaPasiHistoryChart(records = records)
            }
        }
    }

}