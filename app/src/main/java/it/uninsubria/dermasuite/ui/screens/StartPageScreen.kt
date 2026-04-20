package it.uninsubria.dermasuite.ui.screens

import androidx.compose.foundation.Image
import it.uninsubria.dermasuite.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.uninsubria.dermasuite.ui.components.DermaButton
import it.uninsubria.dermasuite.ui.theme.Placeholder
import it.uninsubria.dermasuite.viewmodels.StartPageViewModel

@Composable
fun StartPageScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: StartPageViewModel = viewModel() // Iniezione automatica
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo e Titolo
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground), // Questo è il logo
                contentDescription = "Logo DermaSuite",
                modifier = Modifier.size(110.dp)
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            // Aggiunge 8dp di spazio verticale
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.subtitle_home),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = Placeholder
            )
        }
        // --- SPAZIO TRA SOTTOTITOLO E IMMAGINE ---
        // Riduci questo valore per avvicinare l'immagine al testo
        Spacer(modifier = Modifier.height(32.dp))

        // Immagine centrale (Laboratorio)
        Image(
            painter = painterResource(id = R.drawable.img_doc_home),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(32.dp)),
            contentScale = ContentScale.Crop
        )

        // --- SPAZIO TRA IMMAGINE E BOTTONI ---
        // Riduci questo valore per avvicinare i bottoni all'immagine
        //Spacer(modifier = Modifier.height(10.dp))


        // Bottoni di azione
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DermaButton(stringResource(R.string.btn_home_login),onNavigateToLogin)

            TextButton(onClick = onNavigateToRegister) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.btn_home_register),
                        style = MaterialTheme.typography.bodyLarge, // Usa il tuo stile da Type.kt
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    // Aggiunge un piccolo spazio tra il testo e la freccia
                    Spacer(modifier = Modifier.width(8.dp))

                    // Se hai un tuo file vettoriale in drawable
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                }
            }

            Spacer(modifier = Modifier.height(32.dp))

        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartPagePreview() {
    it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme() {
        StartPageScreen({}, {})
    }
}