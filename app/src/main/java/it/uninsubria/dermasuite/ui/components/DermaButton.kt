package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.R

@Composable
fun DermaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        enabled = enabled
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
        ){
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                painter = painterResource(R.drawable.ic_arrow),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp).align(Alignment.Bottom)
            )
        }

    }
}
