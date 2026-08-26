package com.example.digisim.SimulationHandling

import androidx.lifecycle.ViewModel
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.ParsingLogic.Wire
import engineLogic.computeSimulation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import logicGates.BasicComponent
import logicGates.inputWire
import logicGates.outputWire

class SimulationViewModel : ViewModel() {




 fun runSimulation(viewModel : CanvasViewModel  , scope : CoroutineScope) {
        scope.launch {
        val tmp = splitToStages(viewModel)
        val splitToStages = convertAll(tmp)
        val mappedToWires = mapWires(splitToStages , viewModel)
        val result = computeSimulation(mappedToWires)
        }
    }

    private fun splitToStages(viewModel: CanvasViewModel): MutableList<MutableList<Component>>{
        val result = mutableListOf<MutableList<Component>>()
        val tmp = mutableListOf<Component>()
        var done = false
        tmp.addAll(viewModel.components.filter(){it.componentType == ComponentType.OUTPUT && checkIfLastStage(it , viewModel) })
        result += tmp.toMutableList()
        var stageIndex = 1

        while (!done){
            val wires = viewModel.wires
            tmp.clear()
            tmp += parseStage(result[stageIndex - 1] , wires , viewModel)
            result += tmp.toMutableList()
            stageIndex ++

            if(checkIfLast(result))    done = true

        }
        return result
    }
    private fun parseStage(input : MutableList<Component> , wires : MutableList<Wire> , viewModel: CanvasViewModel): MutableList<Component>{
        val result = mutableListOf<Component>()
        val tmpWireList = mutableListOf<Wire>()
        for (i in input){
            tmpWireList.addAll(wires.filter { it.targetGateId == i.ID })
        }
        for (i in tmpWireList){
            result.addAll(viewModel.components.filter { it.ID == i.sourceGateId })
        }
        return result
    }
    private fun checkIfLast (input : MutableList<MutableList<Component>>): Boolean{
        for (i in input.last()){
            if (i.componentType !== ComponentType.INPUT){
                return false
            }
        }
        return true
    }
    private fun checkIfLastStage(component: Component , viewModel: CanvasViewModel): Boolean{
        var result = true
        val potentialWires = mutableListOf<Wire>()
        for (i in viewModel.wires){
            if (i.targetGateId == component.ID){
                potentialWires.add(i)
            }
        }
        for (i in potentialWires){
            val gateId =(i.sourceGateId)

            if((viewModel.wires.filter { it.sourceGateId == gateId }).size > 1){
                result = false
            }
        }
        return result
    }

    private fun mapWires(input: MutableList<MutableList<BasicComponent>> , viewModel: CanvasViewModel)
    :MutableList<MutableList<BasicComponent>>
    {
        val result = input
        val wires = viewModel.wires
        for (stage in result){
            for (i in stage){
                wires.forEach { wire ->
                    if (wire.targetGateId == i.id){
                        i.inputFrom += inputWire( wire.sourceGateId, wire.targetPortIndex)
                    }
                    else if (wire.sourceGateId == i.id){
                        i.outputTo += outputWire(wire.targetGateId , wire.targetPortIndex)
                    }
                }
            }
        }
        return result
    }
}
