package com.example.digisim.DrawingLogic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEvent
import com.example.digisim.Wiring.Button
import com.example.digisim.Wiring.Input
import com.example.digisim.StandardComponents.DipSwitch4BitComp
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
        component.containsPoint(worldPosition)
    }

    if (hitGate is Input) {
        hitGate.switch()
        if (simulation != null && simulation.isRunning) {
            if (scope != null) {
                scope.launch {
                    simulation.runSimulation(viewModel, scope, clockManager)
                }
            } else {
                kotlinx.coroutines.runBlocking {
                    simulation.runSimulation(viewModel, null, clockManager)
                }
            }
        }
    } else if (hitGate is Button) {
        hitGate.press()
        if (simulation != null && simulation.isRunning) {
            if (scope != null) {
                scope.launch {
                    simulation.runSimulation(viewModel, scope, clockManager)
                }
            } else {
                kotlinx.coroutines.runBlocking {
                    simulation.runSimulation(viewModel, null, clockManager)
                }
            }
        }
    } else if (hitGate is DipSwitch4BitComp) {
        val outPositions = hitGate.rotatedOutputPortPositions()
        val closestIndex = outPositions.indices.minByOrNull { idx ->
            viewModel.distance(worldPosition, outPositions[idx])
        } ?: 0
        hitGate.toggleSwitch(closestIndex)
        if (simulation != null && simulation.isRunning) {
            if (scope != null) {
                scope.launch {
                    simulation.runSimulation(viewModel, scope, clockManager)
                }
            } else {
                kotlinx.coroutines.runBlocking {
                    simulation.runSimulation(viewModel, null, clockManager)
                }
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
        component.containsPoint(worldPosition)
    }
    if (hitGate != null) {
        if (hitGate.ID !in viewModel.selectedGateIds) {
            viewModel.selectSingleGate(hitGate.ID)
        } else {
            viewModel.selectedGateId = hitGate.ID
        }
        val groupOffsets = viewModel.selectedGateIds
            .mapNotNull { id -> viewModel.findGate(id) }
            .associate { comp ->
                comp.ID to Offset(worldPosition.x - comp.x, worldPosition.y - comp.y)
            }
        viewModel.dragState = DragState(
            componentId = hitGate.ID,
            pointerOffset = Offset(worldPosition.x - hitGate.x, worldPosition.y - hitGate.y),
            originalX = hitGate.x,
            originalY = hitGate.y,
            groupOffsets = groupOffsets
        )
    } else {
        viewModel.wireSource = null
    }
}

internal fun startSelectionBox(viewModel: CanvasViewModel, position: Offset) {
    val worldPosition = screenToWorld(position, viewModel.viewportX, viewModel.viewportY)
    viewModel.clearSelection()
    viewModel.selectionBoxStart = worldPosition
    viewModel.selectionBoxEnd = worldPosition
}

internal fun updateSelectionBox(viewModel: CanvasViewModel, position: Offset) {
    val start = viewModel.selectionBoxStart ?: return
    val worldPosition = screenToWorld(position, viewModel.viewportX, viewModel.viewportY)
    viewModel.selectionBoxEnd = worldPosition

    val minX = minOf(start.x, worldPosition.x)
    val maxX = maxOf(start.x, worldPosition.x)
    val minY = minOf(start.y, worldPosition.y)
    val maxY = maxOf(start.y, worldPosition.y)
    val boxRect = Rect(minX, minY, maxX, maxY)

    viewModel.selectedGateIds.clear()
    viewModel.components.forEach { comp ->
        if (boxRect.overlaps(comp.rotatedBounds())) {
            viewModel.selectedGateIds.add(comp.ID)
        }
    }
    viewModel.selectedGateId = viewModel.selectedGateIds.firstOrNull()
}

internal fun finishSelectionBox(viewModel: CanvasViewModel) {
    viewModel.selectionBoxStart = null
    viewModel.selectionBoxEnd = null
}

internal fun dragComponent(
    viewModel: CanvasViewModel,
    position: Offset,
    canvasWidth: Float = Float.MAX_VALUE,
    canvasHeight: Float = Float.MAX_VALUE
) {
    viewModel.dragState?.let { state ->
        val worldPosition = screenToWorld(position, viewModel.viewportX, viewModel.viewportY)
        if (state.groupOffsets.size > 1) {
            state.groupOffsets.forEach { (id, offset) ->
                val comp = viewModel.findGate(id)
                if (comp != null) {
                    val rawX = worldPosition.x - offset.x
                    val rawY = worldPosition.y - offset.y
                    val maxX = (canvasWidth - comp.width).coerceAtLeast(0f)
                    val maxY = (canvasHeight - comp.height).coerceAtLeast(0f)
                    val newX = snap(rawX).coerceIn(0f, maxX)
                    val newY = snap(rawY).coerceIn(0f, maxY)
                    val index = viewModel.components.indexOf(comp)
                    if (index != -1) {
                        viewModel.components[index].x = newX
                        viewModel.components[index].y = newY
                    }
                }
            }
        } else {
            val component = viewModel.findGate(state.componentId)
            if (component != null) {
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
        y = finalY,
        inputCount = pending.inputCount,
        outputCount = pending.outputCount,
        logic = pending.abstractedLogic,
        name = pending.customComponentName
    ).apply {
        rotationSteps = pending.rotationSteps
    }
    viewModel.components.add(placedComponent)
    viewModel.pendingComponent = null
}


internal fun editComponent(viewModel: CanvasViewModel , position: Offset){
    val worldPosition = screenToWorld(position, viewModel.viewportX, viewModel.viewportY)
    val hitGate = viewModel.components.findLast { gate ->
        gate.containsPoint(worldPosition)
    }

    if (hitGate != null){
        viewModel.selectSingleGate(hitGate.ID)
    } else {
        viewModel.clearSelection()
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