package com.example.digisim.Persistance

import com.example.digisim.DrawingLogic.CanvasViewModel
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
import com.example.digisim.Chips74.IC7486
import com.example.digisim.Chips74.IC74138
import com.example.digisim.Chips74.IC74139
import com.example.digisim.Chips74.IC74151
import com.example.digisim.Chips74.IC74157
import com.example.digisim.Chips74.IC74266
import com.example.digisim.Chips74.IC74283
import com.example.digisim.StandardComponents.*
import com.example.digisim.LibraryLoader.CustomComponent
import com.example.digisim.LogicGates.And
import com.example.digisim.LogicGates.Nand
import com.example.digisim.LogicGates.Nor
import com.example.digisim.LogicGates.Not
import com.example.digisim.LogicGates.Or
import com.example.digisim.LogicGates.XNor
import com.example.digisim.LogicGates.Xor
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.Wiring.Button
import com.example.digisim.Wiring.Clock
import com.example.digisim.Wiring.Input
import com.example.digisim.Wiring.Output
import io.github.vinceglb.filekit.PlatformFile

//internal fun saveFile(viewModel: CanvasViewModel , file: File){
//    val componentDto = mutableListOf<ComponentDto>()
//    viewModel.components.forEach { component ->
//        componentDto += component.toDto()
//    }
//    persistanceRepository.saveAs(
//        file = file,
//        components = componentDto,
//        wires = viewModel.wires
//    )
//
//}

internal suspend fun loadFile(viewModel: CanvasViewModel, file: PlatformFile?){
    val data = persistanceRepository.load(file)
    viewModel.components.clear()
    viewModel.wires.clear()
    val ids = mutableListOf<Int>()
    val component = mutableListOf<Component>()
    data?.components?.forEach { componentDto ->
        component += componentDto.toComponent()
        ids += componentDto.id
    }
    viewModel.components += component
    data?.wires.let{ viewModel.wires.addAll(it!!)
            it.forEach { wire ->
                ids += wire.id
            }
    }
    val maxId = ids.max()
    viewModel.nextId = maxId + 1


}
fun Component.toDto(): ComponentDto = ComponentDto(
    id = ID,
    type = componentType,
    x = x,
    y = y,
    inputCount = inputCount,
    outputCount = outputCount,
    label = label,
    customLogic = abstractedLogic,
    customName = customComponentName

)

