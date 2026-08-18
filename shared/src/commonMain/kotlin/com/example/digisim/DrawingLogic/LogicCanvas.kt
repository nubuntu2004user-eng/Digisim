package com.example.digisim.DrawingLogic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt


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

                                    val hitPort = viewModel.findPortAt(position)
                                    if (hitPort != null) {
                                        if (viewModel.wireSource == null) {
                                            if (!hitPort.isInput) {
                                                viewModel.wireSource = hitPort
                                            }
                                        } else {
                                            if (hitPort.isInput) {
                                                val source = viewModel.wireSource!!
                                                viewModel.wires.add(
                                                    Wire(
                                                        id = viewModel.nextId++,
                                                        sourceGateId = source.gateId,
                                                        sourcePortIndex = source.portIndex,
                                                        targetGateId = hitPort.gateId,
                                                        targetPortIndex = hitPort.portIndex
                                                    )
                                                )
                                                viewModel.wireSource = null
                                            } else {
                                                viewModel.wireSource = hitPort
                                            }
                                        }
                                        continue
                                    }

                                    val hitGate = viewModel.gates.findLast { gate ->
                                        position.x in gate.x..(gate.x + gate.width) &&
                                                position.y in gate.y..(gate.y + gate.height)
                                    }
                                    if (hitGate != null) {
                                        viewModel.dragState = DragState(
                                            gateId = hitGate.id,
                                            pointerOffset = Offset(position.x - hitGate.x, position.y - hitGate.y),
                                            originalX = hitGate.x,
                                            originalY = hitGate.y
                                        )
                                    } else {
                                        viewModel.wireSource = null
                                    }
                                }
                                PointerEventType.Move -> {
                                    viewModel.dragState?.let { state ->
                                        val position = event.changes.first().position
                                        val gate = viewModel.findGate(state.gateId)
                                        if (gate != null) {
                                            val newX = position.x - state.pointerOffset.x
                                            val newY = position.y - state.pointerOffset.y
                                            // Replace the old gate with a new one (copy with updated position)
                                            val index = viewModel.gates.indexOf(gate)
                                            if (index != -1) {
                                                viewModel.gates[index] = gate.copy(x = newX, y = newY)
                                            }
                                        }
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




