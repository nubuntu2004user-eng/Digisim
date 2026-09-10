package com.example.digisim.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.SettingsViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.ComponentLibrariesViewModel
import com.example.digisim.UiUtils.TranslationViewModel
import com.example.digisim.UiUtils.UiState

@Composable
fun leftPanel(viewModel: CanvasViewModel,
              simulation: SimulationViewModel ,
              librariesViewModel: ComponentLibrariesViewModel,
              translationViewModel: TranslationViewModel ,
              uiState: UiState,
              settingsViewModel: SettingsViewModel
){
    Column(modifier = Modifier.fillMaxSize()) {
        if (!uiState.showSettings){
        componentLibraries(viewModel , librariesViewModel, translationViewModel)
        componentSettings(viewModel, simulation, translationViewModel)}
        else{
            settingsPanel(settingsViewModel, uiState , translationViewModel)
        }
    }
}