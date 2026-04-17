package it.uninsubria.dermasuite.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import it.uninsubria.dermasuite.viewmodels.RegisterPageViewModel

@Composable
fun RegisterPageScreen(
    viewModel: RegisterPageViewModel = viewModel()
){
    val uiState = viewModel.uiState
}

