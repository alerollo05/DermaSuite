package it.uninsubria.dermasuite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.uninsubria.dermasuite.ui.screens.LoginPageScreen
import it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme
import it.uninsubria.dermasuite.viewmodels.StartPageViewModel
import it.uninsubria.dermasuite.ui.screens.StartPageScreen
import it.uninsubria.dermasuite.ui.screens.DermaRegisterPageScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DermaSuiteTheme {
                // Oggetto che effettivamente esegue l'ordine di cambiare la pagina
                val navController = rememberNavController()

                // NavHost definisce i percorsi dell'app
                NavHost(
                    navController = navController,
                    startDestination = "start_screen"
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
                            //onLoginSuccess = { navController.navigate("dashboard_screen")},
                            onNavigateToRegister = { navController.navigate("register_screen")},
                            onNavigateToStart = { navController.navigate("start_screen")}

                        )
                    }

                    // Rotta per la Registrazione
                    composable("register_screen") {
                        DermaRegisterPageScreen(
                            onNavigateToLogin = { navController.navigate("login_screen") },
                            onNavigateToStart = {navController.navigate("start_screen")}
                        )
                    }
                }
            }
        }
    }
}

