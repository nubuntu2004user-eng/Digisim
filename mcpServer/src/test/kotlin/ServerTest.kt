package com.example

import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.mcpClient.mcpClient
import com.example.mcpServer.addComponent
import com.example.mcpServer.deleteComponent
import com.example.mcpServer.deleteWire
import com.example.mcpServer.parseForLLM
import com.example.mcpServer.updateComponent
import com.example.mcpServer.wireComponents
import engineLogic.ClockManager
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.testing.testApplication
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequestParams
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import logicGates.Pin
import java.net.ServerSocket
import kotlin.test.*

class ServerTest {

    private fun findFreePort(): Int = ServerSocket(0).use { it.localPort }

    private suspend fun withLiveMcpSession(
        viewModel: CanvasViewModel = CanvasViewModel(),
        block: suspend (mcpClient, CanvasViewModel) -> Unit
    ) {
        val port = findFreePort()
        val server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> =
            startServer(canvasViewModel = viewModel, wait = false, port = port)
        val client = mcpClient(url = "http://127.0.0.1:$port/mcp")
        try {
            client.initializeClient()
            block(client, viewModel)
        } finally {
            client.httpClient.close()
            server.stop(100, 1000)
        }
    }

    private fun extractText(result: io.modelcontextprotocol.kotlin.sdk.types.CallToolResult): String =
        result.content.filterIsInstance<TextContent>().joinToString("\n") { it.text }

    @Test
    fun `test root endpoint`() = testApplication {
        application {
            rootModule()
        }
        assertEquals(HttpStatusCode.OK, client.get("/").status)
    }

    @Test
    fun `test mcp client lists all 31 registered tools with descriptions and schemas`() = runBlocking {
        withLiveMcpSession { client, _ ->
            val tools = client.listTools()
            val toolNames = tools.map { it.name }.toSet()

            val expectedTools = setOf(
                "get_canvas_state",
                "add_and",
                "add_or",
                "add_invertor",
                "add_not",
                "add_nand",
                "add_nor",
                "add_xor",
                "add_xnor",
                "add_input",
                "add_output",
                "add_button",
                "add_clock",
                "add_rs_flip_flop",
                "add_jk_flip_flop",
                "add_d_flip_flop",
                "add_t_flip_flop",
                "add_jk_flip_flop_async",
                "add_sr_flip_flop_async",
                "add_d_flip_flop_async",
                "add_t_flip_flop_async",
                "add_ic_7400",
                "add_ic_7402",
                "add_ic_7404",
                "add_ic_7408",
                "add_ic_7432",
                "add_ic_7486",
                "add_wire",
                "update_component",
                "delete_component",
                "delete_wire"
            )

            assertEquals(expectedTools, toolNames, "MCP Server must expose all 31 expected tools")
            tools.forEach { tool ->
                assertFalse(tool.description.isNullOrBlank(), "Tool ${tool.name} must have a non-blank description")
                assertNotNull(tool.inputSchema, "Tool ${tool.name} must define an inputSchema")
            }
        }
    }

