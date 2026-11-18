package com.example.mcpServer

import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.StandardComponents.DipSwitch4BitComp
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import logicGates.Pin

private const val TICKS_PER_SECOND = 1000.0f

private val multiInputGateTypes = setOf(
    ComponentType.AND,
    ComponentType.OR,
    ComponentType.NAND,
    ComponentType.NOR,
    ComponentType.XOR,
    ComponentType.XNOR,
    ComponentType.ODD_PARITY,
    ComponentType.EVEN_PARITY
)

suspend fun updateComponent(
    request: CallToolRequest,
    canvasViewModel: CanvasViewModel
): CallToolResult = runOnCanvasThread {
    val args = request.arguments
    val id = args?.get("id")?.jsonPrimitive?.contentOrNull?.toIntOrNull()
        ?: return@runOnCanvasThread CallToolResult(
            content = listOf(TextContent("Failed to update component: missing or invalid integer 'id'."))
        )

    val component = canvasViewModel.components.find { it.ID == id }
        ?: return@runOnCanvasThread CallToolResult(
            content = listOf(TextContent("Failed to update component: no component found with ID $id."))
        )

    val changes = mutableListOf<String>()

    args["x"]?.jsonPrimitive?.contentOrNull?.toFloatOrNull()?.let { newX ->
        component.x = newX
        changes.add("x=$newX")
    }

    args["y"]?.jsonPrimitive?.contentOrNull?.toFloatOrNull()?.let { newY ->
        component.y = newY
        changes.add("y=$newY")
    }

    val newLabel = args["label"]?.jsonPrimitive?.contentOrNull
        ?: args["name"]?.jsonPrimitive?.contentOrNull
    if (newLabel != null) {
        component.label = newLabel
        changes.add("label='$newLabel'")
    }

    args["inputCount"]?.jsonPrimitive?.contentOrNull?.toIntOrNull()?.let { requestedInputs ->
        if (component.componentType in multiInputGateTypes) {
            val clamped = requestedInputs.coerceIn(2, 64)
            component.inputCount = clamped
            canvasViewModel.wires.removeAll { wire ->
                wire.targetGateId == component.ID && wire.targetPortIndex >= clamped
            }
            changes.add("inputCount=$clamped")
        } else {
            changes.add("inputCount ignored (${component.componentType} has fixed inputCount=${component.inputCount})")
        }
    }

    val explicitDelay = args["delay"]?.jsonPrimitive?.contentOrNull?.toFloatOrNull()
    val frequencyHz = args["frequencyHz"]?.jsonPrimitive?.contentOrNull?.toFloatOrNull()
    if (explicitDelay != null || frequencyHz != null) {
        if (component.componentType == ComponentType.CLOCK) {
            val newDelay = when {
                explicitDelay != null && explicitDelay > 0f -> explicitDelay
                frequencyHz != null && frequencyHz > 0f -> TICKS_PER_SECOND / (frequencyHz * 2.0f)
                else -> null
            }
            if (newDelay != null) {
                component.delay = newDelay
                changes.add("delay=$newDelay")
            }
        } else {
            changes.add("delay/frequencyHz ignored (only applicable to CLOCK)")
        }
    }

    val highArg = args["high"]?.jsonPrimitive?.let {
        it.booleanOrNull ?: it.contentOrNull?.toBooleanStrictOrNull()
    }
    val switchIndexArg = args["switchIndex"]?.jsonPrimitive?.contentOrNull?.toIntOrNull()
    if (highArg != null) {
        if (component.componentType == ComponentType.INPUT || component.componentType == ComponentType.BUTTON) {
            component.outputPin = if (highArg) Pin.HIGH else Pin.LOW
            changes.add("outputPin=${component.outputPin}")
        } else if (component is DipSwitch4BitComp && switchIndexArg != null && switchIndexArg in 0..3) {
            component.setSwitch(switchIndexArg, highArg)
            changes.add("SW$switchIndexArg=${if (highArg) "HIGH" else "LOW"}")
        } else {
            changes.add("high ignored (only applicable to INPUT, BUTTON, or DIP_SWITCH_4BIT with switchIndex 0..3)")
        }
    }

    val memAddr = args["memoryAddress"]?.jsonPrimitive?.contentOrNull?.toIntOrNull()
    val memVal = args["memoryValue"]?.jsonPrimitive?.contentOrNull?.toIntOrNull()
    if (memVal != null) {
        val targetIdx = (memAddr ?: 0).coerceIn(0, 15)
        while (component.memoryData.size <= targetIdx) component.memoryData.add(0)
        component.memoryData[targetIdx] = memVal and 0xF
        if (component is DipSwitch4BitComp) component.syncOutputsFromMask()
        changes.add("memory[$targetIdx]=${memVal and 0xF}")
    }

    val report = if (changes.isEmpty()) {
        "No changes provided for component ID $id (${component.componentType})."
    } else {
        "Updated component ID $id (${component.componentType}): ${changes.joinToString(", ")}."
    }

    CallToolResult(content = listOf(TextContent(report)))
}

suspend fun deleteComponent(
    request: CallToolRequest,
    canvasViewModel: CanvasViewModel
): CallToolResult = runOnCanvasThread {
    val args = request.arguments
    val id = args?.get("id")?.jsonPrimitive?.contentOrNull?.toIntOrNull()
        ?: return@runOnCanvasThread CallToolResult(
            content = listOf(TextContent("Failed to delete component: missing or invalid integer 'id'."))
        )

    val component = canvasViewModel.components.find { it.ID == id }
        ?: return@runOnCanvasThread CallToolResult(
            content = listOf(TextContent("Failed to delete component: no component found with ID $id."))
        )

    val wiresBefore = canvasViewModel.wires.size
    canvasViewModel.wires.removeAll { it.sourceGateId == id || it.targetGateId == id }
    val removedWires = wiresBefore - canvasViewModel.wires.size

    canvasViewModel.components.remove(component)
    if (canvasViewModel.selectedGateId == id) {
        canvasViewModel.selectedGateId = null
    }

    CallToolResult(
        content = listOf(
            TextContent(
                "Deleted ${component.componentType} component (ID: $id) and removed $removedWires connected wire(s)."
            )
        )
    )
}

suspend fun deleteWire(
    request: CallToolRequest,
    canvasViewModel: CanvasViewModel
): CallToolResult = runOnCanvasThread {
    val args = request.arguments
    val id = args?.get("id")?.jsonPrimitive?.contentOrNull?.toIntOrNull()
        ?: return@runOnCanvasThread CallToolResult(
            content = listOf(TextContent("Failed to delete wire: missing or invalid integer 'id'."))
        )

    val wire = canvasViewModel.wires.find { it.id == id }
        ?: return@runOnCanvasThread CallToolResult(
            content = listOf(TextContent("Failed to delete wire: no wire found with ID $id."))
        )

    canvasViewModel.wires.remove(wire)
    CallToolResult(
        content = listOf(
            TextContent(
                "Deleted wire (ID: $id) from gate ${wire.sourceGateId} port ${wire.sourcePortIndex} to gate ${wire.targetGateId} port ${wire.targetPortIndex}."
            )
        )
    )
}
