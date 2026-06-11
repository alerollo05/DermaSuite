package it.uninsubria.dermasuite.ui.screens.paziente

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
    onNavigateToProfileP: () -> Unit,
    onNavigateToBsaHistory: () -> Unit,
    navController: NavController,
    viewModel: BsaPageViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current


    val peso by viewModel.peso.collectAsState()
    val altezza by viewModel.altezza.collectAsState()
    val sesso by viewModel.sesso.collectAsState()
    val risultatoBsa by viewModel.risultatoBsa.collectAsState()
    val valutazione by viewModel.valutazione.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()


    val snackbarHostState = remember { SnackbarHostState() }
    val msgSuccess = stringResource(id = R.string.bsa_calc_saved_success)


    LaunchedEffect(Unit) {
        viewModel.saveSuccess.collect { success ->
            if (success) {
                snackbarHostState.showSnackbar(
                    message = msgSuccess,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.errorMessage.collect { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(risultatoBsa) {
        if (risultatoBsa != null) {
            kotlinx.coroutines.delay(150)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    val listaIcone = listOf(
        BottomBarAction(stringResource(R.string.menu_home), R.drawable.ic_home, "dashboard_screen_paziente", { onBack() }),
        BottomBarAction(stringResource(R.string.menu_history), R.drawable.ic_history, "bmi_history_screen", { onNavigateToBsaHistory() }),
        BottomBarAction(stringResource(R.string.menu_profile), R.drawable.ic_profile, "profile_screen_paziente", { onNavigateToProfileP() })
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            DermaTopBar(
                title = stringResource(R.string.bsa_title),
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
                titolo = stringResource(R.string.bsa_heading_title),
                sottotitolo = stringResource(R.string.bsa_heading_subtitle),
                modifier = Modifier.padding(16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                )
            ) {
                Row(
                    modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)
                ) {

                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .fillMaxHeight()
                            .background(color = MaterialTheme.colorScheme.primary)
                    )


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = stringResource(R.string.bsa_card_title),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )


                        DermaTextField(
                            label = stringResource(R.string.bsa_label_height),
                            placeholder = stringResource(R.string.bsa_placeholder_height),
                            value = altezza,
                            onValueChange = { viewModel.onAltezzaChange(it) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DermaTextField(
                            label = stringResource(R.string.bsa_label_weight),
                            placeholder = stringResource(R.string.bsa_placeholder_weight),
                            value = peso,
                            onValueChange = { viewModel.onPesoChange(it) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        DermaButton(
                            text = if (isLoading) stringResource(R.string.bsa_btn_saving) else stringResource(R.string.bsa_btn_calculate),
                            onClick = { viewModel.calcolaBsa(context) },
                            enabled = viewModel.isCalcoloAbilitato(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(20.dp))

            // Visualizzazione Risultato
            if (risultatoBsa != null) {
                val risultatoNumerico = risultatoBsa.toString().toDoubleOrNull() ?: 0.0

                DermaResultCard(
                    title = stringResource(R.string.bsa_result_title),
                    result = risultatoNumerico,
                    severity = valutazione,
                    max = 3
                )
            }
        }
    }
}