package com.example.digisim.DrawingLogic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val canvasWidth = size.width.toFloat()
                            val canvasHeight = size.height.toFloat()
                            when (event.type) {
                                PointerEventType.Press -> {

                                    val position = event.changes.first().position

                                    if (viewModel.pendingComponent != null) {
                                        placePendingComponent(viewModel, position, canvasWidth, canvasHeight)
                                        continue
                                    }

                                    if (viewModel.currentMode == CanvasViewModel.editingMode.WIRE){   // do the wiring
                                        wirePins(viewModel, position)
                                        continue
                                    }

                                    else if (viewModel.currentMode == CanvasViewModel.editingMode.EDIT){
                                        editComponent(viewModel, position)

                                    }

                                    else if (viewModel.currentMode == CanvasViewModel.editingMode.POKE){
                                        pokeComponent(viewModel, simulation, scope, position)
                                    }

                                    handleDrag(viewModel, position)

                                }
                                PointerEventType.Move -> {
                                    val position = event.changes.first().position
                                    if (viewModel.pendingComponent != null) {
                                        updatePendingComponentPosition(viewModel, position, canvasWidth, canvasHeight)
                                    } else if (viewModel.currentMode == CanvasViewModel.editingMode.DRAG){  //do the draging
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
                                    viewModel.dragState = null
                                }
                            }

                        }
                    }
                }
        ) {
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

        }
    }
}






