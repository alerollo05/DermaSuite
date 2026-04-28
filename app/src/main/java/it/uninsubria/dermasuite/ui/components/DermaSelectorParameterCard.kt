package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.R

@Composable
fun DermaSelectorParameterCard(
    title: String,
    subtitle: String,
    IconRes: Int,
    selectedValue: Int,
    isCompleted: Boolean = selectedValue != -1, // Calcolato automaticamente se non passato
    //Il valore che viene modificato dall'utente
    onValueChange: (Int) -> Unit
){
    
    Card(
        modifier = Modifier.fillMaxWidth().padding( vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(2.dp)
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()){
                Icon(
                    painter = painterResource(id = IconRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                if(isCompleted){
                    Icon(
                        painter = painterResource(id = R.drawable.ic_checkbox_ok),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }else{
                    Icon(
                        painter = painterResource(id = R.drawable.ic_checkbox_no),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                (0..4).forEach {
                    score ->
                    val isSelected = selectedValue == score
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(if(isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF8F9FA)
                            )
                            .clickable{onValueChange(score)}, //Quando lo clicco metto come valore score
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = score.toString(),
                            color = if(isSelected) Color.White else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

        }
    }

}