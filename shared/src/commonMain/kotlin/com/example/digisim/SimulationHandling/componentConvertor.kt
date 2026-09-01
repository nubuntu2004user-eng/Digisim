package com.example.digisim.SimulationHandling

import com.example.digisim.FlipFlops.DFlipFlop
import com.example.digisim.FlipFlops.JKFlipFlop
import com.example.digisim.FlipFlops.RSFlipFlop
import com.example.digisim.FlipFlops.TFlipFlop
import com.example.digisim.LogicGates.And
import com.example.digisim.Wiring.Button
import com.example.digisim.Wiring.Input
import com.example.digisim.LogicGates.Nand
import com.example.digisim.LogicGates.Nor
import com.example.digisim.LogicGates.Not
import com.example.digisim.LogicGates.Or
import com.example.digisim.Wiring.Output
import com.example.digisim.LogicGates.XNor
import com.example.digisim.LogicGates.Xor
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.Wiring.Clock
import engineLogic.ClockManager
import logicGates.BasicComponent

fun convertForSimulation(input : Component , clockManager : ClockManager): List<BasicComponent>?{
    when (input){
        is And -> return listOf( logicGates.And(input.ID , mutableListOf() ,mutableListOf(input.outputPin) , input.inputCount , mutableListOf() , mutableListOf() ))
        is Nand ->return listOf( logicGates.Nand(input.ID , mutableListOf() ,mutableListOf(input.outputPin) , input.inputCount , mutableListOf() , mutableListOf() ))
        is Input -> return listOf(
            wiring.Input(
                input.ID,
                mutableListOf(),
                mutableListOf(input.outputPin),
                input.inputCount,
                mutableListOf(),
                mutableListOf()
            )
        )
        is Output ->return listOf(
            wiring.Output(
                input.ID,
                mutableListOf(),
                mutableListOf(input.outputPin),
                input.inputCount,
                mutableListOf(),
                mutableListOf()
            )
        )
        is Not ->return listOf( logicGates.Invertor(input.ID , mutableListOf() ,mutableListOf(input.outputPin) , input.inputCount , mutableListOf() , mutableListOf() ))
        is Nor ->return listOf( logicGates.Nor(input.ID , mutableListOf() ,mutableListOf(input.outputPin) , input.inputCount , mutableListOf() , mutableListOf() ))
        is Or ->return listOf( logicGates.Or(input.ID , mutableListOf() ,mutableListOf(input.outputPin) , input.inputCount , mutableListOf() , mutableListOf() ))
        is XNor -> return listOf( logicGates.XNor(input.ID , mutableListOf() ,mutableListOf(input.outputPin) , input.inputCount , mutableListOf() , mutableListOf() ))
        is Xor ->return listOf( logicGates.Xor(input.ID , mutableListOf() ,mutableListOf(input.outputPin) , input.inputCount , mutableListOf() , mutableListOf() ))
        is Clock -> return listOf(wiring.CLOCK(input.ID ,mutableListOf() , mutableListOf(input.outputPin), input.inputCount , mutableListOf(), mutableListOf(), delayTicks = input.delay , clockManager = clockManager))
        is Button -> return listOf(
            wiring.Button(
                input.ID,
                mutableListOf(),
                mutableListOf(input.outputPin),
                input.inputCount,
                mutableListOf(),
                mutableListOf()
            )
        )
        is RSFlipFlop -> return listOf(flipFlops.RSFlipFlop(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf(), currentQ = input.outputPin))
        is JKFlipFlop -> return listOf(flipFlops.JKFlipFlop(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf(), currentQ = input.outputPin))
        is DFlipFlop -> return listOf(flipFlops.DFlipFlop(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf(), currentQ = input.outputPin))
        is TFlipFlop -> return listOf(flipFlops.TFlipFlop(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf(), currentQ = input.outputPin))
        else -> return null
    }
}
fun convertAll (input: MutableList<MutableList<Component>> , clockManager: ClockManager): MutableList<MutableList<BasicComponent>>{
    val result = mutableListOf<MutableList<BasicComponent>>()
    for (stage in input){
        val tmp = mutableListOf<BasicComponent>()
        for (i in stage){
            val converted = convertForSimulation(i , clockManager = clockManager)
            if (converted != null && converted.isNotEmpty()) {
                tmp.add(converted.first())
            }
        }
        result.add(tmp)
    }
    return  result
}
