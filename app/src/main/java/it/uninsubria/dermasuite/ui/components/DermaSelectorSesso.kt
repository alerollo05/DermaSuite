package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.R

@Composable
fun DermaSelectorSesso(
    label: String = stringResource(R.string.select_sex),
    selectedSesso: String,
    onSessoSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedSesso == "Maschio",
                onClick = { onSessoSelected("Maschio") }
            )
            Text(text = stringResource(R.string.male))

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = selectedSesso == "Femmina",
                onClick = { onSessoSelected("Femmina") }
            )
            Text(text = stringResource(R.string.female))
        }
    }
}
