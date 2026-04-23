package it.uninsubria.dermasuite.ui.screens.paziente
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaHeading
import it.uninsubria.dermasuite.ui.components.DermaTopBar
@Composable
fun DermaPASIScreen(
    onBack: () -> Unit,
){
    Scaffold(
        topBar = {
            DermaTopBar(
                title = "Calcolo PASI",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { padding ->

        DermaColumnScreen(innerPadding = padding) {
            DermaHeading(
                titolo = "Calcolo PASI",
                sottotitolo = "Descrizione del pasi",
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}