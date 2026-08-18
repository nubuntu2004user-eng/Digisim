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


@Composable
fun DigitalLogicSimulator(modifier: Modifier = Modifier , viewModel : CanvasViewModel) {

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
                            when (event.type) {
                                PointerEventType.Press -> {

                                    val position = event.changes.first().position

                                    if (viewModel.currentMode == CanvasViewModel.editingMode.WIRE){   // do the wiring
                                        wirePins(viewModel, position)
                                        continue
                                    }

                                    else if (viewModel.currentMode == CanvasViewModel.editingMode.EDIT){
                                        editComponent(viewModel, position)

                                    }

                                handleDrag(viewModel, position)

                                }
                                PointerEventType.Move -> {
                                    if (viewModel.currentMode == CanvasViewModel.editingMode.DRAG){  //do the draging

                                        dragComponent(viewModel, event)
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
                drawWire(
                    sourceComponent = viewModel.components.find { it.ID == wire.sourceGateId }!!,
                    sourcePortIndex = wire.sourcePortIndex,
                    targetComponent = viewModel.components.find { it.ID == wire.targetGateId }!!,
                    targetPortIndex = wire.targetPortIndex
                )
            }

            //Draw All
            viewModel.components.forEach { component ->
                drawComponent(component , textMeasurer)
            }

            // Draw gates
//            viewModel.components.forEach { component ->
//                if (component.componentType == ComponentType.GATE){
//                drawGate(component, textMeasurer)
//                }
//            }
//
//            // Draw InputsAndOutputs
//            viewModel.components.forEach { component ->
//                if (component.componentType == ComponentType.INPUT || component.componentType == ComponentType.OUTPUT){
//                    drawInputOrOutput(component, textMeasurer)
//                }

            }




        }
    }






