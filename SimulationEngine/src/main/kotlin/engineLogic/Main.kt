package engineLogic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logicGates.And
import logicGates.BasicComponent
import logicGates.ComponentType
import logicGates.Invertor
import logicGates.Nand
import logicGates.Nor
import logicGates.Or
import logicGates.Pin
import logicGates.XNor
import logicGates.Xor

//suspend fun parseSimulation(
//    elements : List<List<BasicComponent>> ,
//    state: SimulationState
//) = withContext(Dispatchers.Default) {  // order is very important
//    for (tick in elements){
//        val tmp = mutableListOf<BasicComponent>()
//        for (element in tick){
//            tmp +=  when(element.type){
//                ComponentType.AND ->  And(element.id , element.inputs , element.output , element.inputCount)
//                ComponentType.NAND -> Nand(element.id , element.inputs , element.output, element.inputCount)
//                ComponentType.NOR -> Nor(element.id, element.inputs, element.output, element.inputCount)
//                ComponentType.NOT -> Invertor(element.id , element.inputs , element.output, element.inputCount)
//                ComponentType.OR -> Or(element.id , element.inputs , element.output, element.inputCount)
//                ComponentType.XNOR -> XNor(element.id , element.inputs , element.output, element.inputCount)
//                ComponentType.XOR -> Xor(element.id , element.inputs , element.output, element.inputCount)
//            }
//        }
//        state.CircuitData.add(tmp)
//
//    }
//}

suspend fun computeSimulation(  //optimize it
    components : List<List<BasicComponent>>,
    state: SimulationState,
    input : MutableList<Pin>
    ): MutableList<Pin> {

    val lastTickOutput : MutableList<MutableList<Pin>> = mutableListOf()
    lastTickOutput.add(input)

    var currentInputIndex = 0
    for (stage in components){
        val tmp = mutableListOf<Pin>()
        for (element in stage){

            element.inputs = lastTickOutput.get(lastTickOutput.lastIndex).subList(currentInputIndex , currentInputIndex + element.inputCount).toMutableList()
            currentInputIndex += element.inputCount
            element.output = element.evaluate()
            tmp.addAll(element.output)
        }
        currentInputIndex = 0
        lastTickOutput.add(tmp)

    }
    state.outputs = lastTickOutput.get(lastTickOutput.lastIndex) // move to new function
    return lastTickOutput.get(lastTickOutput.lastIndex)



    }


suspend fun manageSimulation(){
    //should create the instance of simulation state class and then compute a "simulationTree"
}



