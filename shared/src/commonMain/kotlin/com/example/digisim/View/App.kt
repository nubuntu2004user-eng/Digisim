package com.example.digisim.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.tooling.preview.Preview
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.DrawingLogic.DigitalLogicSimulator
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.ComponentLibrariesViewModel
import com.example.digisim.UiUtils.TranslationViewModel
import com.example.digisim.UiUtils.undoOnce
import engineLogic.ClockManager
import kotlinx.coroutines.CoroutineScope
import androidx.compose.ui.input.key.*
import com.example.digisim.LlmClients.OpenAiClient
import com.example.digisim.Persistance.AppStateDto
import com.example.digisim.Persistance.persistanceRepository.loadState
import com.example.digisim.SettingsViewModel
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.Language
import com.example.digisim.mcpClient.mcpClient
import kotlinx.coroutines.launch

@Composable
@Preview
fun App(viewModel: CanvasViewModel = remember { CanvasViewModel() }) {
    MaterialTheme {
        val scope: CoroutineScope = rememberCoroutineScope()
        val simulation = remember { SimulationViewModel() }
        val librariesViewModel  = remember { ComponentLibrariesViewModel() }
        val clockManager = remember { ClockManager() }
        val translationViewModel = remember<TranslationViewModel> { TranslationViewModel() }
        val mcpClient = remember { mcpClient() }
        val openAiClient = remember {OpenAiClient(mcpClient)}
        val appState = remember { AppState() }
        
        LaunchedEffect(Unit) {
            mcpClient.initializeClient()
            val loadedDto = loadState()
            appState.applyDto(loadedDto)
            appState.loadLibraries(librariesViewModel, scope, appState)
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown &&
                    event.isCtrlPressed &&
                    event.key == Key.Z
                ) {
                    undoOnce(viewModel)
                    true
                } else {
                    false
                }
            }

        ) {
            TopRow(simulation , viewModel , scope , clockManager, translationViewModel , appState)
            Row {
                Column(modifier = Modifier.weight(0.3f)) {
                    leftPanel(viewModel, simulation , librariesViewModel, translationViewModel , appState , SettingsViewModel.default , scope , llmClient = openAiClient)
                }
                Column(modifier = Modifier.weight(1f)) {
                    ToolSelectorRow(viewModel , simulation, translationViewModel ,appState)
                    Box(modifier = Modifier.weight(1f)) {
                        DigitalLogicSimulator(viewModel = viewModel, simulation = simulation, scope = scope , clockManager = clockManager)
                    }
                }

            }
        }
    }
}