package com.example.digisim.SimulationHandling

import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.LogicGates.And
import com.example.digisim.LogicGates.Input
import com.example.digisim.LogicGates.Nand
import com.example.digisim.LogicGates.Nor
import com.example.digisim.LogicGates.Not
import com.example.digisim.LogicGates.Or
import com.example.digisim.LogicGates.Output
import com.example.digisim.LogicGates.XNor
import com.example.digisim.LogicGates.Xor
import com.example.digisim.ParsingLogic.Component
import logicGates.BasicComponent
import logicGates.Pin

fun convertForSimulation(input : Component): List<BasicComponent>?{
    when (input){
        is And -> return listOf( logicGates.And(input.ID , emptyList() ,emptyList() , input.inputCount , mutableListOf() , mutableListOf() ))
        is Nand ->return listOf( logicGates.Nand(input.ID , emptyList() ,emptyList() , input.inputCount , mutableListOf() , mutableListOf() ))
        is Input -> return listOf( logicGates.Input(input.ID , emptyList() ,emptyList() , input.inputCount , mutableListOf() , mutableListOf() ))
        is Output ->return listOf( logicGates.Output(input.ID , emptyList() ,emptyList() , input.inputCount , mutableListOf() , mutableListOf() ))
        is Not ->return listOf( logicGates.Invertor(input.ID , emptyList() ,emptyList() , input.inputCount , mutableListOf() , mutableListOf() ))
        is Nor ->return listOf( logicGates.Nor(input.ID , emptyList() ,emptyList() , input.inputCount , mutableListOf() , mutableListOf() ))
        is Or ->return listOf( logicGates.Or(input.ID , emptyList() ,emptyList() , input.inputCount , mutableListOf() , mutableListOf() ))
        is XNor -> return listOf( logicGates.XNor(input.ID , emptyList() ,emptyList() , input.inputCount , mutableListOf() , mutableListOf() ))
        is Xor ->return listOf( logicGates.Xor(input.ID , emptyList() ,emptyList() , input.inputCount , mutableListOf() , mutableListOf() ))
        else -> return null


    }


}
fun convertAll (input: MutableList<MutableList<Component>>): MutableList<MutableList<BasicComponent>>{
    val result = mutableListOf<MutableList<BasicComponent>>()
    val tmp = mutableListOf<BasicComponent>()
    for (stage in input){
        for (i in stage){
            if (i !== null) tmp.add(convertForSimulation(i)!!.first())
        }
        result.add(tmp.toMutableList())
        tmp.clear()
    }
    return  result
}
