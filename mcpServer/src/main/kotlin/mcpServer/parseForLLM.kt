package com.example.mcpServer

import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.ParsingLogic.ComponentType
import logicGates.SimulationComponent

fun parseForLLM(canvasViewModel: CanvasViewModel): String {
    var result = ""
    val components = canvasViewModel.components
    val wires = canvasViewModel.wires
    result += "Placed components and their attributes:\n"
    for (component in components) {
        val delayInfo = if (component.componentType == ComponentType.CLOCK) ", Clock delay ticks = ${component.delay}" else ""
        result += "Component of type ${component.componentType}, ID ${component.ID},\n" +
            "Coordinates x = ${component.x} y = ${component.y}\n" +
            "Input count = ${component.inputCount}, Output count = ${component.outputCount}, " +
            "Custom label = ${component.label}, Output state = ${component.outputPin}$delayInfo\n"
        if (component.componentType == ComponentType.CUSTOM) {
            val abstractedLogic = parseAbstractedLogic(component.abstractedLogic)
            result += "This component is also custom (user made) so it has some additional attributes:\n" +
                "Custom name ${component.customComponentName}\n" +
                "Abstracted logic (the way it functions) of this element:\n" +
                abstractedLogic
        }
    }
    result += "Placed wires and their attributes:\n"
    for (wire in wires) {
        result += "Id ${wire.id}, target port index ${wire.targetPortIndex} , target gate id ${wire.targetGateId}, source port index ${wire.sourcePortIndex}, source gate id ${wire.sourceGateId}.\n"
    }
    return result
}

private fun parseAbstractedLogic(input: MutableList<MutableList<SimulationComponent>>?): String {
    if (input != null) {
        var result = ""
        var i = 0
        for (stage in input) {
            result += "Tick $i:"
            i++
            for (component in stage) {
                result += "component of type ${component.componentType} ID ${component.id} Inputs outputs inputs from = ${component.inputFrom} outputs to  = ${component.output}\nInput count = ${component.inputCount}" +
                    "Output = ${component.output}\n"
            }
        }
        return result
    }
    return "Failed to parse"
}
