package com.example.digisim.View

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.Persistance.loadWrapper
import com.example.digisim.Persistance.saveFileAs
import com.example.digisim.SettingsViewModel
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.TranslationViewModel
import com.example.digisim.UiUtils.undoOnce
import engineLogic.ClockManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun controlButtons(
    viewModel: CanvasViewModel,
    scope: CoroutineScope,
    translationViewModel: TranslationViewModel,
    clockManager: ClockManager,
    appState: AppState
) {
    val settings = SettingsViewModel.default
    val lang = appState.currentLanguage
    var showInputDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Swiss Typographic Brand Mark ("DIGISIM")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(end = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(settings.accentTaupe, RoundedCornerShape(1.dp))
                    .border(1.dp, settings.strongBorderColor, RoundedCornerShape(1.dp))
            )
            Text(
                text = "DIGISIM",
                color = settings.textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                fontFamily = FontFamily.SansSerif
            )
        }

        SwissVerticalDivider(modifier = Modifier.height(22.dp), settings = settings)

        // File Menu
        Box {
            var showFileMenu by remember { mutableStateOf(false) }
            SwissButton(
                text = translationViewModel.getString("File", lang),
                onClick = { showFileMenu = !showFileMenu },
                isActive = showFileMenu,
                compact = true,
                settings = settings
            )
            DropdownMenu(
                expanded = showFileMenu,
                onDismissRequest = { showFileMenu = false },
                modifier = Modifier
                    .background(settings.elevatedSurfaceColor)
                    .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            translationViewModel.getString("Load", lang),
                            color = settings.textColor,
                            fontSize = 12.sp
                        )
                    },
                    onClick = {
                        showFileMenu = false
                        scope.launch { loadWrapper(viewModel) }
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            translationViewModel.getString("Save as", lang),
                            color = settings.textColor,
                            fontSize = 12.sp
                        )
                    },
                    onClick = {
                        showFileMenu = false
                        scope.launch { saveFileAs(viewModel) }
                    }
                )
                SwissHorizontalDivider(settings)
                DropdownMenuItem(
                    text = {
                        Text(
                            translationViewModel.getString("Clear", lang),
                            color = settings.dangerColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    onClick = {
                        showFileMenu = false
                        viewModel.components.clear()
                        viewModel.wires.clear()
                        viewModel.selectedGateId = null
                        viewModel.nextId = 0
                        clockManager.tick = 0
                    }
                )
            }
        }

        // Edit Menu
        Box {
            var showEditMenu by remember { mutableStateOf(false) }
            SwissButton(
                text = translationViewModel.getString("Edit", lang),
                onClick = { showEditMenu = !showEditMenu },
                isActive = showEditMenu,
                compact = true,
                settings = settings
            )
            DropdownMenu(
                expanded = showEditMenu,
                onDismissRequest = { showEditMenu = false },
                modifier = Modifier
                    .background(settings.elevatedSurfaceColor)
                    .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
            ) {
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                translationViewModel.getString("Undo", lang),
                                color = settings.textColor,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "Ctrl+Z",
                                color = settings.secondaryTextColor,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    },
                    onClick = {
                        showEditMenu = false
                        undoOnce(viewModel)
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            translationViewModel.getString("Undo multiple", lang),
                            color = settings.textColor,
                            fontSize = 12.sp
                        )
                    },
                    onClick = {
                        showEditMenu = false
                        showInputDialog = true
                    }
                )
            }
        }

        // Direct Undo Quick Button
        SwissButton(
            text = translationViewModel.getString("Undo", lang),
            onClick = { undoOnce(viewModel) },
            compact = true,
            settings = settings
        )

        if (showInputDialog) {
            var userInput by remember { mutableStateOf("") }
            var hasError by remember { mutableStateOf(false) }
            AlertDialog(
                onDismissRequest = { showInputDialog = false },
                containerColor = settings.elevatedSurfaceColor,
                shape = RoundedCornerShape(2.dp),
                title = {
                    Text(
                        text = translationViewModel.getString("How many steps to undo", lang),
                        color = settings.textColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SwissTextField(
                            value = userInput,
                            onValueChange = {
                                userInput = it
                                hasError = false
                            },
                            label = translationViewModel.getString("Enter number here", lang),
                            placeholder = "e.g. 5",
                            settings = settings
                        )
                        if (hasError) {
                            Text(
                                text = translationViewModel.getString("Must be number!", lang),
                                color = settings.dangerColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                confirmButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SwissButton(
                            text = translationViewModel.getString("Cancel", lang),
                            onClick = { showInputDialog = false },
                            settings = settings
                        )
                        SwissButton(
                            text = translationViewModel.getString("Undo", lang),
                            isPrimary = true,
                            settings = settings,
                            onClick = {
                                val count = userInput.trim().toIntOrNull()
                                if (count != null && count > 0) {
                                    repeat(count) {
                                        undoOnce(viewModel)
                                    }
                                    showInputDialog = false
                                } else {
                                    hasError = true
                                }
                            }
                        )
                    }
                }
            )
        }
    }
}
