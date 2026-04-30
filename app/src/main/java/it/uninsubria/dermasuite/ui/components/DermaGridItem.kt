package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.viewmodels.paziente.DistrettoCorpo

@Composable
fun DermaGridItem(
    distretto: DistrettoCorpo,
    iconRes: Int, //Icona della parte del corpo
    isSelected: Boolean, //Selezionato o no
    isComplete: Boolean, //Parametro per la spunta
    onSelect: () -> Unit, //Funzione da eseguire quando viene selezionato
    modifier: Modifier
){
    //Setup dei colori
    val backgroundColor = if (isSelected) Color.White else Color(0xFFF8F9FA)
    val indicatorColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF40E0D0)  // Turchese per la percentuale


    Card(
        modifier = Modifier
            .padding(4.dp)
            .height(110.dp)
            .clickable{onSelect()},
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ){
            //Barra di selezione laterale
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(indicatorColor)
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(id = distretto.nameResId).uppercase(),
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
                        color = indicatorColor,
                        maxLines = 2,//Evitiamo che il testo vada a capo e rompa il layout
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = indicatorColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Surface(
                        color = if(isSelected) indicatorColor else indicatorColor.copy(alpha = 0.2f), //diamo uno sfondo un po' più leggero
                        shape = MaterialTheme.shapes.large,
                    ){
                        Text(
                            //Andiamo a prendere dalla classe del distretto il peso che ha e lo stampiamo
                            text = "${(distretto.weight * 100).toInt()}%",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = if(isSelected) Color.White else indicatorColor
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                        if(isComplete){
                            Icon(
                                painter = painterResource(id = R.drawable.ic_checkbox_ok),
                                contentDescription = null,
                                tint = indicatorColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }else{
                            Icon(
                                painter = painterResource(id = R.drawable.ic_checkbox_no),
                                contentDescription = null,
                                tint = indicatorColor,
                                modifier = Modifier.size(20.dp)
                            )
                    }
                }

            }
        }

    }

}