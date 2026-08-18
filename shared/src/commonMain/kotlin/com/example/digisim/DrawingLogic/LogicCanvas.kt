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

                                        dragGate(viewModel, event)
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
                    sourceGate = viewModel.gates.find { it.id == wire.sourceGateId }!!,
                    sourcePortIndex = wire.sourcePortIndex,
                    targetGate = viewModel.gates.find { it.id == wire.targetGateId }!!,
                    targetPortIndex = wire.targetPortIndex
                )
            }

            // Draw gates
            viewModel.gates.forEach { gate ->
                drawGate(gate, textMeasurer)
            }


        }
    }

}




