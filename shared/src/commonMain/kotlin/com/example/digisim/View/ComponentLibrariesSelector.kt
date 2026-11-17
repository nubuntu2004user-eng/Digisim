package com.example.digisim.View

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.DrawingLogic.createComponent
import com.example.digisim.DrawingLogic.drawComponent
import com.example.digisim.LibraryLoader.addCustomComponent
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.Persistance.persistanceRepository.saveState
import com.example.digisim.SettingsViewModel
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.ComponentLibrariesViewModel
import com.example.digisim.UiUtils.Libraries
import com.example.digisim.UiUtils.TranslationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun componentLibraries(
    viewModel: CanvasViewModel,
    libraryViewModel: ComponentLibrariesViewModel,
    translationViewModel: TranslationViewModel,
    appState: AppState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var showNewLibraryDialog by remember { mutableStateOf(false) }
    val settings = SettingsViewModel.default
    val lang = appState.currentLanguage

    val safeIndex = libraryViewModel.currentLibraryIndex.coerceIn(0, (libraryViewModel.libraries.size - 1).coerceAtLeast(0))
    if (safeIndex != libraryViewModel.currentLibraryIndex) {
        libraryViewModel.currentLibraryIndex = safeIndex
    }
    val selectedLibrary = libraryViewModel.libraries[safeIndex]
    val componentList = selectedLibrary.components
    val currentLibraryName = selectedLibrary.name

    val previewComponents = remember(componentList.size, currentLibraryName) {
        componentList.associate {
            it.name to createComponent(
                it.type,
                id = -1,
                x = 0f,
                y = 0f,
                inputCount = if (it.inputCount > 0) it.inputCount else 2,
                outputCount = if (it.outputCount > 0) it.outputCount else 1,
                name = it.name,
                logic = it.abstractedLogic
            )
        }
    }
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(settings.panelBackgroundColor)
    ) {
        // Section Header: 01 / COMPONENT LIBRARY
        SwissSectionHeader(
            indexAndTitle = translationViewModel.getString("01 / COMPONENT LIBRARY", lang),
            settings = settings,
            trailingContent = {
                SwissButton(
                    text = "+ " + translationViewModel.getString("New Library", lang),
                    onClick = { showNewLibraryDialog = true },
                    compact = true,
                    settings = settings
                )
            }
        )

        // Category Tabs Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(settings.panelBackgroundColor)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            libraryViewModel.libraries.forEachIndexed { idx, lib ->
                val isSelected = idx == safeIndex
                SwissButton(
                    text = translationViewModel.getString(lib.name, lang),
                    onClick = { libraryViewModel.currentLibraryIndex = idx },
                    isActive = isSelected,
                    compact = true,
                    settings = settings
                )
            }
        }
        SwissHorizontalDivider(settings)

        // Component Grid List
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (componentList.isEmpty() && selectedLibrary.isMutable) {
                // Explicit Empty State for user-created libraries (R-27)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(settings.elevatedSurfaceColor, RoundedCornerShape(2.dp))
                        .border(1.dp, settings.borderColor, RoundedCornerShape(2.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = translationViewModel.getString(
                            "Empty custom library. Import a circuit JSON file to create a reusable sub-component.",
                            lang
                        ),
                        color = settings.secondaryTextColor,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            } else {
                // 2-column Swiss Grid of schematic component tiles
                val rows = componentList.chunked(2)
                for (rowItems in rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (item in rowItems) {
                            val isPending = viewModel.pendingComponent?.componentType == item.type &&
                                    (item.type != ComponentType.CUSTOM || viewModel.pendingComponent?.customComponentName == item.name)

                            Box(modifier = Modifier.weight(1f)) {
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                                    tooltip = {
                                        PlainTooltip {
                                            Text(translationViewModel.getString(item.description, lang))
                                        }
                                    },
                                    state = rememberTooltipState()
                                ) {
                                    Surface(
                                        onClick = {
                                            if (item.type == ComponentType.CUSTOM) {
                                                viewModel.addComponent(
                                                    item.type,
                                                    item.abstractedLogic,
                                                    item.name,
                                                    item.inputCount,
                                                    item.outputCount
                                                )
                                            } else {
                                                viewModel.addComponent(item.type)
                                            }
                                        },
                                        shape = RoundedCornerShape(2.dp),
                                        color = if (isPending) settings.primaryControlFill else settings.elevatedSurfaceColor,
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = if (isPending) settings.accentTaupe else settings.borderColor
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // Miniature schematic symbol preview box
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp, 30.dp)
                                                    .background(
                                                        settings.canvasBackgroundColor,
                                                        RoundedCornerShape(2.dp)
                                                    )
                                                    .border(
                                                        1.dp,
                                                        settings.borderColor,
                                                        RoundedCornerShape(2.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (item.icon != null) {
                                                    Icon(
                                                        imageVector = item.icon,
                                                        contentDescription = item.name,
                                                        tint = settings.textColor
                                                    )
                                                } else {
                                                    val preview = previewComponents[item.name]
                                                    if (preview != null) {
                                                        Canvas(modifier = Modifier.size(34.dp, 24.dp)) {
                                                            val totalW = preview.width + 16f
                                                            val totalH = preview.height + 16f
                                                            val scale = minOf(
                                                                size.width / totalW,
                                                                size.height / totalH
                                                            )
                                                            val offsetX =
                                                                (size.width - preview.width * scale) / 2f
                                                            val offsetY =
                                                                (size.height - preview.height * scale) / 2f

                                                            val previewSettings = SettingsViewModel(drawText = false).also {
                                                                it.applyTheme(settings.isDarkMode)
                                                            }

                                                            withTransform({
                                                                translate(left = offsetX, top = offsetY)
                                                                scale(scale, scale, pivot = Offset.Zero)
                                                            }) {
                                                                drawComponent(
                                                                    preview,
                                                                    textMeasurer,
                                                                    settings = previewSettings
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            Text(
                                                text = translationViewModel.getString(item.name, lang),
                                                color = if (isPending) settings.primaryControlText else settings.textColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                lineHeight = 13.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Mutable Custom Library Controls
            if (selectedLibrary.isMutable) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SwissButton(
                        text = translationViewModel.getString("Import Component (.json)", lang),
                        onClick = {
                            scope.launch {
                                addCustomComponent(libraryViewModel, selectedLibrary.name, appState)
                                saveState(appState)
                            }
                        },
                        isPrimary = true,
                        compact = true,
                        settings = settings,
                        modifier = Modifier.weight(1f),
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                    SwissButton(
                        text = translationViewModel.getString("Delete Library", lang),
                        onClick = {
                            scope.launch {
                                libraryViewModel.libraries.remove(selectedLibrary)
                                libraryViewModel.previousLib()
                                val toRemove = appState.libraries.find { it.name == selectedLibrary.name }
                                appState.libraries.remove(toRemove)
                                saveState(appState)
                            }
                        },
                        isDanger = true,
                        compact = true,
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

        SwissHorizontalDivider(settings)
    }

    if (showNewLibraryDialog) {
        var userInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewLibraryDialog = false },
            containerColor = settings.elevatedSurfaceColor,
            shape = RoundedCornerShape(2.dp),
            title = {
                Text(
                    translationViewModel.getString("New library name", lang),
                    color = settings.textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                SwissTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = translationViewModel.getString("Enter name here", lang),
                    placeholder = "e.g. ALU Modules",
                    settings = settings
                )
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SwissButton(
                        text = translationViewModel.getString("Cancel", lang),
                        onClick = { showNewLibraryDialog = false },
                        settings = settings
                    )
                    SwissButton(
                        text = translationViewModel.getString("Add library", lang),
                        isPrimary = true,
                        settings = settings,
                        onClick = {
                            val trimmed = userInput.trim()
                            if (trimmed.isNotEmpty()) {
                                libraryViewModel.libraries.add(
                                    Libraries(trimmed, mutableStateListOf(), true)
                                )
                                libraryViewModel.currentLibraryIndex = libraryViewModel.libraries.lastIndex
                                showNewLibraryDialog = false
                            }
                        }
                    )
                }
            }
        )
    }
}
