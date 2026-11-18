package com.example.digisim.SimulationHandling

import com.example.digisim.FlipFlops.DFlipFlop
import com.example.digisim.FlipFlops.JKFlipFlop
import com.example.digisim.FlipFlops.RSFlipFlop
import com.example.digisim.FlipFlops.TFlipFlop
import com.example.digisim.FlipFlops.DFlipFlopAsync
import com.example.digisim.FlipFlops.JKFlipFlopAsync
import com.example.digisim.FlipFlops.SRFlipFlopAsync
import com.example.digisim.FlipFlops.TFlipFlopAsync
import com.example.digisim.Chips74.IC7400
import com.example.digisim.Chips74.IC7402
import com.example.digisim.Chips74.IC7404
import com.example.digisim.Chips74.IC7407
import com.example.digisim.Chips74.IC7408
import com.example.digisim.Chips74.IC7410
import com.example.digisim.Chips74.IC7411
import com.example.digisim.Chips74.IC7420
import com.example.digisim.Chips74.IC7421
import com.example.digisim.Chips74.IC7427
import com.example.digisim.Chips74.IC7430
import com.example.digisim.Chips74.IC7432
import com.example.digisim.Chips74.IC7473
import com.example.digisim.Chips74.IC7474
import com.example.digisim.Chips74.IC7476
import com.example.digisim.LibraryLoader.CustomComponent
import com.example.digisim.Chips74.IC7486
import com.example.digisim.Chips74.IC74138
import com.example.digisim.Chips74.IC74139
import com.example.digisim.Chips74.IC74151
import com.example.digisim.Chips74.IC74157
import com.example.digisim.Chips74.IC74266
import com.example.digisim.Chips74.IC74283
import com.example.digisim.StandardComponents.*
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
import logicGates.SimulationComponent
import logicGates.SimulationCustomComponent
import com.example.digisim.ParsingLogic.Wire
import logicGates.inputWire
import logicGates.outputWire

