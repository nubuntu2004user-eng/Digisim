package com.example.digisim.View

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.TranslationViewModel

@Composable
fun componentSettings(
    viewModel: CanvasViewModel,
    simulationViewModel: SimulationViewModel,
    translationViewModel: TranslationViewModel,
    appState: AppState,
    modifier: Modifier = Modifier
) {
    val settings = SettingsViewModel.default
    val lang = appState.currentLanguage
    val selectedComponent = viewModel.components.find { it.ID == viewModel.selectedGateId }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(settings.panelBackgroundColor)
    ) {
        SwissSectionHeader(
            indexAndTitle = translationViewModel.getString("02 / INSPECTOR", lang),
            settings = settings,
            trailingContent = {
                if (selectedComponent != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        SwissButton(
                            text = "↺",
                            onClick = { selectedComponent.rotateLeft() },
                            modifier = Modifier.testTag("rotate_left_button"),
                            compact = true,
                            settings = settings
                        )
                        SwissButton(
                            text = "↻",
                            onClick = { selectedComponent.rotateRight() },
                            modifier = Modifier.testTag("rotate_right_button"),
                            compact = true,
                            settings = settings
                        )
                    }
                    SwissMonoBadge(
                        text = "ID:${selectedComponent.ID}",
                        settings = settings,
                        highlight = true
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedComponent == null) {
                // Explicit Empty State (R-27)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(settings.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                        .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = translationViewModel.getString("No component selected", lang),
                        color = settings.textColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = translationViewModel.getString(
                            "Select the EDIT tool and click any component on the schematic canvas to inspect its ports, label, and clock parameters.",
                            lang
                        ),
                        color = settings.secondaryTextColor,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                    if (!simulationViewModel.isRunning && viewModel.currentMode != CanvasViewModel.editingMode.EDIT) {
                        SwissButton(
                            text = translationViewModel.getString("Switch to Edit Mode", lang),
                            onClick = {
                                viewModel.currentMode = CanvasViewModel.editingMode.EDIT
                                viewModel.pendingComponent = null
                            },
                            isPrimary = true,
                            compact = true,
                            settings = settings
                        )
                    }
                }
            } else {
                val wiresIn = viewModel.wires.filter { it.targetGateId == selectedComponent.ID }
                val wiresOut = viewModel.wires.filter { it.sourceGateId == selectedComponent.ID }

                // Technical Metadata Table Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(settings.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                        .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = translationViewModel.getString("Component type: ", lang).trim(),
                            color = settings.secondaryTextColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        SwissMonoBadge(
                            text = selectedComponent.componentType.name,
                            settings = settings
                        )
                    }

                    SwissHorizontalDivider(settings)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "COORDINATES",
                            color = settings.secondaryTextColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "(${selectedComponent.x.toInt()}, ${selectedComponent.y.toInt()})",
                            color = settings.textColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Component Label Field
                var userLabel by remember(selectedComponent.ID, selectedComponent.label) {
                    mutableStateOf(selectedComponent.label)
                }
                SwissTextField(
                    value = userLabel,
                    onValueChange = { userLabel = it },
                    modifier = Modifier.onFocusChanged { viewModel.isTextInputFocused = it.isFocused },
                    label = translationViewModel.getString("Component Label", lang),
                    placeholder = selectedComponent.componentType.name,
                    settings = settings,
                    trailingIcon = {
                        IconButton(onClick = { selectedComponent.label = userLabel }) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = translationViewModel.getString("Apply", lang),
                                tint = settings.textColor
                            )
                        }
                    }
                )

                // Multi-input Gate Input Count Control
                val isMultiInputGate = selectedComponent.componentType in listOf(
                    ComponentType.AND,
                    ComponentType.OR,
                    ComponentType.NAND,
                    ComponentType.NOR,
                    ComponentType.XOR,
                    ComponentType.XNOR,
                    ComponentType.ODD_PARITY,
                    ComponentType.EVEN_PARITY
                )
                if (isMultiInputGate) {
                    var userInputCount by remember(selectedComponent.ID, selectedComponent.inputCount) {
                        mutableStateOf(selectedComponent.inputCount.toString())
                    }
                    SwissTextField(
                        value = userInputCount,
                        onValueChange = { userInputCount = it },
                        modifier = Modifier.onFocusChanged { viewModel.isTextInputFocused = it.isFocused },
                        label = translationViewModel.getString("Input count: ", lang) + selectedComponent.inputCount,
                        placeholder = translationViewModel.getString("Change input count", lang),
                        settings = settings,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    userInputCount.trim().toIntOrNull()?.let { count ->
                                        selectedComponent.inputCount = count.coerceIn(2, 64)
                                        userInputCount = selectedComponent.inputCount.toString()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = translationViewModel.getString("Apply", lang),
                                    tint = settings.textColor
                                )
                            }
                        }
                    )
                }

                // Clock Frequency Selector
                if (selectedComponent.componentType == ComponentType.CLOCK) {
                    val frequencyHz = toggleDelayTicksToFrequencyHz(selectedComponent.delay)
                    var dropdownExpanded by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(settings.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                            .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = translationViewModel.getString("Frequency", lang).uppercase(),
                                color = settings.secondaryTextColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.6.sp
                            )
                            SwissMonoBadge(
                                text = if (frequencyHz != null)
                                    "${frequencyHz.toInt()} ${translationViewModel.getString("Hz", lang)}"
                                else
                                    translationViewModel.getString("Not set", lang),
                                settings = settings,
                                highlight = true
                            )
                        }

                        Box {
                            SwissButton(
                                text = translationViewModel.getString("Change frequency", lang),
                                onClick = { dropdownExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                settings = settings
                            )
                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier
                                    .background(settings.elevatedSurfaceColor)
                                    .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
                            ) {
                                val frequencies = listOf(500f, 250f, 125f, 100f, 50f, 25f, 20f, 10f, 5f, 4f, 1f)
                                frequencies.forEach { freq ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "${freq.toInt()} " + translationViewModel.getString("Hz", lang),
                                                color = settings.textColor,
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        },
                                        onClick = {
                                            val delayTicks = frequencyHzToToggleDelayTicks(freq)
                                            if (delayTicks != null) {
                                                selectedComponent.delay = delayTicks
                                            }
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Incoming Wires Table
                if (wiresIn.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(settings.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                            .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = translationViewModel.getString("Wires in: ", lang).uppercase(),
                            color = settings.secondaryTextColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                        wiresIn.forEach { wireIn ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SRC:${wireIn.sourceGateId} [P${wireIn.sourcePortIndex}]",
                                    color = settings.textColor,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                SwissButton(
                                    text = translationViewModel.getString("Delete", lang),
                                    onClick = { viewModel.wires.remove(wireIn) },
                                    isDanger = true,
                                    compact = true,
                                    settings = settings
                                )
                            }
                        }
                    }
                }

                // Outgoing Wires Table
                if (wiresOut.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(settings.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                            .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = translationViewModel.getString("Wires Out: ", lang).uppercase(),
                            color = settings.secondaryTextColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                        wiresOut.forEach { wireOut ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "DST:${wireOut.targetGateId} [P${wireOut.targetPortIndex}]",
                                    color = settings.textColor,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                SwissButton(
                                    text = translationViewModel.getString("Delete", lang),
                                    onClick = { viewModel.wires.remove(wireOut) },
                                    isDanger = true,
                                    compact = true,
                                    settings = settings
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Delete Component Action
                SwissButton(
                    text = translationViewModel.getString("Delete Component", lang),
                    onClick = { viewModel.deleteActiveComponent() },
                    isDanger = true,
                    modifier = Modifier.fillMaxWidth(),
                    settings = settings,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                )
            }
        }
    }
}

private const val TICKS_PER_SECOND = 1000.0f

private fun frequencyHzToToggleDelayTicks(frequencyHz: Float?): Float? {
    if (frequencyHz == null || frequencyHz <= 0.0f) {
        return null
    }
    return TICKS_PER_SECOND / (frequencyHz * 2.0f)
}

private fun toggleDelayTicksToFrequencyHz(delayTicks: Float?): Float? {
    if (delayTicks == null || delayTicks <= 0.0f) {
        return null
    }
    return TICKS_PER_SECOND / (delayTicks * 2.0f)
}
