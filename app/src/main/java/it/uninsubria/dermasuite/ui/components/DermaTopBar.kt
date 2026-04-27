package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.uninsubria.dermasuite.R
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DermaTopBar(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
    //mettendo un @Composable, andiamo a dire che in quel campo potrà essere messo dentro anche un
    //contenuto grafico di tipo composable (ES. icona dell'avatar dell'utente, icona export dello storico ecc.)
    //Lo chiamo action appunto perchè mi permette di fare delle azioni aggiuntive
    //Con RowScope andiamo a dire che i componenti UI che inseriremo verranno racchiusi nell scope di una riga
    //Quindi potrai usare i modifier specifici delle righe
    //-> Unit lo mettiamo per indicare che è una funzione che non restituisce nulla,
    //ma fa solo un azione (disegnare)
    //mentre le {} sono il valore di default, se non inserisco nulla non disegno e faccio nulla
){
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically){
                if(title == "DermaSuite"){
                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground), // Usa foreground drawable
                        contentDescription = "Logo DermaSuite",
                        modifier = Modifier.size(50.dp) // Dimensione adatta alla TopBar
                            .clip(MaterialTheme.shapes.medium))
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Torna indietro",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}
@Preview(showBackground = true)
@Composable
private fun DermaTopBarPreview() {
    it.uninsubria.dermasuite.ui.theme.DermaSuiteTheme() {
        DermaTopBar("DermaSuite", showBackButton = true, onBackClick = {})
    }
}