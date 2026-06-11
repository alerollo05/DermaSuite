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
    scrollState: ScrollState? = rememberScrollState(),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally, // Se non specifico nulla quando chiamo la funzione, usa come valore predefinito il centro (CenterHorizontally).
    verticalArrangement: Arrangement.Vertical = Arrangement.Center, // Uguale a horizzontalAlignmet
    content: @Composable ColumnScope.() -> Unit // contenuto della column
) {

    val columnModifier = modifier
        .fillMaxSize()
        // Il padding dello Scaffold va applicato per primo
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)

    val finalModifier = if (scrollState != null) {
        columnModifier.verticalScroll(scrollState)
    } else {
        columnModifier
    }

    Column(
        modifier = finalModifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}