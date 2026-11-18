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
                    put("description", "Set signal state to HIGH (true) or LOW (false) (for INPUT, BUTTON, or DIP_SWITCH_4BIT)")
                }
                putJsonObject("switchIndex") {
                    put("type", "integer")
                    put("description", "Switch index (0..3) when toggling a DIP_SWITCH_4BIT with 'high'")
                }
                putJsonObject("memoryAddress") {
                    put("type", "integer")
                    put("description", "Memory word index (0..15) for ROM_16X4 or RAM_16X4")
                }
                putJsonObject("memoryValue") {
                    put("type", "integer")
                    put("description", "4-bit value (0..15) to store at memoryAddress (or initial state for REGISTER_4BIT/COUNTER_4BIT/DIP_SWITCH_4BIT)")
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

        mcpServer.addTool(
            name = "add_ic_7407",
            description = "Add a 7407 Hex Buffer IC to the canvas. Inputs (8): [0: 1A, 1: 2A, 2: 3A, 3: GND, 4: 4A, 5: 5A, 6: 6A, 7: VCC], Outputs (6): [0: 1Y, 1: 2Y, 2: 3Y, 3: 4Y, 4: 5Y, 5: 6Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7407, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_7410",
            description = "Add a 7410 Triple 3-Input NAND Gate IC to the canvas. Inputs (11): [0: 1A, 1: 1B, 2: 2A, 3: 2B, 4: 2C, 5: GND, 6: 3A, 7: 3B, 8: 3C, 9: 1C, 10: VCC], Outputs (3): [0: 1Y, 1: 2Y, 2: 3Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7410, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_7411",
            description = "Add a 7411 Triple 3-Input AND Gate IC to the canvas. Inputs (11): [0: 1A, 1: 1B, 2: 2A, 3: 2B, 4: 2C, 5: GND, 6: 3A, 7: 3B, 8: 3C, 9: 1C, 10: VCC], Outputs (3): [0: 1Y, 1: 2Y, 2: 3Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7411, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_7420",
            description = "Add a 7420 Dual 4-Input NAND Gate IC to the canvas. Inputs (10): [0: 1A, 1: 1B, 2: 1C, 3: 1D, 4: GND, 5: 2A, 6: 2B, 7: 2C, 8: 2D, 9: VCC], Outputs (2): [0: 1Y, 1: 2Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7420, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_7421",
            description = "Add a 7421 Dual 4-Input AND Gate IC to the canvas. Inputs (10): [0: 1A, 1: 1B, 2: 1C, 3: 1D, 4: GND, 5: 2A, 6: 2B, 7: 2C, 8: 2D, 9: VCC], Outputs (2): [0: 1Y, 1: 2Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7421, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_7427",
            description = "Add a 7427 Triple 3-Input NOR Gate IC to the canvas. Inputs (11): [0: 1A, 1: 1B, 2: 2A, 3: 2B, 4: 2C, 5: GND, 6: 3A, 7: 3B, 8: 3C, 9: 1C, 10: VCC], Outputs (3): [0: 1Y, 1: 2Y, 2: 3Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7427, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_7430",
            description = "Add a 7430 Single 8-Input NAND Gate IC to the canvas. Inputs (10): [0: A, 1: B, 2: C, 3: D, 4: E, 5: F, 6: GND, 7: G, 8: H, 9: VCC], Outputs (1): [0: Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7430, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_7473",
            description = "Add a 7473 Dual JK Flip-Flop with Clear IC to the canvas. Inputs (10): [0: 1CLR, 1: 1J, 2: 1CLK, 3: 1K, 4: GND, 5: 2CLR, 6: 2J, 7: 2CLK, 8: 2K, 9: VCC], Outputs (4): [0: 1Q, 1: 1~Q, 2: 2Q, 3: 2~Q].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7473, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_7474",
            description = "Add a 7474 Dual D Flip-Flop with Preset and Clear IC to the canvas. Inputs (10): [0: 1CLR, 1: 1D, 2: 1CLK, 3: 1PRE, 4: GND, 5: 2CLR, 6: 2D, 7: 2CLK, 8: 2PRE, 9: VCC], Outputs (4): [0: 1Q, 1: 1~Q, 2: 2Q, 3: 2~Q].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7474, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_7476",
            description = "Add a 7476 Dual JK Flip-Flop with Preset and Clear IC to the canvas. Inputs (12): [0: 1PRE, 1: 1CLR, 2: 1J, 3: 1CLK, 4: 1K, 5: GND, 6: 2PRE, 7: 2CLR, 8: 2J, 9: 2CLK, 10: 2K, 11: VCC], Outputs (4): [0: 1Q, 1: 1~Q, 2: 2Q, 3: 2~Q].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_7476, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_74138",
            description = "Add a 74138 3-to-8 Line Decoder/Demultiplexer IC to the canvas. Inputs (8): [0: A, 1: B, 2: C, 3: ~G2A, 4: ~G2B, 5: G1, 6: GND, 7: VCC], Outputs (8, active-LOW): [0..7: ~Y0..~Y7].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_74138, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_74139",
            description = "Add a 74139 Dual 2-to-4 Line Decoder IC to the canvas. Inputs (8): [0: ~1G, 1: 1A, 2: 1B, 3: GND, 4: ~2G, 5: 2A, 6: 2B, 7: VCC], Outputs (8, active-LOW): [0..3: 1Y0..1Y3, 4..7: 2Y0..2Y3].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_74139, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_74151",
            description = "Add a 74151 8-to-1 Data Selector/Multiplexer IC to the canvas. Inputs (14): [0..7: D0..D7, 8: A, 9: B, 10: C, 11: ~G, 12: GND, 13: VCC], Outputs (2): [0: Y, 1: W (~Y)].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_74151, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_74157",
            description = "Add a 74157 Quad 2-to-1 Data Selector/Multiplexer IC to the canvas. Inputs (12): [0: SEL, 1: 1A, 2: 1B, 3: 2A, 4: 2B, 5: GND, 6: 3A, 7: 3B, 8: 4A, 9: 4B, 10: ~G, 11: VCC], Outputs (4): [0: 1Y, 1: 2Y, 2: 3Y, 3: 4Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_74157, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_74266",
            description = "Add a 74266 Quad 2-Input XNOR Gate IC to the canvas. Inputs (10): [0: 1A, 1: 1B, 2: 2A, 3: 2B, 4: GND, 5: 3A, 6: 3B, 7: 4A, 8: 4B, 9: VCC], Outputs (4): [0: 1Y, 1: 2Y, 2: 3Y, 3: 4Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_74266, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ic_74283",
            description = "Add a 74283 4-Bit Binary Full Adder IC to the canvas. Inputs (11): [0: A1, 1: B1, 2: A2, 3: B2, 4: A3, 5: B3, 6: A4, 7: B4, 8: C0, 9: GND, 10: VCC], Outputs (5): [0: S1, 1: S2, 2: S3, 3: S4, 4: C4].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.IC_74283, canvasViewModel = canvasViewModel)
        }

        // 5b. Additional Gates, Wiring, Plexers, Arithmetic, Memory, and I/O Components
        mcpServer.addTool(
            name = "add_buffer",
            description = "Add a 1-bit non-inverting BUFFER gate to the canvas. Input: 0, Output: 0.",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.BUFFER, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_controlled_buffer",
            description = "Add a Tri-State CONTROLLED_BUFFER to the canvas. Inputs: [0: DATA, 1: EN], Output: 0.",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.CONTROLLED_BUFFER, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_controlled_inverter",
            description = "Add a Tri-State CONTROLLED_INVERTER to the canvas. Inputs: [0: DATA, 1: EN], Output: 0.",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.CONTROLLED_INVERTER, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_odd_parity",
            description = "Add an ODD_PARITY checker gate to the canvas. Inputs: 0..(inputCount-1), Output: 0 (HIGH when odd number of inputs are HIGH).",
            inputSchema = multiInputGateSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.ODD_PARITY, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_even_parity",
            description = "Add an EVEN_PARITY checker gate to the canvas. Inputs: 0..(inputCount-1), Output: 0 (HIGH when even number of inputs are HIGH).",
            inputSchema = multiInputGateSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.EVEN_PARITY, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_constant_high",
            description = "Add a permanent HIGH (1 / VCC) constant source to the canvas. Inputs: none, Output: 0.",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.CONSTANT_HIGH, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_constant_low",
            description = "Add a permanent LOW (0 / GND) constant source to the canvas. Inputs: none, Output: 0.",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.CONSTANT_LOW, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_probe",
            description = "Add a PROBE signal monitor to the canvas. Input: 0, Outputs: none.",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.PROBE, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_tunnel",
            description = "Add a wireless net TUNNEL to the canvas (connects with all other tunnels sharing the same label/name). Input: 0, Output: 0.",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.TUNNEL, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_multiplexer_2_to_1",
            description = "Add a 2-to-1 Multiplexer to the canvas. Inputs (3): [0: D0, 1: D1, 2: SEL], Output (1): [0: Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.MULTIPLEXER_2_TO_1, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_multiplexer_4_to_1",
            description = "Add a 4-to-1 Multiplexer to the canvas. Inputs (6): [0..3: D0..D3, 4: SEL0, 5: SEL1], Output (1): [0: Y].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.MULTIPLEXER_4_TO_1, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_demultiplexer_1_to_2",
            description = "Add a 1-to-2 Demultiplexer to the canvas. Inputs (2): [0: D, 1: SEL], Outputs (2): [0: Y0, 1: Y1].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.DEMULTIPLEXER_1_TO_2, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_demultiplexer_1_to_4",
            description = "Add a 1-to-4 Demultiplexer to the canvas. Inputs (3): [0: D, 1: SEL0, 2: SEL1], Outputs (4): [0..3: Y0..Y3].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.DEMULTIPLEXER_1_TO_4, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_decoder_2_to_4",
            description = "Add a 2-to-4 active-HIGH Line Decoder to the canvas. Inputs (3): [0: A0, 1: A1, 2: EN], Outputs (4): [0..3: Y0..Y3].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.DECODER_2_TO_4, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_decoder_3_to_8",
            description = "Add a 3-to-8 active-HIGH Line Decoder to the canvas. Inputs (4): [0: A0, 1: A1, 2: A2, 3: EN], Outputs (8): [0..7: Y0..Y7].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.DECODER_3_TO_8, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_priority_encoder_4_to_2",
            description = "Add a 4-to-2 Priority Encoder to the canvas. Inputs (4): [0..3: D0..D3], Outputs (3): [0: A0, 1: A1, 2: VALID].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.PRIORITY_ENCODER_4_TO_2, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_half_adder",
            description = "Add a 1-bit Half Adder to the canvas. Inputs (2): [0: A, 1: B], Outputs (2): [0: SUM, 1: CARRY].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.HALF_ADDER, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_full_adder",
            description = "Add a 1-bit Full Adder to the canvas. Inputs (3): [0: A, 1: B, 2: CIN], Outputs (2): [0: SUM, 1: COUT].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.FULL_ADDER, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_adder_4bit",
            description = "Add a 4-bit Binary Full Adder to the canvas. Inputs (9): [0..3: A0..A3, 4..7: B0..B3, 8: CIN], Outputs (5): [0..3: S0..S3, 4: COUT].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.ADDER_4BIT, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_subtractor_4bit",
            description = "Add a 4-bit Binary Subtractor to the canvas. Inputs (9): [0..3: A0..A3, 4..7: B0..B3, 8: BIN], Outputs (5): [0..3: D0..D3, 4: BOUT].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.SUBTRACTOR_4BIT, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_comparator_4bit",
            description = "Add a 4-bit Magnitude Comparator to the canvas. Inputs (8): [0..3: A0..A3, 4..7: B0..B3], Outputs (3): [0: A_GT_B, 1: A_EQ_B, 2: A_LT_B].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.COMPARATOR_4BIT, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_negator_4bit",
            description = "Add a 4-bit Two's Complement Negator (-A mod 16) to the canvas. Inputs (4): [0..3: A0..A3], Outputs (4): [0..3: Y0..Y3].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.NEGATOR_4BIT, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_register_4bit",
            description = "Add a 4-bit D Register to the canvas. Inputs (7): [0..3: D0..D3, 4: CLK, 5: EN, 6: CLR], Outputs (4): [0..3: Q0..Q3].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.REGISTER_4BIT, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_counter_4bit",
            description = "Add a 4-bit Up/Down Binary Counter to the canvas. Inputs (9): [0: CLK, 1: EN, 2: UP_DOWN, 3: CLR, 4: LOAD, 5..8: D0..D3], Outputs (5): [0..3: Q0..Q3, 4: RCO].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.COUNTER_4BIT, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_shift_register_4bit",
            description = "Add a 4-bit Shift Register to the canvas. Inputs (8): [0: SER_IN, 1: CLK, 2: LOAD, 3: CLR, 4..7: D0..D3], Outputs (4): [0..3: Q0..Q3].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.SHIFT_REGISTER_4BIT, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_ram_16x4",
            description = "Add a 16x4-bit Static RAM to the canvas. Inputs (10): [0..3: ADDR0..ADDR3, 4..7: DIN0..DIN3, 8: WE, 9: CLK], Outputs (4): [0..3: DOUT0..DOUT3].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.RAM_16X4, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_rom_16x4",
            description = "Add a 16x4-bit Read-Only Memory to the canvas. Inputs (4): [0..3: ADDR0..ADDR3], Outputs (4): [0..3: DOUT0..DOUT3].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.ROM_16X4, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_led",
            description = "Add a visual LED lamp indicator to the canvas. Input: 0, Outputs: none.",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.LED, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_seven_segment_display",
            description = "Add a 7-Segment Display to the canvas. Inputs (8): [0..6: segments a..g, 7: dp], Outputs: none.",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.SEVEN_SEGMENT_DISPLAY, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_hex_digit_display",
            description = "Add a 4-bit Hexadecimal Digit Display to the canvas. Inputs (5): [0..3: D0..D3 (0..F), 4: dp], Outputs: none.",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.HEX_DIGIT_DISPLAY, canvasViewModel = canvasViewModel)
        }

        mcpServer.addTool(
            name = "add_dip_switch_4bit",
            description = "Add a 4-bit interactive DIP Switch bank to the canvas. Inputs: none, Outputs (4): [0..3: SW0..SW3].",
            inputSchema = fixedPortComponentSchema
        ) { request ->
            addComponent(request = request, componentType = ComponentType.DIP_SWITCH_4BIT, canvasViewModel = canvasViewModel)
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
