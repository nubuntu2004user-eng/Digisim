package com.example.mcpServer

import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.ParsingLogic.ComponentType
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.types.*
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

        val multiInputGateSchema = ToolSchema(
            properties = buildJsonObject {
                putJsonObject("name") {
                    put("type", "string")
                    put("description", "Optional label or name for the logic gate")
                }
                putJsonObject("label") {
                    put("type", "string")
                    put("description", "Optional label for the logic gate (alias for name)")
                }
                putJsonObject("inputCount") {
                    put("type", "integer")
                    put("description", "Number of input pins for the logic gate (default: 2, range: 2..64)")
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

        val fixedPortComponentSchema = ToolSchema(
            properties = buildJsonObject {
                putJsonObject("name") {
                    put("type", "string")
                    put("description", "Optional label or name for the component")
                }
                putJsonObject("label") {
                    put("type", "string")
                    put("description", "Optional label for the component (alias for name)")
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

        val inputPinSchema = ToolSchema(
            properties = buildJsonObject {
                putJsonObject("name") {
                    put("type", "string")
                    put("description", "Optional label or name for the input pin")
                }
                putJsonObject("label") {
                    put("type", "string")
                    put("description", "Optional label for the input pin (alias for name)")
                }
                putJsonObject("high") {
                    put("type", "boolean")
                    put("description", "Optional initial signal state: true for HIGH, false for LOW (default: false)")
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

        val clockSchema = ToolSchema(
            properties = buildJsonObject {
                putJsonObject("name") {
                    put("type", "string")
                    put("description", "Optional label or name for the clock")
                }
                putJsonObject("label") {
                    put("type", "string")
                    put("description", "Optional label for the clock (alias for name)")
                }
                putJsonObject("delay") {
                    put("type", "number")
                    put("description", "Optional toggle delay in simulation ticks (default: 999)")
                }
                putJsonObject("frequencyHz") {
                    put("type", "number")
                    put("description", "Optional clock frequency in Hz (e.g. 1, 5, 10, 50, 100)")
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

        val addWireSchema = ToolSchema(
            properties = buildJsonObject {
                putJsonObject("source_gate_id") {
                    put("type", "integer")
                    put("description", "ID of the source component from which the wire originates")
                }
                putJsonObject("source_port_index") {
                    put("type", "integer")
                    put("description", "Output port index on the source component (0-based)")
                }
                putJsonObject("target_gate_id") {
                    put("type", "integer")
                    put("description", "ID of the target component to which the wire connects")
                }
                putJsonObject("target_port_index") {
                    put("type", "integer")
                    put("description", "Input port index on the target component (0-based)")
                }
            },
            required = listOf("source_gate_id", "source_port_index", "target_gate_id", "target_port_index")
        )

        val updateComponentSchema = ToolSchema(
            properties = buildJsonObject {
                putJsonObject("id") {
                    put("type", "integer")
                    put("description", "ID of the component to modify")
                }
                putJsonObject("x") {
                    put("type", "number")
                    put("description", "New X coordinate on the canvas in pixels")
                }
                putJsonObject("y") {
                    put("type", "number")
                    put("description", "New Y coordinate on the canvas in pixels")
                }
                putJsonObject("label") {
                    put("type", "string")
                    put("description", "New custom label for the component")
                }
                putJsonObject("name") {
                    put("type", "string")
                    put("description", "New custom label for the component (alias for label)")
                }
                putJsonObject("inputCount") {
                    put("type", "integer")
                    put("description", "New input pin count (2..64) for multi-input logic gates (AND, OR, NAND, NOR, XOR, XNOR)")
                }
                putJsonObject("delay") {
                    put("type", "number")
                    put("description", "New toggle delay in ticks (for CLOCK components)")
                }
                putJsonObject("frequencyHz") {
                    put("type", "number")
                    put("description", "New clock frequency in Hz (for CLOCK components)")
                }
                putJsonObject("high") {
                    put("type", "boolean")
                    put("description", "Set signal state to HIGH (true) or LOW (false) (for INPUT or BUTTON components)")
                }
            },
            required = listOf("id")
        )

        val deleteByIdSchema = ToolSchema(
            properties = buildJsonObject {
                putJsonObject("id") {
                    put("type", "integer")
                    put("description", "ID of the item to delete from the canvas")
                }
            },
            required = listOf("id")
        )

        // 1. Canvas state inspection
        mcpServer.addTool(
            name = "get_canvas_state",
            description = "Use this to get information about currently placed components and wires on the simulation canvas"
        ) { _ ->
            val result = parseForLLM(canvasViewModel = canvasViewModel)
            CallToolResult(listOf(TextContent(result)))
        }

        // 2. Logic Gates
        mcpServer.addTool(
            name = "add_and",
            description = "Add an AND logic gate to the canvas. Inputs: 0..(inputCount-1), Output: 0.",
            inputSchema = multiInputGateSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.AND, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_or",
            description = "Add an OR logic gate to the canvas. Inputs: 0..(inputCount-1), Output: 0.",
            inputSchema = multiInputGateSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.OR, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_invertor",
            description = "Add a NOT (inverter) logic gate to the canvas. Input: 0, Output: 0.",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.NOT, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_not",
            description = "Add a NOT (inverter) logic gate to the canvas. Input: 0, Output: 0.",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.NOT, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_nand",
            description = "Add a NAND logic gate to the canvas. Inputs: 0..(inputCount-1), Output: 0.",
            inputSchema = multiInputGateSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.NAND, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_nor",
            description = "Add a NOR logic gate to the canvas. Inputs: 0..(inputCount-1), Output: 0.",
            inputSchema = multiInputGateSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.NOR, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_xor",
            description = "Add an XOR logic gate to the canvas. Inputs: 0..(inputCount-1), Output: 0.",
            inputSchema = multiInputGateSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.XOR, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_xnor",
            description = "Add an XNOR logic gate to the canvas. Inputs: 0..(inputCount-1), Output: 0.",
            inputSchema = multiInputGateSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.XNOR, canvasViewModel = canvasViewModel)
        }

        // 3. Wiring Components
        mcpServer.addTool(
            name = "add_input",
            description = "Add an INPUT toggle pin to the canvas. Inputs: none, Output: 0.",
            inputSchema = inputPinSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.INPUT, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_output",
            description = "Add an OUTPUT probe pin to the canvas. Input: 0, Outputs: none.",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.OUTPUT, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_button",
            description = "Add a momentary push BUTTON (1-tick pulse) to the canvas. Inputs: none, Output: 0.",
            inputSchema = inputPinSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.BUTTON, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_clock",
            description = "Add a CLOCK oscillator source to the canvas. Inputs: none, Output: 0.",
            inputSchema = clockSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.CLOCK, canvasViewModel = canvasViewModel)
        }

        // 4. Flip-Flop Components
        mcpServer.addTool(
            name = "add_rs_flip_flop",
            description = "Add an RS Flip-Flop to the canvas. Inputs: [0: S, 1: R], Outputs: [0: Q, 1: Q_NOT].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.RS_FLIP_FLOP, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_jk_flip_flop",
            description = "Add a synchronous JK Flip-Flop to the canvas. Inputs: [0: J, 1: CLK, 2: K], Outputs: [0: Q, 1: Q_NOT].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.JK_FLIP_FLOP, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_d_flip_flop",
            description = "Add a synchronous D Flip-Flop to the canvas. Inputs: [0: D, 1: CLK], Outputs: [0: Q, 1: Q_NOT].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.D_FLIP_FLOP, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_t_flip_flop",
            description = "Add a synchronous T Flip-Flop to the canvas. Inputs: [0: T, 1: CLK], Outputs: [0: Q, 1: Q_NOT].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.T_FLIP_FLOP, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_jk_flip_flop_async",
            description = "Add an asynchronous JK Flip-Flop (with Set/Reset) to the canvas. Inputs: [0: SET, 1: J, 2: CLK, 3: K, 4: RESET], Outputs: [0: Q, 1: Q_NOT].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.JK_FLIP_FLOP_ASYNC, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_sr_flip_flop_async",
            description = "Add an asynchronous SR Flip-Flop (with Set/Reset) to the canvas. Inputs: [0: SET, 1: S, 2: CLK, 3: R, 4: RESET], Outputs: [0: Q, 1: Q_NOT].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.SR_FLIP_FLOP_ASYNC, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_d_flip_flop_async",
            description = "Add an asynchronous D Flip-Flop (with Set/Reset) to the canvas. Inputs: [0: SET, 1: D, 2: CLK, 3: RESET], Outputs: [0: Q, 1: Q_NOT].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.D_FLIP_FLOP_ASYNC, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_t_flip_flop_async",
            description = "Add an asynchronous T Flip-Flop (with Set/Reset) to the canvas. Inputs: [0: SET, 1: T, 2: CLK, 3: RESET], Outputs: [0: Q, 1: Q_NOT].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.T_FLIP_FLOP_ASYNC, canvasViewModel = canvasViewModel)
        }

        // 5. 74-Series Integrated Circuits
        mcpServer.addTool(
            name = "add_ic_7400",
            description = "Add a 7400 Quad 2-Input NAND Gate IC to the canvas. Inputs (10): [0: 1A, 1: 1B, 2: 2A, 3: 2B, 4: GND, 5: 3A, 6: 3B, 7: 4A, 8: 4B, 9: VCC], Outputs (4): [0: 1Y, 1: 2Y, 2: 3Y, 3: 4Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7400, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_7402",
            description = "Add a 7402 Quad 2-Input NOR Gate IC to the canvas. Inputs (10): [0: 1A, 1: 1B, 2: 2A, 3: 2B, 4: GND, 5: 3A, 6: 3B, 7: 4A, 8: 4B, 9: VCC], Outputs (4): [0: 1Y, 1: 2Y, 2: 3Y, 3: 4Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7402, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_7404",
            description = "Add a 7404 Hex Inverter IC to the canvas. Inputs (8): [0: 1A, 1: 2A, 2: 3A, 3: GND, 4: 4A, 5: 5A, 6: 6A, 7: VCC], Outputs (6): [0: 1Y, 1: 2Y, 2: 3Y, 3: 4Y, 4: 5Y, 5: 6Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7404, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_7408",
            description = "Add a 7408 Quad 2-Input AND Gate IC to the canvas. Inputs (10): [0: 1A, 1: 1B, 2: 2A, 3: 2B, 4: GND, 5: 3A, 6: 3B, 7: 4A, 8: 4B, 9: VCC], Outputs (4): [0: 1Y, 1: 2Y, 2: 3Y, 3: 4Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7408, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_7432",
            description = "Add a 7432 Quad 2-Input OR Gate IC to the canvas. Inputs (10): [0: 1A, 1: 1B, 2: 2A, 3: 2B, 4: GND, 5: 3A, 6: 3B, 7: 4A, 8: 4B, 9: VCC], Outputs (4): [0: 1Y, 1: 2Y, 2: 3Y, 3: 4Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7432, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_7486",
            description = "Add a 7486 Quad 2-Input XOR Gate IC to the canvas. Inputs (10): [0: 1A, 1: 1B, 2: 2A, 3: 2B, 4: GND, 5: 3A, 6: 3B, 7: 4A, 8: 4B, 9: VCC], Outputs (4): [0: 1Y, 1: 2Y, 2: 3Y, 3: 4Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7486, canvasViewModel = canvasViewModel)
        }

        // 6. Wiring, Component Modification, and Deletion
        mcpServer.addTool(
            name = "add_wire",
            description = "Connect an output port of a source component to an input port of a target component",
            inputSchema = addWireSchema
        ) { request ->
            wireComponents(request = request, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "update_component",
            description = "Change data of an existing component by ID (x, y coordinates, label/name, inputCount for multi-input logic gates, delay/frequencyHz for CLOCK, or high state for INPUT/BUTTON)",
            inputSchema = updateComponentSchema
        ) { request ->
            updateComponent(request = request, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "delete_component",
            description = "Delete a component by ID from the canvas along with any wires connected to it",
            inputSchema = deleteByIdSchema
        ) { request ->
            deleteComponent(request = request, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "delete_wire",
            description = "Delete a wire by ID from the canvas",
            inputSchema = deleteByIdSchema
        ) { request ->
            deleteWire(request = request, canvasViewModel = canvasViewModel)
        }

        return mcpServer
    }
}
