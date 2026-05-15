package it.uninsubria.dermasuite.ui.components

import android.content.Context
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.ui.screens.getFlagEmoji
import it.uninsubria.dermasuite.viewmodels.paziente.ProfilePazPageViewModel

@Composable
fun DermaDoctorListDialog(
    viewModel: ProfilePazPageViewModel = viewModel(),
    context: Context
){
    // Filtriamo la lista escludendo il medico già assegnato (confrontando gli UID)
    val filteredDoctorList = viewModel.doctorList.filter { it.uid != viewModel.currentDoctor?.uid }
    val isLoadingDoctors = viewModel.isLoadingDoctors


    Dialog(
        onDismissRequest = { viewModel.closeDoctorDialog() },
        properties = DialogProperties(
            dismissOnBackPress = true, // Si chiude col tasto indietro del telefono
            dismissOnClickOutside = true, // Si chiude cliccando nello spazio vuoto
            usePlatformDefaultWidth = false // Disabilita la larghezza forzata di sistema
        )
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)// Occupa solo l'85% della larghezza, lasciando i bordi liberi
                .wrapContentHeight(),// L'altezza si adatta solo al contenuto
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ){
            Column(
                modifier = Modifier
                    .padding(24.dp) // Padding interno spazioso
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){

                if(isLoadingDoctors){
                    DermaIsLoading()
                }else{
                    LazyColumn { // Usa LazyColumn per liste scrollabili
                        if(filteredDoctorList.isEmpty()){

                                // Inseriamo il titolo come primo elemento della lista
                                item {
                                    Text(
                                        text = stringResource(R.string.dialog_doctor_title),
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                }
                                item {
                                    Text(
                                        text = stringResource(R.string.dialog_doctor_not_found),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                }
                        }else {
                            // Inseriamo il titolo come primo elemento della lista
                            item {
                                Text(
                                    text = stringResource(R.string.dialog_doctor_title),
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }

                            items(filteredDoctorList) { doctor ->

                                val stringaNome = doctor.nome.lowercase().replaceFirstChar { it.uppercase() }
                                val stringaCognome = doctor.cognome.lowercase().replaceFirstChar { it.uppercase() }
                                ElevatedCard(
                                    onClick = {
                                        viewModel.selectDoctor(
                                            doctor.uid,
                                            context = context
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = Color.White,
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(IntrinsicSize.Min)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .width(6.dp)
                                                .background(MaterialTheme.colorScheme.primary)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        DermaAvatar(
                                            avatarURL = doctor.avatarUrl,
                                            isComplex = getFlagEmoji(doctor.language),
                                            size = 48,
                                            modifier = Modifier.padding(top = 12.dp),
                                            iconResId = R.drawable.ic_button_medico
                                        )
                                        Column(modifier = Modifier.padding(16.dp)) {

                                            Text(
                                                text = "${stringResource(R.string.label_doctor_prefix)} $stringaNome$stringaCognome",
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = doctor.specialization ?: stringResource(R.string.desc_no_doctor_specialization),
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 2
                                            )
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}