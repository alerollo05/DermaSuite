package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.viewmodels.paziente.ProfilePazPageViewModel
import com.google.firebase.Timestamp
import it.uninsubria.dermasuite.ui.screens.getFlagEmoji
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun DermaSpecialistCard(
    doctorName: String,
    doctorRole: String,
    doctorDescription: String,
    doctorDataNascita: Timestamp? = null,
    doctorMail: String,
    doctorLanguage: String,
    avatarURL: String? = null,
    // Opzionale: passa l'ID della tua icona se ne hai una specifica per il medico
    iconResId: Int = R.drawable.ic_button_medico,
    viewModel: ProfilePazPageViewModel = viewModel()
) {

    // Variabile di stato interna al componente per gestire l'apertura/chiusura del popup
    var showDialog by remember { mutableStateOf(false) }



    // La Card del Medico
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { showDialog = true }, // Al click apriamo il popup
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp) // Ombra sotto la card
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DermaAvatar(
                avatarURL = avatarURL,
                size = 48,
                iconResId = iconResId,
                isComplex = getFlagEmoji(doctorLanguage)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Testi centrali (Nome e Ruolo)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = doctorName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = doctorRole,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 2
                )
            }

            // Freccia a destra
            Icon(
                painter = painterResource(id=R.drawable.ic_mezza_freccia_destra),
                contentDescription = "Vedi dettagli",
                tint = Color.Gray
            )
        }
    }

    // Il Popup (Dialog) che appare quando showDialog è true
    if (showDialog) {
        //Formattiamo la data da stampare
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dataNascitaFormattata = formatter.format(doctorDataNascita?.toDate() ?: Date())

        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = true, // Si chiude col tasto indietro del telefono
                dismissOnClickOutside = true, // Si chiude cliccando nello spazio vuoto
                usePlatformDefaultWidth = false // Disabilita la larghezza forzata di sistema
            )
        ){
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.90f)// Occupa solo l'85% della larghezza, lasciando i bordi liberi
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DermaAvatar(
                        avatarURL = avatarURL,
                        size = 80,
                        iconResId = iconResId,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            doctorName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                    Spacer(modifier = Modifier.height(20.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = stringResource(R.string.dialog_description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = doctorDescription,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                maxLines = 3,
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ){
                            Text(
                                text = stringResource(R.string.dialog_mail),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = doctorMail,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                maxLines = 1,
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ){
                            Text(
                                text = stringResource(R.string.dialog_lingua),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = doctorLanguage.uppercase(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                maxLines = 1,
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ){
                            Text(
                                text = stringResource(R.string.dialog_data_nascita),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = dataNascitaFormattata,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                maxLines = 1,
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                showDialog = false
                            },
                            modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally)

                        ) {
                            androidx.compose.material.Text(
                                text = stringResource(R.string.dialog_close),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }