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
import logicGates.SimulationComponent
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire
import kotlin.time.Duration.Companion.milliseconds

class SimulationViewModel : ViewModel() {

    var isRunning by mutableStateOf(false)

    var isAuto by mutableStateOf(false)
    val componentsState by mutableStateOf(mutableListOf<MutableList<SimulationComponent>>())

    fun startSimulation(viewModel: CanvasViewModel, scope: CoroutineScope? = null, clockManager: ClockManager , forward : Boolean = false) {
        isRunning = true
        if(!forward){
        viewModel.components.forEach {
            if (it is Input) {
                it.outputPin = Pin.LOW
            }
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
            val allWires = effectiveWires(viewModel)
            val tmp = splitToStages(viewModel, allWires)
            if (tmp.isNotEmpty() && tmp.any { it.isNotEmpty() }) {
                val splitToStages = convertCircuit(tmp, allWires, clockManager)
                val mappedToWires = mapWires(splitToStages, allWires)
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
                            comp.inputPins.clear()
                            comp.inputPins.addAll(elem.inputs)
                            comp.outputPins.clear()
                            comp.outputPins.addAll(elem.output)
                            if (elem.statefulMemory.isNotEmpty()) {
                                comp.memoryData.clear()
                                comp.memoryData.addAll(elem.statefulMemory)
                            }
                            comp.prevClockPin = when (elem) {
                                is flipFlops.JKFlipFlop -> elem.prevClock
                                is flipFlops.DFlipFlop -> elem.prevClock
                                is flipFlops.TFlipFlop -> elem.prevClock
                                is flipFlops.JKFlipFlopAsync -> elem.prevClock
                                is flipFlops.SRFlipFlopAsync -> elem.prevClock
                                is flipFlops.DFlipFlopAsync -> elem.prevClock
                                is flipFlops.TFlipFlopAsync -> elem.prevClock
                                else -> elem.statefulClockPin
                            }
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

    private fun effectiveWires(viewModel: CanvasViewModel): List<Wire> {
        val baseWires = viewModel.wires.toMutableList()
        val tunnels = viewModel.components.filter { it.componentType == ComponentType.TUNNEL && it.label.isNotBlank() }
        if (tunnels.size >= 2) {
            val drivers = tunnels.filter { tx -> baseWires.any { it.targetGateId == tx.ID } }
            for (tx in drivers) {
                val receivers = tunnels.filter { rx ->
                    rx.ID != tx.ID && rx.label == tx.label && baseWires.none { it.targetGateId == rx.ID }
                }
                for (rx in receivers) {
                    baseWires.add(
                        Wire(
                            id = -1,
                            sourceGateId = tx.ID,
                            sourcePortIndex = 0,
                            targetGateId = rx.ID,
                            targetPortIndex = 0
                        )
                    )
                }
            }
        }
        return baseWires
    }

    private fun splitToStages(viewModel: CanvasViewModel, wires: List<Wire> = effectiveWires(viewModel)): MutableList<MutableList<Component>> {
        val result = mutableListOf<MutableList<Component>>()
        val components = viewModel.components

        if (components.isEmpty()) return result

        val sourceTypes = setOf(
            ComponentType.INPUT,
            ComponentType.BUTTON,
            ComponentType.CLOCK,
            ComponentType.CONSTANT_HIGH,
            ComponentType.CONSTANT_LOW,
            ComponentType.DIP_SWITCH_4BIT
        )
        val sinkTypes = setOf(
            ComponentType.OUTPUT,
            ComponentType.PROBE,
            ComponentType.LED,
            ComponentType.SEVEN_SEGMENT_DISPLAY,
            ComponentType.HEX_DIGIT_DISPLAY
        )

        val initialOutputs = components.filter { comp ->
            comp.componentType in sinkTypes ||
                    (wires.none { it.sourceGateId == comp.ID } && comp.componentType !in sourceTypes)
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

        val allInputs = components.filter { it.componentType in sourceTypes }
        val inputsInStages = result.flatten().filter { it.componentType in sourceTypes }.map { it.ID }.toSet()
        val missingInputs = allInputs.filter { it.ID !in inputsInStages }.toMutableList()
        if (missingInputs.isNotEmpty()) {
            if (result.isNotEmpty() && result.last().all { it.componentType in sourceTypes }) {
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
        val sourceTypes = setOf(
            ComponentType.INPUT,
            ComponentType.CLOCK,
            ComponentType.BUTTON,
            ComponentType.CONSTANT_HIGH,
            ComponentType.CONSTANT_LOW,
            ComponentType.DIP_SWITCH_4BIT
        )
        for (i in input.last()) {
            if (i.componentType !in sourceTypes) {
                return false
            }
        }
        return true
    }

    private fun mapWires(input: MutableList<MutableList<SimulationComponent>>, wires: List<Wire>): MutableList<MutableList<SimulationComponent>> {
        val result = input
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
