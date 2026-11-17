package com.example.digisim

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
            "NOT Gate", "AND Gate", "OR Gate", "NAND Gate", "NOR Gate", "XOR Gate", "XNOR Gate"
        )
        for (gateName in expectedGates) {
            val node = onNodeWithText(gateName).assertExists().assertIsDisplayed()
            val bounds = node.fetchSemanticsNode().boundsInRoot
            assertTrue(bounds.width > 10f, "Expected $gateName to have visible width, got ${bounds.width}")
            assertTrue(bounds.right <= 330f, "Expected $gateName to fit inside sidebar, got right=${bounds.right}")
        }

        // 2. Click "Wiring" tab and verify all 4 components are visible
        onNodeWithText("Wiring").performClick()
        waitForIdle()

        val expectedWiring = listOf("Input pin", "Button", "Output pin", "Clock")
        for (itemName in expectedWiring) {
            val node = onNodeWithText(itemName).assertExists().assertIsDisplayed()
            val bounds = node.fetchSemanticsNode().boundsInRoot
            assertTrue(bounds.width > 10f, "Expected $itemName to have visible width, got ${bounds.width}")
            assertTrue(bounds.right <= 330f, "Expected $itemName to fit inside sidebar, got right=${bounds.right}")
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

        // 4. Click "74**" tab and verify all 6 74-series ICs are visible
        onNodeWithText("74**").performClick()
        waitForIdle()

        val expectedChips = listOf("7400", "7402", "7404", "7408", "7432", "7486")
        for (chipName in expectedChips) {
            val node = onNodeWithText(chipName).assertExists().assertIsDisplayed()
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
}
