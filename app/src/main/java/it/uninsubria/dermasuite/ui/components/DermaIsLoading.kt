package it.uninsubria.dermasuite.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun DermaIsLoading(
    modifier: Modifier = Modifier,
    contentAlignment: androidx.compose.ui.Alignment = androidx.compose.ui.Alignment.Center
) {
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ){
        CircularProgressIndicator(color = Color(0xFF003366))
    }
}