package com.example.digisim

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.digisim.DrawingLogic.CanvasViewModel
import com.example.digisim.DrawingLogic.getComponentInnerRectColor
import com.example.digisim.DrawingLogic.getComponentTextColor
import com.example.digisim.DrawingLogic.pokeComponent
import com.example.digisim.DrawingLogic.wirePins
import com.example.digisim.LogicGates.And
import com.example.digisim.LogicGates.Input
import com.example.digisim.LogicGates.Not
import com.example.digisim.LogicGates.Output
import com.example.digisim.ParsingLogic.Wire
import com.example.digisim.SimulationHandling.SimulationViewModel
import logicGates.Pin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SharedCommonTest {

    @Test
    fun testSimulation() {
        val viewModel = CanvasViewModel()
        val in0 = Input(0, 0f, 0f, 0, 1)
        val in1 = Input(1, 0f, 0f, 0, 1)
        val and2 = And(2, 0f, 0f, 2, 1)
        val out3 = Output(3, 0f, 0f, 1, 0)

        viewModel.components.addAll(listOf(in0, in1, and2, out3))
        viewModel.wires.addAll(listOf(
            Wire(4, 0, 0, 2, 0),
            Wire(5, 1, 0, 2, 1),
            Wire(6, 2, 0, 3, 0)
        ))

        val simulation = SimulationViewModel()
        simulation.runSimulation(viewModel)

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

        viewModel.components.addAll(listOf(in0, in1, and2, out3))
        viewModel.wires.addAll(listOf(
            Wire(4, 0, 0, 2, 0),
            Wire(5, 1, 0, 2, 1),
            Wire(6, 2, 0, 3, 0)
        ))

        val simulation = SimulationViewModel()
        simulation.startSimulation(viewModel)

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

        viewModel.components.addAll(listOf(in0, in1, and2, out3))
        viewModel.wires.addAll(listOf(
            Wire(4, 0, 0, 2, 0),
            Wire(5, 1, 0, 2, 1),
            Wire(6, 2, 0, 3, 0)
        ))

        val simulation = SimulationViewModel()
        simulation.startSimulation(viewModel)

        assertEquals(Pin.LOW, in0.outputPin)
        assertEquals(Pin.LOW, in1.outputPin)
        assertEquals(Pin.LOW, and2.outputPin)
        assertEquals(Pin.LOW, out3.outputPin)

        // Poke in0 (clicking within in0's bounds: x in 10..90, y in 10..70)
        pokeComponent(viewModel, simulation, null, Offset(20f, 20f))
        assertEquals(Pin.HIGH, in0.outputPin)
        assertEquals(Pin.LOW, in1.outputPin)
        assertEquals(Pin.LOW, and2.outputPin)
        assertEquals(Pin.LOW, out3.outputPin)

        // Poke in1 (clicking within in1's bounds: x in 10..90, y in 100..160)
        pokeComponent(viewModel, simulation, null, Offset(20f, 110f))
        assertEquals(Pin.HIGH, in0.outputPin)
        assertEquals(Pin.HIGH, in1.outputPin)
        assertEquals(Pin.HIGH, and2.outputPin)
        assertEquals(Pin.HIGH, out3.outputPin)

        // Poke in0 again -> turns OFF
        pokeComponent(viewModel, simulation, null, Offset(20f, 20f))
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

        viewModel.components.addAll(listOf(in0, not1, out2))
        viewModel.wires.addAll(listOf(
            Wire(3, 0, 0, 1, 0),
            Wire(4, 1, 0, 2, 0)
        ))

        val simulation = SimulationViewModel()
        simulation.startSimulation(viewModel)

        // Initially input is LOW, so NOT output is HIGH, Output is HIGH
        assertEquals(Pin.LOW, in0.outputPin)
        assertEquals(Pin.HIGH, not1.outputPin)
        assertEquals(Pin.HIGH, out2.outputPin)

        // Poke input to HIGH -> NOT output becomes LOW, Output becomes LOW
        pokeComponent(viewModel, simulation, null, Offset(20f, 20f))
        assertEquals(Pin.HIGH, in0.outputPin)
        assertEquals(Pin.LOW, not1.outputPin)
        assertEquals(Pin.LOW, out2.outputPin)
    }

    @Test
    fun testColorHelpers() {
        val highColor = getComponentInnerRectColor(Pin.HIGH)
        val lowColor = getComponentInnerRectColor(Pin.LOW)

        assertNotEquals(highColor, lowColor)
        assertEquals(Color(0xFF81C784), highColor)
        assertEquals(Color(0xFF2E7D32), lowColor)

        assertEquals(Color.Black, getComponentTextColor(Pin.HIGH))
        assertEquals(Color.White, getComponentTextColor(Pin.LOW))
    }

    @Test
    fun testWiringNotElementInUi() {
        val viewModel = CanvasViewModel()
        val in0 = Input(0, 10f, 10f, 0, 1)
        val not1 = Not(1, 150f, 10f, 1, 1)
        val out2 = Output(2, 300f, 10f, 1, 0)

        viewModel.components.addAll(listOf(in0, not1, out2))

        // Wire from in0 output port to not1 input port
        val in0OutPortPos = in0.findPortOffset() // Offset(90f, 40f)
        wirePins(viewModel, in0OutPortPos)
        val not1InPortPos = not1.inputPortPositions().first() // Offset(150f, 40f)
        wirePins(viewModel, not1InPortPos)

        assertEquals(1, viewModel.wires.size)
        assertEquals(0, viewModel.wires[0].sourceGateId)
        assertEquals(1, viewModel.wires[0].targetGateId)

        // Wire from out2 input port to not1 output port (reverse direction)
        val out2InPortPos = out2.findPortOffset() // Offset(300f, 40f)
        wirePins(viewModel, out2InPortPos)
        val not1OutPortPos = not1.outputPortPositions().first() // Offset(230f, 40f)
        wirePins(viewModel, not1OutPortPos)

        assertEquals(2, viewModel.wires.size)
        assertEquals(1, viewModel.wires[1].sourceGateId)
        assertEquals(2, viewModel.wires[1].targetGateId)

        // Run simulation on the wired circuit
        val simulation = SimulationViewModel()
        simulation.startSimulation(viewModel)

        assertEquals(Pin.LOW, in0.outputPin)
        assertEquals(Pin.HIGH, not1.outputPin)
        assertEquals(Pin.HIGH, out2.outputPin)
    }
}