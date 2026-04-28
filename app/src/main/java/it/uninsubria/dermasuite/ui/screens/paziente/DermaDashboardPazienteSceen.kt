package it.uninsubria.dermasuite.ui.screens.paziente

import android.R.attr.icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.components.BottomBarAction
import it.uninsubria.dermasuite.ui.components.DermaBottomBar
import it.uninsubria.dermasuite.ui.components.DermaButton
import it.uninsubria.dermasuite.ui.components.DermaColumnScreen
import it.uninsubria.dermasuite.ui.components.DermaTopBar
import it.uninsubria.dermasuite.viewmodels.paziente.DashboardPagePazienteViewModel

@Composable
fun DermaDashBoardPazienteScreen(
    navController: NavController,
    onNavigateToChatP: () -> Unit,
    onNavigateToProfileP: () -> Unit,
    onNavigateDashboardPASI: () -> Unit,
    onNavigateDashboardEASI: () -> Unit = {},
    onNavigateDashboardBMI: () -> Unit = {},
    onNavigateDashboardBSA: () -> Unit = {},
    viewModel: DashboardPagePazienteViewModel = viewModel() // Iniezione del ViewModel
){
    // Definiamo le azioni per questa specifica schermata
    val dashboardActions = listOf(
        BottomBarAction("HOME", R.drawable.ic_home, "dashboard_screen_paziente", { /* Sei già qui */ }),
        BottomBarAction("CHAT", R.drawable.ic_chat, "chat_screen_paziente", onNavigateToChatP),
        BottomBarAction("PROFILE", R.drawable.ic_profile, "profile_screen_paziente", onNavigateToProfileP)
    )
    val nomeUtente = viewModel.username // Recupera l'username dal ViewModel

    Scaffold(
        topBar= {
            DermaTopBar(title = "DermaSuite", showBackButton = false, onBackClick = {})
        },
        bottomBar = {
            DermaBottomBar(navController = navController, actions = dashboardActions)
        }
    ) { padding ->
        DermaColumnScreen(innerPadding = padding, verticalArrangement = Arrangement.Top) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Ciao, $nomeUtente!", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(20.dp))


            ElevatedCard(
                onClick = { onNavigateDashboardPASI() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(24.dp), // Arrotonda gli angoli della card
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface // Colore di sfondo della card
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp) // Ombra sotto la card
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp) // Padding interno per distanziare i campi dai bordi della card
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_pasi),
                        contentDescription = null,
                        tint = Color(0xFF005691), // Colore blu come nell'immagine
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "PASI",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )

                    Text(
                        text = "Psoriasis Area and Severity Index.\n" +
                                "Quantitative assessment of plaque severity.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }
            }

            ElevatedCard(
                onClick = { onNavigateDashboardEASI() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(24.dp), // Arrotonda gli angoli della card
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface // Colore di sfondo della card
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp) // Ombra sotto la card
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp) // Padding interno per distanziare i campi dai bordi della card
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_easi),
                        contentDescription = null,
                        tint = Color(0xFF005691), // Colore blu come nell'immagine
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "EASI",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )

                    Text(
                        text = "Eczema Area and Severity Index.\n" +
                                "Validated tool for atopic dermatitis extent.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }
            }

            ElevatedCard(
                onClick = { onNavigateDashboardBMI() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(24.dp), // Arrotonda gli angoli della card
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface // Colore di sfondo della card
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp) // Ombra sotto la card
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp) // Padding interno per distanziare i campi dai bordi della card
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bmi),
                        contentDescription = null,
                        tint = Color(0xFF005691), // Colore blu come nell'immagine
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "BMI",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )

                    Text(
                        text = "Body Mass Index calculation for metabolic profile and systemic therapy dosing.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }
            }
            ElevatedCard(
                onClick = { onNavigateDashboardBSA() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(24.dp), // Arrotonda gli angoli della card
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface // Colore di sfondo della card
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp) // Ombra sotto la card
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp) // Padding interno per distanziare i campi dai bordi della card
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bsa),
                        contentDescription = null,
                        tint = Color(0xFF005691), // Colore blu come nell'immagine
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "BSA",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )

                    Text(
                        text = "Body Surface Area.\n"+
                                "Critical metric for calculating pharmacological coverage.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}