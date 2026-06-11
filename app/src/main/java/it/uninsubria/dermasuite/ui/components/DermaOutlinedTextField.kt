package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import it.uninsubria.dermasuite.R

@Composable
fun DermaOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    singleLine: Boolean = true
) {

    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth(),

        // Se password disattivo il correttore
        keyboardOptions = if (isPassword) {
            KeyboardOptions(keyboardType = KeyboardType.Password)
        } else {
            KeyboardOptions.Default
        },

        // Nasconde o mostra il testo in base allo stato
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },

        // Se è una password disegna l'icona dell'occhio
        trailingIcon = if (isPassword) {
            {
                val iconImage = if (passwordVisible) R.drawable.ic_password_eye_hide else R.drawable.ic_password_eye
                val description = if (passwordVisible) "Nascondi password" else "Mostra password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(id = iconImage),
                        contentDescription = description,
                        tint = Color.Gray
                    )
                }
            }
        } else null // Se NON è una password nessuna icona
    )
}