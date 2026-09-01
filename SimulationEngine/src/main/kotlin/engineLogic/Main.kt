package engineLogic

import logicGates.BasicComponent
import logicGates.ComponentType
import logicGates.Pin

suspend fun computeSimulation ( input : MutableList<MutableList<BasicComponent>>): MutableList<MutableList<BasicComponent>>{
    val result = input.reversed().toMutableList()
    for (stage in result){
        for (element in stage){
            if (element.componentType == ComponentType.INPUT || element.componentType == ComponentType.BUTTON) {
                val evalList = element.evaluate()
                val outVal = evalList.firstOrNull() ?: Pin.LOW
                if (element.componentType == ComponentType.INPUT) {
                    element.output = mutableListOf(outVal)
                }
                for (i in element.outputTo) {
                    i.value = outVal
                    for (downstreamStage in result) {
                        for (downstreamElem in downstreamStage) {
                            if (downstreamElem.id == i.targetGateId) {
                                downstreamElem.inputFrom.filter { it.sourceGateId == element.id && it.portId == i.portId }.forEach { it.value = outVal }
                            }
                        }
                    }
                }
            }
            else {
                element.inputs.clear()
                for (i in element.inputFrom){
                    element.inputs.add(i.value)
                }
                while (element.inputs.size < element.inputCount) {
                    element.inputs.add(Pin.UNDEFINED)
                }
                val evalList = element.evaluate()
                element.output = evalList
                for (i in element.outputTo){
                    val outVal = evalList.getOrElse(i.sourcePortId) { evalList.firstOrNull() ?: Pin.LOW }
                    i.value = outVal
                    for (downstreamStage in result) {
                        for (downstreamElem in downstreamStage) {
                            if (downstreamElem.id == i.targetGateId) {
                                downstreamElem.inputFrom.filter { it.sourceGateId == element.id && it.portId == i.portId }.forEach { it.value = outVal }
                            }
                        }
                    }
                }
            }
        }
    }
    return result
}



