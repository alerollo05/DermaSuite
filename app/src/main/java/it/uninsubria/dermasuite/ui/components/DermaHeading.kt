package it.uninsubria.dermasuite.ui.components

import android.R.attr.bottom
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun DermaHeading(
    titolo: String,
    sottotitolo: String,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier) {
        Text(
            text = titolo,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary, // Blu scuro Dermasuite
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = sottotitolo,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DermaHeadingPreview() {
    it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme() {
        DermaHeading("Heading", "Subheading")    }
}