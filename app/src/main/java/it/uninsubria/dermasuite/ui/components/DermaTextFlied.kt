package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.R

@Composable
fun DermaTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit, // funzione che viene chiamata quando il valore cambia
    // Unit è una funzione che non riceve in input nulla e non restituisce nulla
    placeholder: String, //prende la stringa da mettere come placheholder
    leadingIconRes: Int? = null, // prende (se c'è l'id del icona drawable che dobbiamo mettere
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier.fillMaxWidth()){
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.tertiary) },
            leadingIcon = if (leadingIconRes != null) {
                {
                    Icon(
                        painter = painterResource(id = leadingIconRes),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else null,
            trailingIcon =
                if (isPassword){
                    //qui andiamo a racchiudere il disegnamento di un icona che non si dovrebbe fare in un composable statico
                    // in una lamda function in modo tale da poterla mettere direttamente nel codice riutilizzabile senza doverlo
                    //fare ogni volta
                    {Icon(painter = painterResource(R.drawable.ic_password_eye),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp))}
                }else null,
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F4F8),
                unfocusedContainerColor = Color(0xFFF1F4F8),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DermaTextFieldPreview() {
    it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme() {
        DermaTextField("Nome", "", {}, "Inserisci il tuo nome")
    }
}