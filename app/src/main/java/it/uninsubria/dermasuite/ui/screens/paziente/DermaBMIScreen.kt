package it.uninsubria.dermasuite.ui.screens.paziente

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaBMICalculationCard
import it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaHeading
import it.uninsubria.dermasuite.ui.components.DermaResultCard
import it.uninsubria.dermasuite.ui.components.DermaTopBar
import it.uninsubria.dermasuite.viewmodels.paziente.BmiPageViewModel
import it.uninsubria.dermasuite.viewmodels.paziente.EasiPageViewModel
import kotlinx.coroutines.delay

@Composable
fun DermaBMIScreen(
    onBack: () -> Unit,
    onNavigateToProfileP: () -> Unit,
    onNavigateToBmiHistory: () -> Unit,
    navController: NavController,
    viewModel: BmiPageViewModel = viewModel()

){
    val snakBarHostState = remember { SnackbarHostState() }

    val scrollState = rememberScrollState()

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
            "bmi_history_screen",
            {onNavigateToBmiHistory()}),
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
        },
        snackbarHost = {
            SnackbarHost(hostState = snakBarHostState)
        }
    ) { padding ->
        DermaColumnScreen(
            innerPadding = padding,
            //Collego lo stato dello scroll alla column che contiene tutti i componenti della pagina
            scrollState = scrollState) {
            DermaHeading(
                titolo = stringResource(R.string.title_bmi_page),
                sottotitolo = stringResource(R.string.description_bmi_page),
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            DermaBMICalculationCard(
                title = stringResource(R.string.title_bmi_card),
                viewModel = viewModel,
                snackBarHostState = snakBarHostState
            )

            if(viewModel.showResult){
                Spacer(modifier = Modifier.height(16.dp))
                DermaResultCard(
                    title = stringResource(R.string.title_bmi_result),
                    result = viewModel.uiState.calculatedBMI,
                    max = 100,
                    severity = viewModel.uiState.calculatedCategory,
                    isBMI = true
                )
            }
        }
    }
}