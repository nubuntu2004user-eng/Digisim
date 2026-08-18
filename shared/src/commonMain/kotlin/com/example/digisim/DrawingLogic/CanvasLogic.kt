package com.example.digisim.DrawingLogic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import com.example.digisim.ParsingLogic.DragState
import com.example.digisim.ParsingLogic.Wire

internal fun wirePins(viewModel : CanvasViewModel , position : Offset){
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
    }
}

internal fun handleDrag(viewModel: CanvasViewModel , position: Offset){
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

internal fun dragComponent(viewModel: CanvasViewModel, event : PointerEvent){
    viewModel.dragState?.let { state ->
        val position = event.changes.first().position
        val gate = viewModel.findGate(state.gateId)
        if (gate != null) {
            val newX = position.x - state.pointerOffset.x
            val newY = position.y - state.pointerOffset.y
            val index = viewModel.gates.indexOf(gate)
            if (index != -1) {
                viewModel.gates[index] = gate.copy(x = newX, y = newY)
            }
        }
    }
}


internal fun editComponent(viewModel: CanvasViewModel , position: Offset){

    val hitGate = viewModel.gates.findLast { gate ->
        position.x in gate.x..(gate.x + gate.width) &&
                position.y in gate.y..(gate.y + gate.height)
    }

    if (hitGate != null){

        viewModel.selectedGateId = hitGate.id
    }

}