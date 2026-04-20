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

@Composable //specifichiamo che questa funzione serve a disegnare un componente UI
fun DermaButton(
    text: String,            // Il testo che apparirà sul bottone
    onClick: () -> Unit,     // Cosa succede quando viene cliccato (una funzione passata come dato)
    modifier: Modifier = Modifier, // Per posizionarlo dall'esterno
    enabled: Boolean = true  // Se il bottone è attivo o "grigetto" (es. se i campi non sono compilati)
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth() // Si allarga per tutta la larghezza disponibile (come nei tuoi design)
            .height(56.dp),  // Forza un'altezza precisa di 56 "density pixels" per farlo sembrare moderno e "cliccabile"
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
                style = MaterialTheme.typography.labelLarge, // Quello che abbiamo impostato in Plus Jakarta nel file Type.kt
                color = Color.White,
            )
            Spacer(modifier = Modifier.width(10.dp)) // Questo spazio serve a separare il testo dall'icona
            Icon(
                painter = painterResource(R.drawable.ic_arrow),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp).align(Alignment.Bottom)
            )
        }

    }
}
@Preview(showBackground = true)
@Composable
fun DermaButtonPreview() {
    it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme() {
        DermaButton("Login", onClick = {})
    }
}