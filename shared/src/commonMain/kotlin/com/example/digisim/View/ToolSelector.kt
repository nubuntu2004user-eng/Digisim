package com.example.digisim.View

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.DrawingLogic.createComponent
import com.example.digisim.DrawingLogic.drawComponent
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.TranslationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolSelectorRow(viewModel: CanvasViewModel , simulationViewModel: SimulationViewModel, translationViewModel: TranslationViewModel) {
    val buttonList = listOf(
        ToolButtonParams(Icons.Filled.TouchApp, {
            viewModel.currentMode = CanvasViewModel.editingMode.POKE
            viewModel.pendingComponent = null },
            simulationViewModel.isRunning,
            translationViewModel.getString("Poke")
            ),
        ToolButtonParams(Icons.Filled.Edit, {
            viewModel.currentMode = CanvasViewModel.editingMode.EDIT
            viewModel.pendingComponent = null },
            !simulationViewModel.isRunning,
            translationViewModel.getString("Edit")
            ),
        ToolButtonParams(Icons.Filled.AdsClick, {
            viewModel.currentMode = CanvasViewModel.editingMode.DRAG
            viewModel.pendingComponent = null },
            !simulationViewModel.isRunning,
            translationViewModel.getString("Drag")
            ),
        ToolButtonParams(Icons.Filled.Cable, {
            viewModel.currentMode = CanvasViewModel.editingMode.WIRE
            viewModel.pendingComponent = null },
            !simulationViewModel.isRunning,
            translationViewModel.getString("Wire")
            ),
        ToolButtonParams(Icons.Filled.TextFields, {
            viewModel.pendingComponent = null },
            !simulationViewModel.isRunning,
            translationViewModel.getString("Text")
        ),
    )

    val componentButtonList = listOf(
        ComponentButtonParams(ComponentType.INPUT, { viewModel.addComponent(ComponentType.INPUT) }),
        ComponentButtonParams(ComponentType.OUTPUT, { viewModel.addComponent(ComponentType.OUTPUT) }),
        ComponentButtonParams(ComponentType.NOT, { viewModel.addComponent(ComponentType.NOT) }),
        ComponentButtonParams(ComponentType.AND, { viewModel.addComponent(ComponentType.AND) }),
        ComponentButtonParams(ComponentType.OR, { viewModel.addComponent(ComponentType.OR) }),
        ComponentButtonParams(ComponentType.XOR, { viewModel.addComponent(ComponentType.XOR) }),
        ComponentButtonParams(ComponentType.NAND, { viewModel.addComponent(ComponentType.NAND) }),
        ComponentButtonParams(ComponentType.NOR, { viewModel.addComponent(ComponentType.NOR) }),
        ComponentButtonParams(ComponentType.XNOR, { viewModel.addComponent(ComponentType.XNOR) }),
    )

    val previewComponents = remember {
        componentButtonList.associate { it.type to createComponent(it.type, id = -1, x = 0f, y = 0f) }
    }
    val previewSettings = remember { SettingsViewModel(drawText = false) }
    val textMeasurer = rememberTextMeasurer()

    Row {
        for (i in buttonList) {
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text(i.tooltip)
                    }
                },
                state = rememberTooltipState()
            ){
            IconButton(onClick = i.onClick , enabled = i.enabled) {
                Icon(
                    imageVector = i.icon,
                    contentDescription = null
                )
                }
            }
        }
        for (i in componentButtonList) {
            val preview = previewComponents[i.type]
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text(i.type.name)
                    }
                },
                state = rememberTooltipState()
            ) {
                IconButton(onClick = i.onClick) {
                    if (preview != null) {
                        Canvas(modifier = Modifier.size(36.dp, 28.dp)) {
                            val totalW = preview.width + 16f
                            val totalH = preview.height + 16f
                            val scale = minOf(size.width / totalW, size.height / totalH)
                            val offsetX = (size.width - preview.width * scale) / 2f
                            val offsetY = (size.height - preview.height * scale) / 2f

                            withTransform({
                                translate(left = offsetX, top = offsetY)
                                scale(scale, scale, pivot = Offset.Zero)
                            }) {
                                drawComponent(preview, textMeasurer, settings = previewSettings)
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class ToolButtonParams(
    val icon: ImageVector,
    val onClick: () -> Unit,
    val enabled : Boolean,
    val tooltip : String

)

private data class ComponentButtonParams(
    val type: ComponentType,
    val onClick: () -> Unit
)