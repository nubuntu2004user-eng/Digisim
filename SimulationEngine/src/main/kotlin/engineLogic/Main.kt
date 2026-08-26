package engineLogic

import logicGates.BasicComponent
import logicGates.ComponentType

suspend fun computeSimulation ( input : MutableList<MutableList<BasicComponent>>): MutableList<MutableList<BasicComponent>>{
    val result = input.reversed().toMutableList()
    for (stage in result){
        for (element in stage){
            if (element.componentType == ComponentType.INPUT ){
            for(i in element.outputTo){
                 i.value = element.evaluate().first()
                }
            }
            else {
                element.inputs.clear()
                for (i in element.inputFrom){
                    element.inputs.add(i.value)
                }
                for (i in element.outputTo){
                    i.value = element.evaluate().first()
                }
            }
        }
    }
    return result
}



