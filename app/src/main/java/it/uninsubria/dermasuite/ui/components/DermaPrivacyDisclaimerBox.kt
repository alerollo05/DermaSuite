package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.R

@Composable
fun DermaPrivacyDisclaimerBox(
    text: String
){
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7F9)),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ){
        Row(modifier = Modifier.padding(16.dp)){
            Icon(
                painter = painterResource(id = R.drawable.ic_privacy_shield),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF00ACC1)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF006064)
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DermaPrivacyDisclaimerBoxPreview() {
    it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme() {
        DermaPrivacyDisclaimerBox("Questo è un disclaimer")
    }
}