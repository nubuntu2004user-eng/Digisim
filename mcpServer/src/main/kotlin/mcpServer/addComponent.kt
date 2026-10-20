package com.example.mcpServer

import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.LogicGates.And
import com.example.digisim.LogicGates.Nand
import com.example.digisim.LogicGates.Nor
import com.example.digisim.LogicGates.Not
import com.example.digisim.LogicGates.Or
import com.example.digisim.LogicGates.XNor
import com.example.digisim.LogicGates.Xor
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

suspend  fun  addComponent(request : CallToolRequest , componentType: ComponentType , canvasViewModel: CanvasViewModel) = withContext(Dispatchers.Main){
    val args = request.arguments
    val name = args?.get("name")?.jsonPrimitive?.contentOrNull
    val inputCount = args?.get("inputCount")?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 2
    val x = args?.get("x")?.jsonPrimitive?.contentOrNull?.toFloatOrNull() ?: 0f
    val y = args?.get("y")?.jsonPrimitive?.contentOrNull?.toFloatOrNull() ?: 0f
    val componentToAdd = mutableListOf<Component>()
    var failed = false
    when (componentType){
        ComponentType.AND -> componentToAdd.add(
            And(
                id = canvasViewModel.nextId++,
                initialX = x,
                initialY = y,
                initialInputCount = inputCount,
                initialOutputCount = 1,
                label = name ?: "")
        )
        ComponentType.OR -> componentToAdd.add(
            Or(
                id = canvasViewModel.nextId++,
                initialX = x,
                initialY = y,
                initialInputCount = inputCount,
                initialOutputCount = 1,
                label = name ?: "")
        )

        ComponentType.NOT -> componentToAdd.add(
            Not(
                id = canvasViewModel.nextId++,
                initialX = x,
                initialY = y,
                initialInputCount = 1,
                initialOutputCount = 1,
                label = name ?: ""
            )
        )
        ComponentType.NAND -> componentToAdd.add(
            Nand(
                id = canvasViewModel.nextId++,
                initialX = x,
                initialY = y,
                initialInputCount = inputCount,
                initialOutputCount = 1,
                label = name ?: "")
        )
        ComponentType.NOR -> componentToAdd.add(
            Nor(
                id = canvasViewModel.nextId++,
                initialX = x,
                initialY = y,
                initialInputCount = inputCount,
                initialOutputCount = 1,
                label = name ?: "")
        )
        ComponentType.XOR -> componentToAdd.add(
            Xor(
                id = canvasViewModel.nextId++,
                initialX = x,
                initialY = y,
                initialInputCount = inputCount,
                initialOutputCount = 1,
                label = name ?: "")
        )
        ComponentType.XNOR -> componentToAdd.add(
            XNor(
                id = canvasViewModel.nextId++,
                initialX = x,
                initialY = y,
                initialInputCount = inputCount,
                initialOutputCount = 1,
                label = name ?: "")
        )
        else -> {failed = true}
    }

    try {
        withContext(Dispatchers.Main) {
            canvasViewModel.components.add(componentToAdd[0])
        }
    } catch (e: Throwable) {
        canvasViewModel.components.add(componentToAdd[0])
    }


    CallToolResult(
        content = listOf(
            if(!failed){
                TextContent(
                    text = "Added AND gate (ID: ${componentToAdd[0].ID}) at position ($x, $y) with $inputCount inputs."
                )}else{
                TextContent(
                    text = "Failed to add a component most likely incorrect input arguments"
                )
            }
        )
    )
}