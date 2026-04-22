package it.uninsubria.dermasuite.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.dermasuite.firebase.AuthRepository

@Composable
fun DermaCheckRoleAndNavigateScreen(navController: NavController) {
    val repository = AuthRepository()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // LaunchedEffect viene eseguito una sola volta all'avvio della schermata
    LaunchedEffect(Unit) {
        if (currentUser != null) {
            // Usiamo il tuo metodo getUserData per recuperare l'oggetto DermaUser
            val userModel = repository.getUserData(currentUser.uid)

            if (userModel != null) {
                // Decidiamo la rotta in base all'accountType salvato nel DB
                val targetRoute = if (userModel.role == "Medico") {
                    "dashboard_screen_medico"
                } else {
                    "dashboard_screen_paziente"
                }

                // Navighiamo verso la dashboard e puliamo il backstack
                navController.navigate(targetRoute) {
                    popUpTo("check_role_screen") { inclusive = true }
                }
            } else {
                // Se non troviamo i dati su Firestore, forse c'è un errore: torniamo alla start
                navController.navigate("start_screen") {
                    popUpTo(0)
                }
            }
        } else {
            // Se per qualche motivo l'utente non è loggato, torna alla start
            navController.navigate("start_screen") {
                popUpTo(0)
            }
        }
    }

    // Mentre il sistema "pensa", mostriamo un caricamento centrato
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp),
            color = Color(0xFF003366) // Usa il blu del tuo brand
        )
    }
}