    @Test
    fun `test all 26 component creation tools over live mcp client protocol`() = runBlocking {
        withLiveMcpSession { client, viewModel ->
            data class ToolSpec(
                val toolName: String,
                val expectedType: ComponentType,
                val expectedInputs: Int,
                val expectedOutputs: Int,
                val extraArgs: JsonObject = buildJsonObject {}
            )

            val specs = listOf(
                ToolSpec("add_and", ComponentType.AND, 3, 1, buildJsonObject { put("inputCount", 3) }),
                ToolSpec("add_or", ComponentType.OR, 4, 1, buildJsonObject { put("inputCount", 4) }),
                ToolSpec("add_invertor", ComponentType.NOT, 1, 1),
                ToolSpec("add_not", ComponentType.NOT, 1, 1),
                ToolSpec("add_nand", ComponentType.NAND, 3, 1, buildJsonObject { put("inputCount", 3) }),
                ToolSpec("add_nor", ComponentType.NOR, 2, 1),
                ToolSpec("add_xor", ComponentType.XOR, 2, 1),
                ToolSpec("add_xnor", ComponentType.XNOR, 2, 1),
                ToolSpec("add_input", ComponentType.INPUT, 0, 1, buildJsonObject { put("high", true) }),
                ToolSpec("add_output", ComponentType.OUTPUT, 1, 0),
                ToolSpec("add_button", ComponentType.BUTTON, 0, 1, buildJsonObject { put("high", true) }),
                ToolSpec("add_clock", ComponentType.CLOCK, 0, 1, buildJsonObject { put("frequencyHz", 50) }),
                ToolSpec("add_rs_flip_flop", ComponentType.RS_FLIP_FLOP, 2, 2),
                ToolSpec("add_jk_flip_flop", ComponentType.JK_FLIP_FLOP, 3, 2),
                ToolSpec("add_d_flip_flop", ComponentType.D_FLIP_FLOP, 2, 2),
                ToolSpec("add_t_flip_flop", ComponentType.T_FLIP_FLOP, 2, 2),
                ToolSpec("add_jk_flip_flop_async", ComponentType.JK_FLIP_FLOP_ASYNC, 5, 2),
                ToolSpec("add_sr_flip_flop_async", ComponentType.SR_FLIP_FLOP_ASYNC, 5, 2),
                ToolSpec("add_d_flip_flop_async", ComponentType.D_FLIP_FLOP_ASYNC, 4, 2),
                ToolSpec("add_t_flip_flop_async", ComponentType.T_FLIP_FLOP_ASYNC, 4, 2),
                ToolSpec("add_ic_7400", ComponentType.IC_7400, 10, 4),
                ToolSpec("add_ic_7402", ComponentType.IC_7402, 10, 4),
                ToolSpec("add_ic_7404", ComponentType.IC_7404, 8, 6),
                ToolSpec("add_ic_7408", ComponentType.IC_7408, 10, 4),
                ToolSpec("add_ic_7432", ComponentType.IC_7432, 10, 4),
                ToolSpec("add_ic_7486", ComponentType.IC_7486, 10, 4)
            )

            specs.forEachIndexed { idx, spec ->
                val x = (idx * 60).toFloat()
                val y = (idx * 40).toFloat()
                val label = "COMP_$idx"
                val args = buildJsonObject {
                    put("x", x)
                    put("y", y)
                    put("label", label)
                    spec.extraArgs.forEach { (k, v) -> put(k, v) }
                }

                val response = extractText(client.callTool(spec.toolName, args))
                assertTrue(
                    response.contains("Added ${spec.expectedType} component"),
                    "Unexpected response for ${spec.toolName}: $response"
                )
            }

            assertEquals(specs.size, viewModel.components.size)
            specs.forEachIndexed { idx, spec ->
                val comp = viewModel.components[idx]
                assertEquals(spec.expectedType, comp.componentType, "Mismatch for tool ${spec.toolName}")
                assertEquals((idx * 60).toFloat(), comp.x)
                assertEquals((idx * 40).toFloat(), comp.y)
                assertEquals("COMP_$idx", comp.label)
                assertEquals(spec.expectedInputs, comp.inputCount, "Input count mismatch for ${spec.toolName}")
                assertEquals(spec.expectedOutputs, comp.outputCount, "Output count mismatch for ${spec.toolName}")
            }

            val inputPin = viewModel.components.first { it.componentType == ComponentType.INPUT }
            assertEquals(Pin.HIGH, inputPin.outputPin)

            val clockPin = viewModel.components.first { it.componentType == ComponentType.CLOCK }
            assertEquals(10f, clockPin.delay, "50 Hz clock should have delay = 1000 / (50 * 2) = 10 ticks")
        }
    }