fun ComponentDto.toComponent(): Component = when (type) {
    ComponentType.BUTTON -> Button(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )

    ComponentType.AND -> And(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.NAND -> Nand(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.NOR -> Nor(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.OR -> Or(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.XNOR -> XNor(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.XOR -> Xor(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.NOT -> Not(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.INPUT -> Input(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.OUTPUT -> Output(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.CLOCK -> Clock(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        initialDelay = 999.0f,
        label = label
    )
    ComponentType.RS_FLIP_FLOP -> RSFlipFlop(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.JK_FLIP_FLOP -> JKFlipFlop(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.D_FLIP_FLOP -> DFlipFlop(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.T_FLIP_FLOP -> TFlipFlop(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.JK_FLIP_FLOP_ASYNC -> JKFlipFlopAsync(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.SR_FLIP_FLOP_ASYNC -> SRFlipFlopAsync(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.D_FLIP_FLOP_ASYNC -> DFlipFlopAsync(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.T_FLIP_FLOP_ASYNC -> TFlipFlopAsync(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7400 -> IC7400(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7402 -> IC7402(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7404 -> IC7404(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7408 -> IC7408(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7432 -> IC7432(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7486 -> IC7486(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7407 -> IC7407(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7410 -> IC7410(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7411 -> IC7411(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7420 -> IC7420(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7421 -> IC7421(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7427 -> IC7427(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7430 -> IC7430(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7473 -> IC7473(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7474 -> IC7474(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_7476 -> IC7476(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_74138 -> IC74138(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_74139 -> IC74139(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_74151 -> IC74151(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_74157 -> IC74157(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_74266 -> IC74266(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.IC_74283 -> IC74283(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        label = label
    )
    ComponentType.BUFFER -> BufferGate(id, x, y, inputCount, outputCount, label)
    ComponentType.CONTROLLED_BUFFER -> ControlledBufferGate(id, x, y, inputCount, outputCount, label)
    ComponentType.CONTROLLED_INVERTER -> ControlledInverterGate(id, x, y, inputCount, outputCount, label)
    ComponentType.ODD_PARITY -> OddParityGate(id, x, y, inputCount, outputCount, label)
    ComponentType.EVEN_PARITY -> EvenParityGate(id, x, y, inputCount, outputCount, label)
    ComponentType.CONSTANT_HIGH -> ConstantHighPin(id, x, y, inputCount, outputCount, label)
    ComponentType.CONSTANT_LOW -> ConstantLowPin(id, x, y, inputCount, outputCount, label)
    ComponentType.PROBE -> ProbePin(id, x, y, inputCount, outputCount, label)
    ComponentType.TUNNEL -> TunnelPin(id, x, y, inputCount, outputCount, label)
    ComponentType.MULTIPLEXER_2_TO_1 -> Multiplexer2To1Comp(id, x, y, inputCount, outputCount, label)
    ComponentType.MULTIPLEXER_4_TO_1 -> Multiplexer4To1Comp(id, x, y, inputCount, outputCount, label)
    ComponentType.DEMULTIPLEXER_1_TO_2 -> Demultiplexer1To2Comp(id, x, y, inputCount, outputCount, label)
    ComponentType.DEMULTIPLEXER_1_TO_4 -> Demultiplexer1To4Comp(id, x, y, inputCount, outputCount, label)
    ComponentType.DECODER_2_TO_4 -> Decoder2To4Comp(id, x, y, inputCount, outputCount, label)
    ComponentType.DECODER_3_TO_8 -> Decoder3To8Comp(id, x, y, inputCount, outputCount, label)
    ComponentType.PRIORITY_ENCODER_4_TO_2 -> PriorityEncoder4To2Comp(id, x, y, inputCount, outputCount, label)
    ComponentType.HALF_ADDER -> HalfAdderComp(id, x, y, inputCount, outputCount, label)
    ComponentType.FULL_ADDER -> FullAdderComp(id, x, y, inputCount, outputCount, label)
    ComponentType.ADDER_4BIT -> Adder4BitComp(id, x, y, inputCount, outputCount, label)
    ComponentType.SUBTRACTOR_4BIT -> Subtractor4BitComp(id, x, y, inputCount, outputCount, label)
    ComponentType.COMPARATOR_4BIT -> Comparator4BitComp(id, x, y, inputCount, outputCount, label)
    ComponentType.NEGATOR_4BIT -> Negator4BitComp(id, x, y, inputCount, outputCount, label)
    ComponentType.REGISTER_4BIT -> Register4BitComp(id, x, y, inputCount, outputCount, label)
    ComponentType.COUNTER_4BIT -> Counter4BitComp(id, x, y, inputCount, outputCount, label)
    ComponentType.SHIFT_REGISTER_4BIT -> ShiftRegister4BitComp(id, x, y, inputCount, outputCount, label)
    ComponentType.RAM_16X4 -> Ram16x4Comp(id, x, y, inputCount, outputCount, label)
    ComponentType.ROM_16X4 -> Rom16x4Comp(id, x, y, inputCount, outputCount, label)
    ComponentType.LED -> LedComp(id, x, y, inputCount, outputCount, label)
    ComponentType.SEVEN_SEGMENT_DISPLAY -> SevenSegmentDisplayComp(id, x, y, inputCount, outputCount, label)
    ComponentType.HEX_DIGIT_DISPLAY -> HexDigitDisplayComp(id, x, y, inputCount, outputCount, label)
    ComponentType.DIP_SWITCH_4BIT -> DipSwitch4BitComp(id, x, y, inputCount, outputCount, label)

    ComponentType.CUSTOM -> CustomComponent(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        abstractedLogic = customLogic,
        customComponentName = customName,
        label = label
    )
}