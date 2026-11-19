package com.example.digisim

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.ComponentLibrariesViewModel
import com.example.digisim.UiUtils.TranslationViewModel
import com.example.digisim.View.App
import engineLogic.ClockManager
import logicGates.Pin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SharedLogicDesktopTest {

    /**
     * Playwright-style Test 1:
     * Verifies that every single library tab ("Gates", "Wiring", "Flip-Flops", "74**")
     * displays ALL of its components on screen with non-clipped positive width & height.
     */
    @Test
    fun testAllLibrariesDisplayEveryComponentVisibleInUi() = runComposeUiTest {
        val viewModel = CanvasViewModel()
        val simulation = SimulationViewModel()
        val librariesVm = ComponentLibrariesViewModel()
        val clockManager = ClockManager()
        val translationVm = TranslationViewModel()
        val appState = AppState()

        setContent {
            Box(modifier = Modifier.size(1280.dp, 820.dp)) {
                App(
                    viewModel = viewModel,
                    simulation = simulation,
                    librariesViewModel = librariesVm,
                    clockManager = clockManager,
                    translationViewModel = translationVm,
                    appState = appState
                )
            }
        }
        waitForIdle()

        // 1. Check "Gates" library (default tab)
        val expectedGates = listOf(
            "NOT Gate", "Buffer", "AND Gate", "OR Gate", "NAND Gate", "NOR Gate",
            "XOR Gate", "XNOR Gate", "Tri-State Buffer", "Tri-State Inverter", "Odd Parity", "Even Parity"
        )
        for (gateName in expectedGates) {
            val node = onNodeWithText(gateName).assertExists().performScrollTo().assertIsDisplayed()
            val bounds = node.fetchSemanticsNode().boundsInRoot
            assertTrue(bounds.width > 10f, "Expected $gateName to have visible width, got ${bounds.width}")
            assertTrue(bounds.right <= 330f, "Expected $gateName to fit inside sidebar, got right=${bounds.right}")
        }

        // 2. Click "Wiring" tab and verify all 8 components are visible
        onNodeWithText("Wiring").performClick()
        waitForIdle()

        val expectedWiring = listOf(
            "Input pin", "Button", "Output pin", "Clock",
            "Constant 1 (VCC)", "Constant 0 (GND)", "Probe", "Tunnel"
        )
        for (itemName in expectedWiring) {
            val node = onNodeWithText(itemName).assertExists().performScrollTo().assertIsDisplayed()
            val bounds = node.fetchSemanticsNode().boundsInRoot
            assertTrue(bounds.width > 10f, "Expected $itemName to have visible width, got ${bounds.width}")
            assertTrue(bounds.right <= 330f, "Expected $itemName to fit inside sidebar, got right=${bounds.right}")
        }

        // 2b. Click "Plexers", "Arithmetic", "Memory", and "I/O" tabs and verify components
        onNodeWithText("Plexers").performClick()
        waitForIdle()
        for (itemName in listOf("MUX 2:1", "MUX 4:1", "DEMUX 1:2", "Decoder 2:4", "Priority Encoder")) {
            onNodeWithText(itemName).assertExists().performScrollTo().assertIsDisplayed()
        }

        onNodeWithText("Arithmetic").performClick()
        waitForIdle()
        for (itemName in listOf("Half Adder", "Full Adder", "4-Bit Adder", "4-Bit Subtractor", "4-Bit Comparator", "4-Bit Negator")) {
            onNodeWithText(itemName).assertExists().performScrollTo().assertIsDisplayed()
        }

        onNodeWithText("Memory").performClick()
        waitForIdle()
        for (itemName in listOf("4-Bit Register", "4-Bit Counter", "4-Bit Shift Reg", "RAM 16x4", "ROM 16x4")) {
            onNodeWithText(itemName).assertExists().performScrollTo().assertIsDisplayed()
        }

        onNodeWithText("I/O").performClick()
        waitForIdle()
        for (itemName in listOf("LED", "7-Segment", "Hex Display", "4-Bit DIP Switch")) {
            onNodeWithText(itemName).assertExists().performScrollTo().assertIsDisplayed()
        }

        // 3. Click "Flip-Flops" tab and verify flip-flop items exist
        onNodeWithText("Flip-Flops").performClick()
        waitForIdle()

        val expectedFlipFlops = listOf(
            "RS Flip-Flop", "JK Flip-Flop", "D Flip-Flop", "T Flip-Flop"
        )
        for (ffName in expectedFlipFlops) {
            val node = onNodeWithText(ffName).assertExists().assertIsDisplayed()
            val bounds = node.fetchSemanticsNode().boundsInRoot
            assertTrue(bounds.width > 10f, "Expected $ffName to have visible width, got ${bounds.width}")
            assertTrue(bounds.right <= 330f, "Expected $ffName to fit inside sidebar, got right=${bounds.right}")
        }

        // 4. Click "74**" tab and verify all 22 74-series ICs are visible (scrolling through the 2-column list)
        onNodeWithText("74**").performClick()
        waitForIdle()

        val expectedChips = listOf(
            "7400", "7402", "7404", "7407", "7408", "7410", "7411", "7420",
            "7421", "7427", "7430", "7432", "7473", "7474", "7476", "7486",
            "74138", "74139", "74151", "74157", "74266", "74283"
        )
        for (chipName in expectedChips) {
            val node = onNodeWithText(chipName).assertExists().performScrollTo().assertIsDisplayed()
            val bounds = node.fetchSemanticsNode().boundsInRoot
            assertTrue(bounds.width > 10f, "Expected $chipName to have visible width, got ${bounds.width}")
            assertTrue(bounds.right <= 330f, "Expected $chipName to fit inside sidebar, got right=${bounds.right}")
        }
    }

    /**
     * Playwright-style Test 2:
     * Builds a NOT Inverter circuit completely through UI clicks (Library clicks, Canvas clicks,
     * Wire mode clicks, Start button click, and Poke clicks on canvas) and verifies the Output pin state.
     */
    @Test
    fun testBuildAndSimulateInverterCircuitViaUiClicks() = runComposeUiTest {
        val viewModel = CanvasViewModel()
        val simulation = SimulationViewModel()
        val librariesVm = ComponentLibrariesViewModel()
        val clockManager = ClockManager()
        val translationVm = TranslationViewModel()
        val appState = AppState()

        setContent {
            Box(modifier = Modifier.size(1280.dp, 820.dp)) {
                App(
                    viewModel = viewModel,
                    simulation = simulation,
                    librariesViewModel = librariesVm,
                    clockManager = clockManager,
                    translationViewModel = translationVm,
                    appState = appState
                )
            }
        }
        waitForIdle()

        val canvasNode = onNodeWithTag("logic_canvas").assertExists()

        // Step 1: Select "Wiring" tab -> click "Input pin" -> click canvas at (100, 120)
        onNodeWithText("Wiring").performClick()
        waitForIdle()
        onNodeWithText("Input pin").performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(100f, 120f)) }
        waitForIdle()

        // Step 2: Click "Gates" tab -> click "NOT Gate" -> click canvas at (260, 120)
        onNodeWithText("Gates").performClick()
        waitForIdle()
        onNodeWithText("NOT Gate").performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(260f, 120f)) }
        waitForIdle()

        // Step 3: Click "Wiring" tab -> click "Output pin" -> click canvas at (440, 120)
        onNodeWithText("Wiring").performClick()
        waitForIdle()
        onNodeWithText("Output pin").performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(440f, 120f)) }
        waitForIdle()

        assertEquals(3, viewModel.components.size, "Expected 3 placed components on canvas")
        val inComp = viewModel.components[0]
        val notComp = viewModel.components[1]
        val outComp = viewModel.components[2]
        assertEquals(ComponentType.INPUT, inComp.componentType)
        assertEquals(ComponentType.NOT, notComp.componentType)
        assertEquals(ComponentType.OUTPUT, outComp.componentType)

        // Step 4: Click "Wire" mode button in the top tool ribbon and wire the pins on the canvas
        onNodeWithText("Wire").performClick()
        waitForIdle()
        assertEquals(CanvasViewModel.editingMode.WIRE, viewModel.currentMode)

        // Wire Input output port -> NOT input port
        val inPort = inComp.findPortOffset()
        val notInPort = notComp.inputPortPositions().first()
        canvasNode.performMouseInput { click(inPort) }
        waitForIdle()
        canvasNode.performMouseInput { click(notInPort) }
        waitForIdle()

        // Wire NOT output port -> Output input port
        val notOutPort = notComp.outputPortPositions().first()
        val outPort = outComp.findPortOffset()
        canvasNode.performMouseInput { click(notOutPort) }
        waitForIdle()
        canvasNode.performMouseInput { click(outPort) }
        waitForIdle()

        assertEquals(2, viewModel.wires.size, "Expected 2 wires connected via UI")

        // Step 5: Click "Start" simulation button in the top transport bar
        onNodeWithText("Start").performClick()
        waitForIdle()
        assertTrue(simulation.isRunning)
        assertEquals(CanvasViewModel.editingMode.POKE, viewModel.currentMode)

        // When Input is LOW (0), NOT output and Output pin must be HIGH (1)
        assertEquals(Pin.LOW, inComp.outputPin)
        assertEquals(Pin.HIGH, notComp.outputPin)
        assertEquals(Pin.HIGH, outComp.outputPin)

        // Step 6: Poke the Input component on the canvas -> Input becomes HIGH (1), Output becomes LOW (0)
        val inCenter = Offset(inComp.x + inComp.width / 2f, inComp.y + inComp.height / 2f)
        canvasNode.performMouseInput { click(inCenter) }
        waitForIdle()

        assertEquals(Pin.HIGH, inComp.outputPin)
        assertEquals(Pin.LOW, notComp.outputPin)
        assertEquals(Pin.LOW, outComp.outputPin)
    }

    /**
     * Playwright-style Test 3:
     * Builds a 2-input AND Gate circuit via UI clicks, wires both inputs and the output,
     * runs the simulation, and pokes both inputs through all truth-table states (00, 10, 11, 01).
     */
    @Test
    fun testBuildAndSimulateAndGateTruthTableViaUiClicks() = runComposeUiTest {
        val viewModel = CanvasViewModel()
        val simulation = SimulationViewModel()
        val librariesVm = ComponentLibrariesViewModel()
        val clockManager = ClockManager()
        val translationVm = TranslationViewModel()
        val appState = AppState()

        setContent {
            Box(modifier = Modifier.size(1280.dp, 820.dp)) {
                App(
                    viewModel = viewModel,
                    simulation = simulation,
                    librariesViewModel = librariesVm,
                    clockManager = clockManager,
                    translationViewModel = translationVm,
                    appState = appState
                )
            }
        }
        waitForIdle()

        val canvasNode = onNodeWithTag("logic_canvas").assertExists()

        // Place Input 0 at (100, 100) and Input 1 at (100, 220) using Quick-Insert "INPUT" button
        onNodeWithTag("quick_insert_INPUT").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performTouchInput { click(Offset(100f, 100f)) }
        waitForIdle()

        onNodeWithTag("quick_insert_INPUT").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performTouchInput { click(Offset(100f, 220f)) }
        waitForIdle()

        // Place AND gate at (280, 160) using Quick-Insert "AND" button
        onNodeWithTag("quick_insert_AND").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performTouchInput { click(Offset(280f, 160f)) }
        waitForIdle()

        // Place Output pin at (460, 160) using Quick-Insert "OUTPUT" button
        onNodeWithTag("quick_insert_OUTPUT").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performTouchInput { click(Offset(460f, 160f)) }
        waitForIdle()

        assertEquals(4, viewModel.components.size, "Components on canvas: ${viewModel.components.map { it.componentType }}")
        val in0 = viewModel.components[0]
        val in1 = viewModel.components[1]
        val andGate = viewModel.components[2]
        val outPin = viewModel.components[3]

        // Switch to Wire mode via UI button and connect all 3 wires
        onNodeWithText("Wire").performClick()
        waitForIdle()

        // Wire in0 -> andGate input 0
        canvasNode.performMouseInput { click(in0.findPortOffset()) }
        waitForIdle()
        canvasNode.performMouseInput { click(andGate.inputPortPositions()[0]) }
        waitForIdle()

        // Wire in1 -> andGate input 1
        canvasNode.performMouseInput { click(in1.findPortOffset()) }
        waitForIdle()
        canvasNode.performMouseInput { click(andGate.inputPortPositions()[1]) }
        waitForIdle()

        // Wire andGate output 0 -> outPin
        canvasNode.performMouseInput { click(andGate.outputPortPositions()[0]) }
        waitForIdle()
        canvasNode.performMouseInput { click(outPin.findPortOffset()) }
        waitForIdle()

        assertEquals(3, viewModel.wires.size)

        // Start simulation via UI button
        onNodeWithText("Start").performClick()
        waitForIdle()

        // State 00 -> Output LOW
        assertEquals(Pin.LOW, outPin.outputPin)

        // Poke in0 -> State 10 -> Output LOW
        val in0Center = Offset(in0.x + in0.width / 2f, in0.y + in0.height / 2f)
        val in1Center = Offset(in1.x + in1.width / 2f, in1.y + in1.height / 2f)
        canvasNode.performMouseInput { click(in0Center) }
        waitForIdle()
        assertEquals(Pin.HIGH, in0.outputPin)
        assertEquals(Pin.LOW, in1.outputPin)
        assertEquals(Pin.LOW, outPin.outputPin)

        // Poke in1 -> State 11 -> Output HIGH
        canvasNode.performMouseInput { click(in1Center) }
        waitForIdle()
        assertEquals(Pin.HIGH, in0.outputPin)
        assertEquals(Pin.HIGH, in1.outputPin)
        assertEquals(Pin.HIGH, andGate.outputPin)
        assertEquals(Pin.HIGH, outPin.outputPin)

        // Poke in0 again -> State 01 -> Output LOW
        canvasNode.performMouseInput { click(in0Center) }
        waitForIdle()
        assertEquals(Pin.LOW, in0.outputPin)
        assertEquals(Pin.HIGH, in1.outputPin)
        assertEquals(Pin.LOW, outPin.outputPin)
    }

    /**
     * Playwright-style Test 4:
     * Builds an Asynchronous D Flip-Flop circuit via UI clicks, selects a component in Edit mode
     * to verify the Inspector panel displays its ID & properties, and verifies simulation output.
     */
    @Test
    fun testBuildAsyncFlipFlopAndInspectComponentViaUi() = runComposeUiTest {
        val viewModel = CanvasViewModel()
        val simulation = SimulationViewModel()
        val librariesVm = ComponentLibrariesViewModel()
        val clockManager = ClockManager()
        val translationVm = TranslationViewModel()
        val appState = AppState()

        setContent {
            Box(modifier = Modifier.size(1280.dp, 820.dp)) {
                App(
                    viewModel = viewModel,
                    simulation = simulation,
                    librariesViewModel = librariesVm,
                    clockManager = clockManager,
                    translationViewModel = translationVm,
                    appState = appState
                )
            }
        }
        waitForIdle()

        val canvasNode = onNodeWithTag("logic_canvas").assertExists()

        // Place Input pin for SET at (100, 140)
        onNodeWithText("INPUT").performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(100f, 140f)) }
        waitForIdle()

        // Open "Flip-Flops" library -> click "D Flip-Flop (Async)" -> place at (280, 140)
        onNodeWithText("Flip-Flops").performClick()
        waitForIdle()
        onNodeWithText("D Flip-Flop (Async)").performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(280f, 140f)) }
        waitForIdle()

        // Place Output pin for Q at (480, 140)
        onNodeWithText("OUTPUT").performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(480f, 140f)) }
        waitForIdle()

        assertEquals(3, viewModel.components.size)
        val setIn = viewModel.components[0]
        val dAsync = viewModel.components[1]
        val qOut = viewModel.components[2]
        assertEquals(ComponentType.D_FLIP_FLOP_ASYNC, dAsync.componentType)

        // Click "Switch to Edit Mode" inside the Inspector empty state and select D Flip-Flop on canvas
        onNodeWithText("Switch to Edit Mode").performClick()
        waitForIdle()
        assertEquals(CanvasViewModel.editingMode.EDIT, viewModel.currentMode)

        val dAsyncCenter = Offset(dAsync.x + dAsync.width / 2f, dAsync.y + dAsync.height / 2f)
        canvasNode.performMouseInput { click(dAsyncCenter) }
        waitForIdle()

        assertEquals(dAsync.ID, viewModel.selectedGateId)
        onNodeWithText("ID:${dAsync.ID}").assertExists().assertIsDisplayed()
        onAllNodesWithText("D_FLIP_FLOP_ASYNC")[0].assertIsDisplayed()

        // Switch to Wire mode and connect SET (port 0) and Q (output 0)
        onNodeWithText("Wire").performClick()
        waitForIdle()

        canvasNode.performMouseInput { click(setIn.findPortOffset()) }
        waitForIdle()
        canvasNode.performMouseInput { click(dAsync.inputPortPositions()[0]) }
        waitForIdle()

        canvasNode.performMouseInput { click(dAsync.outputPortPositions()[0]) }
        waitForIdle()
        canvasNode.performMouseInput { click(qOut.findPortOffset()) }
        waitForIdle()

        assertEquals(2, viewModel.wires.size)

        // Start simulation and poke SET input -> Q output must become HIGH immediately
        onNodeWithText("Start").performClick()
        waitForIdle()
        assertEquals(Pin.LOW, qOut.outputPin)

        val setCenter = Offset(setIn.x + setIn.width / 2f, setIn.y + setIn.height / 2f)
        canvasNode.performMouseInput { click(setCenter) }
        waitForIdle()

        assertEquals(Pin.HIGH, setIn.outputPin)
        assertEquals(Pin.HIGH, dAsync.outputPin)
        assertEquals(Pin.HIGH, qOut.outputPin)
    }

    /**
     * Playwright-style Test 5:
     * Combines Memory ("4-Bit Counter"), I/O ("Hex Display" + "LED"), and Wiring ("Input pin")
     * via UI clicks, wires the 4-bit counter outputs to the Hex Display and LED, and pokes the clock
     * input across multiple ticks to verify the counter advances (0 -> 1 -> 2 -> 3) in the UI.
     */
    @Test
    fun testBuildAndSimulate4BitCounterWithHexDisplayAndLedViaUiClicks() = runComposeUiTest {
        val viewModel = CanvasViewModel()
        val simulation = SimulationViewModel()
        val librariesVm = ComponentLibrariesViewModel()
        val clockManager = ClockManager()
        val translationVm = TranslationViewModel()
        val appState = AppState()

        setContent {
            Box(modifier = Modifier.size(1280.dp, 820.dp)) {
                App(
                    viewModel = viewModel,
                    simulation = simulation,
                    librariesViewModel = librariesVm,
                    clockManager = clockManager,
                    translationViewModel = translationVm,
                    appState = appState
                )
            }
        }
        waitForIdle()

        val canvasNode = onNodeWithTag("logic_canvas").assertExists()

        // 1. Place Input pin (Clock pulse source) at (100, 140)
        onNodeWithText("Wiring").performClick()
        waitForIdle()
        onNodeWithText("Input pin").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(100f, 140f)) }
        waitForIdle()

        // 2. Place 4-Bit Counter from "Memory" tab at (280, 160)
        onNodeWithText("Memory").performClick()
        waitForIdle()
        onNodeWithText("4-Bit Counter").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(280f, 160f)) }
        waitForIdle()

        // 3. Place Hex Display at (480, 140) and LED at (480, 320) from "I/O" tab
        onNodeWithText("I/O").performClick()
        waitForIdle()
        onNodeWithText("Hex Display").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(480f, 140f)) }
        waitForIdle()

        onNodeWithText("LED").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(480f, 320f)) }
        waitForIdle()

        assertEquals(4, viewModel.components.size)
        val clkPin = viewModel.components[0]
        val counter = viewModel.components[1]
        val hexDisplay = viewModel.components[2]
        val led = viewModel.components[3]

        assertEquals(ComponentType.COUNTER_4BIT, counter.componentType)
        assertEquals(ComponentType.HEX_DIGIT_DISPLAY, hexDisplay.componentType)
        assertEquals(ComponentType.LED, led.componentType)

        // 4. Switch to Wire mode and wire Clock -> Counter[0], Counter Q0..Q3 -> Hex Display D0..D3, Counter Q0 -> LED
        onNodeWithText("Wire").performClick()
        waitForIdle()

        canvasNode.performMouseInput { click(clkPin.findPortOffset()) }
        waitForIdle()
        canvasNode.performMouseInput { click(counter.inputPortPositions()[0]) }
        waitForIdle()

        for (bit in 0..3) {
            canvasNode.performMouseInput { click(counter.outputPortPositions()[bit]) }
            waitForIdle()
            canvasNode.performMouseInput { click(hexDisplay.inputPortPositions()[bit]) }
            waitForIdle()
        }

        canvasNode.performMouseInput { click(counter.outputPortPositions()[0]) }
        waitForIdle()
        canvasNode.performMouseInput { click(led.findPortOffset()) }
        waitForIdle()

        assertEquals(6, viewModel.wires.size)

        // 5. Start simulation and poke clkPin to generate rising edges
        onNodeWithText("Start").performClick()
        waitForIdle()
        assertEquals(0, counter.memoryData.first())
        assertEquals(Pin.LOW, led.outputPin)

        val clkCenter = Offset(clkPin.x + clkPin.width / 2f, clkPin.y + clkPin.height / 2f)

        // Rising edge 1 -> Count = 1 (Q0=HIGH, Q1=LOW), LED=HIGH
        canvasNode.performMouseInput { click(clkCenter) }
        waitForIdle()
        assertEquals(1, counter.memoryData.first())
        assertEquals(Pin.HIGH, led.outputPin)
        assertEquals(Pin.HIGH, hexDisplay.inputPins[0])
        assertEquals(Pin.LOW, hexDisplay.inputPins[1])

        // Falling edge -> Count holds at 1
        canvasNode.performMouseInput { click(clkCenter) }
        waitForIdle()
        assertEquals(1, counter.memoryData.first())

        // Rising edge 2 -> Count = 2 (Q0=LOW, Q1=HIGH), LED=LOW
        canvasNode.performMouseInput { click(clkCenter) }
        waitForIdle()
        assertEquals(2, counter.memoryData.first())
        assertEquals(Pin.LOW, led.outputPin)
        assertEquals(Pin.LOW, hexDisplay.inputPins[0])
        assertEquals(Pin.HIGH, hexDisplay.inputPins[1])
    }

    /**
     * Playwright-style Test 6:
     * Combines Plexers ("MUX 2:1") with Wiring ("Constant 0 (GND)", "Constant 1 (VCC)", "Input pin", "Probe")
     * via UI clicks and verifies the Probe toggles from 0 to 1 when the MUX select pin is poked.
     */
    @Test
    fun testBuildAndSimulateMux2To1WithConstantPinsAndProbeViaUiClicks() = runComposeUiTest {
        val viewModel = CanvasViewModel()
        val simulation = SimulationViewModel()
        val librariesVm = ComponentLibrariesViewModel()
        val clockManager = ClockManager()
        val translationVm = TranslationViewModel()
        val appState = AppState()

        setContent {
            Box(modifier = Modifier.size(1280.dp, 820.dp)) {
                App(
                    viewModel = viewModel,
                    simulation = simulation,
                    librariesViewModel = librariesVm,
                    clockManager = clockManager,
                    translationViewModel = translationVm,
                    appState = appState
                )
            }
        }
        waitForIdle()

        val canvasNode = onNodeWithTag("logic_canvas").assertExists()

        // 1. Place Constant 0 (GND), Constant 1 (VCC), Input pin (SEL), and Probe from "Wiring" tab
        onNodeWithText("Wiring").performClick()
        waitForIdle()

        onNodeWithText("Constant 0 (GND)").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(100f, 80f)) }
        waitForIdle()

        onNodeWithText("Constant 1 (VCC)").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(100f, 180f)) }
        waitForIdle()

        onNodeWithText("Input pin").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(100f, 280f)) }
        waitForIdle()

        onNodeWithText("Probe").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(460f, 160f)) }
        waitForIdle()

        // 2. Place MUX 2:1 from "Plexers" tab at (280, 160)
        onNodeWithText("Plexers").performClick()
        waitForIdle()
        onNodeWithText("MUX 2:1").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(280f, 160f)) }
        waitForIdle()

        assertEquals(5, viewModel.components.size)
        val gnd = viewModel.components[0]
        val vcc = viewModel.components[1]
        val selPin = viewModel.components[2]
        val probe = viewModel.components[3]
        val mux = viewModel.components[4]

        // 3. Wire GND -> MUX D0 (0), VCC -> MUX D1 (1), SEL -> MUX S (2), MUX Y (0) -> Probe
        onNodeWithText("Wire").performClick()
        waitForIdle()

        canvasNode.performMouseInput { click(gnd.findPortOffset()) }
        waitForIdle()
        canvasNode.performMouseInput { click(mux.inputPortPositions()[0]) }
        waitForIdle()

        canvasNode.performMouseInput { click(vcc.findPortOffset()) }
        waitForIdle()
        canvasNode.performMouseInput { click(mux.inputPortPositions()[1]) }
        waitForIdle()

        canvasNode.performMouseInput { click(selPin.findPortOffset()) }
        waitForIdle()
        canvasNode.performMouseInput { click(mux.inputPortPositions()[2]) }
        waitForIdle()

        canvasNode.performMouseInput { click(mux.outputPortPositions()[0]) }
        waitForIdle()
        canvasNode.performMouseInput { click(probe.findPortOffset()) }
        waitForIdle()

        assertEquals(4, viewModel.wires.size)

        // 4. Start simulation: SEL=LOW selects D0 (GND=LOW) -> Probe=LOW
        onNodeWithText("Start").performClick()
        waitForIdle()
        assertEquals(Pin.LOW, probe.outputPin)

        // 5. Poke SEL Input pin -> SEL=HIGH selects D1 (VCC=HIGH) -> Probe=HIGH
        val selCenter = Offset(selPin.x + selPin.width / 2f, selPin.y + selPin.height / 2f)
        canvasNode.performMouseInput { click(selCenter) }
        waitForIdle()
        assertEquals(Pin.HIGH, selPin.outputPin)
        assertEquals(Pin.HIGH, mux.outputPin)
        assertEquals(Pin.HIGH, probe.outputPin)
    }

    /**
     * Playwright-style Test 7:
     * Combines I/O ("4-Bit DIP Switch") with Arithmetic ("Half Adder") and Wiring ("Output pin")
     * via UI clicks, and pokes individual switches on the 4-Bit DIP Switch bank on the canvas
     * to verify the Half Adder SUM and CARRY outputs update in real time.
     */
    @Test
    fun testBuildAndSimulateDipSwitchAndHalfAdderViaUiClicks() = runComposeUiTest {
        val viewModel = CanvasViewModel()
        val simulation = SimulationViewModel()
        val librariesVm = ComponentLibrariesViewModel()
        val clockManager = ClockManager()
        val translationVm = TranslationViewModel()
        val appState = AppState()

        setContent {
            Box(modifier = Modifier.size(1280.dp, 820.dp)) {
                App(
                    viewModel = viewModel,
                    simulation = simulation,
                    librariesViewModel = librariesVm,
                    clockManager = clockManager,
                    translationViewModel = translationVm,
                    appState = appState
                )
            }
        }
        waitForIdle()

        val canvasNode = onNodeWithTag("logic_canvas").assertExists()

        // 1. Place 4-Bit DIP Switch from "I/O" tab at (100, 140)
        onNodeWithText("I/O").performClick()
        waitForIdle()
        onNodeWithText("4-Bit DIP Switch").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(100f, 140f)) }
        waitForIdle()

        // 2. Place Half Adder from "Arithmetic" tab at (280, 140)
        onNodeWithText("Arithmetic").performClick()
        waitForIdle()
        onNodeWithText("Half Adder").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(280f, 140f)) }
        waitForIdle()

        // 3. Place two Output pins (SUM and CARRY) at (460, 100) and (460, 220)
        onNodeWithText("Wiring").performClick()
        waitForIdle()
        onNodeWithText("Output pin").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(460f, 100f)) }
        waitForIdle()

        onNodeWithText("Output pin").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(460f, 220f)) }
        waitForIdle()

        assertEquals(4, viewModel.components.size)
        val dipSwitch = viewModel.components[0]
        val halfAdder = viewModel.components[1]
        val sumOut = viewModel.components[2]
        val carryOut = viewModel.components[3]

        // 4. Wire DIP SW0 -> HalfAdder A (0), DIP SW1 -> HalfAdder B (1), HalfAdder S (0) -> sumOut, HalfAdder C (1) -> carryOut
        onNodeWithText("Wire").performClick()
        waitForIdle()

        canvasNode.performMouseInput { click(dipSwitch.outputPortPositions()[0]) }
        waitForIdle()
        canvasNode.performMouseInput { click(halfAdder.inputPortPositions()[0]) }
        waitForIdle()

        canvasNode.performMouseInput { click(dipSwitch.outputPortPositions()[1]) }
        waitForIdle()
        canvasNode.performMouseInput { click(halfAdder.inputPortPositions()[1]) }
        waitForIdle()

        canvasNode.performMouseInput { click(halfAdder.outputPortPositions()[0]) }
        waitForIdle()
        canvasNode.performMouseInput { click(sumOut.findPortOffset()) }
        waitForIdle()

        canvasNode.performMouseInput { click(halfAdder.outputPortPositions()[1]) }
        waitForIdle()
        canvasNode.performMouseInput { click(carryOut.findPortOffset()) }
        waitForIdle()

        assertEquals(4, viewModel.wires.size)

        // 5. Start simulation: initially SW0=0, SW1=0 -> SUM=LOW, CARRY=LOW
        onNodeWithText("Start").performClick()
        waitForIdle()
        assertEquals(Pin.LOW, sumOut.outputPin)
        assertEquals(Pin.LOW, carryOut.outputPin)

        // 6. Poke SW0 row on the DIP Switch -> SW0=1, SW1=0 -> SUM=HIGH, CARRY=LOW
        val sw0Pos = Offset(dipSwitch.x + dipSwitch.width / 2f, dipSwitch.outputPortPositions()[0].y)
        val sw1Pos = Offset(dipSwitch.x + dipSwitch.width / 2f, dipSwitch.outputPortPositions()[1].y)

        canvasNode.performMouseInput { click(sw0Pos) }
        waitForIdle()
        assertEquals(Pin.HIGH, sumOut.outputPin)
        assertEquals(Pin.LOW, carryOut.outputPin)

        // 7. Poke SW1 row on the DIP Switch -> SW0=1, SW1=1 -> SUM=LOW, CARRY=HIGH
        canvasNode.performMouseInput { click(sw1Pos) }
        waitForIdle()
        assertEquals(Pin.LOW, sumOut.outputPin)
        assertEquals(Pin.HIGH, carryOut.outputPin)
    }

    /**
     * Playwright-style Test 8:
     * Combines 74** TTL ("7404" Hex Inverter IC) with Gates ("Buffer") and Wiring ("Constant 0 (GND)",
     * "Constant 1 (VCC)", "Input pin", "Output pin") via UI clicks and verifies the chain simulation.
     */
    @Test
    fun testBuildAndSimulate7404WithBufferAndPowerRailsViaUiClicks() = runComposeUiTest {
        val viewModel = CanvasViewModel()
        val simulation = SimulationViewModel()
        val librariesVm = ComponentLibrariesViewModel()
        val clockManager = ClockManager()
        val translationVm = TranslationViewModel()
        val appState = AppState()

        setContent {
            Box(modifier = Modifier.size(1280.dp, 820.dp)) {
                App(
                    viewModel = viewModel,
                    simulation = simulation,
                    librariesViewModel = librariesVm,
                    clockManager = clockManager,
                    translationViewModel = translationVm,
                    appState = appState
                )
            }
        }
        waitForIdle()

        val canvasNode = onNodeWithTag("logic_canvas").assertExists()

        // 1. Place Input pin, GND, VCC, and Output pin from "Wiring" tab
        onNodeWithText("Wiring").performClick()
        waitForIdle()

        onNodeWithText("Input pin").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(80f, 80f)) }
        waitForIdle()

        onNodeWithText("Constant 0 (GND)").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(80f, 220f)) }
        waitForIdle()

        onNodeWithText("Constant 1 (VCC)").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(80f, 320f)) }
        waitForIdle()

        onNodeWithText("Output pin").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(620f, 120f)) }
        waitForIdle()

        // 2. Place 7404 Hex Inverter IC from "74**" tab at (260, 140)
        onNodeWithText("74**").performClick()
        waitForIdle()
        onNodeWithText("7404").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(260f, 140f)) }
        waitForIdle()

        // 3. Place Buffer from "Gates" tab at (460, 120)
        onNodeWithText("Gates").performClick()
        waitForIdle()
        onNodeWithText("Buffer").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(460f, 120f)) }
        waitForIdle()

        assertEquals(6, viewModel.components.size)
        val inPin = viewModel.components[0]
        val gnd = viewModel.components[1]
        val vcc = viewModel.components[2]
        val outPin = viewModel.components[3]
        val ic7404 = viewModel.components[4]
        val buffer = viewModel.components[5]

        // 4. Wire inPin -> 7404 1A (0), GND -> 7404 GND (3), VCC -> 7404 VCC (7), 7404 1Y (0) -> Buffer (0), Buffer (0) -> outPin
        onNodeWithText("Wire").performClick()
        waitForIdle()

        canvasNode.performMouseInput { click(inPin.findPortOffset()) }
        waitForIdle()
        canvasNode.performMouseInput { click(ic7404.inputPortPositions()[0]) }
        waitForIdle()

        canvasNode.performMouseInput { click(gnd.findPortOffset()) }
        waitForIdle()
        canvasNode.performMouseInput { click(ic7404.inputPortPositions()[3]) }
        waitForIdle()

        canvasNode.performMouseInput { click(vcc.findPortOffset()) }
        waitForIdle()
        canvasNode.performMouseInput { click(ic7404.inputPortPositions()[7]) }
        waitForIdle()

        canvasNode.performMouseInput { click(ic7404.outputPortPositions()[0]) }
        waitForIdle()
        canvasNode.performMouseInput { click(buffer.inputPortPositions()[0]) }
        waitForIdle()

        canvasNode.performMouseInput { click(buffer.outputPortPositions()[0]) }
        waitForIdle()
        canvasNode.performMouseInput { click(outPin.findPortOffset()) }
        waitForIdle()

        assertEquals(5, viewModel.wires.size)

        // 5. Start simulation: inPin=LOW -> 7404 1Y=HIGH -> Buffer=HIGH -> outPin=HIGH
        onNodeWithText("Start").performClick()
        waitForIdle()
        assertEquals(Pin.HIGH, outPin.outputPin)

        // 6. Poke inPin -> inPin=HIGH -> 7404 1Y=LOW -> Buffer=LOW -> outPin=LOW
        val inCenter = Offset(inPin.x + inPin.width / 2f, inPin.y + inPin.height / 2f)
        canvasNode.performMouseInput { click(inCenter) }
        waitForIdle()
        assertEquals(Pin.HIGH, inPin.outputPin)
        assertEquals(Pin.LOW, outPin.outputPin)
    }

    /**
     * Playwright-style Test 9:
     * Verifies rotating components 90 degrees via Q / E shortcuts when placing a new component
     * and when in Edit mode, as well as via the two turn buttons to the left of the component ID
     * in the Edit / Inspector menu header, and wiring rotated components.
     */
    @Test
    fun testRotateComponentViaShortcutsAndInspectorButtonsInUi() = runComposeUiTest {
        val viewModel = CanvasViewModel()
        val simulation = SimulationViewModel()
        val librariesVm = ComponentLibrariesViewModel()
        val clockManager = ClockManager()
        val translationVm = TranslationViewModel()
        val appState = AppState()

        setContent {
            Box(modifier = Modifier.size(1280.dp, 820.dp)) {
                App(
                    viewModel = viewModel,
                    simulation = simulation,
                    librariesViewModel = librariesVm,
                    clockManager = clockManager,
                    translationViewModel = translationVm,
                    appState = appState
                )
            }
        }
        waitForIdle()

        val canvasNode = onNodeWithTag("logic_canvas").assertExists()

        // 1. Select NOT Gate to start placing a new component on canvas
        onNodeWithTag("quick_insert_NOT").performScrollTo().performClick()
        waitForIdle()
        assertNotNull(viewModel.pendingComponent)
        assertEquals(0, viewModel.pendingComponent!!.rotationSteps)

        // Press 'E' key to rotate right 90 degrees while placing
        canvasNode.performKeyInput { pressKey(Key.E) }
        waitForIdle()
        assertEquals(1, viewModel.pendingComponent!!.rotationSteps)

        // Place rotated NOT gate on canvas at (260, 200)
        canvasNode.performMouseInput { click(Offset(260f, 200f)) }
        waitForIdle()
        assertEquals(1, viewModel.components.size)
        val notComp = viewModel.components.first()
        assertEquals(1, notComp.rotationSteps)
        assertEquals(90f, notComp.rotationDegrees)

        // 2. Switch to Edit Mode and click the rotated NOT gate
        onNodeWithText("Switch to Edit Mode").performClick()
        waitForIdle()
        val notCenter = Offset(notComp.x + notComp.width / 2f, notComp.y + notComp.height / 2f)
        canvasNode.performMouseInput { click(notCenter) }
        waitForIdle()
        assertEquals(notComp.ID, viewModel.selectedGateId)

        // Verify the two rotation buttons exist in the Inspector header to the left of the ID badge
        val leftBtn = onNodeWithTag("rotate_left_button").assertExists().assertIsDisplayed()
        val rightBtn = onNodeWithTag("rotate_right_button").assertExists().assertIsDisplayed()
        val idBadge = onNodeWithText("ID:${notComp.ID}").assertExists().assertIsDisplayed()

        val leftBounds = leftBtn.fetchSemanticsNode().boundsInRoot
        val rightBounds = rightBtn.fetchSemanticsNode().boundsInRoot
        val idBounds = idBadge.fetchSemanticsNode().boundsInRoot
        assertTrue(leftBounds.right <= rightBounds.left + 1f, "Turn left button should be to the left of turn right button")
        assertTrue(rightBounds.right <= idBounds.left + 1f, "Turn right button should be to the left of ID badge")

        // Click turn right button -> rotationSteps becomes 2 (180 degrees)
        rightBtn.performClick()
        waitForIdle()
        assertEquals(2, notComp.rotationSteps)

        // Click turn left button -> rotationSteps becomes 1 (90 degrees)
        leftBtn.performClick()
        waitForIdle()
        assertEquals(1, notComp.rotationSteps)

        // Press 'Q' shortcut in Edit mode -> rotationSteps becomes 0 (0 degrees)
        canvasNode.performKeyInput { pressKey(Key.Q) }
        waitForIdle()
        assertEquals(0, notComp.rotationSteps)

        // Press 'E' shortcut in Edit mode -> rotationSteps becomes 1 (90 degrees)
        canvasNode.performKeyInput { pressKey(Key.E) }
        waitForIdle()
        assertEquals(1, notComp.rotationSteps)

        // 3. Start placing a new AND gate, then press 'Delete' key to discard it before placement
        onNodeWithTag("quick_insert_AND").performScrollTo().performClick()
        waitForIdle()
        assertNotNull(viewModel.pendingComponent)
        canvasNode.performKeyInput { pressKey(Key.Delete) }
        waitForIdle()
        assertNull(viewModel.pendingComponent)
        assertEquals(1, viewModel.components.size)

        // 4. With notComp selected in Edit mode, press 'Delete' key to delete the selected component
        assertEquals(notComp.ID, viewModel.selectedGateId)
        canvasNode.performKeyInput { pressKey(Key.Delete) }
        waitForIdle()
        assertEquals(0, viewModel.components.size)
        assertEquals(null, viewModel.selectedGateId)
    }

    /**
     * Playwright-style Test 10:
     * Verifies single-click selection in DRAG mode, left-button drag marquee multi-selection,
     * group dragging, group rotation with Q/E, group deletion with Delete, and right-mouse-button canvas panning.
     */
    @Test
    fun testDragModeMultiSelectionGroupDragRotateDeleteAndRightClickPan() = runComposeUiTest {
        val viewModel = CanvasViewModel()
        val simulation = SimulationViewModel()
        val librariesVm = ComponentLibrariesViewModel()
        val clockManager = ClockManager()
        val translationVm = TranslationViewModel()
        val appState = AppState()

        setContent {
            Box(modifier = Modifier.size(1280.dp, 820.dp)) {
                App(
                    viewModel = viewModel,
                    simulation = simulation,
                    librariesViewModel = librariesVm,
                    clockManager = clockManager,
                    translationViewModel = translationVm,
                    appState = appState
                )
            }
        }
        waitForIdle()

        val canvasNode = onNodeWithTag("logic_canvas").assertExists()

        // 1. Place two AND gates in DRAG mode at (140, 140) and (280, 140)
        onNodeWithTag("quick_insert_AND").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(140f, 140f)) }
        waitForIdle()

        onNodeWithTag("quick_insert_AND").performScrollTo().performClick()
        waitForIdle()
        canvasNode.performMouseInput { click(Offset(280f, 140f)) }
        waitForIdle()

        assertEquals(2, viewModel.components.size)
        val c1 = viewModel.components[0]
        val c2 = viewModel.components[1]
        assertEquals(CanvasViewModel.editingMode.DRAG, viewModel.currentMode)

        // 2. Single-click c1 in DRAG mode -> highlights/selects c1 and allows rotating with E/Q
        val c1Center = Offset(c1.x + c1.width / 2f, c1.y + c1.height / 2f)
        canvasNode.performMouseInput { click(c1Center) }
        waitForIdle()
        assertEquals(c1.ID, viewModel.selectedGateId)
        assertTrue(c1.ID in viewModel.selectedGateIds)

        canvasNode.performKeyInput { pressKey(Key.E) }
        waitForIdle()
        assertEquals(1, c1.rotationSteps)
        canvasNode.performKeyInput { pressKey(Key.Q) }
        waitForIdle()
        assertEquals(0, c1.rotationSteps)

        // 3. Left-click and drag across empty canvas from (50, 50) to (400, 260) to select both c1 and c2
        canvasNode.performMouseInput {
            moveTo(Offset(50f, 50f))
            press( MouseButton.Primary )
            moveTo(Offset(400f, 260f))
            release( MouseButton.Primary )
        }
        waitForIdle()
        assertEquals(2, viewModel.selectedGateIds.size)
        assertTrue(c1.ID in viewModel.selectedGateIds)
        assertTrue(c2.ID in viewModel.selectedGateIds)

        // 4. Drag c1 by +60px X, +60px Y -> both c1 and c2 move together
        val origC1X = c1.x
        val origC2X = c2.x
        val startDragPos = Offset(c1.x + c1.width / 2f, c1.y + c1.height / 2f)
        canvasNode.performMouseInput {
            moveTo(startDragPos)
            press(MouseButton.Primary)
            moveTo(startDragPos + Offset(60f, 60f))
            release(MouseButton.Primary)
        }
        waitForIdle()
        assertEquals(origC1X + 60f, c1.x)
        assertEquals(origC2X + 60f, c2.x)

        // 5. Press 'E' -> both selected components rotate 90 degrees right
        canvasNode.performKeyInput { pressKey(Key.E) }
        waitForIdle()
        assertEquals(1, c1.rotationSteps)
        assertEquals(1, c2.rotationSteps)

        // 6. Right-mouse-button drag pans the canvas viewport
        assertEquals(0f, viewModel.viewportX)
        assertEquals(0f, viewModel.viewportY)
        canvasNode.performMouseInput {
            moveTo(Offset(500f, 300f))
            press(MouseButton.Secondary)
            moveTo(Offset(450f, 260f))
            release(MouseButton.Secondary)
        }
        waitForIdle()
        assertEquals(50f, viewModel.viewportX)
        assertEquals(40f, viewModel.viewportY)

        // 7. Press 'Delete' -> deletes all selected components in DRAG mode
        canvasNode.performKeyInput { pressKey(Key.Delete) }
        waitForIdle()
        assertEquals(0, viewModel.components.size)
        assertTrue(viewModel.selectedGateIds.isEmpty())
    }
}
