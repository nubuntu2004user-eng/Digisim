package com.example.mcpServer

import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.LogicGates.And
import com.example.digisim.LogicGates.Or
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.types.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

class mcpServer(private val canvasViewModel: CanvasViewModel = CanvasViewModel()) {
    fun initializeServer(): Server {
        val mcpServer = Server(
            serverInfo = Implementation(
                name = "DigisimMcpServer",
                version = "1.0.0"
            ),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = true),
                )
            )
        )

        val addComponentSchema = ToolSchema(
            properties = buildJsonObject {
                putJsonObject("name") {
                    put("type", "string")
                    put("description", "Optional label or name for the AND gate")
                }
                putJsonObject("inputCount") {
                    put("type", "integer")
                    put("description", "Number of input pins for the AND gate (default: 2)")
                }
                putJsonObject("x") {
                    put("type", "number")
                    put("description", "Canvas X coordinate in pixels")
                }
                putJsonObject("y") {
                    put("type", "number")
                    put("description", "Canvas Y coordinate in pixels")
                }
            },
            required = listOf("x", "y")
        )
        mcpServer.addTool(
            name = "add_and",
            description = "Use this to add an AND component to the simulation canvas ",
            inputSchema = addComponentSchema
        ) { request ->
            addComponent(request = request , componentType = ComponentType.AND, canvasViewModel = canvasViewModel)
        }


        mcpServer.addTool(
            name = "add_or",
            description = "Use this to add an OR component to the simulation canvas ",
            inputSchema = addComponentSchema
        ) { request ->
            addComponent(request = request , componentType = ComponentType.OR, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_invertor",
            description = "Use this to add an invertor component to the simulation canvas ",
            inputSchema = addComponentSchema
        ) { request ->
            addComponent(request = request , componentType = ComponentType.NOT, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_nand",
            description = "Use this to add an NAND component to the simulation canvas ",
            inputSchema = addComponentSchema
        ) { request ->
            addComponent(request = request , componentType = ComponentType.NAND , canvasViewModel = canvasViewModel)
        }
        mcpServer.addTool(
            name = "add_nor",
            description = "Use this to add an NOR component to the simulation canvas ",
            inputSchema = addComponentSchema
        ) { request ->
            addComponent(request = request , componentType = ComponentType.NOR, canvasViewModel = canvasViewModel)
        }
        mcpServer.addTool(
            name = "add_xor",
            description = "Use this to add an XOR component to the simulation canvas ",
            inputSchema = addComponentSchema
        ) { request ->
            addComponent(request = request , componentType = ComponentType.XOR, canvasViewModel = canvasViewModel)
        }
        mcpServer.addTool(
            name = "add_xnor",
            description = "Use this to add an XNOR component to the simulation canvas ",
            inputSchema = addComponentSchema
        ) { request ->
            addComponent(request = request , componentType = ComponentType.XNOR, canvasViewModel = canvasViewModel)
        }

        return mcpServer
    }
}