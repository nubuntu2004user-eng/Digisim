package engineLogic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logicGates.And
import logicGates.BasicComponentData
import logicGates.ComponentType
import logicGates.Invertor
import logicGates.Nand
import logicGates.Nor
import logicGates.Or
import logicGates.Pin
import logicGates.XNor
import logicGates.Xor

suspend fun simulationTick(elements : List<List<BasicComponentData>>) = withContext(Dispatchers.Default) {  // order is very important
    val tickResult = mutableListOf<Pin>()



    for (tick in elements){
        for (element in tick){

             when(element.type){
                ComponentType.AND ->  And(element.id , element.inputs , element.output)
                ComponentType.NAND -> Nand(element.id , element.inputs , element.output)
                ComponentType.NOR -> Nor(element.id, element.inputs, element.output)
                ComponentType.NOT -> Invertor(element.id , element.inputs , element.output)
                ComponentType.OR -> Or(element.id , element.inputs , element.output)
                ComponentType.XNOR -> XNor(element.id , element.inputs , element.output)
                ComponentType.XOR -> Xor(element.id , element.inputs , element.output)
            }


        }



    }

}