fun convertForSimulation(input : Component , clockManager : ClockManager): List<SimulationComponent>?{
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
        is CustomComponent -> return listOf(
            SimulationCustomComponent(
                input.ID,
                mutableListOf(),
                mutableListOf(),
                input.inputCount,
                mutableListOf(),
                mutableListOf(),
                input.abstractedLogic ?: mutableListOf()
            )
        )
        is RSFlipFlop -> return listOf(flipFlops.RSFlipFlop(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf(), currentQ = input.outputPin))
        is JKFlipFlop -> return listOf(flipFlops.JKFlipFlop(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf(), currentQ = input.outputPin, prevClock = input.prevClockPin))
        is DFlipFlop -> return listOf(flipFlops.DFlipFlop(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf(), currentQ = input.outputPin, prevClock = input.prevClockPin))
        is TFlipFlop -> return listOf(flipFlops.TFlipFlop(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf(), currentQ = input.outputPin, prevClock = input.prevClockPin))
        is JKFlipFlopAsync -> return listOf(flipFlops.JKFlipFlopAsync(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf(), currentQ = input.outputPin, prevClock = input.prevClockPin))
        is SRFlipFlopAsync -> return listOf(flipFlops.SRFlipFlopAsync(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf(), currentQ = input.outputPin, prevClock = input.prevClockPin))
        is DFlipFlopAsync -> return listOf(flipFlops.DFlipFlopAsync(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf(), currentQ = input.outputPin, prevClock = input.prevClockPin))
        is TFlipFlopAsync -> return listOf(flipFlops.TFlipFlopAsync(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf(), currentQ = input.outputPin, prevClock = input.prevClockPin))
        is IC7400 -> return listOf(chips74.IC7400(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7402 -> return listOf(chips74.IC7402(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7404 -> return listOf(chips74.IC7404(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7407 -> return listOf(chips74.IC7407(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7408 -> return listOf(chips74.IC7408(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7410 -> return listOf(chips74.IC7410(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7411 -> return listOf(chips74.IC7411(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7420 -> return listOf(chips74.IC7420(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7421 -> return listOf(chips74.IC7421(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7427 -> return listOf(chips74.IC7427(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7430 -> return listOf(chips74.IC7430(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7432 -> return listOf(chips74.IC7432(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7473 -> return listOf(chips74.IC7473(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7474 -> return listOf(chips74.IC7474(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7476 -> return listOf(chips74.IC7476(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC7486 -> return listOf(chips74.IC7486(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC74138 -> return listOf(chips74.IC74138(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC74139 -> return listOf(chips74.IC74139(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC74151 -> return listOf(chips74.IC74151(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC74157 -> return listOf(chips74.IC74157(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC74266 -> return listOf(chips74.IC74266(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is IC74283 -> return listOf(chips74.IC74283(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is BufferGate -> return listOf(logicGates.Buffer(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf()))
        is ControlledBufferGate -> return listOf(logicGates.ControlledBuffer(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf()))
        is ControlledInverterGate -> return listOf(logicGates.ControlledInverter(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf()))
        is OddParityGate -> return listOf(logicGates.OddParity(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf()))
        is EvenParityGate -> return listOf(logicGates.EvenParity(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf()))
        is ConstantHighPin -> return listOf(wiring.ConstantHigh(input.ID))
        is ConstantLowPin -> return listOf(wiring.ConstantLow(input.ID))
        is ProbePin -> return listOf(wiring.Probe(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf()))
        is TunnelPin -> return listOf(wiring.Tunnel(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf()))
        is Multiplexer2To1Comp -> return listOf(plexers.Multiplexer2To1(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is Multiplexer4To1Comp -> return listOf(plexers.Multiplexer4To1(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is Demultiplexer1To2Comp -> return listOf(plexers.Demultiplexer1To2(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is Demultiplexer1To4Comp -> return listOf(plexers.Demultiplexer1To4(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is Decoder2To4Comp -> return listOf(plexers.Decoder2To4(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is Decoder3To8Comp -> return listOf(plexers.Decoder3To8(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is PriorityEncoder4To2Comp -> return listOf(plexers.PriorityEncoder4To2(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is HalfAdderComp -> return listOf(arithmetic.HalfAdder(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is FullAdderComp -> return listOf(arithmetic.FullAdder(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is Adder4BitComp -> return listOf(arithmetic.Adder4Bit(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is Subtractor4BitComp -> return listOf(arithmetic.Subtractor4Bit(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is Comparator4BitComp -> return listOf(arithmetic.Comparator4Bit(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is Negator4BitComp -> return listOf(arithmetic.Negator4Bit(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is Register4BitComp -> return listOf(memory.Register4Bit(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()).apply {
            statefulClockPin = input.prevClockPin
            statefulMemory = input.memoryData.toMutableList()
        })
        is Counter4BitComp -> return listOf(memory.Counter4Bit(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()).apply {
            statefulClockPin = input.prevClockPin
            statefulMemory = input.memoryData.toMutableList()
        })
        is ShiftRegister4BitComp -> return listOf(memory.ShiftRegister4Bit(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()).apply {
            statefulClockPin = input.prevClockPin
            statefulMemory = input.memoryData.toMutableList()
        })
        is Ram16x4Comp -> return listOf(memory.Ram16x4(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()).apply {
            statefulClockPin = input.prevClockPin
            statefulMemory = input.memoryData.toMutableList()
        })
        is Rom16x4Comp -> return listOf(memory.Rom16x4(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()).apply {
            statefulMemory = input.memoryData.toMutableList()
        })
        is LedComp -> return listOf(wiring.Led(input.ID, mutableListOf(), mutableListOf(input.outputPin), input.inputCount, mutableListOf(), mutableListOf()))
        is SevenSegmentDisplayComp -> return listOf(wiring.SevenSegmentDisplay(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is HexDigitDisplayComp -> return listOf(wiring.HexDigitDisplay(input.ID, mutableListOf(), mutableListOf(), input.inputCount, mutableListOf(), mutableListOf()))
        is DipSwitch4BitComp -> return listOf(wiring.DipSwitch4Bit(input.ID, mutableListOf(), input.outputPins.toMutableList(), input.inputCount, mutableListOf(), mutableListOf()).apply {
            statefulMemory = input.memoryData.toMutableList()
        })
        else -> return null
    }
}

fun convertCircuit(
    components: MutableList<MutableList<Component>>,
    wires: List<Wire>,
    clockManager: ClockManager
): MutableList<MutableList<SimulationComponent>> {
    val simComponents = mutableMapOf<Int, SimulationComponent>()
    
    // 1. Convert all components
    val result = mutableListOf<MutableList<SimulationComponent>>()
    for (stage in components) {
        val tmp = mutableListOf<SimulationComponent>()
        for (i in stage) {
            val converted = convertForSimulation(i, clockManager)
            if (converted != null && converted.isNotEmpty()) {
                val comp = converted.first()
                tmp.add(comp)
                simComponents[i.ID] = comp
            }
        }
        result.add(tmp)
    }
    
    // 2. Map wires
    for (wire in wires) {
        val source = simComponents[wire.sourceGateId]
        val target = simComponents[wire.targetGateId]
        
        if (source != null && target != null) {
            source.outputTo.add(outputWire(targetGateId = target.id, portId = wire.targetPortIndex, sourcePortId = wire.sourcePortIndex))
            target.inputFrom.add(inputWire(sourceGateId = source.id, portId = wire.targetPortIndex))
        }
    }
    
    return result
}
