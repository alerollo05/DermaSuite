package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import it.uninsubria.dermasuite.viewmodels.medico.DashboardPageMedicoViewModel

@Composable
fun DermaListaPazienti(
    viewModel: DashboardPageMedicoViewModel,
    modifier: Modifier = Modifier
){
        LazyColumn(
            modifier = modifier.fillMaxWidth()
        ) {
            items(viewModel.filteredPatients) { paziente ->
                DermaCardPaziente(paziente = paziente, onClick = { /* TODO() */ })
            }
        }
}