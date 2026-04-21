package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
    // AGGIUNTA: permettiamo di decidere se è a riga singola (default true)
    singleLine: Boolean = true,
    // AGGIUNTA: per gestire il tasto della tastiera (es. Next o Done)
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
){
    //Creiamo una variabile per ricordarci se la password è visibile oppure no
    var passwordIsVisibile by remember { mutableStateOf(false) }
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
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            placeholder = { Text(placeholder, color = Color.Gray) },
            leadingIcon = if (leadingIconRes != null) {
                {
                    Icon(
                        painter = painterResource(id = leadingIconRes),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else null,
            trailingIcon = if (isPassword) {
                {
                    val iconRes = if (passwordIsVisibile) R.drawable.ic_password_eye else R.drawable.ic_password_eye_hide
                    IconButton(onClick = { passwordIsVisibile = !passwordIsVisibile }) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = if (passwordIsVisibile) "Nascondi password" else "Mostra password",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else null,
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Gray,
                disabledTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray,
                focusedContainerColor = Color(0xFFF1F4F8),
                unfocusedContainerColor = Color(0xFFF1F4F8),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            visualTransformation = if (isPassword && !passwordIsVisibile) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}
