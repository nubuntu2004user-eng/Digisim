package com.example.digisim.DrawingLogic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import com.example.digisim.LogicGates.Input
import com.example.digisim.ParsingLogic.DragState
import com.example.digisim.ParsingLogic.Wire
import com.example.digisim.SimulationHandling.SimulationViewModel
import kotlinx.coroutines.CoroutineScope

private const val GRID_SIZE = 20f

private fun snap(value: Float): Float =
    kotlin.math.round(value / GRID_SIZE) * GRID_SIZE

internal fun pokeComponent(
    viewModel: CanvasViewModel,
    simulation: SimulationViewModel?,
    scope: CoroutineScope?,
    position: Offset
) {
    val hitGate = viewModel.components.findLast { component ->
        position.x in component.x..(component.x + component.width) &&
                position.y in component.y..(component.y + component.height)
    }

    if (hitGate is Input) {
        hitGate.switch()
        if (simulation != null && simulation.isRunning) {
            simulation.runSimulation(viewModel, scope)
        }
    }
}

internal fun wirePins(viewModel: CanvasViewModel, position: Offset) {
    val hitPort = viewModel.findPortAt(position)
    if (hitPort != null) {
        if (viewModel.wireSource == null) {
            viewModel.wireSource = hitPort
        } else {
            val source = viewModel.wireSource!!
            if (source.elementId != hitPort.elementId && source.isInput != hitPort.isInput) {
                val (outPort, inPort) = if (!source.isInput) Pair(source, hitPort) else Pair(hitPort, source)
                viewModel.wires.add(
                    Wire(
                        id = viewModel.nextId++,
                        sourceGateId = outPort.elementId,
                        sourcePortIndex = outPort.portIndex,
                        targetGateId = inPort.elementId,
                        targetPortIndex = inPort.portIndex
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

internal fun dragComponent(
    viewModel: CanvasViewModel,
    position: Offset,
    canvasWidth: Float = Float.MAX_VALUE,
    canvasHeight: Float = Float.MAX_VALUE
) {
    viewModel.dragState?.let { state ->
        val component = viewModel.findGate(state.componentId)
        if (component != null) {
            val rawX = position.x - state.pointerOffset.x
            val rawY = position.y - state.pointerOffset.y
            val maxX = (canvasWidth - component.width).coerceAtLeast(0f)
            val maxY = (canvasHeight - component.height).coerceAtLeast(0f)
            val newX = snap(rawX).coerceIn(0f, maxX)
            val newY = snap(rawY).coerceIn(0f, maxY)
            val index = viewModel.components.indexOf(component)
            if (index != -1) {
                viewModel.components[index].x = newX
                viewModel.components[index].y = newY
            }
        }
    }
}

internal fun dragComponent(
    viewModel: CanvasViewModel,
    event: PointerEvent,
    canvasWidth: Float = Float.MAX_VALUE,
    canvasHeight: Float = Float.MAX_VALUE
) {
    val position = event.changes.first().position
    dragComponent(viewModel, position, canvasWidth, canvasHeight)
}

internal fun updatePendingComponentPosition(
    viewModel: CanvasViewModel,
    position: Offset,
    canvasWidth: Float = Float.MAX_VALUE,
    canvasHeight: Float = Float.MAX_VALUE
) {
    val pending = viewModel.pendingComponent ?: return
    val rawX = position.x - pending.width / 2f
    val rawY = position.y - pending.height / 2f
    val maxX = (canvasWidth - pending.width).coerceAtLeast(0f)
    val maxY = (canvasHeight - pending.height).coerceAtLeast(0f)
    pending.x = snap(rawX).coerceIn(0f, maxX)
    pending.y = snap(rawY).coerceIn(0f, maxY)
}

internal fun placePendingComponent(
    viewModel: CanvasViewModel,
    position: Offset,
    canvasWidth: Float = Float.MAX_VALUE,
    canvasHeight: Float = Float.MAX_VALUE
) {
    val pending = viewModel.pendingComponent ?: return
    val rawX = position.x - pending.width / 2f
    val rawY = position.y - pending.height / 2f
    val maxX = (canvasWidth - pending.width).coerceAtLeast(0f)
    val maxY = (canvasHeight - pending.height).coerceAtLeast(0f)
    val finalX = snap(rawX).coerceIn(0f, maxX)
    val finalY = snap(rawY).coerceIn(0f, maxY)

    val placedComponent = createComponent(
        type = pending.componentType,
        id = viewModel.nextId++,
        x = finalX,
        y = finalY
    )
    viewModel.components.add(placedComponent)
    viewModel.pendingComponent = null
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