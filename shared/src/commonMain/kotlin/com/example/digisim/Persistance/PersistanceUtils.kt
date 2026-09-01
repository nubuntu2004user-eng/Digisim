package com.example.digisim.Persistance

import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.FlipFlops.DFlipFlop
import com.example.digisim.FlipFlops.JKFlipFlop
import com.example.digisim.FlipFlops.RSFlipFlop
import com.example.digisim.FlipFlops.TFlipFlop
import com.example.digisim.LogicGates.And
import com.example.digisim.LogicGates.Nand
import com.example.digisim.LogicGates.Nor
import com.example.digisim.LogicGates.Not
import com.example.digisim.LogicGates.Or
import com.example.digisim.LogicGates.XNor
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.Wiring.Button
import com.example.digisim.Wiring.Clock
import com.example.digisim.Wiring.Input
import com.example.digisim.Wiring.Output
import io.github.vinceglb.filekit.PlatformFile
import java.io.File

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
    val component = mutableListOf<Component>()
    data?.components?.forEach { componentDto ->
        component += componentDto.toComponent()
    }
    viewModel.components += component
    data?.wires.let{ viewModel.wires.addAll(it!!)}
}
fun Component.toDto(): ComponentDto = ComponentDto(
    id = ID,
    type = componentType,
    x = x,
    y = y,
    inputCount = inputCount,
    outputCount = outputCount
)

fun ComponentDto.toComponent(): Component = when (type) {
    ComponentType.BUTTON -> Button(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount
    )

    ComponentType.AND -> And(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount
    )
    ComponentType.NAND -> Nand(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount
    )
    ComponentType.NOR -> Nor(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount
    )
    ComponentType.OR -> Or(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount
    )
    ComponentType.XNOR -> XNor(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount
    )
    ComponentType.XOR -> XNor(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount
    )
    ComponentType.NOT -> Not(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount
    )
    ComponentType.INPUT -> Input(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount
    )
    ComponentType.OUTPUT -> Output(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount
    )
    ComponentType.CLOCK -> Clock(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount,
        initialDelay = 999.0f
    )
    ComponentType.RS_FLIP_FLOP -> RSFlipFlop(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount
    )
    ComponentType.JK_FLIP_FLOP -> JKFlipFlop(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount
    )
    ComponentType.D_FLIP_FLOP -> DFlipFlop(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount
    )
    ComponentType.T_FLIP_FLOP -> TFlipFlop(
        id = id,
        initialX = x,
        initialY = y,
        initialInputCount = inputCount,
        initialOutputCount = outputCount
    )
}