    @Test
    fun `test update_component modifies attributes and prunes out of range wires`() = runBlocking {
        withLiveMcpSession { client, viewModel ->
            client.callTool("add_input", buildJsonObject { put("x", 20); put("y", 20); put("name", "IN0") })
            client.callTool("add_input", buildJsonObject { put("x", 20); put("y", 80); put("name", "IN1") })
            client.callTool("add_nand", buildJsonObject { put("x", 160); put("y", 40); put("inputCount", 4); put("name", "NAND4") })
            client.callTool("add_not", buildJsonObject { put("x", 300); put("y", 40); put("name", "INV") })
            client.callTool("add_clock", buildJsonObject { put("x", 20); put("y", 160); put("delay", 500) })

            val in0 = viewModel.components[0]
            val in1 = viewModel.components[1]
            val nand = viewModel.components[2]
            val notGate = viewModel.components[3]
            val clock = viewModel.components[4]

            // Wire IN0 -> NAND port 0, IN1 -> NAND port 3
            client.callTool(
                "add_wire",
                buildJsonObject {
                    put("source_gate_id", in0.ID)
                    put("source_port_index", 0)
                    put("target_gate_id", nand.ID)
                    put("target_port_index", 0)
                }
            )
            client.callTool(
                "add_wire",
                buildJsonObject {
                    put("source_gate_id", in1.ID)
                    put("source_port_index", 0)
                    put("target_gate_id", nand.ID)
                    put("target_port_index", 3)
                }
            )
            assertEquals(2, viewModel.wires.size)

            // Update NAND: move x/y, rename label, shrink inputCount from 4 to 2
            val updateMsg = extractText(
                client.callTool(
                    "update_component",
                    buildJsonObject {
                        put("id", nand.ID)
                        put("x", 220)
                        put("y", 100)
                        put("label", "NAND2_UPDATED")
                        put("inputCount", 2)
                    }
                )
            )
            assertTrue(updateMsg.contains("Updated component ID ${nand.ID}"))
            assertEquals(220f, nand.x)
            assertEquals(100f, nand.y)
            assertEquals("NAND2_UPDATED", nand.label)
            assertEquals(2, nand.inputCount)
            assertEquals(1, viewModel.wires.size, "Wire on port 3 must be removed when inputCount shrinks to 2")
            assertEquals(0, viewModel.wires.first().targetPortIndex)

            // Verify inputCount clamping (e.g. > 64 clamps to 64)
            client.callTool(
                "update_component",
                buildJsonObject {
                    put("id", nand.ID)
                    put("inputCount", 100)
                }
            )
            assertEquals(64, nand.inputCount)

            // Verify fixed-input component ignores inputCount change
            val fixedUpdateMsg = extractText(
                client.callTool(
                    "update_component",
                    buildJsonObject {
                        put("id", notGate.ID)
                        put("inputCount", 5)
                    }
                )
            )
            assertTrue(fixedUpdateMsg.contains("inputCount ignored"))
            assertEquals(1, notGate.inputCount)

            // Update CLOCK via explicit delay and via frequencyHz
            client.callTool(
                "update_component",
                buildJsonObject {
                    put("id", clock.ID)
                    put("delay", 125)
                }
            )
            assertEquals(125f, clock.delay)

            client.callTool(
                "update_component",
                buildJsonObject {
                    put("id", clock.ID)
                    put("frequencyHz", 100)
                }
            )
            assertEquals(5f, clock.delay)

            // Update INPUT high state
            client.callTool(
                "update_component",
                buildJsonObject {
                    put("id", in0.ID)
                    put("high", true)
                }
            )
            assertEquals(Pin.HIGH, in0.outputPin)

            client.callTool(
                "update_component",
                buildJsonObject {
                    put("id", in0.ID)
                    put("high", false)
                }
            )
            assertEquals(Pin.LOW, in0.outputPin)
        }
    }

