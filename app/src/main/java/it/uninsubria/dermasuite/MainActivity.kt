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
import it.uninsubria.dermasuite.ui.screens.medico.DermaDashBoardMedicoScreen
import it.uninsubria.dermasuite.ui.screens.paziente.DermaDashBoardPazienteScreen
import it.uninsubria.dermasuite.ui.screens.LoginPageScreen
import it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme
import it.uninsubria.dermasuite.ui.screens.StartPageScreen
import it.uninsubria.dermasuite.ui.screens.DermaRegisterPageScreen
import it.uninsubria.dermasuite.ui.screens.paziente.DermaBMIHistoryScreen
import it.uninsubria.dermasuite.ui.screens.paziente.DermaBMIScreen
import it.uninsubria.dermasuite.ui.screens.paziente.DermaBSAHistoryScreen
import it.uninsubria.dermasuite.ui.screens.paziente.DermaBSAScreen
import it.uninsubria.dermasuite.ui.screens.paziente.DermaChatPazienteScreen
import it.uninsubria.dermasuite.ui.screens.paziente.DermaEASIHistoryScreen
import it.uninsubria.dermasuite.ui.screens.paziente.DermaEASIScreen
import it.uninsubria.dermasuite.ui.screens.paziente.DermaPASIHistoryScreen
import it.uninsubria.dermasuite.ui.screens.paziente.DermaPASIScreen
import it.uninsubria.dermasuite.ui.screens.paziente.DermaProfilePazienteScreen


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

                    composable("dashboard_screen_paziente") {
                        DermaDashBoardPazienteScreen(
                            navController = navController,
                            onNavigateToChatP = { navController.navigate("chat_screen_paziente") },
                            onNavigateToProfileP = { navController.navigate("profile_screen_paziente") },
                            onNavigateDashboardPASI = {navController.navigate("pasi_page_screen")},
                            onNavigateDashboardEASI = {navController.navigate("easi_page_screen")},
                            onNavigateDashboardBMI = {navController.navigate("bmi_page_screen")},
                            onNavigateDashboardBSA = {navController.navigate("bsa_page_screen")}
                        )
                    }
                    composable("pasi_page_screen"){
                        DermaPASIScreen(
                            navController = navController,
                            onBack = {navController.navigate("dashboard_screen_paziente")},
                            onNavigateToChatP = {navController.navigate("chat_screen_paziente")},
                            onNavigateToProfileP = {navController.navigate("profile_screen_paziente")},
                            onNavigateToPasiHistory = {navController.navigate("pasi_history_screen")}
                        )
                    }
                    composable("easi_page_screen"){
                        DermaEASIScreen(
                            navController = navController,
                            onBack = {navController.navigate("dashboard_screen_paziente")},
                            onNavigateToChatP = {navController.navigate("chat_screen_paziente")},
                            onNavigateToProfileP = {navController.navigate("profile_screen_paziente")},
                            onNavigateToEasiHistory = {navController.navigate("easi_history_screen")}
                        )
                    }
                    composable("bmi_page_screen"){
                        DermaBMIScreen(
                            navController = navController,
                            onBack = {navController.navigate("dashboard_screen_paziente")},
                            onNavigateToChatP = {navController.navigate("chat_screen_paziente")},
                            onNavigateToProfileP = {navController.navigate("profile_screen_paziente")},
                            onNavigateToBmiHistory = {navController.navigate("bmi_history_screen")}
                        )
                    }
                    composable("bsa_page_screen"){
                        DermaBSAScreen(
                            navController = navController,
                            onBack = {navController.navigate("dashboard_screen_paziente")},
                            onNavigateToChatP = {navController.navigate("chat_screen_paziente")},
                            onNavigateToProfileP = {navController.navigate("profile_screen_paziente")},
                            onNavigateToBsaHistory = {navController.navigate("bsa_history_screen")}
                        )
                    }
                    composable("pasi_history_screen"){
                        DermaPASIHistoryScreen(
                            navController = navController,
                            onBack = {navController.navigate("pasi_page_screen")},
                            onNavigateToChatP = {navController.navigate("chat_screen_paziente")},
                            onNavigateToProfileP = {navController.navigate("profile_screen_paziente")}
                        )
                    }
                    composable("easi_history_screen"){
                        DermaEASIHistoryScreen(
                            navController = navController,
                            onBack = {navController.navigate("easi_page_screen")},
                            onNavigateToChatP = {navController.navigate("chat_screen_paziente")},
                            onNavigateToProfileP = {navController.navigate("profile_screen_paziente")}
                        )
                    }
                    composable("bmi_history_screen"){
                        DermaBMIHistoryScreen(
                            navController = navController,
                            onBack = {navController.navigate("bmi_page_screen")},
                            onNavigateToChatP = {navController.navigate("chat_screen_paziente")},
                            onNavigateToProfileP = {navController.navigate("profile_screen_paziente")}
                        )
                    }
                    composable("bsa_history_screen"){
                        DermaBSAHistoryScreen(
                            navController = navController,
                            onBack = {navController.navigate("bsa_page_screen")},
                            onNavigateToChatP = {navController.navigate("chat_screen_paziente")},
                            onNavigateToProfileP = {navController.navigate("profile_screen_paziente")}
                        )
                    }
                    composable("chat_screen_paziente") {
                        DermaChatPazienteScreen(
                            onBack = {navController.navigate("dashboard_screen_paziente")},
                            navController = navController,
                            onNavigateToDashboardP = {navController.navigate("dashboard_screen_paziente")},
                            onNavigateToProfileP = {navController.navigate("profile_screen_paziente")}
                        )
                    }
                    composable("profile_screen_paziente") {
                        DermaProfilePazienteScreen(
                            onLogout = {
                                Firebase.auth.signOut()
                                navController.navigate("start_screen") {
                                    popUpTo(0)
                                }
                            },
                            onBack = {navController.navigate("dashboard_screen_paziente")},
                            navController = navController,
                            onNavigateToDashboardP = {navController.navigate("dashboard_screen_paziente")},
                            onNavigateToChatP = {navController.navigate("chat_screen_paziente")}
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

