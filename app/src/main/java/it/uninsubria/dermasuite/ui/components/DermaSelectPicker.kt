package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class) //Altrimenti non possiamo usare le nuove API di ExposedDropdownMenuBox
@Composable
fun DermaSelectPicker(
    label: String,
    selectedValue: String,
    placeholder: String,
    onValueSelected: (String) -> Unit,
    unitaMisura : String, // Es. kg, cm ecc
    options: List<Int>, // Lista di numeri (es. 100..250)
    leadingIconRes: Int? = null,
    modifier: Modifier = Modifier
){
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth()
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ){
            if(leadingIconRes != null) {
                Icon(
                    painter = painterResource(id = leadingIconRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.padding(6.dp))
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ){
            // Creiamo il testo da mostrare --> se c'è un valore e un suffisso, li uniamo.
            val displayText = if (selectedValue.isNotEmpty() && unitaMisura.isNotEmpty()) {
                "$selectedValue $unitaMisura"
            } else {
                selectedValue
            }

            OutlinedTextField(
                //In questo modo passiamo la variabile formattata alla UI displayText,
                // senza cambiare il valore passato al viewmodel che se no poi nel calcolo del BMI farebbe crashare l'app
                value = displayText,
                onValueChange = {}, //Non facciamo nulla serve solo per stampare i dati
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(//In questo modo colleghiamo il textField normale al menu a scorrimento
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, //menu di sola lettura non modificabile
                        enabled = true
                    ),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                placeholder = { Text("$placeholder $unitaMisura", color = Color.Gray) },
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.DarkGray,
                    unfocusedTextColor = Color.DarkGray,
                    focusedContainerColor = Color(0xFFF1F4F8),
                    unfocusedContainerColor = Color(0xFFF1F4F8),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            //Creiamo il vero e proprio menu a scelta multipla andando a stampare la lista di numeri
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text ="$option $unitaMisura",color = Color.DarkGray) },
                        onClick = {
                            onValueSelected(option.toString())
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}