    @Test
    fun `test delete_component delete_wire and error handling for invalid arguments`() = runBlocking {
        withLiveMcpSession { client, viewModel ->
            client.callTool("add_input", buildJsonObject { put("x", 40); put("y", 40) })
            client.callTool("add_not", buildJsonObject { put("x", 160); put("y", 40) })
            client.callTool("add_output", buildJsonObject { put("x", 280); put("y", 40) })

            val inComp = viewModel.components[0]
            val notComp = viewModel.components[1]
            val outComp = viewModel.components[2]
            viewModel.selectedGateId = notComp.ID

            client.callTool(
                "add_wire",
                buildJsonObject {
                    put("source_gate_id", inComp.ID)
                    put("source_port_index", 0)
                    put("target_gate_id", notComp.ID)
                    put("target_port_index", 0)
                }
            )
            client.callTool(
                "add_wire",
                buildJsonObject {
                    put("source_gate_id", notComp.ID)
                    put("source_port_index", 0)
                    put("target_gate_id", outComp.ID)
                    put("target_port_index", 0)
                }
            )
            assertEquals(2, viewModel.wires.size)

            // Delete the first wire via delete_wire
            val firstWireId = viewModel.wires[0].id
            val delWireMsg = extractText(
                client.callTool("delete_wire", buildJsonObject { put("id", firstWireId) })
            )
            assertTrue(delWireMsg.contains("Deleted wire (ID: $firstWireId)"))
            assertEquals(1, viewModel.wires.size)

            // Delete the NOT gate component and verify the remaining connected wire and selectedGateId are cleared
            val delCompMsg = extractText(
                client.callTool("delete_component", buildJsonObject { put("id", notComp.ID) })
            )
            assertTrue(delCompMsg.contains("Deleted NOT component (ID: ${notComp.ID})"))
            assertTrue(delCompMsg.contains("removed 1 connected wire(s)"))
            assertEquals(2, viewModel.components.size)
            assertTrue(viewModel.wires.isEmpty())
            assertNull(viewModel.selectedGateId, "Deleting the currently selected component must clear selectedGateId")

            // Test error responses on non-existent IDs or missing arguments
            val missingUpdate = extractText(
                client.callTool("update_component", buildJsonObject { put("id", 9999); put("x", 10) })
            )
            assertTrue(missingUpdate.contains("no component found with ID 9999"))

            val missingDelete = extractText(
                client.callTool("delete_component", buildJsonObject { put("id", 9999) })
            )
            assertTrue(missingDelete.contains("no component found with ID 9999"))

            val missingWireDelete = extractText(
                client.callTool("delete_wire", buildJsonObject { put("id", 9999) })
            )
            assertTrue(missingWireDelete.contains("no wire found with ID 9999"))

            val invalidWireAdd = extractText(
                client.callTool("add_wire", buildJsonObject { put("source_gate_id", inComp.ID) })
            )
            assertTrue(invalidWireAdd.contains("Failed to add wire"))
        }
    }

    @Test
    fun `test end to end half adder circuit built wired and simulated via mcp client`() = runBlocking {
        withLiveMcpSession { client, viewModel ->
            val simulation = SimulationViewModel()
            val clockManager = ClockManager()

            // Build Half Adder: Inputs A & B, XOR (SUM), AND (CARRY), Outputs SUM & CARRY
            client.callTool("add_input", buildJsonObject { put("x", 40); put("y", 40); put("label", "A") })
            client.callTool("add_input", buildJsonObject { put("x", 40); put("y", 140); put("label", "B") })
            client.callTool("add_xor", buildJsonObject { put("x", 200); put("y", 40); put("label", "XOR_SUM") })
            client.callTool("add_and", buildJsonObject { put("x", 200); put("y", 140); put("label", "AND_CARRY") })
            client.callTool("add_output", buildJsonObject { put("x", 360); put("y", 40); put("label", "SUM") })
            client.callTool("add_output", buildJsonObject { put("x", 360); put("y", 140); put("label", "CARRY") })

            val inA = viewModel.components.first { it.label == "A" }
            val inB = viewModel.components.first { it.label == "B" }
            val xorGate = viewModel.components.first { it.label == "XOR_SUM" }
            val andGate = viewModel.components.first { it.label == "AND_CARRY" }
            val outSum = viewModel.components.first { it.label == "SUM" }
            val outCarry = viewModel.components.first { it.label == "CARRY" }

            // Wire A -> XOR[0], B -> XOR[1], A -> AND[0], B -> AND[1], XOR[0] -> SUM[0], AND[0] -> CARRY[0]
            val connections = listOf(
                Triple(inA.ID, xorGate.ID, 0),
                Triple(inB.ID, xorGate.ID, 1),
                Triple(inA.ID, andGate.ID, 0),
                Triple(inB.ID, andGate.ID, 1),
                Triple(xorGate.ID, outSum.ID, 0),
                Triple(andGate.ID, outCarry.ID, 0)
            )
            connections.forEach { (srcId, dstId, dstPort) ->
                client.callTool(
                    "add_wire",
                    buildJsonObject {
                        put("source_gate_id", srcId)
                        put("source_port_index", 0)
                        put("target_gate_id", dstId)
                        put("target_port_index", dstPort)
                    }
                )
            }

            // Verify Half-Adder Truth Table via MCP update_component + SimulationViewModel
            val truthTable = listOf(
                // A, B -> SUM, CARRY
                listOf(false, false, Pin.LOW, Pin.LOW),
                listOf(true, false, Pin.HIGH, Pin.LOW),
                listOf(false, true, Pin.HIGH, Pin.LOW),
                listOf(true, true, Pin.LOW, Pin.HIGH)
            )

            for (row in truthTable) {
                val aVal = row[0] as Boolean
                val bVal = row[1] as Boolean
                val expectedSum = row[2] as Pin
                val expectedCarry = row[3] as Pin

                client.callTool("update_component", buildJsonObject { put("id", inA.ID); put("high", aVal) })
                client.callTool("update_component", buildJsonObject { put("id", inB.ID); put("high", bVal) })

                simulation.runSimulation(viewModel, null, clockManager)

                assertEquals(expectedSum, outSum.outputPin, "Half-adder SUM mismatch for A=$aVal, B=$bVal")
                assertEquals(expectedCarry, outCarry.outputPin, "Half-adder CARRY mismatch for A=$aVal, B=$bVal")
            }

            // Also verify get_canvas_state reflects the final simulated state (A=1, B=1 -> SUM=LOW, CARRY=HIGH)
            val canvasReport = extractText(client.callTool("get_canvas_state"))
            assertTrue(canvasReport.contains("Custom label = SUM, Output state = LOW"))
            assertTrue(canvasReport.contains("Custom label = CARRY, Output state = HIGH"))
        }
    }

