package it.uninsubria.dermasuite.ui.screens.paziente

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaButton
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaDistrictSelector
import it.uninsubria.dermasuite.ui.components.DermaHeading
import it.uninsubria.dermasuite.ui.components.DermaResultCard
import it.uninsubria.dermasuite.ui.components.DermaSelectorParameterCard
import it.uninsubria.dermasuite.ui.components.DermaTopBar
import it.uninsubria.dermasuite.viewmodels.paziente.DistrettoCorpo
import it.uninsubria.dermasuite.viewmodels.paziente.DistrictState
import it.uninsubria.dermasuite.viewmodels.paziente.PasiPageViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DermaPASIScreen(
    onBack: () -> Unit,
    onNavigateToChatP: () -> Unit,
    onNavigateToProfileP: () -> Unit,
    onNavigateToPasiHistory: () -> Unit,
    navController: NavController,
    viewModel: PasiPageViewModel = viewModel()
){
    //Andiamo a definire una variabile che tiene a memoria in che pos si trova lo scroll della pagina
    val scrollState = rememberScrollState()
    //Andiamo a creare una variabile per tenere traccia dello stato in cui si trova la snakbar
    val snakBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel.scrollTrigger){
        if(viewModel.scrollTrigger > 0){
            delay(150)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    val listaIcone = listOf(
        BottomBarAction(
            "HOME", R.drawable.ic_home,
            "dashboard_screen_paziente"
        ) { onBack() },
        BottomBarAction(
            "CHAT", R.drawable.ic_chat,
            "chat_screen_paziente"
        ) { onNavigateToChatP() },
        BottomBarAction(
            "HISTORY", R.drawable.ic_history,
            "pasi_history_screen"
        ) { onNavigateToPasiHistory() },
        BottomBarAction(
            "PROFILE", R.drawable.ic_profile,
            "profile_screen_paziente"
        ) { onNavigateToProfileP() }
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
        },
        snackbarHost = { SnackbarHost(hostState = snakBarHostState) }
    ) { padding ->
        //Andiamo a recuperare i dati relativi al distretto selezionato al momento
        val currentData = viewModel.districtValues[viewModel.currentDistrict] ?: DistrictState()

        DermaColumnScreen(
            innerPadding = padding,
            //Andiamo a collegare lo stato dello scroll alla column che contiene tutti i componenti della pagina
            scrollState = scrollState
        ) {
            DermaHeading(
                titolo = stringResource(R.string.pasi_title),
                sottotitolo = stringResource(R.string.pasi_description),
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaDistrictSelector(
              stringResource(R.string.distretto),
                selectedDistrict = viewModel.currentDistrict,
                onDistrictSelected = { viewModel.currentDistrict = it },
                chkComplete = { distretto -> viewModel.isDistrictComplete(distretto) }
            )
            DermaSelectorParameterCard(
                title = stringResource(R.string.eritema),
                subtitle = stringResource(R.string.desc_eritema),
                IconRes = R.drawable.ic_eritema,
                selectedValue = currentData.eritema,
                isArea = false,
                onValueChange = { viewModel.updateDistrictParameters(eritema = it) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaSelectorParameterCard(
                title = stringResource(R.string.indurimento),
                subtitle = stringResource(R.string.desc_indurimento),
                IconRes = R.drawable.ic_indurimento,
                isArea = false,
                selectedValue = currentData.indurimento,
                onValueChange = { viewModel.updateDistrictParameters(indurimento = it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaSelectorParameterCard(
                title = stringResource(R.string.desquamazione),
                subtitle = stringResource(R.string.desc_desquamazione),
                IconRes = R.drawable.ic_desquamazione,
                isArea = false,
                selectedValue = currentData.desquamazione,
                onValueChange = { viewModel.updateDistrictParameters(desquamazione = it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DermaSelectorParameterCard(
                title = stringResource(R.string.area),
                subtitle = stringResource(R.string.desc_area),
                IconRes = R.drawable.ic_area_parametri,
                isArea = true,
                selectedValue = currentData.percentualeArea,
                onValueChange = { viewModel.updateDistrictParameters(percentualeArea = it) }
            )
            Spacer(modifier = Modifier.height(20.dp))
            //Creiamo quattro variabili con all'interno la stringa perchè la string Resource puoi usarla solo
            //in componenti @Composable
            val succMess = stringResource(R.string.snak_success)
            val errMess = stringResource(R.string.snak_error)
            val completaDistretto = stringResource(R.string.complete_district)
            val completaAllDistretti = stringResource(R.string.complete_all_districts)
            // Pre-mappiamo i nomi dei distretti per evitare l'uso di context.getString nel click listener
            val distrettiNomi = DistrettoCorpo.entries.associateWith { stringResource(it.nameResId) }

            DermaButton(
                text = stringResource(R.string.btn_calculate),
                onClick = {
                    if(viewModel.abilitaCalcolo()){
                        viewModel.calculateTotalPasiAndSave(
                            onSucces = {
                                scope.launch{
                                    snakBarHostState.showSnackbar(
                                        message = succMess,
                                        duration = SnackbarDuration.Short
                                    )
                                }

                            },
                            onError = {
                                scope.launch{
                                    snakBarHostState.showSnackbar(
                                        message = errMess,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    }else{
                        //Andiamo ad identificare il primo distretto mancante che viene trovato e messo in una variabile
                        val distrettoMancante = DistrettoCorpo.entries.find { !viewModel.isDistrictComplete(it) }
                        val messaggio = if (distrettoMancante != null) {
                            val distrettoDaCompletare = distrettiNomi[distrettoMancante] ?: ""
                            "$completaDistretto $distrettoDaCompletare"
                        } else {
                            completaAllDistretti
                        }
                        scope.launch{
                            snakBarHostState.showSnackbar(
                                messaggio,
                                duration = SnackbarDuration.Short
                            )
                        }

                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            //Creaimo una funzione per andare a mappare il livello di severità cambiando in base alla lingua
            val severityLabel = when(viewModel.serverityClass){
                "LEVEL_LOW" -> stringResource(R.string.pasi_severity_low)
                "LEVEL_MODERATE" -> stringResource(R.string.pasi_severity_moderate)
                "LEVEL_SEVERE" -> stringResource(R.string.pasi_severity_severe)
                else -> ""
            }

            if(viewModel.showResult){
               DermaResultCard(
                    title = stringResource(R.string.ris_pasi),
                    result = viewModel.totalPasiResult,
                    max = 72,
                    severity = severityLabel
               )
            }
        }
    }
}