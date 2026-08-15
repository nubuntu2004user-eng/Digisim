package engineLogic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logicGates.And
import logicGates.BasicComponent
import logicGates.BasicComponentData
import logicGates.ComponentType
import logicGates.Invertor
import logicGates.Nand
import logicGates.Nor
import logicGates.Or
import logicGates.Pin
import logicGates.XNor
import logicGates.Xor

suspend fun parseSimulation(
    elements : List<List<BasicComponentData>> ,
    state: SimulationState
) = withContext(Dispatchers.Default) {  // order is very important
    for (tick in elements){
        val tmp = mutableListOf<BasicComponent>()
        for (element in tick){
            tmp +=  when(element.type){
                ComponentType.AND ->  And(element.id , element.inputs , element.output , element.inputCount)
                ComponentType.NAND -> Nand(element.id , element.inputs , element.output, element.inputCount)
                ComponentType.NOR -> Nor(element.id, element.inputs, element.output, element.inputCount)
                ComponentType.NOT -> Invertor(element.id , element.inputs , element.output, element.inputCount)
                ComponentType.OR -> Or(element.id , element.inputs , element.output, element.inputCount)
                ComponentType.XNOR -> XNor(element.id , element.inputs , element.output, element.inputCount)
                ComponentType.XOR -> Xor(element.id , element.inputs , element.output, element.inputCount)
            }
        }
        state.CircuitData.add(tmp)

    }
}

suspend fun computeSimulation(input : List<List<BasicComponent>> , state: SimulationState)= withContext(Dispatchers.Default){
    val lastTickOutput = mutableListOf<Pin>()  //should be empty at on start
    for (tick in input){
        if (lastTickOutput.isNotEmpty()){
        for (element in tick){
            var currentInput = 0
                element.inputs = lastTickOutput.subList(currentInput , currentInput + element.inputCount )


            element.output = element.evaluate()
            lastTickOutput.clear()
            lastTickOutput += element.output
        }
        }
        else{
            for (element in tick){
            element.output = element.evaluate()
            lastTickOutput += element.output
        }
        }
    }
    state.outputs = lastTickOutput

}

