package com.example.digisim.SimulationHandling

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.Wiring.Input
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.ParsingLogic.Wire
import engineLogic.ClockManager
import engineLogic.computeSimulation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import logicGates.BasicComponent
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire
import kotlin.time.Duration.Companion.milliseconds

class SimulationViewModel : ViewModel() {

    var isRunning by mutableStateOf(false)

    var isAuto by mutableStateOf(false)
    val componentsState by mutableStateOf(mutableListOf<MutableList<BasicComponent>>())

    fun startSimulation(viewModel: CanvasViewModel, scope: CoroutineScope? = null, clockManager: ClockManager) {
        isRunning = true
//        isAuto = false
        viewModel.components.forEach {
            if (it is Input) {
                it.outputPin = Pin.LOW
            }
        }
        if (scope != null) {
            scope.launch {
                runSimulation(viewModel, scope, clockManager)
            }
        } else {
            runBlocking {
                runSimulation(viewModel, null, clockManager)
            }
        }
    }

    suspend fun runSimulation(viewModel: CanvasViewModel, scope: CoroutineScope? = null , clockManager: ClockManager) {
        val action: suspend () -> Unit = {
            val tmp = splitToStages(viewModel)
            if (tmp.isNotEmpty() && tmp.any { it.isNotEmpty() }) {
                val splitToStages = convertAll(tmp , clockManager)
                val mappedToWires = mapWires(splitToStages, viewModel)
                val result = computeSimulation(mappedToWires)
                clockManager.tick += 1
                componentsState.clear()
                componentsState.addAll(result)
                for (stage in result) {
                    for (elem in stage) {
                        val comp = viewModel.components.find { it.ID == elem.id }
                        if (comp != null) {
                            val pin = elem.output.firstOrNull() ?: (if (elem.componentType == logicGates.ComponentType.OUTPUT) elem.inputs.firstOrNull() ?: Pin.LOW else Pin.LOW)
                            comp.outputPin = pin
                        }
                    }
                }
            }
        }
        if(isAuto && scope !== null){
            while(isAuto){
                delay(1.milliseconds)
                scope.launch { action() }
            }
        }
        else{
        if (scope != null) {
            scope.launch { action() }
        } else {
            runBlocking { action() }
        }
        }
    }

    private fun splitToStages(viewModel: CanvasViewModel): MutableList<MutableList<Component>> {
        val result = mutableListOf<MutableList<Component>>()
        val wires = viewModel.wires
        val components = viewModel.components

        if (components.isEmpty()) return result

        val initialOutputs = components.filter { comp ->
            comp.componentType == ComponentType.OUTPUT ||
                    (wires.none { it.sourceGateId == comp.ID } && comp.componentType != ComponentType.INPUT && comp.componentType != ComponentType.BUTTON && comp.componentType != ComponentType.CLOCK)
        }.toMutableList()

        val startStage = if (initialOutputs.isNotEmpty()) initialOutputs else components.toMutableList()
        result.add(startStage)

        var stageIndex = 1
        var done = checkIfLast(result)
        val maxStages = components.size + 2

        while (!done && stageIndex < maxStages) {
            val prevStage = result[stageIndex - 1]
            val nextStage = parseStage(prevStage, wires, viewModel)
            if (nextStage.isEmpty()) {
                break
            }
            result.add(nextStage)
            stageIndex++

            if (checkIfLast(result)) done = true
        }

        val allInputs = components.filter { it.componentType == ComponentType.INPUT || it.componentType == ComponentType.CLOCK || it.componentType == ComponentType.BUTTON }
        val inputsInStages = result.flatten().filter { it.componentType == ComponentType.INPUT || it.componentType == ComponentType.CLOCK || it.componentType == ComponentType.BUTTON }.map { it.ID }.toSet()
        val missingInputs = allInputs.filter { it.ID !in inputsInStages }.toMutableList()
        if (missingInputs.isNotEmpty()) {
            if (result.isNotEmpty() && result.last().all { it.componentType == ComponentType.INPUT || it.componentType == ComponentType.CLOCK || it.componentType == ComponentType.BUTTON }) {
                result.last().addAll(missingInputs)
            } else {
                result.add(missingInputs)
            }
        }

        return result
    }

    private fun parseStage(input: MutableList<Component>, wires: List<Wire>, viewModel: CanvasViewModel): MutableList<Component> {
        val result = mutableListOf<Component>()
        val inputIds = input.map { it.ID }.toSet()
        val sourceIds = wires.filter { it.targetGateId in inputIds }.map { it.sourceGateId }.toSet()
        for (id in sourceIds) {
            val comp = viewModel.components.find { it.ID == id }
            if (comp != null) {
                result.add(comp)
            }
        }
        return result
    }

    private fun checkIfLast(input: MutableList<MutableList<Component>>): Boolean {
        if (input.isEmpty() || input.last().isEmpty()) return true
        for (i in input.last()) {
            if (i.componentType != ComponentType.INPUT && i.componentType != ComponentType.CLOCK && i.componentType != ComponentType.BUTTON) {
                return false
            }
        }
        return true
    }

    private fun mapWires(input: MutableList<MutableList<BasicComponent>>, viewModel: CanvasViewModel): MutableList<MutableList<BasicComponent>> {
        val result = input
        val wires = viewModel.wires
        for (stage in result) {
            for (i in stage) {
                i.inputFrom.clear()
                i.outputTo.clear()
                wires.forEach { wire ->
                    if (wire.targetGateId == i.id) {
                        i.inputFrom += inputWire(wire.sourceGateId, wire.targetPortIndex)
                    } else if (wire.sourceGateId == i.id) {
                        i.outputTo += outputWire(wire.targetGateId, wire.targetPortIndex, sourcePortId = wire.sourcePortIndex)
                    }
                }
                i.inputFrom.sortBy { it.portId }
            }
        }
        return result
    }
}
