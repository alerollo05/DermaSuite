package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.ui.screens.StartPageScreen

@Composable
fun AccountTypeSelector(
    selectedType: String, //Paziente o medico
    onTypeSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth())
     {
         Text(text = "TIPO DI ACCOUNT",
             style = MaterialTheme.typography.labelLarge,
             color = MaterialTheme.colorScheme.primary)
         Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)){
             //diamo la spaziatura orizzontale ai bottoni in modo tale da rendere il tutto più bello
             AccountTypeButton(
                 text = "Paziente",
                 iconRes = it.uninsubria.dermasuite.R.drawable.ic_button_paziente, // Andiamo a passargli l'id dell'icona SVG corrispondente a uno o all'altro
                 isSelected = selectedType == "Paziente", //Vado a controllare e se è selezionato paziente, metto la var a ture
                 onClick = { onTypeSelected("Paziente")}, //Quando clicco seleziono il fatto di aver scelto il paziente
                 modifier = Modifier.weight(1f)
             )
             AccountTypeButton(
                 text = "Medico",
                 iconRes = it.uninsubria.dermasuite.R.drawable.ic_button_medico, // Andiamo a passargli l'id dell'icona SVG corrispondente a uno o all'altro
                 isSelected = selectedType == "Medico",//Vado a controllare e se è selezionato medico, metto la var a ture
                 onClick = { onTypeSelected("Medico")}, //Quando clicco seleziono il fatto di aver scelto il medico
                 modifier = Modifier.weight(1f)
                 )
         }
     }
}

@Composable
fun AccountTypeButton(
    text: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier) {
    // Definiamo i colori in base allo stato di selezione
    //In questo modo creo un UI attiva che mi permette di far cambiare l'interfaccia grafica in base
    // al tipo di account selezionato
    val backgroundColor = if (isSelected) Color.White else Color(0xFFF1F4F8)
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
    val shadowElevation = if (isSelected) 4.dp else 0.dp

    //ANDIAMO A CREARE IL BOTTONE A MANO
    Surface( //andiamo a creare una "scatola" e all'interno creiamo il bottone
        onClick = onClick,
        modifier = Modifier.height(56.dp),
        shape = MaterialTheme.shapes.large,
        color = backgroundColor,
        contentColor = contentColor,
        shadowElevation = shadowElevation
    ){
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = contentColor, // viene cambiato in base a se è selezionato oppure no il bottone
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
        }
        }
    }



@Preview(showBackground = true)
@Composable
fun AccountTypeSelectorPreview() {
    it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme() {
        AccountTypeSelector(selectedType = "Paziente", onTypeSelected = {})
    }
}