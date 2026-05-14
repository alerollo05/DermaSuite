package it.uninsubria.dermasuite.ui.components

import androidx.compose.runtime.Composable
import it.uninsubria.dermasuite.viewmodels.medico.DashboardPageMedicoViewModel

@Composable
fun DermaListaPazienti(
    viewModel: DashboardPageMedicoViewModel
){
    viewModel.filteredPatients.forEach{ paziente ->
        DermaCardPaziente(paziente = paziente, onClick = {TODO()})
    }
}