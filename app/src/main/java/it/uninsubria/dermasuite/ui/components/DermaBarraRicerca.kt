package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.viewmodels.medico.DashboardPageMedicoViewModel

@Composable
fun DermaBarraRicerca(
    viewModel: DashboardPageMedicoViewModel,
    //permettiamo di decidere se è a riga singola (default true)
    singleLine: Boolean = true,
    //per gestire il tasto della tastiera (es. Next o Done)
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
){
    OutlinedTextField(
        value = viewModel.searchQuery,
        onValueChange = { viewModel.onSearchingQuery(it) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = { Text(stringResource(R.string.search_placeholder), color = Color.Gray) },
        leadingIcon = { Icon(painter = painterResource(R.drawable.ic_search) , contentDescription = "Cerca", tint = Color.Gray) },
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF0F2F5),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent
        ),
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
    )
}