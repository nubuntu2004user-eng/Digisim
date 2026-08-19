package com.example.digisim.DrawingLogic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import com.example.digisim.ParsingLogic.DragState
import com.example.digisim.ParsingLogic.Wire

private const val GRID_SIZE = 20f

private fun snap(value: Float): Float =
    kotlin.math.round(value / GRID_SIZE) * GRID_SIZE

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
                        sourceGateId = source.elementId,
                        sourcePortIndex = source.portIndex,
                        targetGateId = hitPort.elementId,
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
    val hitGate = viewModel.components.findLast { component ->
        position.x in component.x..(component.x + component.width) &&
                position.y in component.y..(component.y + component.height)
    }
    if (hitGate != null) {
        viewModel.dragState = DragState(
            componentId = hitGate.ID,
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
        val component = viewModel.findGate(state.componentId)
        if (component != null) {
            val newX = snap(position.x - state.pointerOffset.x)
            val newY = snap(position.y - state.pointerOffset.y)
            val index = viewModel.components.indexOf(component)
            if (index != -1) {
                viewModel.components[index].x = newX
                viewModel.components[index].y = newY
            }
        }
    }
}


internal fun editComponent(viewModel: CanvasViewModel , position: Offset){

    val hitGate = viewModel.components.findLast { gate ->
        position.x in gate.x..(gate.x + gate.width) &&
                position.y in gate.y..(gate.y + gate.height)
    }

    if (hitGate != null){

        viewModel.selectedGateId = hitGate.ID
    }

}