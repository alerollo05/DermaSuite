package it.uninsubria.dermasuite.ui.screens.paziente

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.uninsubria.dermasuite.ui.components.DermaButton

@Composable
fun DermaDashBoardPazienteScreen(
    onLogout: () -> Unit, //Questo è il logout che possiamo andare a richiamare dentro un bottone nella UI
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Ciao a tutti i pazienti!", fontSize = 24.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            DermaButton("Log Out", onClick = {onLogout()})
        }
    }
}