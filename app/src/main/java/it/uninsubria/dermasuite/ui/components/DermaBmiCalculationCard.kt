package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.viewmodels.paziente.BmiPageViewModel
import kotlinx.coroutines.launch

@Composable

fun DermaBMICalculationCard (
    title : String,
    viewModel: BmiPageViewModel = viewModel(),
    snackBarHostState: SnackbarHostState,
) {

    val uiState = viewModel.uiState

    // Generiamo i range di valori
    val heightOptions = (110..220).toList()
    val weightOptions = (40..200).toList()

    //Stringhe snackbar
    val stringaErr = stringResource(R.string.snak_bmi_errore)
    val stringaSucc = stringResource(R.string.snak_bmi_successo)

    val scope = rememberCoroutineScope()

    val context  = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
        )
    ){
        Row(
            modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)
        ){
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(color = MaterialTheme.colorScheme.primary)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))

                DermaSelectPicker(
                    label = stringResource(R.string.label_altezza),
                    selectedValue = uiState.height,
                    onValueSelected = { viewModel.onHeightChanged(it)},
                    options = heightOptions,
                    leadingIconRes = R.drawable.ic_height,
                    placeholder = stringResource(R.string.placeholder_altezza),
                    unitaMisura = stringResource(R.string.unità_misura_altezza)
                )
                Spacer(modifier = Modifier.height(16.dp))
                DermaSelectPicker(
                    label = stringResource(R.string.label_peso),
                    selectedValue = uiState.weight,
                    onValueSelected = { viewModel.onWeightChanged(it)},//In questo modo la UI si accorge del cambiamento dello stato delle variabili
                    options = weightOptions,
                    leadingIconRes = R.drawable.ic_weight,
                    placeholder = stringResource(R.string.placeholder_peso),
                    unitaMisura = stringResource(R.string.unità_misura_peso)
                )
                Spacer(modifier = Modifier.height(16.dp))

                DermaButton(
                    text = stringResource(R.string.button_bmi_calculate),
                    onClick = {
                        if(viewModel.isCalcoloAbilitato()) {
                            viewModel.calcolaBMIAndSave(
                                context = context,
                                onSuccess = {
                                    scope.launch{
                                        snackBarHostState.showSnackbar(
                                            message = stringaSucc,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                },
                                onError = {
                                    scope.launch{
                                        snackBarHostState.showSnackbar(
                                            message = stringaErr,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                })
                        }else{
                            scope.launch{
                                snackBarHostState.showSnackbar(
                                    message = stringaErr,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    },
                    enabled = viewModel.isCalcoloAbilitato(),
                )
            }
        }
    }
}
