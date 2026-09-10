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
import com.example.digisim.Chips74.IC7408
import com.example.digisim.Chips74.IC7432
import com.example.digisim.Chips74.IC7486
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