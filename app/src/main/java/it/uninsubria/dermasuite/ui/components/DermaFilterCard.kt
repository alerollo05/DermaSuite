package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.viewmodels.paziente.HistoryPasiPageViewModel
import it.uninsubria.dermasuite.viewmodels.paziente.TimeFilter
import kotlin.collections.chunked
import kotlin.collections.forEach

@Composable
fun DermaFilterCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    currentFilter : TimeFilter,
    viewModel: HistoryPasiPageViewModel
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ){
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp)
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        //Creazione della tabella dei filtri
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dividiamo i 4 filtri in due gruppi da due filtri
            val filterRows = TimeFilter.entries.chunked(2)

            filterRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { filter ->
                        FilterChip(
                            modifier = Modifier.weight(1f).height(60.dp),
                            selected = currentFilter == filter,
                            onClick = { viewModel.applyFilter(filter) }, //diciamo al viewModel di applicare il filtro selezionato
                            label = { Text(stringResource(filter.displayName)) },
                            colors = FilterChipDefaults.filterChipColors(
                                labelColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = MaterialTheme.colorScheme.primary,
                                borderWidth = 1.dp,
                                enabled = false,
                                selected = false
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_date_picker_calendar),
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp).size(20.dp)
                                )
                            }
                        )
                    }
                }

            }
        }
    }
}