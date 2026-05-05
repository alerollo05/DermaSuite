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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.*
import it.uninsubria.dermasuite.viewmodels.paziente.HistoryPasiPageViewModel
import it.uninsubria.dermasuite.viewmodels.paziente.pdfGenerator
import kotlinx.coroutines.launch

@Composable
fun DermaPASIHistoryScreen(
    onBack: () -> Unit,
    navController: NavController,
    onNavigateToChatP: () -> Unit,
    onNavigateToDashBoardPaziente: () -> Unit,
    onNavigateToProfileP: () -> Unit,
    viewModel: HistoryPasiPageViewModel = viewModel()
){
    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val currentFilter by viewModel.currentFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val records by viewModel.uiState.collectAsState()
    val userData by viewModel.userData.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val title = stringResource(R.string.title_PDF_PASI)
    val username = if (userData != null) "${userData?.nome} ${userData?.cognome}" else currentUser?.displayName

    // Launcher per la richiesta dei permessi (necessario per Android < 10)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            coroutineScope.launch {
                pdfGenerator(title, context, records, currentFilter, username)
            }
        } else {
            Toast.makeText(context, context.getString(R.string.stringa_errore_download), Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            viewModel.getHistoryDB(uid)
        }
    }

    val listaIcone = listOf(
        BottomBarAction(stringResource(R.string.menu_home), R.drawable.ic_home, "dashboard_screen_paziente", {onNavigateToDashBoardPaziente()}),
        BottomBarAction(stringResource(R.string.menu_chat), R.drawable.ic_chat, "chat_screen_paziente", {onNavigateToChatP()}),
        BottomBarAction(stringResource(R.string.menu_history), R.drawable.ic_history, "pasi_history_screen", {}),
        BottomBarAction(stringResource(R.string.menu_profile), R.drawable.ic_profile, "profile_screen_paziente", {onNavigateToProfileP()})
    )

    Scaffold(
        topBar = {
            DermaTopBar(
                title = "DermaSuite",
                showBackButton = true,
                onBackClick = onBack,
                actions = {
                    IconButton(
                        onClick = {
                            when {
                                // Caso 1: Android 10+ (Scoped Storage non richiede WRITE_EXTERNAL_STORAGE per Download)
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                                    coroutineScope.launch {
                                        pdfGenerator(title, context, records, currentFilter, username)
                                    }
                                }
                                // Caso 2: Android < 10 ma permesso già concesso
                                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                                    coroutineScope.launch {
                                        pdfGenerator(title, context, records, currentFilter, username)
                                    }
                                }
                                // Caso 3: Android < 10 e dobbiamo chiedere il permesso
                                else -> {
                                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                }
                            }
                        }
                    ){
                        Icon(
                            painter = painterResource(R.drawable.ic_download),
                            contentDescription = "Download PDF",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        bottomBar = { DermaBottomBar(navController = navController, actions = listaIcone) }
    ){ padding ->
        DermaColumnScreen(innerPadding = padding){
            DermaHeading(
                titolo = stringResource(R.string.title_HistoryPASI),
                sottotitolo = stringResource(R.string.description_Hist_PASI),
                modifier = Modifier.padding(16.dp)
            )

            DermaFilterCard(
                title = stringResource(R.string.title_Filter),
                subtitle = stringResource(R.string.description_filter),
                modifier = Modifier.padding(16.dp),
                currentFilter = currentFilter,
                viewModel = viewModel
            )

            if(isLoading){
                DermaIsLoading(modifier = Modifier.fillMaxWidth().height(250.dp))
            } else if(records.isEmpty()){
                Text(text = stringResource(R.string.no_records_found), color = MaterialTheme.colorScheme.primary)
            } else {
                DermaPasiHistoryChart(records = records, timeFilter = currentFilter)
                Spacer(modifier = Modifier.height(16.dp))

                DermaPasiHistoryList(
                    title = title,
                    records = records,
                    timeFilter = currentFilter,
                    username = username
                )
            }
        }
    }
}