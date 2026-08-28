package com.example.digisim.DrawingLogic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SettingsViewModel
import com.example.digisim.SimulationHandling.SimulationViewModel
import kotlinx.coroutines.CoroutineScope


@Composable
fun DigitalLogicSimulator(
    modifier: Modifier = Modifier,
    viewModel: CanvasViewModel,
    simulation: SimulationViewModel? = null,
    scope: CoroutineScope? = null
) {

    val textMeasurer = rememberTextMeasurer()

    Row(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color.White)
                .clip(RectangleShape)
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
                                    lastPosition = position
                                    
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
                                        pokeComponent(viewModel, simulation, scope, position)
                                    }

                                    // Check if we hit a component
                                    val worldPosition = screenToWorld(position, viewModel.viewportX, viewModel.viewportY)
                                    val hitGate = viewModel.components.findLast { component ->
                                        worldPosition.x in component.x..(component.x + component.width) &&
                                                worldPosition.y in component.y..(component.y + component.height)
                                    }
                                    
                                    if (hitGate != null && viewModel.currentMode != CanvasViewModel.editingMode.POKE) {
                                        handleDrag(viewModel, position)
                                    } else if (hitGate == null) {
                                        isPanning = true
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
                                    if (viewModel.pendingComponent != null) {
                                        viewModel.pendingComponent?.x = -1000f
                                        viewModel.pendingComponent?.y = -1000f
                                    }
                                }
                                PointerEventType.Release -> {
                                    isPanning = false
                                    viewModel.dragState = null
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
