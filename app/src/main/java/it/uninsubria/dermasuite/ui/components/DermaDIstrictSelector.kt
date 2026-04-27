package it.uninsubria.dermasuite.ui.components

import android.content.Context
import android.widget.RadioGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.viewmodels.paziente.DistrettoCorpo

@Composable
fun DermaDistrictSelector(
    label: String,
    listaDistretti: DistrettoCorpo
){

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ){
            Text(
                text = label.uppercase()
            )

            //fai il radio button per l'interfaccia grafica


            //Per i selettori dei parametri creali uguali a quelli dell'EASI
        }
    }
}