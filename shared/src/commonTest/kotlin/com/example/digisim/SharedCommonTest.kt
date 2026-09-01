package com.example.digisim

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.DrawingLogic.createComponent
import com.example.digisim.DrawingLogic.dragComponent
import com.example.digisim.DrawingLogic.getComponentInnerRectColor
import com.example.digisim.DrawingLogic.getComponentTextColor
import com.example.digisim.DrawingLogic.handleDrag
import com.example.digisim.DrawingLogic.placePendingComponent
import com.example.digisim.DrawingLogic.pokeComponent
import com.example.digisim.DrawingLogic.updatePendingComponentPosition
import com.example.digisim.DrawingLogic.wirePins
import com.example.digisim.FlipFlops.DFlipFlop
import com.example.digisim.FlipFlops.JKFlipFlop
import com.example.digisim.FlipFlops.RSFlipFlop
import com.example.digisim.FlipFlops.TFlipFlop
import com.example.digisim.LogicGates.And
import com.example.digisim.LogicGates.Not
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.ParsingLogic.Wire
import com.example.digisim.SimulationHandling.SimulationViewModel
import com.example.digisim.UiUtils.ComponentLibrariesViewModel
import com.example.digisim.Wiring.Input
import com.example.digisim.Wiring.Output
import engineLogic.ClockManager
import kotlinx.coroutines.test.runTest
import logicGates.Pin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SharedCommonTest {

    @Test
    fun testSimulation() = runTest {
        val viewModel = CanvasViewModel()
        val in0 = Input(0, 0f, 0f, 0, 1)
        val in1 = Input(1, 0f, 0f, 0, 1)
        val and2 = And(2, 0f, 0f, 2, 1)
        val out3 = Output(3, 0f, 0f, 1, 0)
        val clockManager = ClockManager()

        viewModel.components.addAll(listOf(in0, in1, and2, out3))
        viewModel.wires.addAll(listOf(
            Wire(4, 0, 0, 2, 0),
            Wire(5, 1, 0, 2, 1),
            Wire(6, 2, 0, 3, 0)
        ))

        val simulation = SimulationViewModel()
        simulation.runSimulation(viewModel, clockManager = clockManager)

        assertEquals(Pin.LOW, in0.outputPin)
        assertEquals(Pin.LOW, in1.outputPin)
        assertEquals(Pin.LOW, and2.outputPin)
        assertEquals(Pin.LOW, out3.outputPin)
    }

    @Test
    fun testStartSimulationSetsInputsToLowByDefault() {
        val viewModel = CanvasViewModel()
        val in0 = Input(0, 0f, 0f, 0, 1).apply { outputPin = Pin.HIGH }
        val in1 = Input(1, 0f, 0f, 0, 1).apply { outputPin = Pin.HIGH }
        val and2 = And(2, 0f, 0f, 2, 1)
        val out3 = Output(3, 0f, 0f, 1, 0)
        val clockManager = ClockManager()

        viewModel.components.addAll(listOf(in0, in1, and2, out3))
        viewModel.wires.addAll(listOf(
            Wire(4, 0, 0, 2, 0),
            Wire(5, 1, 0, 2, 1),
            Wire(6, 2, 0, 3, 0)
        ))

        val simulation = SimulationViewModel()
        simulation.startSimulation(viewModel, clockManager = clockManager)

        assertTrue(simulation.isRunning)
        assertEquals(Pin.LOW, in0.outputPin)
        assertEquals(Pin.LOW, in1.outputPin)
        assertEquals(Pin.LOW, and2.outputPin)
        assertEquals(Pin.LOW, out3.outputPin)
    }

    @Test
    fun testPokeInputTogglesAndUpdatesSimulationInRealTime() {
        val viewModel = CanvasViewModel()
        val in0 = Input(0, 10f, 10f, 0, 1)
        val in1 = Input(1, 10f, 100f, 0, 1)
        val and2 = And(2, 150f, 50f, 2, 1)
        val out3 = Output(3, 280f, 50f, 1, 0)
        val clockManager = ClockManager()

        viewModel.components.addAll(listOf(in0, in1, and2, out3))
        viewModel.wires.addAll(listOf(
            Wire(4, 0, 0, 2, 0),
            Wire(5, 1, 0, 2, 1),
            Wire(6, 2, 0, 3, 0)
        ))

        val simulation = SimulationViewModel()
        simulation.startSimulation(viewModel, clockManager = clockManager)

        assertEquals(Pin.LOW, in0.outputPin)
        assertEquals(Pin.LOW, in1.outputPin)
        assertEquals(Pin.LOW, and2.outputPin)
        assertEquals(Pin.LOW, out3.outputPin)

        // Poke in0 (clicking within in0's bounds: x in 10..90, y in 10..70)
        pokeComponent(viewModel, simulation, null, Offset(20f, 20f), clockManager)
        assertEquals(Pin.HIGH, in0.outputPin)
        assertEquals(Pin.LOW, in1.outputPin)
        assertEquals(Pin.LOW, and2.outputPin)
        assertEquals(Pin.LOW, out3.outputPin)

        // Poke in1 (clicking within in1's bounds: x in 10..90, y in 100..160)
        pokeComponent(viewModel, simulation, null, Offset(20f, 110f), clockManager)
        assertEquals(Pin.HIGH, in0.outputPin)
        assertEquals(Pin.HIGH, in1.outputPin)
        assertEquals(Pin.HIGH, and2.outputPin)
        assertEquals(Pin.HIGH, out3.outputPin)

        // Poke in0 again -> turns OFF
        pokeComponent(viewModel, simulation, null, Offset(20f, 20f), clockManager)
        assertEquals(Pin.LOW, in0.outputPin)
        assertEquals(Pin.HIGH, in1.outputPin)
        assertEquals(Pin.LOW, and2.outputPin)
        assertEquals(Pin.LOW, out3.outputPin)
    }

    @Test
    fun testInverterCircuitSimulationAndPoke() {
        val viewModel = CanvasViewModel()
        val in0 = Input(0, 10f, 10f, 0, 1)
        val not1 = Not(1, 120f, 10f, 1, 1)
        val out2 = Output(2, 240f, 10f, 1, 0)
        val clockManager = ClockManager()

        viewModel.components.addAll(listOf(in0, not1, out2))
        viewModel.wires.addAll(listOf(
            Wire(3, 0, 0, 1, 0),
            Wire(4, 1, 0, 2, 0)
        ))

        val simulation = SimulationViewModel()
        simulation.startSimulation(viewModel, clockManager = clockManager)

        // Initially input is LOW, so NOT output is HIGH, Output is HIGH
        assertEquals(Pin.LOW, in0.outputPin)
        assertEquals(Pin.HIGH, not1.outputPin)
        assertEquals(Pin.HIGH, out2.outputPin)

        // Poke input to HIGH -> NOT output becomes LOW, Output becomes LOW
        pokeComponent(viewModel, simulation, null, Offset(20f, 20f), clockManager)
        assertEquals(Pin.HIGH, in0.outputPin)
        assertEquals(Pin.LOW, not1.outputPin)
        assertEquals(Pin.LOW, out2.outputPin)
    }

    @Test
    fun testColorHelpers() {
        val highColor = getComponentInnerRectColor(Pin.HIGH)
        val lowColor = getComponentInnerRectColor(Pin.LOW)

        assertNotEquals(highColor, lowColor)
        assertEquals(Color(0xFF2E7D32), highColor)
        assertEquals(Color(0xFF81C784), lowColor)

        assertEquals(Color.Black, getComponentTextColor(Pin.HIGH))
        assertEquals(Color.White, getComponentTextColor(Pin.LOW))
    }

    @Test
    fun testSettingsViewModelColorCustomization() {
        val defaultSettings = SettingsViewModel()
        assertEquals(true, defaultSettings.drawText)

        val noTextSettings = SettingsViewModel(drawText = false)
        assertEquals(false, noTextSettings.drawText)

        val customSettings = SettingsViewModel()
        customSettings.highPinColor = Color.Cyan
        customSettings.lowPinColor = Color.Magenta
        customSettings.gateOutlineColor = Color.Yellow
        customSettings.portColor = Color.DarkGray
        customSettings.wireHighlightColor = Color.Green
        customSettings.textHighColor = Color.Red
        customSettings.textLowColor = Color.Blue
        customSettings.drawText = false

        assertEquals(false, customSettings.drawText)
        assertEquals(Color.Cyan, customSettings.getComponentInnerRectColor(Pin.HIGH))
        assertEquals(Color.Magenta, customSettings.getComponentInnerRectColor(Pin.LOW))
        assertEquals(Color.Red, customSettings.getComponentTextColor(Pin.HIGH))
        assertEquals(Color.Blue, customSettings.getComponentTextColor(Pin.LOW))
        assertEquals(Color.Yellow, customSettings.gateOutlineColor)
        assertEquals(Color.DarkGray, customSettings.portColor)
        assertEquals(Color.Green, customSettings.wireHighlightColor)
    }

    @Test
    fun testWiringNotElementInUi() {
        val viewModel = CanvasViewModel()
        val in0 = Input(0, 10f, 10f, 0, 1)
        val not1 = Not(1, 150f, 10f, 1, 1)
        val out2 = Output(2, 300f, 10f, 1, 0)
        val clockManager = ClockManager()

        viewModel.components.addAll(listOf(in0, not1, out2))

        // Wire from in0 output port to not1 input port
        val in0OutPortPos = in0.findPortOffset()
        wirePins(viewModel, in0OutPortPos)
        val not1InPortPos = not1.inputPortPositions().first()
        wirePins(viewModel, not1InPortPos)

        assertEquals(1, viewModel.wires.size)
        assertEquals(0, viewModel.wires[0].sourceGateId)
        assertEquals(1, viewModel.wires[0].targetGateId)

        // Wire from out2 input port to not1 output port (reverse direction)
        val out2InPortPos = out2.findPortOffset()
        wirePins(viewModel, out2InPortPos)
        val not1OutPortPos = not1.outputPortPositions().first()
        wirePins(viewModel, not1OutPortPos)

        assertEquals(2, viewModel.wires.size)
        assertEquals(1, viewModel.wires[1].sourceGateId)
        assertEquals(2, viewModel.wires[1].targetGateId)

        // Run simulation on the wired circuit
        val simulation = SimulationViewModel()
        simulation.startSimulation(viewModel, clockManager = clockManager)

        assertEquals(Pin.LOW, in0.outputPin)
        assertEquals(Pin.HIGH, not1.outputPin)
        assertEquals(Pin.HIGH, out2.outputPin)
    }

    @Test
    fun testFlipFlopCreationAndLibraries() {
        val libVm = ComponentLibrariesViewModel()
        val flipFlopsLib = libVm.libraries.find { it.name == "Flip-Flops" }
        assertNotNull(flipFlopsLib)
        assertEquals(4, flipFlopsLib.components.size)

        val rsComp = createComponent(ComponentType.RS_FLIP_FLOP, 1, 10f, 20f)
        assertTrue(rsComp is RSFlipFlop)
        assertEquals(2, rsComp.inputCount)
        assertEquals(2, rsComp.outputCount)

        val jkComp = createComponent(ComponentType.JK_FLIP_FLOP, 2, 10f, 20f)
        assertTrue(jkComp is JKFlipFlop)
        assertEquals(3, jkComp.inputCount)
        assertEquals(2, jkComp.outputCount)

        val dComp = createComponent(ComponentType.D_FLIP_FLOP, 3, 10f, 20f)
        assertTrue(dComp is DFlipFlop)
        assertEquals(2, dComp.inputCount)
        assertEquals(2, dComp.outputCount)

        val tComp = createComponent(ComponentType.T_FLIP_FLOP, 4, 10f, 20f)
        assertTrue(tComp is TFlipFlop)
        assertEquals(2, tComp.inputCount)
        assertEquals(2, tComp.outputCount)

        val btnComp = createComponent(ComponentType.BUTTON, 5, 10f, 20f)
        assertTrue(btnComp is com.example.digisim.Wiring.Button)
        assertEquals(0, btnComp.inputCount)
        assertEquals(1, btnComp.outputCount)

        val wiringLib = libVm.libraries.find { it.name == "Wiring" }
        assertNotNull(wiringLib)
        assertTrue(wiringLib.components.any { it.type == ComponentType.BUTTON })
    }

    @Test
    fun testButtonPokeInteractionInUi() {
        val viewModel = CanvasViewModel()
        val btn = com.example.digisim.Wiring.Button(0, 10f, 10f, 0, 1)
        val out1 = Output(1, 150f, 10f, 1, 0)
        val clockManager = ClockManager()

        viewModel.components.addAll(listOf(btn, out1))
        viewModel.wires.add(Wire(2, 0, 0, 1, 0))

        val simulation = SimulationViewModel()
        simulation.startSimulation(viewModel, clockManager = clockManager)

        // Initially LOW
        assertEquals(Pin.LOW, btn.outputPin)
        assertEquals(Pin.LOW, out1.outputPin)

        // Poke Button -> fires 1-tick pulse (HIGH)
        pokeComponent(viewModel, simulation, null, Offset(20f, 20f), clockManager)
        assertEquals(Pin.HIGH, out1.outputPin)

        // Next tick evaluation without poke -> Button automatically resets to LOW
        kotlinx.coroutines.runBlocking {
            simulation.runSimulation(viewModel, null, clockManager)
        }
        assertEquals(Pin.LOW, btn.outputPin)
        assertEquals(Pin.LOW, out1.outputPin)
    }

    @Test
    fun testDragBoundaryConstraints() {
        val viewModel = CanvasViewModel()
        val andGate = And(0, 100f, 100f, 2, 1)
        viewModel.components.add(andGate)

        val canvasWidth = 500f
        val canvasHeight = 400f

        handleDrag(viewModel, Offset(110f, 110f))
        assertNotNull(viewModel.dragState)

        dragComponent(viewModel, Offset(-200f, -100f), canvasWidth, canvasHeight)
        assertEquals(0f, andGate.x)
        assertEquals(0f, andGate.y)

        dragComponent(viewModel, Offset(1000f, 1000f), canvasWidth, canvasHeight)
        assertEquals(420f, andGate.x)
        assertEquals(340f, andGate.y)

        dragComponent(viewModel, Offset(213f, 154f), canvasWidth, canvasHeight)
        assertEquals(200f, andGate.x)
        assertEquals(140f, andGate.y)
    }

    @Test
    fun testHoverAndPlaceComponent() {
        val viewModel = CanvasViewModel()
        val canvasWidth = 600f
        val canvasHeight = 500f

        assertNull(viewModel.pendingComponent)
        assertEquals(0, viewModel.components.size)

        viewModel.addComponent(ComponentType.AND)
        val pending = viewModel.pendingComponent
        assertNotNull(pending)
        assertEquals(ComponentType.AND, pending.componentType)

        updatePendingComponentPosition(viewModel, Offset(200f, 150f), canvasWidth, canvasHeight)
        assertEquals(160f, pending.x)
        assertEquals(120f, pending.y)

        updatePendingComponentPosition(viewModel, Offset(-50f, -50f), canvasWidth, canvasHeight)
        assertEquals(0f, pending.x)
        assertEquals(0f, pending.y)

        updatePendingComponentPosition(viewModel, Offset(1000f, 1000f), canvasWidth, canvasHeight)
        assertEquals(520f, pending.x)
        assertEquals(440f, pending.y)

        placePendingComponent(viewModel, Offset(200f, 150f), canvasWidth, canvasHeight)

        assertNull(viewModel.pendingComponent)
        assertEquals(1, viewModel.components.size)
        val placed = viewModel.components.first()
        assertEquals(ComponentType.AND, placed.componentType)
        assertEquals(160f, placed.x)
        assertEquals(120f, placed.y)
    }
}