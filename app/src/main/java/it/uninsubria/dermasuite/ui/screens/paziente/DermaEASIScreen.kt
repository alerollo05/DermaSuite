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
import it.uninsubria.dermasuite.model.DistrettoCorpo
import it.uninsubria.dermasuite.model.EasiDistrictState
import it.uninsubria.dermasuite.viewmodels.paziente.EasiPageViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DermaEASIScreen(
    onBack: () -> Unit,
    onNavigateToProfileP: () -> Unit,
    onNavigateToEasiHistory: () -> Unit,
    navController: NavController,
    viewModel: EasiPageViewModel = viewModel()
){
    val scrollState = rememberScrollState()
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
            stringResource(R.string.menu_home), R.drawable.ic_home,
            "dashboard_screen_paziente",
            {onBack()}),
        BottomBarAction(
            stringResource(R.string.menu_history), R.drawable.ic_history,
            "easi_history_screen",
            {onNavigateToEasiHistory()}),
        BottomBarAction(
            stringResource(R.string.menu_profile), R.drawable.ic_profile,
            "profile_screen_paziente",
            {onNavigateToProfileP()})
    )

    Scaffold(
        topBar = {
            DermaTopBar(
                title = stringResource(R.string.easi_title),
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


        val currentData = viewModel.districtValues[viewModel.currentDistrict] ?: EasiDistrictState()

        DermaColumnScreen(
            innerPadding = padding,
            scrollState = scrollState
        ) {
            DermaHeading(
                titolo = stringResource(R.string.easi_heading_title),
                sottotitolo = stringResource(R.string.easi_heading_subtitle),
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
                title = stringResource(R.string.easi_param_eritema_title),
                subtitle = stringResource(R.string.easi_param_eritema_subtitle),
                IconRes = R.drawable.ic_eritema, // Adatta l'icona
                selectedValue = currentData.eritema,
                maxValue = 3,
                onValueChange = { viewModel.updateDistrictParameters(eritema = it) },
            )
            Spacer(modifier = Modifier.height(16.dp))

            DermaSelectorParameterCard(
                title = stringResource(R.string.easi_param_edema_title),
                subtitle = stringResource(R.string.easi_param_edema_subtitle),
                IconRes = R.drawable.ic_indurimento, // Adatta l'icona
                maxValue = 3,
                selectedValue = currentData.edemaPapulizzazione,
                onValueChange = { viewModel.updateDistrictParameters(edemaPapulizzazione = it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            DermaSelectorParameterCard(
                title = stringResource(R.string.easi_param_escoriazione_title),
                subtitle = stringResource(R.string.easi_param_escoriazione_subtitle),
                IconRes = R.drawable.ic_desquamazione, // Adatta l'icona
                maxValue = 3,
                selectedValue = currentData.escoriazione,
                onValueChange = { viewModel.updateDistrictParameters(escoriazione = it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            DermaSelectorParameterCard(
                title = stringResource(R.string.easi_param_lichenificazione_title),
                subtitle = stringResource(R.string.easi_param_lichenificazione_subtitle),
                IconRes = R.drawable.ic_lichenificazione, // Adatta l'icona
                maxValue = 3,
                selectedValue = currentData.lichenificazione,
                onValueChange = { viewModel.updateDistrictParameters(lichenificazione = it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            DermaSelectorParameterCard(
                title = stringResource(R.string.area),
                subtitle = stringResource(R.string.desc_area),
                IconRes = R.drawable.ic_area_parametri,
                maxValue = 6,
                selectedValue = currentData.percentualeArea,
                onValueChange = { viewModel.updateDistrictParameters(percentualeArea = it) }
            )
            Spacer(modifier = Modifier.height(20.dp))


            val succMess = stringResource(R.string.snak_success)
            val errMess = stringResource(R.string.snak_error)
            val completaDistretto = stringResource(R.string.complete_district)
            val completaAllDistretti = stringResource(R.string.complete_all_districts)
            val distrettiNomi = DistrettoCorpo.entries.associateWith { stringResource(it.nameResId) }

            DermaButton(
                text = stringResource(R.string.easi_btn_calculate),
                onClick = {
                    if (viewModel.abilitaCalcolo()) {
                        viewModel.calculateTotalEasiAndSave(
                            onSucces = {
                                scope.launch {
                                    snakBarHostState.showSnackbar(
                                        message = succMess,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            onError = {
                                scope.launch {
                                    snakBarHostState.showSnackbar(
                                        message = errMess,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    } else {
                        val distrettoMancante =
                            DistrettoCorpo.entries.find { !viewModel.isDistrictComplete(it) }
                        val messaggio = if (distrettoMancante != null) {
                            val distrettoDaCompletare = distrettiNomi[distrettoMancante] ?: ""
                            "$completaDistretto $distrettoDaCompletare"
                        } else {
                            completaAllDistretti
                        }
                        scope.launch {
                            snakBarHostState.showSnackbar(
                                messaggio,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                enabled = viewModel.abilitaCalcolo()
            )
            Spacer(modifier = Modifier.height(20.dp))

            val severityLabel = when(viewModel.serverityClass){
                "LEVEL_LOW" -> stringResource(R.string.easi_severity_low)
                "LEVEL_MODERATE" -> stringResource(R.string.easi_severity_moderate)
                "LEVEL_SEVERE" -> stringResource(R.string.easi_severity_severe)
                else -> ""
            }

            if(viewModel.showResult){
                DermaResultCard(
                    title = stringResource(R.string.easi_result_title),
                    result = viewModel.totalEasiResult,
                    max = 72,
                    severity = severityLabel
                )
            }
        }
    }
}