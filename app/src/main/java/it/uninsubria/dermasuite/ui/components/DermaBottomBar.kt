package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// Modello per ogni pulsante della barra
data class BottomBarAction(
    val label: String,
    val iconRes: Int,      // ID del tuo SVG/Vector Drawable
    val route: String,     // La rotta verso cui punta
    val onClick: () -> Unit // L'azione da eseguire (passata dalla MainActivity)
)

@Composable
fun DermaBottomBar(
    navController: NavController,
    actions: List<BottomBarAction> // Lista dei pulsanti da visualizzare con le loro azioni
) {
    // Osserva la "pila" di navigazione. Ogni volta che la pagina cambia, questa riga si aggiorna.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // Estrae il nome (route) della pagina attualmente visibile sullo schermo.
    val currentRoute = navBackStackEntry?.destination?.route

    // Funzione composable di Jetpack Compose per creare la BottomBar con icone e label
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp // Aggiunge una leggera ombra alla BottomBar
    ) {
        // Ciclo su ogni elemento della lista actions passata come argomento
        actions.forEach { action ->
            // Verifica se la rotta del pulsante corrente è uguale a quella in cui si trova l'utente.
            // Se sono uguali, isSelected sarà 'true' e l'icona si illuminerà.
            val isSelected = currentRoute == action.route

            // Il singolo tasto cliccabile della barra
            NavigationBarItem(
                selected = isSelected,
                onClick = action.onClick,
                alwaysShowLabel = true, // Forza il testo a rimanere sempre visibile
                label = null, // Qua ci andrebbe il Text sotto l icona ma se lo metto qua viene evidenziata solo l icona e non il label sotto (Quando si è nello screen corrente)
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = action.iconRes),
                            contentDescription = action.label,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        // Inseriamo il testo qui dentro!
                        // Ora l'indicatore (il colore di sfondo dell'icon) si espanderà per includerlo.
                        Text(
                            text = action.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                // Colore di sfondo highlighting dell'icona quando viene cliccata
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}