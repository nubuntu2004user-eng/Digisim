package com.example.digisim.View

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.DrawingLogic.createComponent
import com.example.digisim.DrawingLogic.drawComponent
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.TranslationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolSelectorRow(
    viewModel: CanvasViewModel,
    simulationViewModel: SimulationViewModel,
    translationViewModel: TranslationViewModel,
    appState: AppState
) {
    val settings = SettingsViewModel.default
    val lang = appState.currentLanguage

    val buttonList = listOf(
        ToolButtonParams(
            mode = CanvasViewModel.editingMode.POKE,
            icon = Icons.Filled.TouchApp,
            onClick = {
                viewModel.currentMode = CanvasViewModel.editingMode.POKE
                viewModel.pendingComponent = null
            },
            enabled = simulationViewModel.isRunning,
            label = translationViewModel.getString("Poke", lang)
        ),
        ToolButtonParams(
            mode = CanvasViewModel.editingMode.EDIT,
            icon = Icons.Filled.Edit,
            onClick = {
                viewModel.currentMode = CanvasViewModel.editingMode.EDIT
                viewModel.pendingComponent = null
            },
            enabled = !simulationViewModel.isRunning,
            label = translationViewModel.getString("Edit", lang)
        ),
        ToolButtonParams(
            mode = CanvasViewModel.editingMode.DRAG,
            icon = Icons.Filled.AdsClick,
            onClick = {
                viewModel.currentMode = CanvasViewModel.editingMode.DRAG
                viewModel.pendingComponent = null
            },
            enabled = !simulationViewModel.isRunning,
            label = translationViewModel.getString("Drag", lang)
        ),
        ToolButtonParams(
            mode = CanvasViewModel.editingMode.WIRE,
            icon = Icons.Filled.Cable,
            onClick = {
                viewModel.currentMode = CanvasViewModel.editingMode.WIRE
                viewModel.pendingComponent = null
            },
            enabled = !simulationViewModel.isRunning,
            label = translationViewModel.getString("Wire", lang)
        )
    )

    val componentButtonList = listOf(
        ComponentButtonParams(ComponentType.INPUT) { viewModel.addComponent(ComponentType.INPUT) },
        ComponentButtonParams(ComponentType.OUTPUT) { viewModel.addComponent(ComponentType.OUTPUT) },
        ComponentButtonParams(ComponentType.NOT) { viewModel.addComponent(ComponentType.NOT) },
        ComponentButtonParams(ComponentType.AND) { viewModel.addComponent(ComponentType.AND) },
        ComponentButtonParams(ComponentType.OR) { viewModel.addComponent(ComponentType.OR) },
        ComponentButtonParams(ComponentType.XOR) { viewModel.addComponent(ComponentType.XOR) },
        ComponentButtonParams(ComponentType.NAND) { viewModel.addComponent(ComponentType.NAND) },
        ComponentButtonParams(ComponentType.NOR) { viewModel.addComponent(ComponentType.NOR) },
        ComponentButtonParams(ComponentType.XNOR) { viewModel.addComponent(ComponentType.XNOR) }
    )

    val previewComponents = remember {
        componentButtonList.associate {
            it.type to createComponent(it.type, id = -1, x = 0f, y = 0f, name = null, logic = null)
        }
    }
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(settings.panelBackgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 46.dp)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Section tag: MODE
            Text(
                text = translationViewModel.getString("MODE", lang),
                color = settings.secondaryTextColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(end = 2.dp)
            )

            for (tool in buttonList) {
                val isSelected = viewModel.currentMode == tool.mode && viewModel.pendingComponent == null
                SwissButton(
                    text = tool.label,
                    onClick = tool.onClick,
                    enabled = tool.enabled,
                    isActive = isSelected,
                    compact = true,
                    settings = settings,
                    leadingContent = {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = tool.label,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.width(6.dp))
            SwissVerticalDivider(modifier = Modifier.height(24.dp), settings = settings)
            Spacer(modifier = Modifier.width(6.dp))

            // Section tag: QUICK INSERT
            Text(
                text = translationViewModel.getString("QUICK INSERT", lang),
                color = settings.secondaryTextColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(end = 2.dp)
            )

            for (compBtn in componentButtonList) {
                val preview = previewComponents[compBtn.type]
                val isPending = viewModel.pendingComponent?.componentType == compBtn.type
                val label = translationViewModel.getString(compBtn.type.name, lang)

                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    tooltip = { PlainTooltip { Text(label) } },
                    state = rememberTooltipState()
                ) {
                    Surface(
                        onClick = compBtn.onClick,
                        enabled = !simulationViewModel.isRunning,
                        shape = RoundedCornerShape(2.dp),
                        color = if (isPending) settings.primaryControlFill else settings.elevatedSurfaceColor,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isPending) settings.accentTaupe else settings.borderColor
                        ),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (preview != null) {
                                Canvas(modifier = Modifier.size(28.dp, 20.dp)) {
                                    val totalW = preview.width + 16f
                                    val totalH = preview.height + 16f
                                    val scale = minOf(size.width / totalW, size.height / totalH)
                                    val offsetX = (size.width - preview.width * scale) / 2f
                                    val offsetY = (size.height - preview.height * scale) / 2f

                                    val previewSettings = SettingsViewModel(drawText = false).also {
                                        it.applyTheme(settings.isDarkMode)
                                    }

                                    withTransform({
                                        translate(left = offsetX, top = offsetY)
                                        scale(scale, scale, pivot = Offset.Zero)
                                    }) {
                                        drawComponent(preview, textMeasurer, settings = previewSettings)
                                    }
                                }
                            }
                            Text(
                                text = compBtn.type.name,
                                color = if (isPending) settings.primaryControlText else settings.textColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
        SwissHorizontalDivider(settings)
    }
}

private data class ToolButtonParams(
    val mode: CanvasViewModel.editingMode,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val enabled: Boolean,
    val label: String
)

private data class ComponentButtonParams(
    val type: ComponentType,
    val onClick: () -> Unit
)
