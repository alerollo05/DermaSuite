package it.uninsubria.dermasuite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import it.uninsubria.dermasuite.ui.screens.DermaCheckRoleAndNavigateScreen
import it.uninsubria.dermasuite.ui.screens.DermaDashBoardMedicoScreen
import it.uninsubria.dermasuite.ui.screens.DermaDashBoardPazienteScreen
import it.uninsubria.dermasuite.ui.screens.LoginPageScreen
import it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme
import it.uninsubria.dermasuite.viewmodels.StartPageViewModel
import it.uninsubria.dermasuite.ui.screens.StartPageScreen
import it.uninsubria.dermasuite.ui.screens.DermaRegisterPageScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Verifica se l'utente è già loggato tramite Firebase
        val currentUser = Firebase.auth.currentUser

        // Decidi la rotta di partenza
        // Se l'utente esiste (non è null), vai alla dashboard, altrimenti alla start_screen
        val destinationIniziale = if (currentUser != null) "check_role_screen" else "start_screen"
        setContent {
            DermaSuiteTheme {
                // Oggetto che effettivamente esegue l'ordine di cambiare la pagina
                val navController = rememberNavController()

                // NavHost definisce i percorsi dell'app
                NavHost(
                    navController = navController,
                    // Usa la variabile dinamica invece della stringa fissa "start_screen"
                    startDestination = destinationIniziale
                ) {
                    // Rotta per la pagina iniziale
                    composable("start_screen") {
                        StartPageScreen(
                            onNavigateToLogin = { navController.navigate("login_screen") },
                            onNavigateToRegister = { navController.navigate("register_screen") }
                        )
                    }

                    // Rotta per il Login (placeholder)
                    composable("login_screen") {
                        // Qui caricherai la tua LoginScreen()
                        LoginPageScreen(
                            onNavigateToRegister = { navController.navigate("register_screen")},
                            onNavigateToStart = { navController.navigate("start_screen")},
                            onLoginSuccess = { role ->
                                // Quando il login ha successo, il ViewModel ci restituisce il ruolo
                                if (role == "Medico") {
                                    navController.navigate("dashboard_screen_medico") {
                                        popUpTo("login_screen") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("dashboard_screen_paziente") {
                                        popUpTo("login_screen") { inclusive = true }
                                    }
                                }
                            }
                        )
                    }

                    // Rotta per la Registrazione
                    composable("register_screen") {
                        DermaRegisterPageScreen(
                            onNavigateToLogin = { navController.navigate("login_screen") },
                            onNavigateToStart = {navController.navigate("start_screen")}
                        )
                    }

                    composable("dashboard_screen_paziente"){
                        DermaDashBoardPazienteScreen(
                            onLogout = {
                                Firebase.auth.signOut()
                                navController.navigate("start_screen") {
                                    popUpTo(0)
                                }
                            }
                        )
                    }
                    composable(route = "dashboard_screen_medico"){
                        DermaDashBoardMedicoScreen(
                            onLogout = {
                                Firebase.auth.signOut()
                                navController.navigate("start_screen") {
                                    popUpTo(0)
                                }
                            }
                        )
                    }
                    // Pagina intermedia per decidere dove andare se l'utente era già loggato
                    composable("check_role_screen") {
                        DermaCheckRoleAndNavigateScreen(navController)
                    }
                }
            }
        }
    }
}

