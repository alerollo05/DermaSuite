package it.uninsubria.dermasuite.ui.screens.paziente

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.*
import it.uninsubria.dermasuite.viewmodels.paziente.HistoryBsaPageViewModel
import it.uninsubria.dermasuite.model.bsaPdfGenerator
import kotlinx.coroutines.launch

@Composable
fun DermaBSAHistoryScreen(
    onBack: () -> Unit,
    navController: NavController,
    onNavigateToChatP: () -> Unit,
    onNavigateToDashBoardPaziente: () -> Unit,
    onNavigateToProfileP: () -> Unit,
    viewModel: HistoryBsaPageViewModel = viewModel()
){
    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val currentFilter by viewModel.currentFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val records by viewModel.uiState.collectAsState()
    val userData by viewModel.userData.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val title = "Report Storico BSA" // O stringResource(R.string.title_PDF_BSA)
    val username = if (userData != null) "${userData?.nome} ${userData?.cognome}" else currentUser?.displayName

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            coroutineScope.launch { bsaPdfGenerator(title, context, records, currentFilter, username) }
        } else {
            Toast.makeText(context, R.string.stringa_errore_download, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid -> viewModel.getHistoryDB(uid) }
    }

    val listaIcone = listOf(
        BottomBarAction("HOME", R.drawable.ic_home, "dashboard_screen_paziente", {onNavigateToDashBoardPaziente()}),
        BottomBarAction("CHAT", R.drawable.ic_chat, "chat_screen_paziente", {onNavigateToChatP()}),
        BottomBarAction("HISTORY", R.drawable.ic_history, "bsa_history_screen", {}),
        BottomBarAction("PROFILE", R.drawable.ic_profile, "profile_screen_paziente", {onNavigateToProfileP()})
    )

    Scaffold(
        topBar = {
            DermaTopBar(
                title = "Storico BSA",
                showBackButton = true,
                onBackClick = onBack,
                actions = {
                    IconButton(
                        onClick = {
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                                    coroutineScope.launch { bsaPdfGenerator(title, context, records, currentFilter, username) }
                                }
                                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                                    coroutineScope.launch { bsaPdfGenerator(title, context, records, currentFilter, username) }
                                }
                                else -> requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            }
                        }
                    ){
                        Icon(painter = painterResource(R.drawable.ic_download), contentDescription = "Download PDF", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        bottomBar = { DermaBottomBar(navController = navController, actions = listaIcone) }
    ){ padding ->
        DermaColumnScreen(innerPadding = padding){
            DermaHeading(
                titolo = "Storico Calcoli BSA",
                sottotitolo = "Monitora l'andamento della tua superficie corporea.",
                modifier = Modifier.padding(16.dp)
            )

            DermaFilterCard(
                title = "Filtro Temporale",
                subtitle = "Seleziona il periodo da visualizzare",
                modifier = Modifier.padding(16.dp),
                currentFilter = currentFilter,
                onFilterSelected = { nuovoFiltro -> viewModel.applyFilter(nuovoFiltro) }
            )

            if(isLoading){
                DermaIsLoading(modifier = Modifier.fillMaxWidth().height(250.dp))
            } else if(records.isEmpty()){
                Text(text = "Nessun calcolo trovato in questo periodo.", color = MaterialTheme.colorScheme.primary)
            } else {
                DermaBsaHistoryChart(records = records, timeFilter = currentFilter)
                Spacer(modifier = Modifier.height(16.dp))
                DermaBsaHistoryList(title = title, timeFilter = currentFilter,records = records, username = username, viewModel = viewModel, userId = currentUser?.uid)
            }
        }
    }
}