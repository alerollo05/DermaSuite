package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.uninsubria.dermasuite.viewmodels.paziente.DistrettoCorpo

@Composable
fun DermaDistrictSelector(
    label: String,
    selectedDistrict: DistrettoCorpo,
    chkComplete: (DistrettoCorpo) -> Boolean, //Devi fornirmi una funzione che accetta un distretto corpo e restituisce un bool
    onDistrictSelected: (DistrettoCorpo) -> Unit
){
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ){
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), //Fissiamo esattamente due colonne
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(275.dp)
            ) {
            items(DistrettoCorpo.values().size) { index ->
                val distretto = DistrettoCorpo.values()[index]
                DermaGridItem(
                    distretto = distretto,
                    iconRes = distretto.iconRes,
                    isSelected = selectedDistrict == distretto,
                    onSelect = { onDistrictSelected(distretto) },
                    isComplete = chkComplete(distretto),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}