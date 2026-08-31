package com.example.digisim.DrawingLogic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import com.example.digisim.Wiring.Input
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.ParsingLogic.DragState
import com.example.digisim.ParsingLogic.PortHit
import com.example.digisim.ParsingLogic.Wire
import com.example.digisim.SimulationHandling.SimulationViewModel
import engineLogic.ClockManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val GRID_SIZE = 20f

private fun snap(value: Float): Float =
    kotlin.math.round(value / GRID_SIZE) * GRID_SIZE

fun screenToWorld(screenPos: Offset, viewportX: Float, viewportY: Float): Offset {
    return Offset(screenPos.x + viewportX, screenPos.y + viewportY)
}

internal fun pokeComponent(
    viewModel: CanvasViewModel,
    simulation: SimulationViewModel?,
    scope: CoroutineScope?,
    position: Offset ,
    clockManager: ClockManager
) {
    val worldPosition = screenToWorld(position, viewModel.viewportX, viewModel.viewportY)
    val hitGate = viewModel.components.findLast { component ->
        worldPosition.x in component.x..(component.x + component.width) &&
                worldPosition.y in component.y..(component.y + component.height)
    }

    if (hitGate is Input) {
        hitGate.switch()
        if (simulation != null && simulation.isRunning) {
            scope?.launch {
            simulation.runSimulation(viewModel, scope , clockManager)
            }
        }
    }
}

internal fun wirePins(viewModel: CanvasViewModel, position: Offset) {
    val worldPosition = screenToWorld(position, viewModel.viewportX, viewModel.viewportY)
    val hitPort = viewModel.findPortAt(worldPosition) ?: return
    
    if (viewModel.wireSource == null) {
        viewModel.wireSource = hitPort
    } else {
        val source = viewModel.wireSource!!
        if (source.elementId != hitPort.elementId && source.isInput != hitPort.isInput) {
            val (outPort, inPort) = if (!source.isInput) Pair(source, hitPort) else Pair(hitPort, source)
            val allowed = checkIfWireIsAllowed(outPort , inPort , viewModel)
            if (allowed){
            viewModel.wires.add(
                Wire(
                    id = viewModel.nextId++,
                    sourceGateId = outPort.elementId,
                    sourcePortIndex = outPort.portIndex,
                    targetGateId = inPort.elementId,
                    targetPortIndex = inPort.portIndex,
                    color = viewModel.currentWiringColor
                )
            )
            }
            viewModel.wireSource = null
        } else {
            viewModel.wireSource = hitPort
        }
    }
}

internal fun handleDrag(viewModel: CanvasViewModel , position: Offset){
    val worldPosition = screenToWorld(position, viewModel.viewportX, viewModel.viewportY)
    val hitGate = viewModel.components.findLast { component ->
        worldPosition.x in component.x..(component.x + component.width) &&
                worldPosition.y in component.y..(component.y + component.height)
    }
    if (hitGate != null) {
        viewModel.dragState = DragState(
            componentId = hitGate.ID,
            pointerOffset = Offset(worldPosition.x - hitGate.x, worldPosition.y - hitGate.y),
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
            val worldPosition = screenToWorld(position, viewModel.viewportX, viewModel.viewportY)
            val rawX = worldPosition.x - state.pointerOffset.x
            val rawY = worldPosition.y - state.pointerOffset.y
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
    val worldPosition = screenToWorld(position, viewModel.viewportX, viewModel.viewportY)
    val rawX = worldPosition.x - pending.width / 2f
    val rawY = worldPosition.y - pending.height / 2f
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
    val worldPosition = screenToWorld(position, viewModel.viewportX, viewModel.viewportY)
    val rawX = worldPosition.x - pending.width / 2f
    val rawY = worldPosition.y - pending.height / 2f
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
    val worldPosition = screenToWorld(position, viewModel.viewportX, viewModel.viewportY)
    val hitGate = viewModel.components.findLast { gate ->
        worldPosition.x in gate.x..(gate.x + gate.width) &&
                worldPosition.y in gate.y..(gate.y + gate.height)
    }

    if (hitGate != null){

        viewModel.selectedGateId = hitGate.ID
    }

}

internal fun checkIfWireIsAllowed(source : PortHit, target : PortHit, viewModel: CanvasViewModel): Boolean{
    val targetComponent = viewModel.components.find { it.ID == target.elementId }
    if (viewModel.wires.any {
        it.sourceGateId == source.elementId
        }&&
        viewModel.wires.any{
            it.sourcePortIndex == source.portIndex
        }&&
        viewModel.wires.any {
            it.targetGateId == target.elementId
        }&&
        viewModel.wires.any {
            it.targetPortIndex == target.portIndex
        }
        ) return false
    if (targetComponent?.componentType == ComponentType.OUTPUT){
        val wiresTo =viewModel.wires.filter { it.targetGateId == targetComponent.ID }
        if (wiresTo.size > 1) return false
    }
     return true
}