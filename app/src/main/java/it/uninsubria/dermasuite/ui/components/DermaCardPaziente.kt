package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.firebase.DermaUser
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DermaCardPaziente(
    paziente: DermaUser,
    onClick: () -> Unit
){
    val dataNascitaFormattata = paziente.dataNascita?.toDate()?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
    } ?: ""

    val stringaNomeFormattata = paziente.nome.lowercase().replaceFirstChar { it.uppercase() }
    val stringaCognomeFormattata = paziente.cognome.lowercase().replaceFirstChar { it.uppercase() }
    val nomeCompletoFormattato = "$stringaNomeFormattata $stringaCognomeFormattata"

    val ultimaValutazione = paziente.ultimaValutazione?.toDate()?.let{
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
    } ?: ""



    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ){
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ){
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ){
                Row(verticalAlignment = Alignment.CenterVertically){
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(painter = painterResource(R.drawable.ic_profile), contentDescription = null, tint = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    // Dati Paziente
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = nomeCompletoFormattato,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dataNascitaFormattata,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    // Chip Stato (Mock)
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(stringResource(R.string.label_stato_paziente).uppercase(), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                HorizontalDivider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                //Sezione inferiore della card

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Column(){
                        Text(
                            text = stringResource(R.string.label_ultima_valutazione),
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = ultimaValutazione,
                            color = Color.Gray
                        )
                    }
                    Icon(painter = painterResource(R.drawable.ic_mezza_freccia_destra), contentDescription = null, tint = Color.Gray)
                }
            }
        }
    }
}

