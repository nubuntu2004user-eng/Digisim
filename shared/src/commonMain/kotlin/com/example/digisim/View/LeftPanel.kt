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
import com.example.digisim.UiUtils.AppState
import kotlinx.coroutines.CoroutineScope

@Composable
fun leftPanel(viewModel: CanvasViewModel,
              simulation: SimulationViewModel,
              librariesViewModel: ComponentLibrariesViewModel,
              translationViewModel: TranslationViewModel,
              appState: AppState,
              settingsViewModel: SettingsViewModel,
              scope : CoroutineScope
){
    Column(modifier = Modifier.fillMaxSize()) {
        if (!appState.showSettings){
        componentLibraries(viewModel , librariesViewModel, translationViewModel ,appState)
        componentSettings(viewModel, simulation, translationViewModel , appState)}
        else{
            settingsPanel(settingsViewModel, appState , translationViewModel ,scope)
        }
    }
}