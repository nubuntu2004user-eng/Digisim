package com.example.digisim.DrawingLogic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.rememberTextMeasurer
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import engineLogic.ClockManager
import kotlinx.coroutines.CoroutineScope


@Composable
fun DigitalLogicSimulator(
    modifier: Modifier = Modifier,
    viewModel: CanvasViewModel,
    simulation: SimulationViewModel? = null,
    scope: CoroutineScope? = null,
    clockManager: ClockManager
) {

    val textMeasurer = rememberTextMeasurer()
    val canvasFocusRequester = remember { FocusRequester() }

    LaunchedEffect(
        viewModel.pendingComponent,
        viewModel.selectedGateId,
        viewModel.selectedGateIds.size,
        viewModel.currentMode
    ) {
        if (!viewModel.isTextInputFocused) {
            try {
                canvasFocusRequester.requestFocus()
            } catch (_: Exception) {
            }
        }
    }

    Row(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(SettingsViewModel.default.canvasBackgroundColor)
                .clip(RectangleShape)
                .focusRequester(canvasFocusRequester)
                .focusable()
                .testTag("logic_canvas")
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        var isPanning = false
                        var lastPosition = Offset.Zero
                        while (true) {
                            val event = awaitPointerEvent()
                            val canvasWidth = size.width.toFloat()
                            val canvasHeight = size.height.toFloat()
                            val position = event.changes.first().position

                            when (event.type) {
                                PointerEventType.Press -> {
                                    viewModel.isTextInputFocused = false
                                    try {
                                        canvasFocusRequester.requestFocus()
                                    } catch (_: Exception) {
                                    }
                                    lastPosition = position

                                    // Right mouse button pans around the canvas
                                    if (event.buttons.isSecondaryPressed) {
                                        isPanning = true
                                        continue
                                    }
                                    
                                    if (viewModel.pendingComponent != null) {
                                        placePendingComponent(viewModel, position, canvasWidth, canvasHeight)
                                        continue
                                    }

                                    if (viewModel.currentMode == CanvasViewModel.editingMode.WIRE){
                                        wirePins(viewModel, position)
                                        continue
                                    }

                                    else if (viewModel.currentMode == CanvasViewModel.editingMode.EDIT){
                                        editComponent(viewModel, position)
                                        continue
                                    }

                                    else if (viewModel.currentMode == CanvasViewModel.editingMode.POKE){
                                        pokeComponent(viewModel, simulation, scope, position , clockManager )
                                        continue
                                    }

                                    // Check if we hit a component in DRAG mode
                                    val worldPosition = screenToWorld(position, viewModel.viewportX, viewModel.viewportY)
                                    val hitGate = viewModel.components.findLast { component ->
                                        component.containsPoint(worldPosition)
                                    }
                                    
                                    if (hitGate != null && viewModel.currentMode != CanvasViewModel.editingMode.POKE) {
                                        handleDrag(viewModel, position)
                                    } else if (hitGate == null && viewModel.currentMode == CanvasViewModel.editingMode.DRAG) {
                                        startSelectionBox(viewModel, position)
                                    }
                                }
                                PointerEventType.Move -> {
                                    if (isPanning) {
                                        val delta = lastPosition - position
                                        viewModel.viewportX += delta.x
                                        viewModel.viewportY += delta.y
                                        lastPosition = position
                                    } else if (viewModel.pendingComponent != null) {
                                        updatePendingComponentPosition(viewModel, position, canvasWidth, canvasHeight)
                                    } else if (viewModel.selectionBoxStart != null) {
                                        updateSelectionBox(viewModel, position)
                                    } else if (viewModel.currentMode == CanvasViewModel.editingMode.DRAG){
                                        dragComponent(viewModel, event, canvasWidth, canvasHeight)
                                    }
                                }
                                PointerEventType.Enter -> {
                                    val position = event.changes.firstOrNull()?.position
                                    if (position != null && viewModel.pendingComponent != null) {
                                        updatePendingComponentPosition(viewModel, position, canvasWidth, canvasHeight)
                                    }
                                }
                                PointerEventType.Exit -> {
                                    // Keep pendingComponent off-screen until next Move/Press on canvas
                                }
                                PointerEventType.Release -> {
                                    isPanning = false
                                    viewModel.dragState = null
                                    if (viewModel.selectionBoxStart != null) {
                                        finishSelectionBox(viewModel)
                                    }
                                }
                            }
                        }
                    }
                }
        ) {
            val settings = SettingsViewModel.default
            val gridSize = settings.gridSize
            val gridColor = settings.gridColor
            
            // Draw Grid
            val canvasWidth = size.width
            val canvasHeight = size.height
            val startX = (viewModel.viewportX % gridSize) - gridSize
            val startY = (viewModel.viewportY % gridSize) - gridSize

            for (x in generateSequence(startX) { it + gridSize }.takeWhile { it < canvasWidth + gridSize }) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, canvasHeight), strokeWidth = 1f)
            }
            for (y in generateSequence(startY) { it + gridSize }.takeWhile { it < canvasHeight + gridSize }) {
                drawLine(gridColor, Offset(0f, y), Offset(canvasWidth, y), strokeWidth = 1f)
            }

            clipRect(0f, 0f, size.width, size.height) {
                drawContext.canvas.save()
                drawContext.canvas.translate(-viewModel.viewportX, -viewModel.viewportY)
                
                // Draw wires
                viewModel.wires.forEach { wire ->
                    val source = viewModel.components.find { it.ID == wire.sourceGateId }
                    val target = viewModel.components.find { it.ID == wire.targetGateId }
                    if (source != null && target != null) {
                        drawWire(
                            sourceComponent = source,
                            sourcePortIndex = wire.sourcePortIndex,
                            targetComponent = target,
                            targetPortIndex = wire.targetPortIndex,
                            color = wire.color
                        )
                    }
                }

                //Draw All
                viewModel.components.forEach { component ->
                    drawComponent(component , textMeasurer , SettingsViewModel.default)
                    val isSelected = viewModel.selectedGateId == component.ID || component.ID in viewModel.selectedGateIds
                    if (isSelected) {
                        val pad = 6f
                        val settings = SettingsViewModel.default
                        val bounds = component.rotatedBounds()
                        drawRect(
                            color = settings.accentTaupe,
                            topLeft = Offset(bounds.left - pad, bounds.top - pad),
                            size = androidx.compose.ui.geometry.Size(bounds.width + pad * 2f, bounds.height + pad * 2f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                        )
                        // Swiss technical corner ticks
                        val c = 5f
                        drawRect(color = settings.strongBorderColor, topLeft = Offset(bounds.left - pad - 2f, bounds.top - pad - 2f), size = androidx.compose.ui.geometry.Size(c, c))
                        drawRect(color = settings.strongBorderColor, topLeft = Offset(bounds.right + pad - 3f, bounds.top - pad - 2f), size = androidx.compose.ui.geometry.Size(c, c))
                        drawRect(color = settings.strongBorderColor, topLeft = Offset(bounds.left - pad - 2f, bounds.bottom + pad - 3f), size = androidx.compose.ui.geometry.Size(c, c))
                        drawRect(color = settings.strongBorderColor, topLeft = Offset(bounds.right + pad - 3f, bounds.bottom + pad - 3f), size = androidx.compose.ui.geometry.Size(c, c))
                    }
                }

                // Draw active rubber-band selection box
                val selStart = viewModel.selectionBoxStart
                val selEnd = viewModel.selectionBoxEnd
                if (selStart != null && selEnd != null) {
                    val left = minOf(selStart.x, selEnd.x)
                    val top = minOf(selStart.y, selEnd.y)
                    val width = kotlin.math.abs(selEnd.x - selStart.x)
                    val height = kotlin.math.abs(selEnd.y - selStart.y)
                    val settings = SettingsViewModel.default
                    drawRect(
                        color = settings.accentTaupe.copy(alpha = 0.15f),
                        topLeft = Offset(left, top),
                        size = androidx.compose.ui.geometry.Size(width, height)
                    )
                    drawRect(
                        color = settings.accentTaupe,
                        topLeft = Offset(left, top),
                        size = androidx.compose.ui.geometry.Size(width, height),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                    )
                }


                // Draw pending component (hovering)
                viewModel.pendingComponent?.let { pending ->
                    if (pending.x >= 0 && pending.y >= 0) {
                        drawComponent(pending, textMeasurer , SettingsViewModel.default)
                    }
                }

                // Highlight wire source if selected
                viewModel.wireSource?.let { source ->
                    drawCircle(
                        color = getWireHighlightColor(),
                        radius = 8f,
                        center = source.position
                    )
                }
                
                drawContext.canvas.restore()
            }
        }
    }
}
