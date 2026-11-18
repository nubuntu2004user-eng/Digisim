package com.example.mcpServer

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
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.FlipFlops.DFlipFlop
import com.example.digisim.FlipFlops.DFlipFlopAsync
import com.example.digisim.FlipFlops.JKFlipFlop
import com.example.digisim.FlipFlops.JKFlipFlopAsync
import com.example.digisim.FlipFlops.RSFlipFlop
import com.example.digisim.FlipFlops.SRFlipFlopAsync
import com.example.digisim.FlipFlops.TFlipFlop
import com.example.digisim.FlipFlops.TFlipFlopAsync
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
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import logicGates.Pin

private const val TICKS_PER_SECOND = 1000.0f

internal suspend fun <T> runOnCanvasThread(block: () -> T): T {
    return try {
        withContext(Dispatchers.Main) {
            block()
        }
    } catch (_: Throwable) {
        block()
    }
}

suspend fun addComponent(
    request: CallToolRequest,
    componentType: ComponentType,
    canvasViewModel: CanvasViewModel
): CallToolResult = runOnCanvasThread {
    val args = request.arguments
    val label = args?.get("label")?.jsonPrimitive?.contentOrNull
        ?: args?.get("name")?.jsonPrimitive?.contentOrNull
        ?: ""
    val rawInputCount = args?.get("inputCount")?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 2
    val gateInputCount = rawInputCount.coerceIn(2, 64)
    val x = args?.get("x")?.jsonPrimitive?.contentOrNull?.toFloatOrNull() ?: 0f
    val y = args?.get("y")?.jsonPrimitive?.contentOrNull?.toFloatOrNull() ?: 0f
    val initialHigh = args?.get("high")?.jsonPrimitive?.booleanOrNull
        ?: (args?.get("high")?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull())
        ?: false
    val frequencyHz = args?.get("frequencyHz")?.jsonPrimitive?.contentOrNull?.toFloatOrNull()
    val explicitDelay = args?.get("delay")?.jsonPrimitive?.contentOrNull?.toFloatOrNull()
    val clockDelay = when {
        explicitDelay != null && explicitDelay > 0f -> explicitDelay
        frequencyHz != null && frequencyHz > 0f -> TICKS_PER_SECOND / (frequencyHz * 2.0f)
        else -> 999f
    }

    val component: Component? = when (componentType) {
        ComponentType.AND -> And(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = gateInputCount,
            initialOutputCount = 1,
            label = label
        )
        ComponentType.OR -> Or(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = gateInputCount,
            initialOutputCount = 1,
            label = label
        )
        ComponentType.NOT -> Not(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 1,
            initialOutputCount = 1,
            label = label
        )
        ComponentType.NAND -> Nand(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = gateInputCount,
            initialOutputCount = 1,
            label = label
        )
        ComponentType.NOR -> Nor(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = gateInputCount,
            initialOutputCount = 1,
            label = label
        )
        ComponentType.XOR -> Xor(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = gateInputCount,
            initialOutputCount = 1,
            label = label
        )
        ComponentType.XNOR -> XNor(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = gateInputCount,
            initialOutputCount = 1,
            label = label
        )
        ComponentType.INPUT -> Input(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 0,
            initialOutputCount = 1,
            label = label
        ).apply {
            if (initialHigh) {
                outputPin = Pin.HIGH
            }
        }
        ComponentType.OUTPUT -> Output(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 1,
            initialOutputCount = 0,
            label = label
        )
        ComponentType.BUTTON -> Button(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 0,
            initialOutputCount = 1,
            label = label
        ).apply {
            if (initialHigh) {
                outputPin = Pin.HIGH
            }
        }
        ComponentType.CLOCK -> Clock(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 0,
            initialOutputCount = 1,
            initialDelay = clockDelay,
            label = label
        )
        ComponentType.RS_FLIP_FLOP -> RSFlipFlop(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 2,
            initialOutputCount = 2,
            label = label
        )
        ComponentType.JK_FLIP_FLOP -> JKFlipFlop(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 3,
            initialOutputCount = 2,
            label = label
        )
        ComponentType.D_FLIP_FLOP -> DFlipFlop(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 2,
            initialOutputCount = 2,
            label = label
        )
        ComponentType.T_FLIP_FLOP -> TFlipFlop(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 2,
            initialOutputCount = 2,
            label = label
        )
        ComponentType.JK_FLIP_FLOP_ASYNC -> JKFlipFlopAsync(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 5,
            initialOutputCount = 2,
            label = label
        )
        ComponentType.SR_FLIP_FLOP_ASYNC -> SRFlipFlopAsync(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 5,
            initialOutputCount = 2,
            label = label
        )
        ComponentType.D_FLIP_FLOP_ASYNC -> DFlipFlopAsync(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 4,
            initialOutputCount = 2,
            label = label
        )
        ComponentType.T_FLIP_FLOP_ASYNC -> TFlipFlopAsync(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 4,
            initialOutputCount = 2,
            label = label
        )
        ComponentType.IC_7400 -> IC7400(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 10,
            initialOutputCount = 4,
            label = label
        )
        ComponentType.IC_7402 -> IC7402(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 10,
            initialOutputCount = 4,
            label = label
        )
        ComponentType.IC_7404 -> IC7404(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 8,
            initialOutputCount = 6,
            label = label
        )
        ComponentType.IC_7408 -> IC7408(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 10,
            initialOutputCount = 4,
            label = label
        )
        ComponentType.IC_7432 -> IC7432(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 10,
            initialOutputCount = 4,
            label = label
        )
        ComponentType.IC_7486 -> IC7486(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 10,
            initialOutputCount = 4,
            label = label
        )
        ComponentType.IC_7407 -> IC7407(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 8,
            initialOutputCount = 6,
            label = label
        )
        ComponentType.IC_7410 -> IC7410(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 11,
            initialOutputCount = 3,
            label = label
        )
        ComponentType.IC_7411 -> IC7411(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 11,
            initialOutputCount = 3,
            label = label
        )
        ComponentType.IC_7420 -> IC7420(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 10,
            initialOutputCount = 2,
            label = label
        )
        ComponentType.IC_7421 -> IC7421(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 10,
            initialOutputCount = 2,
            label = label
        )
        ComponentType.IC_7427 -> IC7427(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 11,
            initialOutputCount = 3,
            label = label
        )
        ComponentType.IC_7430 -> IC7430(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 10,
            initialOutputCount = 1,
            label = label
        )
        ComponentType.IC_7473 -> IC7473(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 10,
            initialOutputCount = 4,
            label = label
        )
        ComponentType.IC_7474 -> IC7474(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 10,
            initialOutputCount = 4,
            label = label
        )
        ComponentType.IC_7476 -> IC7476(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 12,
            initialOutputCount = 4,
            label = label
        )
        ComponentType.IC_74138 -> IC74138(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 8,
            initialOutputCount = 8,
            label = label
        )
        ComponentType.IC_74139 -> IC74139(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 8,
            initialOutputCount = 8,
            label = label
        )
        ComponentType.IC_74151 -> IC74151(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 14,
            initialOutputCount = 2,
            label = label
        )
        ComponentType.IC_74157 -> IC74157(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 12,
            initialOutputCount = 4,
            label = label
        )
        ComponentType.IC_74266 -> IC74266(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 10,
            initialOutputCount = 4,
            label = label
        )
        ComponentType.IC_74283 -> IC74283(
            id = canvasViewModel.nextId++,
            initialX = x,
            initialY = y,
            initialInputCount = 11,
            initialOutputCount = 5,
            label = label
        )
        ComponentType.CUSTOM -> null
    }

    if (component != null) {
        canvasViewModel.components.add(component)
        CallToolResult(
            content = listOf(
                TextContent(
                    text = "Added ${component.componentType} component (ID: ${component.ID}) at position (${component.x}, ${component.y}) with ${component.inputCount} inputs and ${component.outputCount} outputs" +
                        if (component.label.isNotEmpty()) ", label '${component.label}'." else "."
                )
            )
        )
    } else {
        CallToolResult(
            content = listOf(
                TextContent(
                    text = "Failed to add component of type $componentType: unsupported type or invalid arguments."
                )
            )
        )
    }
}