    @Test
    fun `test end to end 7400 quad nand ic circuit built and simulated via mcp client`() = runBlocking {
        withLiveMcpSession { client, viewModel ->
            val simulation = SimulationViewModel()
            val clockManager = ClockManager()

            // Place inputs: 1A, 1B, VCC (HIGH), GND (LOW), IC_7400, and OUTPUT
            client.callTool("add_input", buildJsonObject { put("x", 20); put("y", 20); put("label", "IN_1A"); put("high", true) })
            client.callTool("add_input", buildJsonObject { put("x", 20); put("y", 60); put("label", "IN_1B"); put("high", true) })
            client.callTool("add_input", buildJsonObject { put("x", 20); put("y", 100); put("label", "GND"); put("high", false) })
            client.callTool("add_input", buildJsonObject { put("x", 20); put("y", 140); put("label", "VCC"); put("high", true) })
            client.callTool("add_ic_7400", buildJsonObject { put("x", 180); put("y", 20); put("label", "U1_7400") })
            client.callTool("add_output", buildJsonObject { put("x", 360); put("y", 40); put("label", "OUT_1Y") })

            val in1A = viewModel.components.first { it.label == "IN_1A" }
            val in1B = viewModel.components.first { it.label == "IN_1B" }
            val gnd = viewModel.components.first { it.label == "GND" }
            val vcc = viewModel.components.first { it.label == "VCC" }
            val ic7400 = viewModel.components.first { it.label == "U1_7400" }
            val out1Y = viewModel.components.first { it.label == "OUT_1Y" }

            // IC_7400 inputs: 0: 1A, 1: 1B, 4: GND, 9: VCC; output 0: 1Y
            val wires = listOf(
                Triple(in1A.ID, ic7400.ID, 0),
                Triple(in1B.ID, ic7400.ID, 1),
                Triple(gnd.ID, ic7400.ID, 4),
                Triple(vcc.ID, ic7400.ID, 9),
                Triple(ic7400.ID, out1Y.ID, 0)
            )
            wires.forEach { (src, dst, dstPort) ->
                client.callTool(
                    "add_wire",
                    buildJsonObject {
                        put("source_gate_id", src)
                        put("source_port_index", 0)
                        put("target_gate_id", dst)
                        put("target_port_index", dstPort)
                    }
                )
            }

            // 1A=HIGH, 1B=HIGH -> NAND output 1Y = LOW
            simulation.runSimulation(viewModel, null, clockManager)
            assertEquals(Pin.LOW, out1Y.outputPin)

            // Toggle 1A=LOW via MCP update_component -> NAND output 1Y = HIGH
            client.callTool("update_component", buildJsonObject { put("id", in1A.ID); put("high", false) })
            simulation.runSimulation(viewModel, null, clockManager)
            assertEquals(Pin.HIGH, out1Y.outputPin)
        }
    }
}
