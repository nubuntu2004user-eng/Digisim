package com.example.digisim.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.LlmClients.OpenAiClient
import com.example.digisim.SettingsViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.ComponentLibrariesViewModel
import com.example.digisim.UiUtils.TranslationViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun leftPanel(
    viewModel: CanvasViewModel,
    simulation: SimulationViewModel,
    librariesViewModel: ComponentLibrariesViewModel,
    translationViewModel: TranslationViewModel,
    appState: AppState,
    settingsViewModel: SettingsViewModel,
    scope: CoroutineScope,
    llmClient: OpenAiClient
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(settingsViewModel.panelBackgroundColor)
    ) {
        when {
            appState.showSettings -> {
                settingsPanel(settingsViewModel, appState, translationViewModel, scope)
            }
            appState.agenticMode -> {
                agenticUI(
                    llmClient = llmClient,
                    scope = scope,
                    appState = appState,
                    translationViewModel = translationViewModel
                )
            }
            else -> {
                componentLibraries(
                    viewModel = viewModel,
                    libraryViewModel = librariesViewModel,
                    translationViewModel = translationViewModel,
                    appState = appState,
                    modifier = Modifier.weight(0.52f)
                )
                componentSettings(
                    viewModel = viewModel,
                    simulationViewModel = simulation,
                    translationViewModel = translationViewModel,
                    appState = appState,
                    modifier = Modifier.weight(0.48f)
                )
            }
        }
    }
}
