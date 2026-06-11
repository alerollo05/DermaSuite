package it.uninsubria.dermasuite.ui.screens.paziente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaBmiHistoryChart
import it.uninsubria.dermasuite.ui.components.DermaBmiHistoryList
import it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaFilterCard
import it.uninsubria.dermasuite.ui.components.DermaHeading
import it.uninsubria.dermasuite.ui.components.DermaIsLoading
import it.uninsubria.dermasuite.ui.components.DermaResultCard
import it.uninsubria.dermasuite.ui.components.DermaTopBar
import it.uninsubria.dermasuite.viewmodels.paziente.HistoryBmiPageViewModel

@Composable
fun DermaBMIHistoryScreen(
    onBack: () -> Unit,
    navController: NavController,
    onNavigateToProfileP: () -> Unit,
    onNavigateToDashBoardPaziente: () -> Unit,
    viewModel: HistoryBmiPageViewModel = viewModel()
){

    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val currentFilter by viewModel.currentFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val records by viewModel.uiState.collectAsState()
    val latestRecord by viewModel.latestRecord.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { uid ->
            viewModel.getHistoryBMIList(
                UserId = uid,
                onSuccess = {},
                onError = {}
            )
        }
    }

    val listaIcone = listOf(
        BottomBarAction(
            stringResource(R.string.menu_home), R.drawable.ic_home,
            "dashboard_screen_paziente",
            {onNavigateToDashBoardPaziente()}),
        BottomBarAction(
            stringResource(R.string.menu_history), R.drawable.ic_history,
            "bmi_history_screen",
            {}),
        BottomBarAction(
            stringResource(R.string.menu_profile), R.drawable.ic_profile,
            "profile_screen_paziente",
            {onNavigateToProfileP()})
    )

    Scaffold(
        topBar = {
            DermaTopBar(
                title = stringResource(R.string.top_bar_bmi_history),
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
                titolo = stringResource(R.string.history_bmi_title),
                sottotitolo = stringResource(R.string.history_bmi_subtitle),
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            if(isLoading){
                DermaIsLoading(modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp))
            }else if (records.isEmpty()) {
                Text(text = stringResource(R.string.no_records_found_bmi), modifier = Modifier.padding(16.dp))
            } else {
                latestRecord?.let { latest ->
                    DermaResultCard(
                        title = stringResource(R.string.title_bmi_current_situation),
                        result = latest.BmiTot,
                        max = 100,
                        severity = latest.Category,
                        isBMI = true,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                DermaFilterCard(
                    title = stringResource(R.string.title_filter_bmi),
                    subtitle = stringResource(R.string.description_filter_bmi),
                    modifier = Modifier.padding(16.dp),
                    currentFilter = currentFilter,
                    onFilterSelected = { nuovoFiltro ->
                        viewModel.applyFilter(nuovoFiltro)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                DermaBmiHistoryChart(
                    records = records,
                    timeFilter = currentFilter
                )
                Spacer(modifier = Modifier.height(16.dp))

                DermaBmiHistoryList(
                    title = stringResource(R.string.title_list_history),
                    records = records,
                    timeFilter = currentFilter,
                    username = currentUser?.displayName,
                    viewModel = viewModel
                )
            }
        }

    }
}