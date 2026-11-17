package com.example.digisim.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.DrawingLogic.DigitalLogicSimulator
import com.example.digisim.LlmClients.OpenAiClient
import com.example.digisim.Persistance.persistanceRepository.loadState
import com.example.digisim.SettingsViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.ComponentLibrariesViewModel
import com.example.digisim.UiUtils.TranslationViewModel
import com.example.digisim.UiUtils.undoOnce
import com.example.digisim.mcpClient.mcpClient
import engineLogic.ClockManager
import kotlinx.coroutines.CoroutineScope

@Composable
@Preview
fun App(viewModel: CanvasViewModel = remember { CanvasViewModel() }) {
    val scope: CoroutineScope = rememberCoroutineScope()
    val simulation = remember { SimulationViewModel() }
    val librariesViewModel = remember { ComponentLibrariesViewModel() }
    val clockManager = remember { ClockManager() }
    val translationViewModel = remember { TranslationViewModel() }
    val mcpClient = remember { mcpClient() }
    val openAiClient = remember { OpenAiClient(mcpClient) }
    val appState = remember { AppState() }
    val settings = SettingsViewModel.default

    val swissColorScheme = if (settings.isDarkMode) {
        darkColorScheme(
            primary = settings.primaryControlFill,
            onPrimary = settings.primaryControlText,
            secondary = settings.accentTaupe,
            surface = settings.panelBackgroundColor,
            onSurface = settings.textColor,
            background = settings.canvasBackgroundColor,
            onBackground = settings.textColor,
            error = settings.dangerColor
        )
    } else {
        lightColorScheme(
            primary = settings.primaryControlFill,
            onPrimary = settings.primaryControlText,
            secondary = settings.accentTaupe,
            surface = settings.panelBackgroundColor,
            onSurface = settings.textColor,
            background = settings.canvasBackgroundColor,
            onBackground = settings.textColor,
            error = settings.dangerColor
        )
    }

    LaunchedEffect(Unit) {
        mcpClient.initializeClient()
        val loadedDto = loadState()
        appState.applyDto(loadedDto)
        openAiClient.applySettings(appState = appState)
        appState.loadLibraries(librariesViewModel, scope, appState)
    }

    MaterialTheme(colorScheme = swissColorScheme) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(settings.canvasBackgroundColor)
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
            val isCompactWidth = maxWidth < 760.dp
            val sidebarWidth = if (isCompactWidth) 260.dp else 320.dp

            Column(modifier = Modifier.fillMaxSize()) {
                // Zone 1: Top Command & Simulation Transport Bar
                TopRow(simulation, viewModel, scope, clockManager, translationViewModel, appState)

                // Zone 2 & 3: Main Split Workspace (Left Dock + Right Tool Ribbon & Schematic Canvas)
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .width(sidebarWidth)
                            .fillMaxHeight()
                    ) {
                        leftPanel(
                            viewModel = viewModel,
                            simulation = simulation,
                            librariesViewModel = librariesViewModel,
                            translationViewModel = translationViewModel,
                            appState = appState,
                            settingsViewModel = settings,
                            scope = scope,
                            llmClient = openAiClient
                        )
                    }

                    SwissVerticalDivider(settings = settings)

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        ToolSelectorRow(viewModel, simulation, translationViewModel, appState)

                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            DigitalLogicSimulator(
                                viewModel = viewModel,
                                simulation = simulation,
                                scope = scope,
                                clockManager = clockManager
                            )
                        }

                        // Zone 4: Swiss Technical Status & Coordinates Footer
                        SwissStatusFooter(
                            viewModel = viewModel,
                            simulation = simulation,
                            translationViewModel = translationViewModel,
                            appState = appState,
                            settings = settings
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SwissStatusFooter(
    viewModel: CanvasViewModel,
    simulation: SimulationViewModel,
    translationViewModel: TranslationViewModel,
    appState: AppState,
    settings: SettingsViewModel
) {
    val lang = appState.currentLanguage
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(settings.panelBackgroundColor)
    ) {
        SwissHorizontalDivider(settings)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 28.dp)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "${translationViewModel.getString("MODE", lang)}: ${viewModel.currentMode.name}",
                    color = settings.textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
                SwissVerticalDivider(modifier = Modifier.height(14.dp), settings = settings)
                Text(
                    text = "${translationViewModel.getString("COMPONENTS", lang)}: ${viewModel.components.size}",
                    color = settings.secondaryTextColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                SwissVerticalDivider(modifier = Modifier.height(14.dp), settings = settings)
                Text(
                    text = "${translationViewModel.getString("WIRES", lang)}: ${viewModel.wires.size}",
                    color = settings.secondaryTextColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${translationViewModel.getString("VIEWPORT", lang)}: (${viewModel.viewportX.toInt()}, ${viewModel.viewportY.toInt()})",
                    color = settings.secondaryTextColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                if (viewModel.viewportX != 0f || viewModel.viewportY != 0f) {
                    SwissButton(
                        text = translationViewModel.getString("Reset View", lang),
                        onClick = {
                            viewModel.viewportX = 0f
                            viewModel.viewportY = 0f
                        },
                        compact = true,
                        settings = settings
                    )
                }
            }
        }
    }
}
