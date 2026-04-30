package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DermaColumnScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(0.dp), // Gestisce lo spazio dello Scaffold
    scrollState: ScrollState = rememberScrollState(),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally, // Se non specifico nulla quando chiamo la funzione, usa come valore predefinito il centro (CenterHorizontally).
    verticalArrangement: Arrangement.Vertical = Arrangement.Center, // Uguale a horizzontalAlignmet
    content: @Composable ColumnScope.() -> Unit // contenuto della column
) {
    //Lo mettiamo in modo tale che se i campi sono troppi posso fare lo scrool per vederli tutti
    // e tenere in memoria gli stati

    Column(
        modifier = modifier
            .fillMaxSize()
            // Il padding dello Scaffold va applicato per primo
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
            // Poi applichiamo lo scroll
            .verticalScroll(scrollState)
            // Infine il padding orizzontale interno
            .padding(horizontal = 16.dp),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}