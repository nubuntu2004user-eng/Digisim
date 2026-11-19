package com.example.digisim.View

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.Persistance.persistanceRepository.saveState
import com.example.digisim.SettingsViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.TranslationViewModel
import engineLogic.ClockManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopRow(
    simulation: SimulationViewModel,
    viewModel: CanvasViewModel,
    scope: CoroutineScope,
    clockManager: ClockManager,
    translationViewModel: TranslationViewModel,
    appState: AppState
) {
    val settings = SettingsViewModel.default
    val lang = appState.currentLanguage

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(settings.panelBackgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Brand + File/Edit/Undo controls
            controlButtons(viewModel, scope, translationViewModel, clockManager, appState)

            // Center: Hardware Simulation Transport Cluster
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .background(settings.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                    .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                // Status indicator LED square
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (simulation.isRunning) settings.highPinColor else settings.secondaryTextColor.copy(alpha = 0.4f),
                            RoundedCornerShape(1.dp)
                        )
                )
                Text(
                    text = if (simulation.isRunning)
                        translationViewModel.getString("RUNNING", lang)
                    else
                        translationViewModel.getString("IDLE", lang),
                    color = settings.textColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.6.sp
                )

                SwissVerticalDivider(modifier = Modifier.height(18.dp), settings = settings)

                if (!simulation.isRunning) {
                    SwissButton(
                        text = translationViewModel.getString("Start", lang),
                        onClick = {
                            simulation.startSimulation(viewModel, scope, clockManager = clockManager)
                            viewModel.currentMode = CanvasViewModel.editingMode.POKE
                        },
                        isPrimary = true,
                        compact = true,
                        settings = settings,
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = translationViewModel.getString("Start", lang),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                    SwissButton(
                        text = translationViewModel.getString("Clear", lang),
                        onClick = {
                            viewModel.components.clear()
                            viewModel.wires.clear()
                            viewModel.selectedGateId = null
                            viewModel.nextId = 0
                        },
                        compact = true,
                        settings = settings,
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = translationViewModel.getString("Clear", lang),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                } else {
                    SwissButton(
                        text = translationViewModel.getString("Stop", lang),
                        onClick = {
                            simulation.isRunning = false
                            simulation.isAuto = false
                            simulation.componentsState.clear()
                            clockManager.tick = 0
                        },
                        isDanger = true,
                        compact = true,
                        settings = settings,
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = translationViewModel.getString("Stop", lang),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )

                    if (simulation.isAuto) {
                        SwissButton(
                            text = translationViewModel.getString("Pause", lang),
                            onClick = { simulation.isAuto = false },
                            isActive = true,
                            compact = true,
                            settings = settings,
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.Pause,
                                    contentDescription = translationViewModel.getString("Pause", lang),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                    } else {
                        SwissButton(
                            text = translationViewModel.getString("One step", lang),
                            onClick = {
                                simulation.startSimulation(viewModel, scope, clockManager, true)
                            },
                            compact = true,
                            settings = settings,
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = translationViewModel.getString("One step", lang),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                        SwissButton(
                            text = translationViewModel.getString("Auto", lang),
                            onClick = {
                                simulation.isAuto = true
                                simulation.startSimulation(viewModel, scope, clockManager = clockManager)
                            },
                            compact = true,
                            settings = settings,
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.DoubleArrow,
                                    contentDescription = translationViewModel.getString("Auto", lang),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                    }
                }
            }

            // Right: Contextual Wire Swatches + Workspace Switcher + Theme Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (viewModel.currentMode == CanvasViewModel.editingMode.WIRE) {
                    val colorButtonList = listOf(
                        colorButton(Color(0xFF1E3A8A), translationViewModel.getString("Blue", lang)),
                        colorButton(Color(0xFF141414), translationViewModel.getString("Black", lang)),
                        colorButton(Color(0xFFB91C1C), translationViewModel.getString("Red", lang)),
                        colorButton(Color(0xFF166534), translationViewModel.getString("Green", lang)),
                        colorButton(Color(0xFF7E22CE), translationViewModel.getString("Magenta", lang)),
                        colorButton(Color(0xFF155E75), translationViewModel.getString("Cyan", lang))
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .background(settings.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                            .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = translationViewModel.getString("Select Color: ", lang).trim(),
                            color = settings.secondaryTextColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        for (swatch in colorButtonList) {
                            val isSelected = viewModel.currentWiringColor == swatch.color
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                                tooltip = { PlainTooltip { Text(swatch.name) } },
                                state = rememberTooltipState()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(swatch.color)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) settings.accentTaupe else settings.borderColor,
                                            shape = RoundedCornerShape(2.dp)
                                        )
                                        .clickable { viewModel.currentWiringColor = swatch.color },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(Color.White, RoundedCornerShape(1.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Assistant Mode Toggle
                SwissButton(
                    text = translationViewModel.getString("Chat", lang),
                    onClick = {
                        appState.agenticMode = !appState.agenticMode
                        if (appState.agenticMode) appState.showSettings = false
                    },
                    isActive = appState.agenticMode,
                    compact = true,
                    settings = settings
                )

                // Settings Panel Toggle
                SwissButton(
                    text = translationViewModel.getString("Settings", lang),
                    onClick = {
                        appState.showSettings = !appState.showSettings
                        if (appState.showSettings) appState.agenticMode = false
                    },
                    isActive = appState.showSettings,
                    compact = true,
                    settings = settings
                )

                // Light / Dark Swiss Theme Toggle (R-21 & R-34)
                SwissButton(
                    text = if (settings.isDarkMode)
                        translationViewModel.getString("Basel Dark", lang)
                    else
                        translationViewModel.getString("Zurich Light", lang),
                    onClick = {
                        appState.toggleTheme()
                        scope.launch { saveState(appState) }
                    },
                    compact = true,
                    settings = settings
                )
            }
        }
        SwissHorizontalDivider(settings)
    }
}

private class colorButton(
    val color: Color,
    val name: String
)
